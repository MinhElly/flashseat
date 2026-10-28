# 13 — Đổi tên đồng bộ dự án

Ngày: 2026-10-26. Căn cứ: yêu cầu trực tiếp đổi toàn bộ tên còn lại trong code và thư mục. Chưa stage, commit hoặc push.

## Quy ước định danh

| Phạm vi | Tên chuẩn |
| --- | --- |
| Repository / thư mục gốc | marketing-ai-agent-team |
| Backend folder / Maven artifact | marketing-ai-agent-team-backend |
| Java package | com.vts.minh.marketingaiagentteam |
| Main class / context test | MarketingAiAgentTeamApplication / MarketingAiAgentTeamApplicationTests |
| Spring application / JWT issuer | marketing-ai-agent-team |
| OpenAPI display name | Marketing AI Agent Team API |
| MariaDB database / user trong cấu hình | marketing_ai_agent_team_db / marketing_ai_agent_team |
| Container name trong Compose | marketing-ai-agent-team-mariadb |
| Workspace đích | C:\Code\marketing-ai-agent-team |

Package Java không dùng dấu gạch ngang; DB/user dùng underscore. Module/repository dùng kebab-case.

## Phạm vi

Đã đổi package/import/class, Maven, Spring application, JWT issuer ở cả phát hành lẫn decoder, OpenAPI, cấu hình DB/Compose, đường dẫn IDE, source/test folders và docs. Giữ logic auth/user hiện có ngoài các định danh đã được yêu cầu đổi. Thư mục gốc chưa đổi do khóa tiến trình, như mô tả bên dưới.

Không đổi lịch sử Git hoặc nội dung object/index để xóa dấu vết lịch sử. Không sửa dữ liệu bên ngoài repository hoặc tự migrate DB/volume.

## Lưu ý vận hành

- Token có issuer trước đợt đổi tên không còn khớp decoder mới; đăng nhập lại để lấy token mới.
- DB/user trong cấu hình mới cần tồn tại khi chạy với dữ liệu đã lưu. Biến khởi tạo MariaDB chỉ áp dụng lúc khởi tạo data directory; đổi Compose không tự migrate schema/user trong volume cũ.
- Không xóa volume hoặc chạy câu lệnh DB chỉ để hoàn thành rename. Việc chuyển dữ liệu cần backup và kế hoạch riêng nếu có dữ liệu cần giữ.
- File .env được giữ nguyên; không in credential hoặc token trong báo cáo.

## Kiểm chứng

- Maven offline clean package với skipTests: BUILD SUCCESS; compile 22 source files và 1 test source; tạo JAR theo artifact mới. Context test không được chạy bởi lệnh này.
- Kiểm tra JWT trong bộ nhớ: PASS cho token phát hành → decoder, issuer/subject/role/TTL và từ chối issuer khác; dùng khóa synthetic, không khởi động server/DB.
- Kiểm tra package/path: PASS cho 23 Java source/test; tên class công khai khớp filename.
- Kiểm tra bảo toàn thay đổi có sẵn: 10 file auth/user/config khớp baseline sau khi đảo đúng phép thay định danh; logic nghiệp vụ được giữ nguyên.
- Kiểm tra docs: 32 Markdown, 112 liên kết nội bộ và 4 ví dụ JSON; không còn định danh trước trong nội dung/đường dẫn đang dùng, loại trừ lịch sử Git và sản phẩm build.
- Thư mục gốc: BLOCKED bởi handle của ChatGPT/Codex, các worker và tool runtime. Đã đóng project IntelliJ và thử lại; Windows vẫn từ chối Move-Item. Không cưỡng bức đóng handle hoặc kill tiến trình để tránh ảnh hưởng các phiên chat.
- Script đổi root: PASS kiểm tra cú pháp PowerShell và chạy -WhatIf; chưa thực hiện rename thành công do khóa ở trên. Metadata IDE được rà và đồng bộ lại sau khi đóng project.
- Chưa chạy server/DB, migration dữ liệu hoặc live E2E MKT-CT.

## Hoàn tất bước đổi thư mục gốc

Script `tools/rename-workspace.ps1` lấy source root từ vị trí script, xác minh checkout/module, từ chối target đã tồn tại và chỉ rename trong cùng thư mục cha. Script không kill process, xóa dữ liệu, stage, commit, push hoặc chạy DB.

Trong PowerShell riêng, chuyển tới repo và kiểm tra bằng `& .\tools\rename-workspace.ps1 -WhatIf`. Sau khi đóng Codex và các ứng dụng/terminal còn giữ root, chạy `& .\tools\rename-workspace.ps1`. Script tự chuyển PowerShell ra thư mục cha để giải phóng working directory của chính nó.

Mở lại project ở C:\Code\marketing-ai-agent-team trong Codex/IDE sau khi rename thành công. File vẫn nằm ở workspace hiện tại cho tới khi bước này hoàn tất.
