# 21 — MariaDB + Flyway: implementation và cách tiếp nhận DB

**2026-10-28 — DEC-007 / BASE-01.** Người dùng chọn giữ MariaDB và giao triển khai Flyway. **IMPLEMENTED; VERIFIED có phạm vi trên DB dùng một lần, dữ liệu synthetic.** Không áp dụng migration/baseline vào DB/volume đang dùng, không triển khai workspace/IAM hoặc runtime MKT.

## 1. Phạm vi source đã thay đổi

| File / phần | Thay đổi |
| --- | --- |
| Backend POM | Thêm spring-boot-starter-flyway, flyway-mysql runtime và testcontainers-mariadb test; giữ Boot parent 4.1.1, Java 21, MariaDB driver và các dependency có sẵn |
| application.yaml | Chuyển cấu hình Flyway bị lồng dưới spring.jpa ra spring.flyway; location riêng MariaDB, kiểm naming/location, baseline-on-migrate false, clean-disabled true; Hibernate ddl-auto validate |
| db/migration/mariadb/V1__create_users.sql | Fresh schema users theo entity: BIGINT/AUTO_INCREMENT, email 255, hash/role/status/timestamps hiện có và uk_users_email |
| Hai SQL V1_create_users.sql/V2_update_users_data.sql cũ | Giữ nguyên nội dung và tên; không nằm trong location được scan, không sửa checksum hoặc giả đã biết lịch sử áp dụng |
| Test support | Tạo server/schema dùng một lần qua Testcontainers hoặc native MariaDB được chỉ định rõ; không nhận URL tới DB có sẵn |
| Context/auth và migration tests | Thay context test phụ thuộc DB mặc định bằng kiểm tích hợp cô lập; thêm migration/recovery-safety và auth regression |

Production Java auth/user/config không đổi. Giữ users/Long/BIGINT, role/status/JWT issuer/subject/hash. Không thêm 26 bảng MKT hoặc đổi quyền JWT thành workspace role. Logging summary của Hibernate connection pool đặt WARN vì trong kiểm thử thấy INFO có thể in URL kèm credential; không tắt các cảnh báo tương thích Flyway.

Migration fresh dùng InnoDB, utf8mb4_unicode_ci, email VARCHAR(255), DATETIME(6), defaults CUSTOMER/ACTIVE và timestamps theo SQL cũ/entity. Collation này có semantics case/accent-insensitive; không dùng migration fresh để âm thầm đổi collation của users hiện có. Cần audit/import strategy riêng nếu dữ liệu đã tồn tại. Hibernate validate không thay kiểm định đầy đủ chiều dài/default/collation/index của schema cũ.

## 2. Dependency và bằng chứng version

