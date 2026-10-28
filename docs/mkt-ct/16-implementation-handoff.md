# 16 — Lát cắt triển khai cho coding agent tiếp nhận

**PROPOSED — lập 2026-10-27; cập nhật DEC-007 ngày 2026-10-28.** Người dùng đã giao BASE-01 MariaDB + Flyway; các lát cắt khác còn là hướng dẫn, không phải lệnh chạy backlog. Giữ working tree, chưa stage/commit/push; không tác động DB/volume đang dùng.

Đọc [10 — Quy trình tiếp nhận](10-agent-handoff.md) trước. Invariant chuẩn ở [04](04-workflows-and-state.md), [05](05-data-and-contracts.md), [06](06-tools-permissions-and-qa.md), [14](14-relational-data-model.md); package dự kiến ở [15](15-source-code-structure.md). Mọi tên class/endpoint trong bảng dưới là **dự kiến**, cần kiểm source trước dùng.

## Thứ tự phụ thuộc, không phải lịch cam kết

1. BASE-01 chốt DB/migration và auth baseline; IAM-01 chốt tenant/project access.
2. REG-01 + TASK-01 + ART-01 tạo nền WF-01; AUD-01/USG-01 đi cùng ngay từ lần ghi task/run đầu, không để audit/usage thành việc trang trí cuối.
3. QA-01 hoàn thiện gate; RUN-01 chạy fixture qua cùng contract. Thứ tự code nhỏ có thể xen kẽ nhưng không nghiệm thu E2E khi gate còn thiếu.
4. PLAN-01 lập/thi hành DAG, RT-01/UI-01 hiển thị và phục hồi. MVP có thể dùng DAG xác định sẵn trước khi nối Planner LLM, phải ghi nhãn fixture.
5. KB-01, connector EXT-01 và live model/tool chỉ khi được giao cấu hình/quyền/môi trường cụ thể. Không gọi đủ 11 agent cho một brief đơn giản.

## BASE-01 — Baseline source, DB và migration

Bản quyết định: [ADR-001 tại 20](20-database-baseline-decision.md), DEC-007 chọn A MariaDB + Flyway, thay khuyến nghị PostgreSQL trước đó. Mapping PostgreSQL trong ADR là lịch sử thiết kế, không áp dụng. Bằng chứng test phải ghi môi trường cụ thể và tách kiểm fixture DB với DB đang dùng.

Đã triển khai lát cắt fresh schema và auth regression: [bàn giao 21](21-mariadb-flyway-baseline.md). DB đang dùng chưa audit/adopt; không coi 10 test fixture là migration live PASS. IAM-01 và các lát cắt runtime phía dưới chưa triển khai.

- **Căn cứ:** DEC-003, PROP-006/007/010; OPEN-006/013/014.
- **Đọc/kiểm:** POM, `User.java`, `SecurityConfig`, auth controller/service/JWT code, `application.yaml` (không in secret), SQL V1/V2 và Compose. Source hiện MariaDB/Boot 4.1.1, không lấy mẫu Boot 3.x/UUID user để thay nền.
- **Đầu ra:** decision DB đích; bảng mapping schema hiện tại → schema mới, Flyway baseline/naming/location, kế hoạch giữ ID/hash/role/status/time/email uniqueness; dependency/version được xác minh chính thức. Khi được giao code mới viết migration trong module backend.
- **Nghiệm thu cần chạy:** migration sạch và từ baseline có dữ liệu synthetic trên DB dùng một lần; auth/register/login/me regression; rollback/restore rehearsal nếu thay DB. Kiểm tra `users` không mất/đổi identity.
- **Điểm dừng:** chưa chốt DB hoặc không có môi trường phù hợp thì hoàn tất kế hoạch/DDL review độc lập, báo NOT VERIFIED cho migration/runtime. Không áp dụng lên DB dùng chung hoặc sửa migration checksum đã chạy.

## IAM-01 — Workspace, project, brand và quyền

