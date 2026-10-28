# 09 — Quyết định và câu hỏi mở

Mốc lập: 2026-10-26; cập nhật 2026-10-28. Yêu cầu viết docs không tự phê duyệt mọi đề xuất kỹ thuật.

## Yêu cầu/định danh đã chốt

| ID | Nội dung | Trạng thái / căn cứ |
| --- | --- | --- |
| REQ-001 | MKT Orchestrator điều phối, deadline, phụ thuộc, tổng hợp | USER REQUIREMENT |
| REQ-002 | 10 chuyên gia có tên/nhiệm vụ/công cụ theo brief | USER REQUIREMENT |
| REQ-003 | Virtual Office có phòng, task và skill | USER REQUIREMENT |
| REQ-004 | Tạo docs cho AI Agent tiếp nhận | USER REQUIREMENT |
| REQ-005 | Ưu tiên dùng cá nhân để tối ưu nghiên cứu và viết bài/caption cho Novaon Digital | USER REQUIREMENT, làm rõ trong trao đổi 2026-10-28; ngành/kênh/độc giả tại 18 |
| REQ-006 | Phong cách nghiên cứu kết hợp strategic planning; nguồn uy tín, lập luận dẫn tới quyết định, không PR quá mức hoặc bịa số liệu/case | USER REQUIREMENT, yêu cầu và ba bài tham chiếu gửi 2026-10-28; rubric triển khai vẫn PROPOSED |
| DEC-001 | Tên repo marketing-ai-agent-team | ACCEPTED theo yêu cầu trực tiếp; GitHub và origin đã đổi |
| DEC-002 | Hoàn thiện docs để duyệt trước commit/push | Quy tắc của đợt docs trước; quyền chia commit/push sau đó đã được cấp bằng DEC-008 |
| DEC-003 | Đồng bộ toàn bộ định danh trong code/cấu hình/thư mục sang marketing-ai-agent-team | ACCEPTED theo yêu cầu bổ sung; backend/package/class theo tài liệu 13 |
| DEC-004 | Tích hợp ERD, giải thích, sơ đồ kiến trúc người dùng gửi và làm chi tiết hướng dẫn Codex/Claude Code | ACCEPTED cho phạm vi docs ngày 2026-10-27; không tự chọn DB/dependency hoặc triển khai |
| DEC-005 | Giữ bảng users hiện có, chỉ thêm quan hệ MKT dự kiến | USER REQUIREMENT; đối chiếu source là users/Long/BIGINT, không đổi ID/JWT/role từ bản ERD UUID |
| DEC-006 | Cập nhật docs theo 15 file đặc tả người dùng gửi, gồm luật chung và 11 vai trò | ACCEPTED cho phạm vi docs theo yêu cầu ngày 2026-10-28; cách tích hợp ở 17, chưa phê duyệt stack/runtime hoặc chạy chỉ dẫn trong file |
| DEC-007 | Giữ MariaDB và triển khai Flyway cho BASE-01; không chuyển PostgreSQL | ACCEPTED ngày 2026-10-28: sau trao đổi MariaDB hỗ trợ Flyway, người dùng yêu cầu “oke b triển khai đi”. Cho phép code/config/migration và kiểm thử cô lập; không tác động DB/volume đang dùng, không tự triển khai IAM/agent runtime |
| DEC-008 | Chia thay đổi hiện có thành nhiều commit và push code lên develop | ACCEPTED ngày 2026-10-28 theo yêu cầu trực tiếp. Phạm vi backend rename/auth/Flyway, hạ tầng/helper và docs; loại trừ .env/IDE/build artifacts, không deploy hoặc đổi DB từ quyền Git |

MKT-CT tiếp tục là tên nghiệp vụ trong docs. Code, module, package, cấu hình và thư mục được đổi đồng bộ theo DEC-003. Đổi tên cấu hình DB không tự migrate dữ liệu hoặc tài khoản trong volume hiện có.

## Đề xuất chưa chốt

