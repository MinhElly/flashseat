# 14 — Mô hình dữ liệu quan hệ

**PROPOSED v0.2 — 2026-10-27.** Biên soạn từ ERD và hai bản giải thích người dùng gửi, đối chiếu source. Đây là mô hình logic phục vụ review, **chưa phải DDL/migration đã chạy**. [ERD Mermaid đầy đủ](mkt_ai_team_erd.mermaid), [contract](05-data-and-contracts.md), [workflow/state](04-workflows-and-state.md), [package](15-source-code-structure.md).

**DEC-007, 2026-10-28:** giữ MariaDB và bổ sung Flyway cho BASE-01. Chỉ users có migration fresh trong lát cắt này; 26 bảng MKT mới vẫn chưa triển khai. Các kiểu JSONB/vector/timestamptz và hướng PostgreSQL trong bản logic dưới đây là đề xuất cũ, không phải SQL có thể áp dụng lên MariaDB. Khi giao từng bảng phải chốt mapping MariaDB (JSON/UUID/time/index) trước DDL, không tự chuyển engine hoặc tạo đủ 27 bảng từ ERD.

## Cách đếm và các điều chỉnh có chủ đích

**27 bảng, chia 7 nhóm = 1 bảng `users` hiện có + 26 bảng mới dự kiến.** Con số “26 bảng” chỉ đúng nếu đếm phần thêm mới. Nhóm 1 có 5 bảng khi tính `users`. Catalog có **10 chuyên gia + 1 Orchestrator = 11 vai trò**, không seed 10 dòng và bỏ một vai trò.

| Điểm trong bản gửi | Chuẩn hóa trong docs | Căn cứ / hệ quả |
| --- | --- | --- |
| `USER`, UUID id | `USERS`, `bigint id`; giữ mọi cột source | Entity ánh xạ `users`, `Long` + IDENTITY; chỉ thêm FK từ bảng mới |
| 10 dòng agent gồm Orchestrator | 11 code theo catalog 03 | Không tự giảm danh sách nghiệp vụ đã yêu cầu |
| Task PENDING/DONE | Enum ở tài liệu 04, dùng BLOCKED/READY/COMPLETED | Không để DB/API/UI có hai bộ state cạnh tranh |
| Agent status toàn cục | Trạng thái UI suy ra theo project từ task/run | Một agent phục vụ nhiều project; không thể chỉ có một WORKING/IDLE toàn hệ thống |
| `agent_skill` mô tả tool grant | Allowlist capability có kiểm tra gateway | Skill nghiệp vụ trong profile có thể gồm nhiều tool, không đồng nhất 1 skill = 1 HTTP call |
| QA chỉ PASS/FAIL | PASS/FAIL/NOT_VERIFIED/NOT_APPLICABLE | Giữ kết quả thiếu bằng chứng; policy quyết định tiêu chí bắt buộc |
| Source code UNIQUE toàn cục | UNIQUE `(project_id, source_code)` | Nhiều project có thể đều dùng SRC-001 |
| Approval APPROVED → PUBLISHED | Approval mở gate; task hành động riêng xác nhận provider | Không ghi đã phát hành chỉ vì người dùng duyệt |
| Log, ledger đơn giản | Bổ sung delivery/dedup/reservation metadata trong cùng bảng | Không tuyên bố event bền vững/budget an toàn chỉ vì có bảng |

Tên lowercase trong tài liệu là tên logic dự kiến; tên vật lý/số nhiều phải chốt khi viết DDL. Riêng `users` đã tồn tại, không rename thành `user`.