- **Phụ thuộc:** baseline persistence được chọn; ma trận workspace/action được chốt trước endpoint ghi.
- **Module/bảng:** workspace, project; workspace/member/project/brand_profile, chỉ tham chiếu users.
- **Đầu ra:** create/read/update workspace/project/brand theo quyền; tạo owner membership nguyên tử; access checker tái dùng ở API/worker/retrieval/storage. Role hiện có không tự là role workspace.
- **Nghiệm thu:** hai workspace với user/brand/project khác nhau; thử ID hợp lệ của tenant khác vào mọi reference; chặn đọc/ghi/approve; member bị thu hồi quyền bị chặn dù JWT chưa hết hạn. `can_approve` cùng action policy, không chỉ cờ frontend.
- **Không gộp:** rewrite auth/user hoặc chuyển users sang UUID. Nếu cần thay đổi auth nghiệp vụ, nêu lý do và phạm vi riêng.

## REG-01 — Agent, prompt và capability registry

- **Phụ thuộc:** code chuẩn ở 03, input/output schema của lát cắt đầu, model adapter còn có thể mock.
- **Module/bảng:** agent, skill; agent/prompt_version/skill/agent_skill. Integration credential thêm khi có connector được giao.
- **Đầu ra:** 11 định nghĩa agent, seed/import prompt/schema có kiểm soát, activation/rollback nguyên tử, allowlist; tách business skill và callable tool.
- **Nghiệm thu:** không seed trùng code; hai activation đồng thời không có hai current; run đang chạy giữ prompt version cũ; invalid schema không active; tool ngoài allowlist bị gateway từ chối.
- **Giới hạn:** registry row không chứng minh connector/model hoạt động; cập nhật resource trong JAR không mặc định là hot reload.
- **Bổ sung 2026-10-28:** đọc common + profile v0.2, tách BUSINESS_SKILL và TOOL allowlist; không seed alias RESEARCH thay code catalog. Model tier là tham khảo; cấu hình default/override và schema metadata cần version hóa.

## TASK-01 — Task state, DAG, retry và lease

- **Phụ thuộc:** IAM-01, registry/query contract; state chuẩn 04 và error/version contract 05.
- **Module/bảng:** task, orchestration scheduler tối thiểu; task/dependency.
- **Đầu ra:** tạo task gốc/child; idempotency theo project + request hash; DAG validation cùng transaction; task_version/CAS; atomic claim/lease/fencing; checkpoints cho chờ QA/duyệt/input; retry/revision hữu hạn, pause/cancel/resume.
- **Nghiệm thu:** duplicate create trả cùng receipt; đổi payload cùng key trả conflict; self-edge/cycle/khác project bị từ chối; hai cạnh đồng thời không tạo cycle; hai worker không cùng commit; restart nhận lại việc không mất output; kết quả sau cancel/hết lease không ghi đè; unknown side effect không bị retry mù.
- **Điểm cần tránh:** parent hierarchy không thay dependency; root đang chờ deliverables không thành prerequisite của mọi child. Root BLOCKED/WAITING_DELIVERABLES phải có readiness theo checkpoint để không chạy Planner lại mỗi vòng. Queue Redis mất/trùng không được làm mất/lặp task.

## ART-01 — Artifact, version, source và claim

- **Phụ thuộc:** IAM-01, task/run identity và storage interface. Storage fixture phải có nhãn.
- **Module/bảng:** artifact; artifact/version/source/claim/claim_source.
- **Đầu ra:** commit version bất biến, private file_ref/checksum, input provenance, current pointer cùng artifact; source registry theo project, claim/locator bắt buộc cho claim quan trọng.
- **Nghiệm thu:** revision tạo version_no mới; không cập nhật nội dung/checksum bản cũ; tranh chấp version không mất bản; current pointer không trỏ artifact khác; SRC-001 tái dùng được giữa hai project nhưng không trùng trong một project; claim-source chéo project bị chặn; file download ngoài scope bị chặn.
- **Giới hạn:** claim có source ID chưa chứng minh source hỗ trợ nội dung; QA cần đối chiếu thật. Không public storage object cho tiện preview.

## QA-01 — Report/finding, revision và approval

Với MVP Novaon, dùng thêm [brief/claim–nguồn/rubric ở 19](19-bank-finance-editorial-acceptance.md). Rule set và acceptance cases là đề xuất triển khai, không phải output schema/eval đã có hoặc nguồn được phép đưa thẳng vào fixture.

