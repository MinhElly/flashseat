# 20 — Đề xuất DB và migration baseline cho MVP cá nhân

**ADR-001 — ACCEPTED cho phương án A, 2026-10-28 (DEC-007).** Sau giải thích MariaDB dùng được Flyway, người dùng yêu cầu “oke b triển khai đi”: giữ MariaDB + Flyway cho BASE-01, không chuyển PostgreSQL. Nội dung so sánh/mapping PostgreSQL bên dưới là thiết kế trước quyết định, không phải DDL hiện tại. Người dùng cho biết dữ liệu hiện chỉ để thử; chưa audit hoặc tác động DB đang dùng.

Implementation và kết quả mới hơn: [21 — MariaDB/Flyway BASE-01](21-mariadb-flyway-baseline.md), gồm migration fresh, dependency tree, 10 test trên native DB tạm và giới hạn exact-version/Docker. Các mục OBSERVED/NOT VERIFIED trước implementation bên dưới giữ giá trị lịch sử tương ứng.

Đọc cùng [decision/OPEN](09-decisions-and-open-questions.md), [mô hình dữ liệu](14-relational-data-model.md), [package](15-source-code-structure.md), [lát cắt](16-implementation-handoff.md) và [MVP Novaon](18-novaon-editorial-mvp.md). Giữ users/auth/Long/BIGINT, 27 bảng logic và catalog 11 vai trò.

## 1. Source hiện tại — OBSERVED

Đối chiếu read-only ngày 2026-10-28, cwd `C:\Code\flashseat`, branch develop, HEAD d4f321e:

- POM dùng Java 21, Spring Boot parent 4.1.1, MariaDB driver; chưa khai báo Flyway/PostgreSQL/Spring AI. Không lấy tài liệu mẫu Boot 3.x để thay nền.
- `User` ánh xạ `users`, ID Long/IDENTITY; full_name 100, email 255 và unique; password_hash nullable 255; role/status 30, CUSTOMER/ACTIVE mặc định; timestamps LocalDateTime với CreationTimestamp/UpdateTimestamp.
- Register/login, CustomUserDetailsService và create user đang trim/lowercase email bằng Locale.ROOT. `JwtService` dùng user ID làm subject, claim role và issuer hiện có. `SecurityConfig` xác thực JWT, không có workspace access checker MKT trong phần đã đọc.
- SQL V1 dùng dấu `{}` trong CREATE TABLE và email 100, khác entity 255; V2 có DATETIME(6) và ON UPDATE CURRENT_TIMESTAMP. Tên V1/V2 dùng một underscore; chưa chứng minh chúng đã được migration engine áp dụng.
- Cấu hình nguồn dùng JDBC MariaDB và ddl-auto update theo đối chiếu đã ghi ở 00. Chưa kiểm schema live/collation/SQL mode/timezone/schema history. Không dùng entity hoặc SQL file để suy ngược schema live.

## 2. Hai lựa chọn có thể thực hiện

| Tiêu chí | A — MariaDB + Flyway | B — PostgreSQL mới + Flyway |
| --- | --- | --- |
| Thay đổi ban đầu | Ít thay driver/dialect hơn, nhưng vẫn cần baseline/SQL đúng và auth regression | Cần driver/profile/DDL mới, kiểm mapping/auth; không mang cú pháp MariaDB sang |
| Mô hình đích | Cần mapping JSON/UUID/time và quyết định vector riêng khi đến KB | Gần hướng JSONB/UUID/pgvector của ERD hiện có hơn |
| Dữ liệu hiện tại | Có thể tiếp tục môi trường thử sau khi biết schema/history | Tạo DB tách biệt, dùng dữ liệu synthetic mới; không cần chuyển dữ liệu thử nếu người dùng không yêu cầu |
| Rủi ro cần kiểm | Baseline chưa biết; DDL/constraint/index theo MariaDB; không giả có JSONB như PostgreSQL | Collation/email, identity, timestamp, cập nhật thời gian và behavior JPA/JDBC khác nhau |
| Hệ quả về sau | Chọn A đồng nghĩa duy trì một mapping đích MariaDB rõ ràng; không giữ ERD PostgreSQL như schema vật lý đã thực thi | Chọn B tránh thêm một đợt đổi engine sau khi đã có nhiều bảng MKT, nhưng chưa chứng minh tương thích version chỉ từ docs |