## 1. Identity & Workspace — 5 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `users` | Tài khoản hiện có, được workspace/member/task/approval/activity tham chiếu | Giữ `id`, full_name, email, password_hash, role, status, created_at, updated_at; không đổi JWT/auth trong task schema |
| `workspace` | Không gian công ty/đội; `owner_id → users.id` | Slug duy nhất; tạo owner membership cùng transaction. Ownership không tự bỏ qua kiểm quyền |
| `workspace_member` | Nối workspace/user; role ADMIN/MANAGER/MEMBER/VIEWER và `can_approve` | UNIQUE `(workspace_id, user_id)`; role hệ thống ở users giữ nguyên. Ma trận từng hành động còn cần chốt |
| `project` | Chiến dịch, mục tiêu, status, deadline, token/cost budget; thuộc workspace và brand | Brand cùng workspace; budget null là chưa xác định, không tự coi vô hạn; currency phải rõ |
| `brand_profile` | Brand dùng lại giữa các project cùng workspace: tone, guideline, từ cấm, languages | Context run pin snapshot/version vào input/provenance; sửa brand không làm run cũ đổi nghĩa |

User FK mới dùng BIGINT; ID bảng mới có thể là UUID như ERD mà không ép user đổi kiểu. Không sửa entity `User` hoặc collection hai chiều nếu FK một chiều đã đủ.

## 2. Agent & Skill Registry — 5 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `agent` | Registry cấu hình 11 vai trò; code, name, role_group, model mặc định, temperature, active | Code duy nhất, theo catalog; model thật ghi tại run. Không dùng status global để biểu diễn phiên làm việc |
| `agent_prompt_version` | System prompt và input/output schema theo agent/version | UNIQUE `(agent_id, version)`; tối đa một current mỗi agent, activation nguyên tử; version đã dùng không sửa |
| `skill` | Capability nghiệp vụ/tool có code/schema/type, active, requires_approval, policy | `kind` phân biệt BUSINESS_SKILL/TOOL; side-effect policy áp dụng ở tool/action, không chỉ dựa cờ boolean |
| `agent_skill` | Allowlist agent/capability, enabled và scope/policy hạn chế | PK `(agent_id, skill_id)`; enabled không tự cấp provider scope hay quyền người ủy quyền |
| `integration_credential` | Credential theo workspace/provider/account, scope/expiry | Mã hóa token/refresh hoặc secret reference, metadata key version; không trả token ra API/LLM/log. Một provider có thể nhiều account |

Tách prompt version để debug/rollback. Seed từ resource chỉ là đầu vào import; không tự ghi đè cấu hình hiện hành lúc startup. Quyền hiệu lực là giao của allowlist, membership/ủy quyền, tool policy, integration scope và gate hiện tại.

## 3. Task Engine — 4 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `task` | Một việc, thuộc project; parent tự tham chiếu, agent được giao, người tạo; input/state/priority/deadline/limits/gates | Input/artifact version cố định; optimistic `task_version`, attempt, lease/fencing, retry/revision counters và checkpoint. Parent nullable cho task gốc |
| `task_dependency` | Hai FK cùng trỏ task: task đang chờ và task tiên quyết | PK `(task_id, depends_on_task_id)`; không self/cycle, cùng project/workflow; kiểm DAG trong transaction để tránh hai cạnh đồng thời tạo cycle |
| `agent_run` | **Một lần gọi LLM**, thuộc task/attempt và agent, pin prompt version; model, token/cost/latency/status | Một task attempt nhiều run; run sequence duy nhất theo task/attempt. Run SUCCEEDED không có nghĩa task COMPLETED |
| `tool_call` | Một lần gọi tool trong agent_run; skill, args/result đã lọc, status/latency | Skill kind TOOL, agent có grant; side effect ghi action digest, credential binding, idempotency và external operation ID để đối soát |

`task.idempotency_key` unique theo `(project_id, idempotency_key)` kèm request hash: cùng key/cùng payload trả receipt cũ; khác payload trả conflict. Đây là chống tạo việc trùng, **không thay thế** idempotency của tool ở provider. Task nội bộ cũng cần key ổn định để event lặp không tạo revision/task con trùng.

Task gốc lưu brief, plan metadata/revision trong input/checkpoint; task con + dependency chính là DAG bền vững. Không thêm bảng plan/workflow trong baseline. Contract `workflow_id` định danh task gốc qua parent chain; tách rõ với template WF-01/WF-02. Root không là dependency chờ hoàn thành của child; nó tổng hợp kết quả child theo gate riêng.