- **Phụ thuộc:** ART-01, state/checkpoint, rule set và authority theo action được chốt.
- **Module/bảng:** qa, approval; report/finding/approval; revision đi qua task API.
- **Đầu ra:** report pin version + QA run + rule set; lỗi có locator/revision request; liên kết revision task/version; approval pin action digest/target/account/budget/expiry và decision actor.
- **Nghiệm thu:** FAIL → revision, NOT_VERIFIED → chờ bằng chứng, PASS mở gate theo policy; v2 không kế thừa report/approval v1; người không đúng quyền/bị revoke không approve; target/payload/budget đổi hoặc expiry chặn execute; hai worker không dùng cùng approval để tạo hai action độc lập; approve nội dung không tự PUBLISHED.
- **Tránh deadlock:** QA task mở khi artifact version sẵn sàng, không đợi task sản xuất COMPLETED nếu chính task đó đang WAITING_QA. Checkpoint được lưu bền vững, duplicate event không sinh nhiều QA/revision task.

## AUD-01 / USG-01 — Event bền vững và budget

- **Phụ thuộc:** transaction boundary của task/run/output và usage contract/provider semantics.
- **Module/bảng:** audit, usage; activity_log/usage_ledger.
- **Đầu ra:** ghi activity cùng state transaction, dispatcher sau commit/dedup/replay; payload redacted; reserve trước dispatch, settle/release/adjust có unique entry key, currency và measurement.
- **Nghiệm thu:** crash sau commit trước publish vẫn phát lại; event trùng không lặp output/action; transaction rollback không phát event ảo; hai task cùng reserve không vượt budget; settlement/retry không cộng tiền hai lần; unknown không hiện 0 actual; lỗi trước/sau provider nhận xử lý reservation đúng policy.
- **Điểm phải chốt:** cursor không bỏ sót event do commit đồng thời; ledger reservation/settlement model, retention và reconciliation. In-memory event bus/SUM theo ngày không tự đạt tiêu chí này.

## RUN-01 — Một AgentRunner, nhiều vai trò

- **Phụ thuộc:** REG/TASK/ART/QA/AUD/USG; giới hạn invocation/tool/revision.
- **Module/bảng:** runtime, skill gateway; agent_run/tool_call, output qua artifact API.
- **Đầu ra:** invocation context pin agent/prompt/model/input/brand; LlmClient interface; structured output validator; tool guard trước mỗi call; redaction; run sequence/attempt và usage phân biệt.
- **Nghiệm thu synthetic:** Research → Editorial → QA FAIL → version mới → QA PASS → approval nội bộ; mọi step truy ra prompt/run/claim/source/report; input schema lỗi, output sai, prompt injection từ PDF, tool sai scope, timeout, cancel/lease loss đều cho outcome đúng.
- **Nghiệm thu live riêng:** chỉ khi có provider/config/quyền được giao; chạy bộ brief giới hạn trên model/tool thật, báo provider/model/môi trường/thời điểm/token/cost có căn cứ và phần chưa chạy. Không báo fixture pipeline là live E2E.
- **Không gộp:** 11 AgentRunner class, framework worker khác ngôn ngữ, đăng bài/email/ads thật chỉ để chứng minh runtime.
- **Adapter cần kiểm:** model output_status khác run_status/task.status; DONE không bỏ gate, NEEDS_INPUT không tự retry khi chưa thêm input, BLOCKED không tự tăng quota, invalid JSON không ghi run SUCCEEDED. Test thiếu nguồn/nguồn cũ, copy/translation thêm fact, tool không có grant và PII vào memory theo common/profile; model confidence không biến thành QA PASS.

## PLAN-01 — Planner LLM và Scheduler xác định bằng code

- **Phụ thuộc:** TASK-01 + RUN-01, plan schema, policy chọn agent/gate/limits.
- **Đầu ra:** API persist yêu cầu trả receipt, worker gọi Planner, backend validate/lưu DAG, Scheduler readiness/deadline/retry, merge theo version và gate. Plan revision có lịch sử/checkpoint, không thêm bảng plan trong baseline.
- **Nghiệm thu:** request không giữ HTTP chờ LLM; planner trả agent/tool không có/cycle/khác scope bị chặn; chỉ task độc lập chạy đồng thời; plan trùng/retry không nhân task con; upstream đổi đánh dấu downstream stale; root chỉ xong khi deliverables đủ.
- **Giới hạn:** Planner không trực tiếp cập nhật DB state hoặc tăng budget; deadline/retry không giao cho model tự quyết.