Đã mở [artifact starter Flyway 4.1.1 trên Maven Central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-flyway/4.1.1), đối chiếu [Spring database initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html), [Flyway MariaDB module](https://documentation.red-gate.com/flyway/reference/database-driver-reference/mariadb) và [Testcontainers MariaDB](https://java.testcontainers.org/modules/databases/mariadb/). Trang Spring hiện hành hiển thị 4.1.2; không lấy đó làm bằng chứng BOM 4.1.1. Lần thử đọc BOM trực tiếp từ shell bị lỗi DNS; artifact starter có version 4.1.1 xác nhận được qua Maven Central.

Dependency tree thực tế của project đã resolve:

| Thành phần | Version quan sát |
| --- | --- |
| Spring Boot starter/module Flyway | 4.1.1 |
| Flyway core + mysql | 12.4.0 |
| MariaDB JDBC | 3.5.10 |
| Testcontainers MariaDB | 2.0.5 |

Không override các version do parent quản lý để né lỗi. Build và test bên dưới chứng minh tổ hợp đã chạy trong môi trường nêu rõ, không chứng nhận mọi DB version/provider hoặc deployment khác.

## 3. Cách chạy kiểm thử

Từ backend `C:\Code\flashseat\marketing-ai-agent-team-backend`, Java 21 và Maven 3.9.15 đã có trong PATH. Lượt này dùng Maven cài sẵn; wrapper vẫn trỏ Maven 3.10.0, chưa chạy hoặc sửa wrapper.

Khi Docker engine sẵn sàng:

```powershell
mvn -B verify
```

Testcontainers tạo MariaDB image `mariadb:12.3.3` đúng tag Compose hiện có, cổng/schema/dữ liệu riêng, không mount volume của ứng dụng. **Nhánh Docker này chưa chạy trong lượt hiện tại** vì engine không hoạt động; không tự khởi động toàn bộ Docker Desktop và các container khác.

Trên Windows có native MariaDB, chỉ định rõ thư mục bin để chạy cùng bộ test trên server tạm:

```powershell
mvn -B "-Dtest.mariadb.bin=C:\Program Files\MariaDB 13.0\bin" verify
```

Test helper khởi tạo datadir mới dưới backend target/mariadb-it-*, bind 127.0.0.1 trên cổng ngẫu nhiên, dùng credential synthetic và schema ngẫu nhiên. Không đọc .env cho context test, không dùng URL/credential ứng dụng. Không đăng ký Windows service; shutdown hook dừng instance do test tạo. Log/datadir tạm ở target để chẩn đoán, không thuộc source; không dùng các credential đó ngoài test.

Nếu sandbox chặn TCP localhost, cần quyền chạy kiểm thử ngoài sandbox kết nối; không đổi firewall/security/JWT hoặc chuyển test sang DB đang dùng. Lượt này đã chạy với quyền tương ứng sau lỗi getsockopt, không có auto-review rejection.

## 4. Kết quả thực tế

Môi trường: Windows/PowerShell, Java 21.0.11, Maven 3.9.15, **MariaDB native 13.0.2** dùng một lần; Spring Boot 4.1.1. Ngày 2026-10-28, Asia/Bangkok.

| Lệnh / bộ kiểm | Kết quả |
| --- | --- |
| Kiểm hẹp MariaDbMigrationTest và MarketingAiAgentTeamApplicationTests | Sau sửa test harness và cấp kết nối localhost: 8 tests PASS ở vòng đầu |
| Maven verify với native bin flag như trên | **BUILD SUCCESS**, 10 tests, 0 failures, 0 errors, 0 skipped; tạo/repackage JAR; kết thúc 15:33:13 +07:00 |
| Dependency tree với filter Flyway/MariaDB/Testcontainers | BUILD SUCCESS; version theo bảng ở mục 2 |

Mười test gồm 6 kiểm Spring context/auth và 4 kiểm migration:

- Flyway chạy trước Hibernate; JPA validate và rerun đạt, không dùng ddl-auto update để tạo bảng.
- Register/login/me: Long ID, email normalize/Unicode, password hash giữ đúng cơ chế, JWT subject/role; sai password bị chặn.
- Email dài hơn 100 ký tự lưu được theo entity 255, không bị giới hạn V1 cũ.
- Duplicate registration tuần tự và đồng thời: đúng một user, một response thành công và một conflict trong case đồng thời.
- User BLOCKED hoặc thiếu password không login; me không token bị từ chối.
- Fresh schema/default/email length/timestamp/identity, migrate lần hai không lặp dữ liệu/history.
- Schema không trống chưa có history bị từ chối và giữ nguyên user synthetic.
- History checksum sai bị validate/migrate phát hiện, không sửa migration để qua test.
- Baseline **thủ công trong test** sau khi dựng schema đúng baseline: giữ ID/hash/role/status; auto-increment tiếp tục sau ID đã có. Không tương đương audit/adopt DB hiện tại.

Các vòng thử trước có lỗi đã xử lý: test compilation catch thừa; installer từ chối datadir chứa file log; TCP localhost bị sandbox chặn. Không xóa hoặc làm lỏng assertions để báo xanh.

**Giới hạn tương thích quan trọng:** binary Flyway đang dùng cảnh báo MariaDB 13.0 mới hơn mức nó ghi đã verified (11.7), cùng cảnh báo truy vấn WSREP_ON không tồn tại trên native server. Các test vẫn PASS, cảnh báo được giữ; đây không phải chứng nhận hỗ trợ chính thức MariaDB 13.0. Nhánh exact MariaDB 12.3.3/Docker chưa chạy; không đổi image ứng dụng hoặc nâng/hạ dependency từ cảnh báo này.

## 5. DB mới và DB đang có dữ liệu là hai đường khác nhau

### DB mới, trống và được phép dùng

1. Chỉ trỏ datasource tới schema trống được tạo/cấp quyền cho môi trường đó, xác nhận đúng URL/user; không dùng nhầm DB khác từ .env.
2. Startup dùng Flyway location `classpath:db/migration/mariadb`, migration tạo users/history, sau đó Hibernate validate.
3. Kiểm history/version/checksum, schema users và auth ở môi trường đó trước coi là đã triển khai. Đợt hiện tại chưa khởi động ứng dụng trên datasource mặc định port 3307.

### DB không trống

Mặc định **không auto-baseline**. Startup sẽ từ chối schema không trống mà chưa có Flyway history; đây là hành vi có chủ đích, không phải lý do bật baseline-on-migrate hoặc xóa volume.

Trước tiếp nhận DB cũ phải làm trong scope riêng được giao:

1. Audit read-only schema/history và backup/restore plan. Không dựa vào V1/V2 hoặc ddl-auto để đoán schema đã chạy.
2. So users với migration fresh: đủ cột/length/nullability/defaults, BIGINT/identity, timestamps, uk_users_email, charset/collation; kiểm email trùng theo semantics dự định. Không in password_hash/token/customer data vào docs/log.
3. Nếu schema **đúng đầy đủ baseline v1**: có thể đề xuất baseline thủ công version 1, ghi source/environment/evidence rồi validate/auth regression. Chỉ sau review thực tế và scope thao tác DB phù hợp mới thực hiện; code không tự gọi baseline ở production.
4. Nếu schema khác (ví dụ email 100), có dữ liệu khác hoặc có history engine cũ: không baseline 1 để lách kiểm tra. Cần đường migration/adoption riêng và rehearsal synthetic; không sửa SQL/checksum đã áp dụng.
5. Không gọi Flyway clean, không drop/recreate users, không đổi ID/hash/role/status, không xóa Docker volume để xử lý lỗi startup.

Hai SQL cũ vẫn nằm trong repository như bằng chứng source lịch sử; Flyway chỉ scan thư mục con MariaDB mới. Version 1 trong history mới thuộc location/baseline mới, không có nghĩa V1_create_users.sql cũ đã từng được áp dụng đúng.

## 6. Bàn giao và phần tiếp theo

- **Đã làm:** source/config/migration mới và test tích hợp cho BASE-01; giữ Java auth/user, SQL cũ, Compose, IDE và rename trước đó. Giữ working tree, chưa stage/commit/push.
- **Chưa làm:** DB đang dùng/volume 3307, migration live, full backup/restore rehearsal, Docker exact-version test, IAM/project, 26 bảng MKT, Gemini/model/connector/SSE/UI hoặc live workflow marketing.
- **Tiếp theo:** khi giao lát cắt mới, IAM-01 có thể bắt đầu từ MariaDB/Flyway đã chọn, cần chốt action matrix và mapping MariaDB của từng bảng; không dùng JSONB/vector/timestamptz từ ERD logic như DDL MariaDB. Nếu muốn chạy trên DB hiện tại, bắt đầu bằng audit/adoption có phạm vi ở mục 5, không hỏi lại lựa chọn engine.

Đọc [decision](09-decisions-and-open-questions.md), [lát cắt](16-implementation-handoff.md) và [mẫu bàn giao](templates/agent-handoff.md). Nghiệm thu persistence/auth với synthetic data không chứng minh agent nghiên cứu/viết bài hoặc connector đã chạy thật.
