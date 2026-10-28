# Template báo cáo QA

Trạng thái: PROPOSED; report khóa vào phiên bản cụ thể.

- Report/task/reviewer ID:
- QA task / qa_run_id / qa-compliance prompt version:
- Artifact ID + version / checksum:
- Rule set ID + version:
- Input/source versions:
- Finding ID / revision_task_id / resolved_in_version_id khi có sửa:
- Ngày kiểm tra:
- Nguồn đã mở / accessed_at / locator / snapshot hoặc checksum nếu có:
- Model reviewer / model tạo nội dung (nếu biết); nguồn/rule độc lập:
- Score fact/brand/rights và rubric (optional, chưa đo dùng null):

| Tiêu chí | Verdict | Bằng chứng / vị trí | Yêu cầu sửa |
| --- | --- | --- | --- |
| Claim/số liệu | Chọn verdict | Source + locator | Nếu cần |
| Brand/glossary | Chọn verdict | Guideline version | Nếu cần |
| Kênh/ngôn ngữ/CTA | Chọn verdict | Artifact locator | Nếu cần |
| Quyền dùng/chia sẻ | Chọn verdict | Bằng chứng quyền | Nếu cần |
| Cấu trúc/định dạng | Chọn verdict | Rule | Nếu cần |

Verdict: PASS, FAIL, NOT_VERIFIED, NOT_APPLICABLE. NOT_APPLICABLE có lý do/policy; NOT_VERIFIED không thành PASS.

Mỗi finding ghi ID, category FACT/BRAND/COPYRIGHT/COMPLIANCE, severity BLOCKER/MAJOR/MINOR, content_locator, evidence/reason và revision_request cụ thể. Vi phạm đã xác định → FAIL; chưa kiểm đủ → NOT_VERIFIED; cả hai chặn gate bắt buộc. Chỉ PASS khi mọi mục bắt buộc đạt, không PASS có điều kiện hoặc bù BLOCKER bằng điểm trung bình. Model DONE chỉ là hoàn thành report; QA không sửa artifact tác giả. Mapping nhãn VERIFIED/UNSUPPORTED/OUTDATED trong bản gửi ở [05](../05-data-and-contracts.md).

## Kết luận

- Overall verdict / tiêu chí chặn:
- Revision hoặc bằng chứng cần bổ sung:
- Phạm vi đã/chưa kiểm tra:
- Đề nghị bước tiếp theo:

QA không tự cấp quyền phát hành.

Report map vào `qa_report`/`qa_finding`; qa_run_id trỏ agent_run đúng QA agent/project. Claim/source IDs phải resolve trong scope, có excerpt/locator đối chiếu, không chỉ có link chung. Review bản mới tạo report mới; chỉ reviewer có quyền xác nhận finding đã resolved. Xem [ví dụ NOT_VERIFIED](../examples/qa-report.json).