**Quyết định hiện tại: A — MariaDB + Flyway.** Khuyến nghị B trước đây đã được thay thế sau khi làm rõ nhu cầu: chưa cần đổi engine để làm MVP nghiên cứu/viết/QA. Giữ users/auth, kiểm migration cô lập; vector và mapping bảng MKT triển khai sau. Flyway hỗ trợ cả hai DB, không phải lý do phải chuyển PostgreSQL.

Chọn PostgreSQL **không bắt buộc bật pgvector/RAG, Redis hoặc MinIO ngay**. Lát cắt đầu chỉ thêm thành phần cần để nghiệm thu; vector/model embedding/dimension dành cho KB-01 sau khi có nhu cầu và cấu hình. Không thay provider/model hay framework runtime từ quyết định DB.

## 3. Căn cứ kỹ thuật chính thức và giới hạn version

Đã mở các nguồn sau ngày 2026-10-28, chỉ dùng cho đặc tính/khuyến nghị trong bảng, không chứng nhận tổ hợp dependency chạy được:

| Nguồn | Điều xác nhận / giới hạn |
| --- | --- |
| [PostgreSQL JSON types](https://www.postgresql.org/docs/current/datatype-json.html) | PostgreSQL có json/jsonb, jsonb hỗ trợ index. Không suy mọi payload cần JSONB hoặc mọi query được tăng tốc |
| [MariaDB JSON](https://mariadb.com/docs/server/reference/data-types/string-data-types/json) | JSON là alias LONGTEXT, có JSON validation; không đồng nhất với cách lưu/query/index JSONB PostgreSQL |
| [PostgreSQL identity](https://www.postgresql.org/docs/current/ddl-identity-columns.html) | Có GENERATED AS IDENTITY cho khóa số; chuyển phải kiểm sequence và cách insert ID explicit nếu import |
| [PostgreSQL datetime](https://www.postgresql.org/docs/current/datatype-datetime.html) | Phân biệt timestamp without time zone và with time zone; không tự gán timezone cho LocalDateTime cũ |
| [Spring Boot database initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html) | Khuyến nghị một cơ chế tạo schema; tài liệu hiện hành nêu starter Flyway, module theo DB, naming/location. URL 4.1 chuyển hướng tới trang **4.1.2**, không phải bằng chứng version 4.1.1 |
| [Flyway PostgreSQL](https://documentation.red-gate.com/flyway/reference/database-driver-reference/postgresql-database) và [MariaDB](https://documentation.red-gate.com/flyway/reference/database-driver-reference/mariadb) | Java cần module DB tương ứng: flyway-database-postgresql hoặc flyway-mysql, ngoài phần integration/core và driver phù hợp |
| [Flyway versioned migrations](https://documentation.red-gate.com/fd/versioned-migrations-273973333.html) | Version/checksum/history; migration đã áp dụng không bị sửa để làm mới baseline. Dùng script forward mới |
| [Flyway baselines](https://documentation.red-gate.com/fd/baselines-273973441.html) và [baseline command](https://documentation.red-gate.com/fd/baseline-277578867.html) | DB mới trống khác DB hiện có; baseline đánh dấu mốc, không tạo/migrate lại toàn bộ schema. Không bật baselineOnMigrate tùy tiện |
| [pgvector upstream](https://github.com/pgvector/pgvector) | Vector là extension được bật theo DB, có thể triển khai sau; chưa chọn version/image/dimension trong đợt này |

**Version check chưa hoàn tất:** URL managed dependencies yêu cầu 4.1.1 cũng chuyển tới [trang 4.1.2](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html); lần mở raw source tag v4.1.1 trên GitHub trả 404. Không suy điều này chứng minh release không tồn tại, cũng không lấy version hiện hành làm version source.

Cache Maven cục bộ có BOM mang version 4.1.1, đọc được Flyway 12.4.0, PostgreSQL JDBC 42.7.13, MariaDB JDBC 3.5.10, Hibernate 7.4.5.Final và các artifact Flyway starter/module. Đây chỉ là **OBSERVED từ cache**, chưa xác minh provenance chính thức, effective dependency graph, artifact resolution hoặc test tương thích. Không thêm/pin các số đó vào POM từ báo cáo này. Khi implementation phải đối chiếu artifact/BOM chính thức đúng 4.1.1 và chạy kiểm phù hợp; không tự upgrade/downgrade Boot để né khoảng trống bằng chứng.

## 4. Mapping users cho phương án B — thiết kế để review

| Phần source | Mapping dự kiến | Kiểm cần có |
| --- | --- | --- |
| users.id Long/IDENTITY | BIGINT GENERATED BY DEFAULT AS IDENTITY, PK | Hibernate insert trả ID; FK mới BIGINT. Nếu import explicit IDs thì sequence phải lớn hơn max; không đổi JWT subject |
| full_name | VARCHAR(100), NOT NULL | Giữ trim/validation hiện có và round-trip Unicode |
| email | VARCHAR(255), NOT NULL, unique | Theo entity, không copy độ dài 100 của V1. Giữ trim/lowercase tại entrypoint; test concurrent duplicate và khác hoa/thường. Chưa chọn citext hoặc đổi semantics collation |
| password_hash | VARCHAR(255), nullable | Giữ nguyên hash/prefix encoder, không hash lại; tài khoản thiếu password vẫn không được login theo behavior nguồn |
| role/status | VARCHAR(30), default CUSTOMER/ACTIVE | Giữ enum/tên role/status hiện có; không thay bằng workspace role/UUID |
| created_at/updated_at | TIMESTAMP(6) WITHOUT TIME ZONE cho User hiện dùng LocalDateTime | Không giả timezone lịch sử. Test ORM timestamp và precision; không mang ON UPDATE CURRENT_TIMESTAMP của MariaDB sang |
| Cập nhật updated_at | Ưu tiên cơ chế JPA hiện có cho write qua ứng dụng | Nếu có writer ngoài JPA sau này phải thiết kế riêng; chưa thêm trigger hoặc đổi entity chỉ vì tài liệu đề xuất |
| Event/deadline bảng MKT mới | Dự kiến Instant/OffsetDateTime và TIMESTAMPTZ(6) theo contract | Khác mapping User có chủ đích; test offset/UTC, giữ timezone hiển thị người dùng |

Case folding/collation không thể coi là tương đương chỉ vì các entrypoint đang lowercase. Nếu import dữ liệu sau này: kiểm email trùng sau normalize, accent/case semantics và khả năng login bằng fixture trước quyết định constraint cuối. UUID chỉ cho bảng mới theo ERD khi được chọn, không áp cho users.

## 5. Strategy migration cho DB mới

Đây là kế hoạch, không phải tập lệnh đã tạo hoặc chạy:

1. Tạo datasource/profile và DB/volume **mới, tách biệt** khi được giao implementation môi trường; giữ nguyên MariaDB/volume cũ. Không dùng thao tác down -v hoặc đổi tên DB để giả đã chuyển dữ liệu.
2. Chốt một migration location explicit, ví dụ `classpath:db/migration/postgresql`; không scan mặc định lẫn hai SQL MariaDB hiện có. Tên mới theo `V1__create_users.sql`, tiếp theo tăng version. Location/naming là PROPOSED, chưa có file SQL.
3. Với DB trống: migration mới tạo users theo mapping đã review; không dùng baseline command để bỏ qua script tạo users. Với DB không trống: dừng đường “fresh”, audit schema/history và kế hoạch khác; không auto-baseline.
4. Flyway sở hữu thay đổi schema; Hibernate dự kiến ddl-auto validate, không đồng thời update/create; không trộn schema.sql/import.sql làm đường tạo bảng khác. Không sửa application.yaml hiện tại trong đợt docs.
5. Chạy từ DB dùng một lần: migrate → validate → chạy lại không thay schema/history → JPA/auth regression. Dùng fixture synthetic, không trỏ nhầm DB dùng chung.
6. Thêm workspace/member/project/brand theo IAM-01 sau users; các bảng tiếp theo theo lát cắt ở 16. Vector/knowledge và integration credential khi lát cắt cần, không cần tạo hết 27 bảng ngay.
7. Mỗi script được áp dụng trong môi trường bền vững phải giữ checksum; thay đổi sau đó là migration forward mới. Restore rehearsal trên môi trường dùng một lần; không coi revert Git là rollback DB.

Không chuyển dữ liệu thử mặc định trong phương án B. Nếu người dùng yêu cầu giữ một tập dữ liệu, phải liệt kê rõ và kiểm mapping trước import; quyền tạo môi trường mới không mặc nhiên cho phép xuất dữ liệu/tài khoản thật.

## 6. Quyền cho MVP một người — PROPOSED

Đề xuất chỉ hỗ trợ owner và workspace cá nhân trong UI đầu; backend vẫn truy scope và kiểm membership. Owner tạo cùng membership nguyên tử, có capability duyệt nội dung của chính workspace nếu policy cho phép. Không suy từ users.role/JWT ADMIN.

| Hành động | Owner membership hợp lệ trong scope | User không có membership / scope khác |
| --- | --- | --- |
| Đọc/sửa project, brand, brief | Cho phép theo project và optimistic version | Từ chối, không lộ nội dung |
| Tạo task, pause/cancel/resume | Kiểm input/quota/state/lease hiện tại | Từ chối |
| Đọc nguồn/artifact/file/activity | Kiểm project/file scope | Từ chối kể cả biết UUID hợp lệ |
| Duyệt strategy/article/social | Cần can_approve + policy + đúng version; không bỏ QA bắt buộc | Từ chối; revoke phải có hiệu lực dù JWT còn hạn |
| Publish/send/spend | Ngoài lát cắt; không suy quyền từ duyệt nội dung | Từ chối |

Đây là ma trận để review, chưa có WorkspaceAccessChecker hoặc endpoint mới. Dùng hai workspace/user synthetic để test cách ly dù vận hành ban đầu chỉ một người; không cần làm màn hình invite/phân quyền đầy đủ trong lát cắt đầu.

## 7. Điều kiện nghiệm thu và bàn giao implementation

| Nhóm | Bằng chứng cần chạy khi được giao code |
| --- | --- |
| Dependency | Đúng Boot/Java hiện có; artifact/BOM nguồn chính thức, dependency graph và startup integration Flyway/JPA/JDBC; không chỉ đọc cache |
| Migration | Fresh DB dùng một lần migrate/validate; rerun không trùng bảng/seed; sai location/checksum bị phát hiện; schema không phụ thuộc ddl-auto update |
| Identity/auth | Register/login/me, hash/role/status/JWT subject, inactive/null hash bị chặn; email Unicode/normalize/unique và concurrent duplicate; không mất Long ID |
| User schema | Email 255 vs V1 100 được quyết định có căn cứ; timestamps/create/update/identity đúng mapping; không áp MySQL ON UPDATE trong PostgreSQL |
| IAM | Owner/member transaction; sai project/brand/file scope bị chặn; revoke quyền khi JWT còn hạn; owner không vượt gate QA |
| Recovery | Có cách quay lại config/môi trường cũ; restore rehearsal nếu có dữ liệu cần giữ. Không tác động volume cũ trong thử nghiệm |
| Kiểm backend | Kiểm hẹp theo lát cắt rồi Maven verify khi môi trường hỗ trợ; báo rõ lệnh/kết quả. Đợt docs này chưa chạy các kiểm đó |

**Trạng thái cuối ADR:** **A — MariaDB + Flyway đã ACCEPTED theo DEC-007**; implementation chỉ BASE-01. Mapping PostgreSQL ở mục 4–5 không áp dụng. Quyền workspace/IAM và vector còn là đề xuất; không tự nối model/triển khai runtime/đăng nội dung từ ADR này. Bằng chứng code/test được ghi riêng khi hoàn tất, không dùng các quan sát trước implementation ở mục 1/3 làm trạng thái hiện tại.