Root dùng checkpoint phase (PLANNING/WAITING_DELIVERABLES/MERGING). Sau persist plan, root chờ ở BLOCKED với reason WAITING_DELIVERABLES và nhả lease; Scheduler không đưa root chạy Planner lại chỉ vì root không có depends_on. Chỉ khi child deliverables/gate đủ mới mở phase MERGING qua READY. Đây là readiness riêng của task điều khiển, cần kiểm restart/dedup cùng task_version; phase không thêm vào enum task.status.

`agent_run.prompt_version_id` phải thuộc đúng `agent_id`, agent phù hợp assignee/task invocation. LLM call lỗi/retry là run mới; retry task tăng attempt, không sửa lịch sử run. Lease/fencing bảo vệ attempt ở task; mọi output commit đối chiếu token hiện tại.

## 4. Artifact & Source — 5 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `artifact` | Sản phẩm logic thuộc project/task/agent; type/title/status/current_version_id | Current pointer nullable lúc tạo; pointer chỉ trỏ version của chính artifact. DRAFT/IN_QA/APPROVED/PUBLISHED mô tả vòng đời, không thay task state |
| `artifact_version` | Nội dung JSONB hoặc file_ref, checksum, version_no, producing agent_run và input provenance | UNIQUE `(artifact_id, version_no)`; bất biến sau commit. Storage private; không lưu signed URL sắp hết hạn làm địa chỉ chuẩn |
| `source` | Sổ nguồn theo project: SRC-001, URL/title/publisher/date/access time/license/reliability | UNIQUE `(project_id, source_code)`; origin có thể URL hoặc tài liệu nội bộ; điểm tin cậy không thay bằng chứng |
| `claim` | Nhận định cụ thể thuộc artifact version; text/type/verification status và vị trí trong bài | SUPPORTED/CONTRADICTED/NOT_VERIFIED theo contract; loại có thể FACT/STAT/QUOTE/CALCULATION/ASSUMPTION/FORECAST |
| `claim_source` | Nối claim với nguồn, excerpt và page/paragraph/cell locator | PK `(claim_id, source_id, location_ref)` để một nguồn có nhiều đoạn chứng minh; cùng project, locator chuẩn hóa, lưu quan hệ supports/contradicts |

Editorial và Case Study phải xuất danh sách claim + `source_id/source_code` + locator, không chỉ bài văn có link chung. Research đăng ký source; backend resolve code trong project. Claim thiếu nguồn giữ NOT_VERIFIED, không sinh URL/ID giả để qua gate.

`source.raw_file_ref` thay `raw_file_id` chưa có bảng đích: trỏ object private do storage quản lý, có kiểm quyền, không vẽ FK tới một bảng file không tồn tại. Source evidence cần snapshot/checksum nếu nội dung ngoài thay đổi. Claim status summary có thể cập nhật từ QA, nhưng report/evidence lịch sử gắn version phải giữ để truy vết.

Vòng `artifact → current_version → artifact_version → artifact` xử lý bằng tạo artifact với pointer null, thêm version, cập nhật pointer cùng transaction. Ưu tiên FK/composite constraint kiểm pointer cùng artifact nếu DB cho phép; service vẫn kiểm. Không bỏ FK chỉ vì quan hệ vòng rồi bỏ luôn kiểm tra toàn vẹn.

`artifact.task_id` giữ task sản xuất ban đầu. Nếu sửa bằng revision task riêng, `artifact_version.agent_run_id` có thể thuộc revision task cùng project đã liên kết finding/original task; không ép mọi version.run.task_id bằng artifact.task_id. Backend kiểm scope và đường revision hợp lệ, provenance giữ input/feedback để không lẫn tác giả/lần chạy.

