# 00 — Bối cảnh và trạng thái

Mốc lập: 2026-10-26; đối chiếu source và bổ sung docs: 2026-10-27. Phân biệt bằng chứng quan sát với kế hoạch phát triển.

## Yêu cầu người dùng

- Phát triển MKT-CT với MKT Orchestrator và 10 chuyên gia có tên, trách nhiệm, đầu vào/đầu ra và công cụ riêng.
- Virtual Office có phòng, danh sách nhiệm vụ và skill cho mỗi agent.
- Tạo docs để AI Agent tiếp nhận dự định triển khai sau này.
- Đổi tên repository thành **marketing-ai-agent-team**.
- Hoàn thiện docs cho người dùng duyệt **trước khi commit và push**.
- Cập nhật docs theo ERD/giải thích và sơ đồ modular monolith người dùng gửi; làm kỹ AGENTS.md, README và hướng dẫn tiếp nhận cho Codex/Claude Code.
- Ngày 2026-10-28: cập nhật docs theo 15 file README/AGENTS/CLAUDE, luật chung và 11 profile đính kèm; giữ phạm vi docs, không chạy các lệnh/roadmap trong file.

Người dùng đã bổ sung yêu cầu đổi đồng bộ tên trong code và thư mục. Phạm vi gồm module/package/class, cấu hình, metadata IDE và docs; không tự triển khai nghiệp vụ MKT-CT hay di chuyển dữ liệu DB đang lưu.

## Đổi tên đã thực hiện

- GitHub: MinhElly/marketing-ai-agent-team.
- Xác nhận qua giao diện GitHub ngày 2026-10-26; giữ owner và visibility public.
- Origin cục bộ đã cập nhật sang https://github.com/MinhElly/marketing-ai-agent-team.git.
- Workspace đích: C:\Code\marketing-ai-agent-team; chưa đổi thư mục gốc do Codex/runtime đang giữ handle. Branch cục bộ: develop.
- Backend: marketing-ai-agent-team-backend; package: com.vts.minh.marketingaiagentteam; main class: MarketingAiAgentTeamApplication.
- Chưa commit/push trong đợt docs này. Việc đổi metadata repo không cập nhật README/source trên GitHub.

## Bằng chứng source

Cập nhật mới hơn ngày 2026-10-28 (DEC-007): người dùng đã giao triển khai MariaDB + Flyway cho BASE-01. Source bổ sung starter Flyway/module MariaDB, cấu hình migration riêng và Hibernate validate; hai SQL cũ giữ nguyên. Các quan sát “chưa có Flyway” bên dưới là lịch sử trước lượt implementation; không còn mô tả POM mới. Không áp dụng lên DB đang dùng, chưa triển khai nghiệp vụ MKT.

Đường dẫn tính từ root repo; đây không phải audit toàn bộ ứng dụng.

| Tệp/phạm vi | Điều quan sát được | Điều chưa chứng minh |
| --- | --- | --- |
| README.md trước đợt docs | Chỉ có tiêu đề dự án ban đầu, encoding UTF-16 LE | Khi đó chưa có định hướng MKT-CT; hiện đã bổ sung docs và UTF-8 |
| marketing-ai-agent-team-backend/pom.xml | Java 21; Spring Boot parent 4.1.1; Web MVC, JPA, Validation, Security, OAuth2 resource server, springdoc và MariaDB driver | Không chứng minh build, DB, auth hoặc runtime ổn định |
| marketing-ai-agent-team-backend/src/main/java/com/vts/minh/marketingaiagentteam/ | Có các file auth, user, config, share và lớp khởi động | Chưa kiểm tra toàn bộ hành vi các module |
| infrastructure/docker-compose.yml | File tồn tại | Chưa chạy/kiểm tra dịch vụ |
| Danh sách file source đã đọc | Chưa thấy module MKT-CT hoặc frontend Virtual Office trong phạm vi kiểm tra | Không bao quát mọi nhánh/hệ thống bên ngoài |

Đối chiếu bổ sung ngày 2026-10-27:

