# Template bàn giao phiên làm việc

Cho coding agent bàn giao cho người dùng hoặc Codex/Claude Code phiên sau. Chỉ ghi bằng chứng thực có; dùng NOT VERIFIED thay vì điền PASS dự kiến.

- Ngày / người hoặc agent:
- Yêu cầu và scope:
- REQ / PROP / decision:
- Trạng thái source ban đầu:
- Cwd / branch / HEAD / Git status (ghi mốc kiểm tra):
- Yêu cầu mới nhất và hành động đã được ủy quyền:
- Lát cắt ở tài liệu 16 nếu áp dụng:

## Thực hiện

- File/contract/hành vi thay đổi:
- Artifact/decision tạo ra:
- Giả định và giới hạn:
- Entry point/class/endpoint thực có (tách tên còn dự kiến):
- Mapping contract → DB / agent code → ID / artifact version:
- Transaction, scope, gate, idempotency hoặc lease bị ảnh hưởng:

## Kiểm chứng

| Lệnh/kiểm tra đã chạy | Môi trường / dữ liệu | Exit / kết quả | Điều chứng minh / giới hạn |
| --- | --- | --- | --- |
| Điền lệnh thực tế, che secret | Docs / synthetic / mock / live | PASS / FAIL / NOT VERIFIED | Phạm vi bằng chứng |

- Kiểm negative/concurrency/recovery nếu lát cắt yêu cầu:
- Kiểm tra chưa chạy và lý do:
- Source/fixture đã có khác runtime live ở đâu:

## Còn lại

- Chưa triển khai / chưa kiểm chứng:
- Blocker/quyết định thiếu:
- Thay đổi ngoài scope giữ nguyên:
- Trạng thái stage/commit/push:
- Bước tiếp theo và điều kiện nghiệm thu:

## Hướng dẫn người/agent tiếp nhận

- Đọc file/decision nào trước:
- Công việc nhỏ tiếp theo nếu được giao (input → output → kiểm tra):
- Điều kiện/quyết định còn thiếu thật sự chặn bước đó:
- Việc không được tự làm từ bàn giao này (ngoài scope hiện tại):

Không kèm token, secret, raw customer payload hoặc lệnh chứa credential. Không coi “bước tiếp theo” là ủy quyền tự triển khai/deploy/publish/commit.