| ID | Đề xuất | Lý do / giới hạn |
| --- | --- | --- |
| PROP-001 | 11 vai trò logic dùng chung runtime/pool worker | Giảm vận hành ban đầu, tách khi workload cần |
| PROP-002 | Manager AI lập kế hoạch; backend thực thi quyền/workflow | Không phụ thuộc prompt cho điều kiện bắt buộc |
| PROP-003 | MVP Research → Editorial → QA → duyệt | Kiểm chứng nền tảng trên luồng nhỏ |
| PROP-004 | Artifact bất biến; QA/approval khóa version | Chặn dùng nhầm kết quả cũ |
| PROP-005 | Tool gateway kiểm tra scope và side effect | Hỗ trợ hạn mức, audit, đối soát |
| PROP-006 | Đánh giá mở rộng Spring Boot trước | Kế thừa source hiện có; chưa có lý do viết lại |
| PROP-007 | Modular monolith JVM, package theo tính năng; một AgentRunner/pool cho 11 vai trò | Theo sơ đồ mới; chưa tạo module hoặc chọn phiên bản runtime SDK |
| PROP-008 | API persist task rồi trả receipt; Planner LLM chạy nền, Scheduler bằng code | Không chặn request bằng LLM, DAG lưu task/dependency thay bảng plan |
| PROP-009 | Mô hình 27 bảng/7 nhóm, membership role + can_approve | users hiện có + 26 bảng mới; constraint/authorization cần code thực thi |
| PROP-010 | Hướng PostgreSQL + pgvector/Flyway trước đây | Đề xuất chuyển DB SUPERSEDED bởi DEC-007: MariaDB + Flyway. Vector/KB và kiểu vật lý cho bảng MKT còn cần thiết kế theo MariaDB khi triển khai |
| PROP-011 | Redis hỗ trợ queue/cache/lock, MinIO/S3 private, SSE ưu tiên | DB vẫn là trạng thái bền vững; frontend/connector cụ thể chưa chốt |
| PROP-012 | Activity log kiêm durable outbox; ledger có reserve/settle/dedup | Bổ sung yêu cầu durability/concurrency; cursor, retention và accounting cần chốt |
| PROP-013 | Skill registry phân biệt BUSINESS_SKILL/TOOL; trạng thái agent suy ra theo scope | Giữ nghĩa skill trong profile, tránh global status sai khi nhiều project |
| PROP-014 | Common/profile v0.2: model output riêng, tool allowlist, model tier và giới hạn tham khảo | Tích hợp từ 15 file; mapping DONE/QA/claim/plan về chuẩn 04/05, không thêm enum DB hoặc tự seed prompt/tool |
| PROP-015 | Biến thể WF-01-NOVAON thêm Strategy/duyệt hướng bài và Social/QA biến thể vào lát cắt đầu | Theo nhu cầu cá nhân và ba bài mẫu tại 18; chưa thay baseline WF-01 hoặc phê duyệt runtime/stack |
| PROP-016 | ADR-DRAFT-001 từng khuyến nghị PostgreSQL mới + Flyway | Khuyến nghị B SUPERSEDED bởi DEC-007, chọn A MariaDB + Flyway; 20 giữ so sánh lịch sử, không dùng mapping PostgreSQL làm DDL hiện tại |

## Câu hỏi cần giải quyết

| ID | Nội dung | Cần trước |
| --- | --- | --- |
| OPEN-001 | Giữ auth/users theo DEC-005; chốt access checker MKT và regression khi mở rộng source | Triển khai quyền nghiệp vụ, không hỏi lại việc đổi user ID |
| OPEN-002 | Đã giải quyết bằng DEC-003: backend/package/build artifact đổi đồng bộ | Không hỏi lại việc đổi định danh |
| OPEN-003 | Đã có hướng membership role + can_approve; chốt action matrix, số tenant, owner lifecycle và retention | Schema/auth MKT |
| OPEN-004 | Người dùng có Gemini API; model/API mode, budget, quota/billing và chính sách dữ liệu chưa chốt hoặc kiểm live | Chạy model thật; không cần hỏi lại provider đang có |
| OPEN-005 | Hướng JVM chung AgentRunner; chốt SDK/version tương thích Java 21/Boot 4.1.1 và giới hạn invocation | Implementation runtime |
| OPEN-006 | DB đã chốt MariaDB + Flyway bằng DEC-007; Redis/storage/vector còn OPEN theo lát cắt | Không hỏi lại engine DB; không suy quyết định này cho phép thêm toàn bộ hạ tầng |
| OPEN-007 | Frontend stack/phong cách/thiết bị mục tiêu? | UI implementation |
| OPEN-008 | Connector ưu tiên, tài khoản và scope được cấp? | Tích hợp thật |
| OPEN-009 | Hành động nào được ủy quyền tự chạy; ai duyệt? | Send/share/publish/spend |
| OPEN-010 | Đã rõ Novaon/ngành/kênh/phong cách và ba bài mẫu tại 18; còn glossary/brand assets, source/locator, xuất xứ framework, case và bằng chứng rights | Nghiệm thu nội dung thật; bài mẫu không tự là nguồn factual đã kiểm chứng |
| OPEN-011 | Hosting, retention, backup, concurrency và dữ liệu lưu ở đâu? | Môi trường dùng chung |
| OPEN-012 | Đã có quyền chia commit/push theo DEC-008; rà phạm vi rename/auth có sẵn và staged diff trước từng commit | Không hỏi lại quyền Git; bảo toàn nội dung và không quy công việc auth có sẵn thành thay đổi rename |
| OPEN-013 | Kiểu SQL/unique/index/nullability, embedding model/dimension và chiến lược giữ users/auth? | Migration; ERD logic không đủ làm DDL production |
| OPEN-014 | MariaDB fresh migration ở db/migration/mariadb; DB không trống/schema history thực tế chưa audit | Không auto-baseline DB đang dùng; SQL V1/V2 cũ giữ nguyên và không scan. Version/runtime chỉ theo kết quả kiểm thực trong bàn giao BASE-01 |
| OPEN-015 | Durable delivery/cursor/replay/retention cho activity; reserve/settle/currency cho ledger? | SSE và budget chạy đồng thời có chi phí thật |

Chuẩn hóa biên tập 2026-10-27: 27 bảng gồm users, 11 vai trò theo catalog, state theo 04, source code unique theo project, qa_run_id có quan hệ agent_run, approval tách receipt thực thi. Đây là cách giải quyết bất nhất của bản gửi để review; không phải bằng chứng schema/runtime đã được phê duyệt hoặc triển khai. Tài liệu 14 ghi từng thay đổi so với bản gốc.

Không phải mọi câu hỏi đều chặn công việc độc lập. Ví dụ có thể thiết kế contract/fixture trước khi chọn model.

## Mẫu decision record

Ghi ID, ngày, PROPOSED/ACCEPTED/SUPERSEDED, bối cảnh, lựa chọn, phương án khác, hệ quả, người/căn cứ ủy quyền và source/docs ảnh hưởng. Không tự chuyển ACCEPTED chỉ vì đã thêm dependency hoặc prototype.