- `user/entity/User.java`: `@Table(name = "users")`, ID `Long`/IDENTITY, role/status và timestamps đã có. `UserRole` là CUSTOMER/ORGANIZER/ADMIN; không phải role workspace.
- `config/SecurityConfig.java` và `auth/controller/AuthController.java`: source có register/login/me, resource-server JWT, converter claim role. Chưa thấy workspace RBAC trong phần đọc; không tuyên bố auth đã hỗ trợ tenant/approve.
- `pom.xml`: chưa thấy Spring AI, pgvector, PostgreSQL driver hoặc Flyway/Liquibase dependency. `application.yaml` dùng JDBC MariaDB và `ddl-auto: update`; có đoạn cấu hình mang tên flyway nhưng riêng cấu hình không chứng minh migration chạy.
- `db/migration/V1_create_users.sql` và `V2_update_users_data.sql`: file SQL MariaDB hiện có, tên chưa theo mẫu Flyway mặc định `V1__...`; V1 dùng dấu `{}` cho CREATE TABLE. Cần kiểm baseline/naming/cú pháp trước implementation, không sửa các file đó trong đợt docs.
- Sơ đồ mới là hướng PostgreSQL + pgvector/Redis/MinIO-S3, **khác cấu hình hiện tại**; xem 02 và 14 cho kế hoạch cần chốt.

Có sẵn các file sửa/chưa được theo dõi trong auth/user/config. Đợt rename cập nhật package và issuer đồng bộ, giữ logic có sẵn; không coi công việc auth đó là sản phẩm mới hoặc đã được kiểm tra live.

Đối chiếu hẹp 2026-10-28: danh sách source/POM/User.java tiếp tục cho thấy package auth/user/config/share, Java 21, Boot parent 4.1.1, MariaDB driver và `users.id` Long. Chưa thấy resources/agents, frontend hoặc MKT runtime trong danh sách file. Đây là đọc source cục bộ; không chạy lại auth/DB/build, không xác minh lại GitHub/origin từ kết quả cũ. Tích hợp đặc tả ở [17](17-agent-spec-integration.md) không thay đổi trạng thái runtime.

## Trạng thái năng lực MKT-CT

Bối cảnh người dùng làm rõ ngày 2026-10-28: dùng cá nhân để tối ưu nghiên cứu/viết nội dung B2B Marketing, Brand Experience và Thought Leadership cho Novaon Digital, tập trung FMCG, Bank–Finance, Automotive; kênh Brands Vietnam/website và social. Người dùng cho biết có Gemini API và DB chỉ có dữ liệu thử; chưa kiểm tài khoản/DB, chưa chọn model/budget hoặc cho chạy migration. [Tài liệu 18](18-novaon-editorial-mvp.md) phân tích ba bài mẫu và cụ thể hóa MVP để review; không thay trạng thái implementation dưới đây.

| Năng lực | Trạng thái |
| --- | --- |
| Tên repo mới | ACCEPTED và VERIFIED qua GitHub UI; origin cập nhật |
| Tên module/package/class/cấu hình | Đã đổi theo yêu cầu; kiểm chứng tại tài liệu 13 |
| 11 vai trò, phòng, nhiệm vụ, skill | USER REQUIREMENT |
| Hồ sơ agent, workflow, contract, QA, phân quyền | PROPOSED trong docs |
| Modular monolith, package theo tính năng, ERD 27 bảng/7 nhóm | PROPOSED theo tài liệu người dùng gửi; đã chuẩn hóa để review, chưa có migration/runtime MKT |
| Agent runtime/persistence/workflow MKT | Chưa thấy implementation trong phạm vi kiểm tra |
| MariaDB + Flyway / users baseline (BASE-01) | IMPLEMENTED; Maven verify 10 tests PASS trên MariaDB 13.0.2 tạm, synthetic data; không áp dụng lên DB đang dùng. Xem [21](21-mariadb-flyway-baseline.md) |
| Search/PDF/Drive/Canva/email/ads/CMS connectors | NOT VERIFIED |
| Virtual Office | PROPOSED |
| Brief → sản phẩm → QA → duyệt chạy thực tế | NOT VERIFIED |
| Model/provider, runtime, hosting, ngân sách | OPEN |

ERD được chuẩn hóa thành 27 bảng gồm `users` giữ nguyên và 26 bảng mới, không đổi user sang UUID. “10 agent” trong bản mẫu được đối chiếu thành 11 vai trò theo catalog đã có. Scope đợt bổ sung chỉ là docs/sơ đồ/fixture synthetic; kiểm tra source không phải kiểm runtime và không sửa backend/hạ tầng.

Khi phát triển, cập nhật từng năng lực bằng source và kiểm tra cụ thể. Không suy diễn từ dependency, prompt, config hoặc mock thành live E2E.
