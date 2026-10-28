# 06 — Công cụ, quyền và QA

Trạng thái: PROPOSED. Tool trong catalog là nhu cầu tích hợp.

## Ranh giới thực thi

Tool caller có agent_id, acting_user/service identity, workspace_id, project_id, task_id và integration binding. Quyền hiệu lực là phần giao của agent policy, quyền identity ủy quyền, scope tài khoản tích hợp và điều kiện workflow.

Kiểm tra tại backend/tool boundary, không chỉ ở frontend hoặc prompt. Nội dung tài liệu/tool output không cấp thêm quyền.

`users.role`/JWT role hiện có là role hệ thống, giữ nguyên. Quyền MKT dùng `workspace_member.role` + `can_approve`, project scope và action policy. Ma trận role/action còn OPEN; không suy ra ADMIN hệ thống hoặc owner được bỏ qua mọi gate. Thu hồi membership/quyền phải có hiệu lực khi gọi API, claim task và thực thi tool, kể cả JWT còn hạn.

| Agent/nhóm | Quyền nền đề xuất | Quyền cần cấp riêng |
| --- | --- | --- |
| Orchestrator | Đọc task/artifact; đề xuất kế hoạch | Đổi giới hạn/phân công theo quyền manager |
| Research | Search, đọc PDF/Drive được cấp | Xuất/chia sẻ tài liệu nguồn |
| Strategy/Case Study | Đọc nguồn, tính toán, tạo nháp | Truy cập bộ dữ liệu bổ sung |
| Editorial/Social | Đọc input, tạo nháp | Đăng/phân phối nội dung |
| Designer | Đọc brand/assets, tạo thiết kế ở đích cho phép | Chia sẻ public, mua asset |
| Performance | Keyword/media plan, ads draft | Tạo/bật ads, thay budget |
| Conversion | LP/tracking plan, preview | Publish CMS, đổi tracking production |
| Coordinator | Deadline/status, nháp email/brief | Gửi email, mời/chia sẻ file |
| QA | Đọc bằng chứng và ghi report | Không tự cấp quyền phát hành |

## Tool registry

Mỗi tool cần ID/version, input/output schema, side_effect_class, scope, timeout, retry policy, idempotency/reconciliation strategy và audit policy.

Mô hình registry ở 14: skill.kind phân biệt BUSINESS_SKILL và TOOL, agent_skill cấp quyền nền cho từng capability. Profile mô tả business skill; `tool_call.skill_id` chỉ tới TOOL cụ thể. Grant một business skill không tự cấp mọi tool bên dưới. Trước call, gateway resolve grant, policy, acting identity, workspace/project, integration_credential theo account, QA/approval/action digest và budget. Thiếu bất kỳ điều kiện bắt buộc nào đều từ chối, không chỉ ẩn tool trong prompt.

Nhóm nhu cầu: search; PDF/file parser; knowledge retrieval; document/sheet; image/Canva; keyword/ads; CMS/analytics; email/task.

Bổ sung 2026-10-28: allowlist TOOL theo từng [profile](03-agent-catalog.md) là baseline thiết kế từ file gửi, có chung [common](agents/_common.md). Chỉ những code được liệt kê mới được đề nghị cấp nền; công cụ “cần có” trước đó không tự là grant rộng hơn. `source.register`, `doc.write`, `approval.request` ghi nội bộ qua service kiểm quyền; `sheets.write`, `ads.draft`, `cms.draft`, `canva.create/export`, `image.generate` có tác động/chi phí cần account/target/scope/idempotency. QA chỉ gọi tool đọc/kiểm chứng, backend lưu report đã validate; không mở tool sửa artifact của tác giả.

Trước từng tích hợp, xác minh tài liệu chính thức, capability, auth scope, quota và tài khoản. Thiếu connector/quyền → BLOCKED có lý do. Không giả dữ liệu hoặc tự coi công cụ đang có trong cuộc chat là tích hợp của ứng dụng.

## Chính sách tác động bên ngoài