## 5. QA & Approval — 3 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `qa_report` | Review một version, `qa_run_id → agent_run`, rule_set_version, verdict và scores nếu đo được | Run phải là QA trong scope; report mới khi review lại. Không có FK chỉ ghi nhãn QA là đủ |
| `qa_finding` | Lỗi/thiếu bằng chứng: category, severity, description, revision_request, locator, resolution | Có `revision_task_id` và `resolved_in_version_id` khi sửa; chỉ QA/reviewer có quyền xác nhận, agent viết không tự đóng lỗi |
| `approval` | Quyết định người duyệt theo task/action; artifact version optional cho việc không có artifact | Ghi người yêu cầu/quyết định, subject/action digest, target/account/budget, expiry, policy version và decision time |

`approval` cần project_id trực tiếp; task phải cùng project, artifact version nếu có cũng cùng project. Task hành động không có artifact (ví dụ email nhắc lịch) vẫn phải khóa payload digest; không tạo artifact rỗng để lách FK. CONTENT_ACCEPT/PUBLISH/SEND_EMAIL/RUN_ADS là loại khác nhau; duyệt nội dung không cấp quyền mọi đích/phạm vi phát hành.

Decision lifecycle dự kiến: PENDING/APPROVED/REJECTED/EXPIRED/REVOKED; recheck membership + can_approve + action capability tại lúc quyết định và thực thi. Thay version, target, account, payload, budget hoặc policy liên quan làm approval cũ không áp dụng. QA thiếu bằng chứng giữ gate đóng dù điểm trung bình cao.

Một action approval phải gắn với operation key ổn định của task/payload, gateway claim quyền thực thi nguyên tử để hai worker không dùng cùng approval tạo hai hành động độc lập. Tool idempotency key/operation scope cần unique constraint thích hợp (kể cả tool nội bộ không có credential); provider hỗ trợ idempotency thì chuyển key tương ứng, còn không phải đối soát receipt trước retry. Cách lưu claim/consume và kết quả chưa rõ phải chốt trong QA-01/EXT-01, không giả cờ APPROVED tự bảo đảm execute-once.

Giữ report/findings cũ; revision task tạo version mới, report/approval mới. Approval không phải bằng chứng pháp lý bảo đảm tuyệt đối hoặc receipt provider đã thực hiện.

## 6. Knowledge & Memory — 3 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `knowledge_document` | Tài liệu do người đưa vào; workspace bắt buộc, project optional cho thư viện dùng chung trong workspace | Khi có project phải cùng workspace; file private, status ingest và quyền dùng |
| `knowledge_chunk` | Đoạn content, embedding, metadata/source locator và embedding model/version | UNIQUE `(document_id, chunk_index)`; embedding dimension/index phải chốt theo model trước migration |
| `agent_memory` | Điều rút ra theo project + agent, type/content/embedding/provenance | Không lẫn project/customer; lưu căn cứ, thời điểm/expiry nếu cần, có quyền sửa/xóa rõ |

pgvector là hướng lưu vector đề xuất, không dependency đã có. Lọc tenant/project trước retrieval, cache và rerank; không tìm toàn cục rồi mới lọc ở UI. Knowledge dùng chung workspace và memory riêng project có phạm vi khác nhau; dữ liệu retrieval không cấp thêm quyền tool.

## 7. Realtime, Audit, Cost — 2 bảng

| Bảng | Mục đích / quan hệ | Ràng buộc và lý do |
| --- | --- | --- |
| `activity_log` | Event/audit theo project, task, actor user/agent/service, event_type, payload, correlation/task version | ID bền vững, metadata dispatch/replay. Payload lọc secret; ghi cùng transaction với state, phát sau commit |
| `usage_ledger` | Sổ phát sinh usage/reservation theo workspace/project/task/run, tokens/cost/currency/date | Entry key unique, kind và reservation key; phân biệt actual/estimated/unknown; không cộng lại chi phí đã ghi ở run |

Actor user/agent có thể null cho service event; phải có actor_type/actor_ref hợp lệ. Không bắt event backend giả làm một agent. SSE derive workspace qua project, kiểm scope mỗi kết nối và khi quyền thay đổi; `event_id` = activity_log.id. Cursor cần delivery order an toàn với commit đồng thời; chỉ dùng timestamp hoặc auto-increment không tự bảo đảm không bỏ sót event. Chốt dispatcher/checkpoint/replay ở lát cắt RT-01.

