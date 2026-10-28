# 04 — Workflow và trạng thái

Trạng thái: PROPOSED. Enum cần chốt trước implementation.

Đây là enum chuẩn dùng xuyên docs/API/DB dự kiến; ERD rút gọn PENDING/DONE trong bản gửi được chuẩn hóa thành BLOCKED/READY và COMPLETED. Chi tiết mapping 05, persistence 14. Workflow template WF-01/WF-02 khác workflow_id (task gốc của một yêu cầu).

## Workflow

**WF-01 — MVP:** brief → Research → Editorial → QA → người dùng duyệt → bàn giao.

**WF-01-NOVAON — biến thể PROPOSED cho nhu cầu cá nhân:** thêm Strategy và gate duyệt hướng bài trước Editorial, Social và QA riêng cho biến thể sau bài gốc; chi tiết/điều kiện tại [18](18-novaon-editorial-mvp.md). Đây là workflow template dự kiến, không phải workflow_id mới trong DB, enum state mới hoặc thay đổi baseline đã triển khai.

**WF-02 — Chiến dịch:** brief → Research/Case Study nếu cần → Strategy → mốc duyệt strategy → Editorial/Performance/Conversion theo phụ thuộc → Social/Designer khi đủ đầu vào → QA từng sản phẩm → duyệt → bàn giao.

Coordinator theo dõi lịch/status/hồ sơ xuyên suốt. Không bắt buộc gọi đủ 10 chuyên gia.

## Quy tắc phụ thuộc

Task READY khi đủ input, mọi depends_on đã COMPLETED, artifact versions hợp lệ, đủ quota, agent/skill/tool có quyền. Từ chối cycle, self-dependency hoặc dependency sai scope.

Task mới thiếu điều kiện ở BLOCKED hoặc WAITING_INPUT theo nguyên nhân; đủ điều kiện mới READY. Parent/child biểu diễn phân rã, không tự tạo dependency. Task gốc lưu brief/plan metadata/checkpoint, children/dependencies lưu DAG, không cần bảng plan riêng; root tổng hợp completion sau deliverables chứ không là prerequisite chặn tất cả child.

Sau lập kế hoạch, root BLOCKED với checkpoint phase WAITING_DELIVERABLES và nhả lease. Scheduler áp dụng readiness riêng của root, không replan vô hạn vì depends_on rỗng; đủ child deliverables/gates mới chuyển READY cho phase MERGING. Phase là checkpoint, không phải state enum mới; restart đọc lại phase/plan_revision để không tạo DAG trùng.

QA tạo report qua qa_report_id, không tạo vòng dependency chờ task sản xuất COMPLETED. Nếu tách QA thành task độc lập, dùng checkpoint “artifact version sẵn sàng” để khởi chạy.

Checkpoint này chứa artifact_id/version_id/checksum và task_version, ghi cùng transaction với WAITING_QA/event. QA task được tạo idempotent từ checkpoint (không có cạnh depends_on đợi producer COMPLETED); report link qa_run_id, event/report qua task gate mở producer. Revision task không được phụ thuộc producer đang WAITING_QA nếu gây cùng vòng chờ. Service kiểm readiness đặc thù checkpoint, không lách bằng cách đổi producer thành COMPLETED sớm.

## Task state

| State | Ý nghĩa / bước ra chính |
| --- | --- |
| BLOCKED | Thiếu phụ thuộc, quota hoặc cấu hình; đủ điều kiện → READY |
| READY | Có thể nhận lease → RUNNING |
| RUNNING | Attempt thực hiện và sinh kết quả |
| WAITING_INPUT | Cần thông tin/đối soát cụ thể; có đủ → READY |
| WAITING_QA | Artifact version sẵn sàng để review |
| NEEDS_REVISION | QA/approver yêu cầu sửa; lập revision attempt → READY |
| WAITING_APPROVAL | Đủ QA, cần quyết định đúng thẩm quyền |
| PAUSED | Chủ động dừng lịch chạy; lưu resume_target |
| COMPLETED | Đủ output và gate của loại task |
| FAILED | Hết retry hoặc lỗi không phục hồi; retry có quyền tạo attempt mới qua READY |
| CANCELLED | Hủy; không nhận kết quả đến muộn; làm lại bằng task mới |