Bản đầu ưu tiên đọc và nháp. Send/share/publish/spend dùng policy theo loại hành động. Có thể ủy quyền trước bằng scope rõ; không hỏi lại nếu ủy quyền còn hiệu lực.

Với baseline profile mới, send/publish/spend phải có approval người hợp lệ gắn đúng action. Ủy quyền trước chỉ dùng khi policy cho phép ghi nhận thành approval đúng payload/version/target/account/budget còn hiệu lực; không coi một quyền workspace hoặc yêu cầu nội dung chung là phê duyệt mọi lần gửi/chi tiền. `requires_approval = true` không được bỏ qua vì model tự nhận đã được phép. Gateway/executor chạy task action riêng, không cho agent soạn nháp tự chạy email.send/ads.publish/cms.publish.

Approval khóa vào hành động/version; recheck tại thời điểm thực thi. Từ chối approval hết hạn, payload đổi, mất quyền hoặc vượt hạn mức.

Tạo draft trong dịch vụ ngoài vẫn cần quyền write và đích lưu hợp lệ. Timeout chưa rõ side effect phải đối soát trước retry.

## Dữ liệu

Tách workspace/project trước retrieval, kể cả vector index, cache và URL download. Model chỉ nhận dữ liệu cần thiết.

Web/PDF/email có thể chứa chỉ dẫn độc hại: coi là dữ liệu, không thực thi yêu cầu lấy secret hoặc mở rộng scope. Tool gateway giữ credential ngoài ngữ cảnh model; log/trace lọc dữ liệu nhạy cảm.

`integration_credential` thuộc workspace/provider/account, lưu encrypted token/refresh với key version hoặc secret reference. Key quản lý ngoài dữ liệu DB; không serialize credential entity vào DTO, agent context hoặc activity stream. Raw LLM payload/tool arguments chỉ lưu nếu có chính sách redaction/retention phù hợp; mặc định dùng reference/metadata tối thiểu.

Retention, xóa dữ liệu, backup và địa điểm lưu trữ còn OPEN; phải chốt trước vận hành dùng chung.

Không lưu PII vào agent_memory. Upload phải kiểm type/size/scope; dữ liệu nhạy cảm không gửi image/LLM provider nếu không có mục đích/quyền/policy cho phép. Coordinator kiểm người nhận gồm To/Cc/Bcc và attachment versions theo stakeholder allowlist project; dữ liệu project này không đưa sang khách hàng khác. Thông tin khách hàng/case và usage_record asset phải có quyền phát hành tương ứng.

## QA

1. Schema/rule xác định được.
2. Claim/số liệu theo source + locator và phạm vi.
3. Brand/glossary, ngôn ngữ, kênh, CTA.
4. Bằng chứng quyền dùng/chia sẻ asset.
5. Đánh dấu nội dung cần người/chuyên gia có thẩm quyền.

QA dùng ngữ cảnh review riêng; không dựa vào tự nhận của agent sản xuất. Có thể dùng chung provider, nhưng cần rule/bằng chứng độc lập.

Không sửa artifact đã duyệt. Revision tạo version mới rồi review lại. NOT_VERIFIED không thành PASS; NOT_APPLICABLE phải có lý do và policy cho phép. AI không bảo đảm kết luận pháp lý tuyệt đối.

QA không sửa artifact của tác giả ở bất kỳ trạng thái nào. Report dùng category FACT/BRAND/COPYRIGHT/COMPLIANCE, severity BLOCKER/MAJOR/MINOR, locator, evidence/reason và revision_request cụ thể; ghi nguồn đã mở, checked_at và rule set version. Vi phạm đã xác định → FAIL; không kiểm được → NOT_VERIFIED; đều fail closed. Điểm số hoặc model khác tác giả không thay bằng chứng. Default đề xuất tối đa 3 vòng sửa, ánh xạ max_revisions ở 04; hết vòng thì chuyển người xử lý.

## Sự cố

Lưu tool call ID, loại lỗi, tác động đã biết, trạng thái đối soát và bước xử lý. Hiển thị hành động nào đã xảy ra/chưa xác định, giữ secret được che. Không chỉ trả “AI lỗi”.