`usage_ledger` là sổ chi tiết, không mặc định là bảng tổng hợp nhanh theo ngày. Muốn báo cáo nhanh cần index/aggregation/projection phù hợp. Reserve budget nguyên tử, append settlement/release có dedup; khoản unknown không ghi 0 giả actual. Không suy ra budget được chặn từ phép SUM cuối ngày. Contract reservation/currency/conversion phải chốt trước chạy song song có chi phí thật.

## Ràng buộc chung cần chuyển thành DDL và service

| Invariant | Thực thi dự kiến |
| --- | --- |
| Không truy cập chéo tenant | Query/service scope bắt buộc; FK/composite key khi phù hợp; test hai workspace với ID hợp lệ |
| Quan hệ cùng project/workspace | Project-brand, task-parent/dependency, artifact-task/run, claim-source, QA/approval, ledger-run đều kiểm đường truy scope |
| Một writer cho task | Task version + atomic claim + lease/fencing; reject kết quả worker cũ/cancelled |
| Không phát hành bản stale | Pin input versions, provenance ở version; upstream đổi đánh dấu stale/checkpoint và review lại |
| Không xóa lịch sử review/cost | Restrict/soft archive theo retention đã chốt; không cascade xóa audit/ledger khi xóa task |
| Không dùng reference tùy ý trong JSON | Resolve ID/version/checksum/scope trước commit; JSONB không thay FK/validation cho identity cốt lõi |
| Không giữ secret trong trace | Redaction trước lưu; request/response payload optional, có retention; credential encryption key quản lý ngoài bảng |

ERD ghi FK logic và trường quan trọng; không biểu diễn hết composite/check constraint, index, nullability hoặc mọi state enum. Cardinality theo số bản ghi của mỗi quan hệ, không tuyên bố mọi vòng đời đều tạo đủ child ngay. UUID/JSONB/vector/timestamptz ở bảng mới là hướng PostgreSQL; `users` phản ánh source hiện tại.

Index ứng viên khi viết migration: task `(project_id,status,priority,deadline)`, lease expiry; dependency reverse lookup; artifact `(project_id,task_id)`; source code; report version; approval pending/expiry; activity `(project_id,created_at,id)` cùng cursor đã chốt; ledger `(project_id,usage_date,entry_kind)`; vector index sau chọn model/query. Chọn theo query đo được, không tự tạo hết từ docs.

## Chuẩn bị migration sau khi được giao

1. Chốt DB đích và kiểu vật lý; ghi decision chuyển MariaDB → PostgreSQL nếu được chọn. Không đổi user ID/role/hash; cần mapping timestamp/collation/identity/email unique và kiểm JWT/login sau chuyển.
2. Kiểm baseline thực tế trên DB được phép đọc; không lấy entity/DDL file làm bằng chứng schema đang chạy. Source V1 hiện có `CREATE TABLE users { ... }`, cú pháp cần xử lý trước khi coi là migration hợp lệ; V1/V2 dùng MariaDB và chưa có Flyway dependency trong POM.
3. Chốt Flyway location/naming/version và baseline cho DB cũ. Không sửa checksum migration đã áp dụng; DB mới và DB có dữ liệu cần đường nâng cấp riêng, backup/restore/rehearsal.
4. Migration từng lát cắt: identity/workspace/project → registry/task → artifact/source/claim → QA/approval → audit/usage; knowledge/vector và connector chỉ khi được giao. FK nullable/deferrable cho current_version xử lý trong cùng kế hoạch.
5. Kiểm trên DB dùng một lần: constraints, hai tenant, cycle/concurrency, migration từ baseline và auth regression. Nếu chưa chạy DB chỉ báo docs/schema review; không báo migration PASS.

Chưa thêm bảng notification, plan hoặc một bảng cho mỗi agent. Reminder có thể bắt đầu bằng event + scheduler, nhưng read/unread, preference và delivery bền vững cần thiết kế riêng nếu được yêu cầu; activity log không tự đáp ứng toàn bộ chức năng notification.
