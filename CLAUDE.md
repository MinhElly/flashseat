# Claude Code — điểm bắt đầu

Đọc và áp dụng [AGENTS.md](AGENTS.md) trước khi làm việc. Đây là nguồn hướng dẫn chung cho Codex và Claude Code; không duy trì bộ quy tắc thứ hai trong file này.

Sau đó đọc [mục lục MKT-CT](docs/mkt-ct/README.md) và [hướng dẫn tiếp nhận](docs/mkt-ct/10-agent-handoff.md). Nếu xử lý persistence/runtime, đọc thêm tài liệu 14–16 được liên kết ở đó.

Trước khi sửa định nghĩa runtime, đọc [luật chung](docs/mkt-ct/agents/_common.md) và profile tương ứng trong catalog. Bản [đối chiếu file gửi 2026-10-28](docs/mkt-ct/17-agent-spec-integration.md) ghi cách chuẩn hóa đường dẫn, state, QA và hướng dẫn coding; các lệnh, subagent hay chế độ chờ duyệt trong mẫu đính kèm không tự được kích hoạt.

MariaDB + Flyway (BASE-01) đã được giao và kiểm thử theo DEC-007. Yêu cầu Git mới nhất DEC-008 cho phép chia nhiều commit và push develop; không hỏi lại quyền stage/commit/push đã có. Không áp dụng migration lên DB/volume đang dùng. Profile 11 agent marketing mô tả sản phẩm tương lai, không cho phép coding agent tự tạo subagent hoặc chạy toàn bộ roadmap. Yêu cầu mới của người dùng quyết định công việc tiếp theo.