RUNNING có thể tới WAITING_QA, WAITING_INPUT, BLOCKED hoặc FAILED theo kết quả. Chỉ tới COMPLETED trực tiếp nếu loại task không cần QA/approval và đã đạt mọi điều kiện khác.

QA PASS → WAITING_APPROVAL hoặc COMPLETED theo gate_policy. QA FAIL → NEEDS_REVISION. QA NOT_VERIFIED → WAITING_INPUT với bằng chứng còn thiếu. NOT_APPLICABLE chỉ dành tiêu chí policy cho phép, không thay PASS cho gate bắt buộc.

Approval approve → COMPLETED cho task sản xuất khi mọi gate còn hợp lệ. Reject vì nội dung → NEEDS_REVISION; hủy theo quyết định hợp lệ → CANCELLED. Approval hết hạn hoặc mất hiệu lực tiếp tục chặn, không tự approve.

## Task, run và hành động bên ngoài

Một task có nhiều run/attempt. Run SUCCEEDED nghĩa runtime kết thúc hợp lệ, không chứng minh task đã qua QA, được duyệt hoặc phát hành.

Phân biệt: **attempt** là phiên claim/lease của task; **agent_run** là từng lần gọi LLM trong attempt (có invocation_no). Lease/fencing ở task bảo vệ mọi invocation/output của attempt. Tool call thuộc một agent_run; runtime có thể gọi LLM nhiều vòng rồi mới trả output cuối. Retry LLM ghi run mới; retry task tạo attempt mới; revision nghiệp vụ có counter riêng với giới hạn ở limits.

Task sản xuất COMPLETED là sản phẩm được chấp nhận. Đăng bài, gửi email, publish CMS hoặc bật ads là task hành động riêng với policy/idempotency tương ứng. Duyệt nội dung không mặc nhiên duyệt mọi hành động.

## Revision và đầu vào thay đổi

Artifact version giao QA/duyệt phải bất biến. Sửa tạo version mới, QA/approval cũ không tự áp dụng.

Khi upstream thay đổi, gắn cờ stale cho downstream bị ảnh hưởng, giữ lịch sử. Chặn sử dụng/phát hành bản stale; tạo task sửa/đánh giá lại. Không viết lại kết quả COMPLETED trong quá khứ.

Hạn mức revision do task cấu hình. Hết hạn mức → WAITING_INPUT cho manager thay vì lặp vô hạn.

Bổ sung từ profile gửi 2026-10-28: `max_revision_rounds` là tên tham khảo, ánh xạ cùng `limits.max_revisions` (default đề xuất 3), không tách counter thứ hai. Đếm vòng sửa nghiệp vụ khác retry LLM/tool và task attempt. Hết vòng vẫn FAIL/NOT_VERIFIED thì leo thang cho người với report/bằng chứng, không tự bỏ QA. Ví dụ task có max_revisions cụ thể vẫn dùng giá trị task đó.

Model `output_status = DONE / NEEDS_INPUT / BLOCKED / FAILED` là payload nội bộ theo [common](agents/_common.md) và mapping 05, không bổ sung DONE vào task enum. Model đề nghị side effect không tự chuyển WAITING_APPROVAL; backend tạo task action/yêu cầu duyệt theo policy và trạng thái hiện tại.

## Retry, pause/cancel và phục hồi

- Lỗi tạm thời: retry hữu hạn với backoff; ghi attempt.
- Sai quyền/dữ liệu: trả lỗi hoặc BLOCKED/WAITING_INPUT, không lặp vô hạn.
- Timeout sau side effect chưa rõ kết quả: đối soát trước retry.
- Pause/cancel ngăn lịch mới, yêu cầu worker dừng hợp tác; không thu hồi hành động đã xảy ra.
- Backend kiểm tra task_version, trạng thái và lease/fencing token trước khi nhận kết quả.
- Resume đánh giá lại scope, input freshness, quota, approval và resume_target.
- Restart đọc checkpoint/state bền vững; event lặp không gây hành động trùng.

## Ví dụ

Research COMPLETED với research-pack v1 → Editorial RUNNING → article v1 → QA FAIL → NEEDS_REVISION → article v2 → QA PASS → WAITING_APPROVAL → duyệt article v2 → COMPLETED.

Việc đăng article v2 cần task riêng nếu nằm trong phạm vi.