## RT-01 / UI-01 — SSE và Virtual Office

- **Phụ thuộc:** event/snapshot contract, quyền, task/artifact/QA query API; frontend stack được chọn trước implementation.
- **Đầu ra:** SSE ưu tiên, snapshot + replay cursor; React/Next.js được chọn qua decision; phòng 11 vai trò lấy trạng thái theo project, board, version/claim/source preview, review queue và usage.
- **Nghiệm thu:** reconnect/expired cursor/duplicate/out-of-order event hội tụ về snapshot; không lộ event/data tenant khác; quyền bị thu hồi đóng hoặc ngừng trả dữ liệu; animation không tự complete; approval stale báo conflict; empty/loading/blocked/error/accessibility theo 07.
- **Giới hạn:** activity feed chưa phải notification read/unread/nhắc deadline được giao thành công; mock UI phải ghi rõ API fixture.

## KB-01 — Knowledge và project memory

- **Phụ thuộc:** DB/vector/model embedding, retention và quyền dùng tài liệu.
- **Module/bảng:** knowledge; document/chunk/agent_memory.
- **Đầu ra:** ingest/chunk/embed có model/dimension/provenance; retrieval filter tenant/project; memory tách theo agent/project với nguồn/expiry phù hợp.
- **Nghiệm thu:** doc dùng chung workspace không lộ sang workspace khác; project-private không hiện trong project khác; cache/index/rerank giữ scope; re-ingest không trùng chunk; đổi embedding model cần chiến lược re-index. Fixture vector không chứng minh RAG chất lượng thật.

## EXT-01 — Một connector/hành động ngoài mỗi lần

- **Phụ thuộc:** authority, tài khoản/scope, provider API/version được xác minh, QA/approval/budget/idempotency.
- **Đầu ra:** credential mã hóa/secret ref, adapter gateway, tool schema và policy, external operation ID/reconciliation, trace redacted. Bắt đầu connector ưu tiên được người dùng giao.
- **Nghiệm thu:** đọc/ghi đúng account/target, scope thiếu bị chặn; expiry/revoke/429/timeout có xử lý; provider đã nhận nhưng response mất phải đối soát; duplicate request không gửi email/đăng/chạy ads hai lần; approval đổi payload/target không thực hiện.
- **Giới hạn:** tool có sẵn trong phiên Codex/Claude không tự là connector backend. Live side effect phải nằm trong yêu cầu/ủy quyền thực tế, không phát sinh từ fixture hoặc roadmap.

## Khung API cần thiết kế khi bắt đầu

Danh sách sau là hướng nhóm resource, **chưa có OpenAPI hoặc controller MKT**:

| Resource dự kiến | Hành vi phải làm rõ |
| --- | --- |
| workspace/member/project/brand | Tenant access, membership capability, concurrency và archive |
| project request / task / run | Async receipt, idempotency hash, root workflow, state command, lease nội bộ |
| agent / prompt / skill | Code vs DB ID, version/activation, management permissions |
| artifact / version / source / claim | Private file, fixed versions, citation locator, pagination |
| QA / approval | Version gate, decision actor, action digest, expiry/revocation, conflict |
| snapshot / activity stream / usage | Scope, retention/cursor/replay, measurement/currency |

Chốt method/path/status, DTO, quyền, optimistic version, idempotency, error envelope, pagination và transaction trước nối frontend. Không copy endpoint dự kiến vào README như API đang chạy.

## Definition of done cho mỗi lát cắt

- Chỉ có thay đổi thuộc yêu cầu; source, migration, contract/example và decision liên quan đồng bộ. Các OPEN ảnh hưởng trực tiếp được giải quyết hoặc giới hạn rõ.
- Có kiểm tra hành vi quan trọng/negative case theo lát cắt, ghi lệnh, môi trường và kết quả thực; không chỉ build hoặc nhìn dependency.
- Không lộ secret/customer data. Không mất working-tree change ngoài phạm vi; không stage/commit/push nếu chưa được giao bước Git đó.
- Bàn giao bằng [template](templates/agent-handoff.md): implemented/configured/tested/runtime rõ ràng, file/entrypoint thực tế, việc còn lại và bước tiếp theo. Khi chưa chạy live phải ghi NOT VERIFIED.
