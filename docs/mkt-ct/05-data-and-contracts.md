# 05 — Dữ liệu và hợp đồng

Trạng thái: **PROPOSED**. API/example contract giữ **v0.1**; mô hình quan hệ bổ sung **v0.2 ngày 2026-10-27**. Hai version có phạm vi khác nhau. Không phải DDL, migration, OpenAPI hoặc runtime đã hoạt động.

Đọc [14 — Mô hình 27 bảng](14-relational-data-model.md) và [ERD](mkt_ai_team_erd.mermaid) cho persistence; các tên khái niệm dưới đây không phải bảng bổ sung ngoài ERD.

## Thực thể

| Thực thể | Trách nhiệm / dữ liệu quan trọng |
| --- | --- |
| Workspace/Membership | Biên quyền, principal và capabilities |
| Project/Campaign | Brand, mục tiêu, brief, stakeholder, deadline |
| AgentDefinition/SkillDefinition | ID, version, contract, policy và giới hạn |
| Task | Input/output, assignee, dependencies, state, version và gates |
| Run/ToolCall | Invocation trong task attempt, config/model, usage, tool outcome; lease thuộc task |
| Artifact/ArtifactVersion | ID logic, version bất biến, file_ref, checksum, input versions |
| SourceRecord/ClaimEvidence | Xuất xứ, locator, thời điểm, phạm vi và claim |
| QAReport | Artifact version, rule_set_version, tiêu chí và verdict |
| Approval | Subject version/action digest, actor, scope, thời hạn |
| IntegrationBinding | Provider/account/scope và secret reference |
| Event/AuditEntry | Actor, state/version, timestamp và correlation |

Mọi tài nguyên dự án phải truy được workspace_id/project_id. Quyền áp dụng cho metadata, retrieval, search, cache, tải file và URL xuất.

## Mapping contract với 27 bảng

| Khái niệm / field contract | Persistence dự kiến / quy tắc |
| --- | --- |
| Workspace/Membership, Project/Campaign | workspace/member/project/brand_profile; không thêm bảng campaign riêng |
| `agent_id` trong ví dụ v0.1 | Là **code** catalog như editorial-writer; resolve `agent.code → agent.id` UUID khi ghi FK, không ghi chuỗi code vào UUID |
| `skill_id` trong task/profile | Là code BUSINESS_SKILL; tool_call resolve code TOOL riêng, kiểm allowlist của tool, không tự kế thừa toàn quyền từ skill nghiệp vụ |
| `workflow_id` | ID task gốc của yêu cầu qua parent chain, không phải ID template WF-01 và không có bảng workflow/plan riêng |
| `depends_on`, task state | task_dependency hai FK, state 04; task.workspace_id suy qua project, mọi lookup phải giữ scope |
| Task input / expected_outputs / acceptance / limits / gates | task.input chứa objective, input_artifacts, expected_outputs, acceptance_criteria và metadata; limits/gate_policy/checkpoint có field riêng trong ERD, tránh hai bản ghi lệch nhau |
| `due_at`, `task_version` | task.deadline có timezone; task.task_version optimistic concurrency |
| Run/attempt, `run_id` | attempt là phiên claim task; run_id ở result v0.1 trỏ agent_run invocation sinh output cuối; một attempt có thể nhiều invocation_no, không dùng run_id như lease |
| `artifact_id` + `version` | Resolve artifact_version theo `(artifact_id, version_no)`; FK QA/claim/approval dùng version ID, input_provenance giữ fixed versions |
| SourceRecord, ClaimEvidence | source + claim + claim_source; source_refs chỉ là tóm tắt, không thay claim rows/locator |
| `evidence_status` | claim.verify_status: SUPPORTED/CONTRADICTED/NOT_VERIFIED; quan hệ excerpt/locator ở claim_source |
| QAReport / Approval | qa_report/finding/approval; pin version/rule/action, không dùng artifact.current_version khi quyết định |
| IntegrationBinding | integration_credential theo workspace/provider/account; binding public chỉ metadata, không token |
| Event / AuditEntry | activity_log, event_id = id; workspace/workflow suy qua project/task, redacted payload giữ actor/correlation/version |
| Usage | agent_run ghi measurement; usage_ledger làm accounting/reservation có dedup, không cộng cả hai |

ID `*-synthetic` trong ví dụ là placeholder đọc được, **không phải UUID insert được vào DB**. Fixture tích hợp phải materialize surrogate ID và FK đúng DB; `users` vẫn BIGINT theo source. Quy tắc mapping cần được thể hiện trong DTO/mapper/OpenAPI khi triển khai, chưa có code thực hiện.

## Task contract v0.1

Bắt buộc: contract_version, task_id, workspace_id, project_id, workflow_id, agent_id, skill_id, objective, input_artifacts, depends_on, expected_outputs, acceptance_criteria, gate_policy, limits, status, task_version.

- input_artifacts: artifact_id + version cố định, không dùng latest trong run.
- depends_on: task cùng scope, không tự phụ thuộc/cycle.
- expected_outputs: loại, ngôn ngữ, định dạng, yêu cầu nguồn.
- gate_policy: có QA hay không và approval capability nào cần thiết.
- limits: max_runtime_seconds, max_retries, max_revisions, max_cost và currency. Null là chưa có dữ liệu; không đồng nghĩa vô hạn. Policy production phải giải quyết giới hạn còn thiếu trước chạy.
- due_at có timezone là deadline nghiệp vụ, khác giới hạn runtime.
- task_version dùng optimistic concurrency; từ chối bản cập nhật lỗi thời.

## Result contract v0.1

Bắt buộc: contract_version, task_id, run_id, agent_id, attempt, run_status, output_artifacts, assumptions, unresolved_items, usage, proposed_next_action.

Artifact output gồm artifact_id, version, type, title, language, file_ref, input_artifacts, source_refs. file_ref do kho backend quản lý; không phải URL public mặc định.

Run SUCCEEDED không cho phép agent tự chuyển task COMPLETED. Backend kiểm tra file/reference, cấu trúc và gates.

usage phân biệt actual/estimated/unknown; token/cost chưa biết dùng null, không dùng 0 để giả đo được.

## Model output và adapter dự kiến — bổ sung 2026-10-28

[Common](agents/_common.md) chuẩn hóa JSON do model sinh từ `_common.md` bản gửi. Đây là payload nội bộ trước validate/persist, không thay result v0.1 hoặc API đã có. Đổi field `status` trong bản gửi thành `output_status` để tránh dùng nhầm enum task. `artifacts` của model map sang `output_artifacts` sau backend kiểm nội dung, fixed input versions, source scope và quyền lưu; run/task IDs, attempt, usage thực đo do backend gắn.

| Model output_status | Run và task do backend quyết định |
| --- | --- |
| DONE | JSON/schema hợp lệ mới ghi run SUCCEEDED; artifact còn đi WAITING_QA/WAITING_APPROVAL hoặc COMPLETED theo gate 04. QA trả DONE với verdict FAIL vẫn là review đã chạy, sản phẩm chưa đạt |
| NEEDS_INPUT | Nếu output hợp lệ, run có thể SUCCEEDED vì trả yêu cầu thông tin đúng contract; task WAITING_INPUT, lưu questions. Không gọi LLM lại khi chưa có input mới |
| BLOCKED | Nếu output hợp lệ, run có thể SUCCEEDED với blocker; task BLOCKED và reason. Hết quota/tool thiếu không tự mở quyền hoặc tăng budget |
| FAILED | Run FAILED và error reason; task retry hữu hạn hoặc FAILED theo policy. Output sai schema không dùng model status để cho qua |

`summary` tối đa 3 câu, confidence 0..1/null không là điểm fact-check. `questions`, `next_suggestions`, `warnings` là metadata/redacted content của kết quả/checkpoint, không phát raw prompt ra SSE. Backend chọn proposed_next_action theo output và policy; model không tự đổi state. Contract schema/adapter còn PROPOSED, chưa thêm output.schema.json runtime trong lượt docs.

### Planner: chuẩn hóa bản gửi sang DAG hiện có

Bản gửi dùng `temp_id`, `agent_code: RESEARCH`, `input`, `depends_on`, `deadline`, `requires_qa`, `requires_approval`. Chuẩn target là tasks với local_key và một danh sách dependencies; không duy trì hai schema runtime đồng thời:

| Bản gửi | Target/mapping dự kiến |
| --- | --- |
| temp_id | local_key duy nhất trong plan_revision; backend cấp task ID thật |
| RESEARCH | research-intelligence theo catalog; code khác phải resolve hợp lệ, không tự tạo alias agent |
| input | objective/input_refs/expected_outputs/acceptance_criteria theo task contract; reference pin version và scope |
| depends_on | dependencies gồm task_key và depends_on_key; mọi key tồn tại, không self/cycle/khác scope |
| deadline | due_at ở contract, task.deadline ở persistence; timezone bắt buộc, không vượt deadline project |
| requires_qa / requires_approval | gate_policy; boolean không đủ cấp quyền action hoặc bỏ QA. Backend áp policy tối thiểu và authority theo loại task |

Plan có business_skill_code/limits; thiếu dữ liệu bắt buộc bị từ chối hoặc yêu cầu input. `max_tasks_per_plan` mặc định đề xuất 20, `max_depth` 6, kiểm theo DAG hoàn chỉnh. Hợp nhất chỉ chọn/đối chiếu artifact version đã qua gate, không viết thay chuyên gia hoặc tự chuyển PUBLISHED.

## Source và claim

SourceRecord: source_id, origin_type, title, origin_ref, publisher, published_at nếu có, accessed_at, locator, usage_rights_status, trust_notes.

ClaimEvidence: claim_id, artifact_id/version, claim_text, source_id + locator, evidence_status. Evidence status: SUPPORTED, CONTRADICTED, NOT_VERIFIED.

Editorial/Case Study output bắt buộc có danh sách claim (claim_text, claim_type, content_locator, source_refs/excerpt/location_ref và evidence_status), kể cả khi kết quả là NOT_VERIFIED. Backend resolve source_code theo project, kiểm nguồn/version/scope trước lưu claim_source. Claim giả định/dự báo ghi đúng loại, không biến thành fact đã được kiểm chứng. [Ví dụ claim/source](examples/claim-evidence.json) và [QA thiếu bằng chứng](examples/qa-report.json) là synthetic, không phải fact-check thật.

Phân biệt số liệu gốc, tính toán, giả định và dự báo. Claim quan trọng phải đối chiếu thời kỳ/địa lý/đơn vị. Không bịa URL hoặc KPI.

Mapping nhãn claim của QA trong file gửi, **không thêm enum DB thứ hai**:

| Nhãn bản gửi | evidence_status / verify_status chuẩn | Finding/điều kiện |
| --- | --- | --- |
| VERIFIED | SUPPORTED | Đã mở nguồn, đối chiếu đúng locator/ngữ cảnh/thời kỳ; không map chỉ vì có source_id |
| UNSUPPORTED | NOT_VERIFIED | Nguồn thiếu/không hỗ trợ claim: finding vi phạm FACT, FAIL khi đã xác định thiếu bằng chứng bắt buộc |
| CONTRADICTED | CONTRADICTED | Ghi đoạn bằng chứng trái claim; FAIL cho claim thực tế bắt buộc |
| OUTDATED | NOT_VERIFIED cho claim hiện tại chưa được hỗ trợ | Ghi thời kỳ/độ mới/reason; nguồn lịch sử vẫn có thể SUPPORTED cho claim lịch sử đúng phạm vi, không phủ định máy móc theo tuổi nguồn |

`opinion`/`hypothesis` là nhãn nội dung hoặc loại diễn giải, map ASSUMPTION kèm provenance/lý do khi lưu claim; không phải fact đã SUPPORTED. Field chuyên môn như `usage_record`, `channel_rules`, `channel_specs`, `max_generations`, tracking/A-B plan thuộc content/input/limits dự kiến, không tự thành bảng/cột mới ngoài ERD.

Quyền dùng UNKNOWN giữ nguyên cho tới khi có bằng chứng. Tìm thấy asset trên web không chứng minh quyền tái sử dụng.

## QA và approval

QAReport khóa vào artifact version và rule_set_version. Verdict: PASS, FAIL, NOT_VERIFIED, NOT_APPLICABLE. Mỗi issue có vị trí, bằng chứng và yêu cầu sửa.

Finding dùng category FACT / BRAND / COPYRIGHT / COMPLIANCE và severity BLOCKER / MAJOR / MINOR theo policy/version. QA đánh giá riêng từng tiêu chí và overall; PASS chỉ khi mọi tiêu chí bắt buộc đạt. Vi phạm đã xác định → FAIL; không có khả năng kiểm đủ → NOT_VERIFIED; cả hai chặn gate. NOT_APPLICABLE cần policy cho phép, không dùng làm overall vượt một gate bắt buộc. Score fact/brand/rights optional, chưa đo dùng null, không ghi 0 giả; điểm không bù được BLOCKER.

Mẫu QA trong file gửi dùng `status: DONE`, `result` và `artifact_version_id`: chuẩn hóa thành model output_status và qa_report.verdict; backend resolve ID từ artifact_id/version của ví dụ v0.1. Không cho model tự lấy current_version hoặc tạo ID để né pin version. Báo cáo ghi sources/locators đã mở, checked_at, rule_set_version và giới hạn để tái lập; các metadata này cần schema nội dung khi triển khai.

Approval khóa vào nội dung hoặc hành động cụ thể. Với hành động ngoài, lưu target, provider/account, payload hash, ngân sách và expiry nếu áp dụng. Payload/version đổi làm approval không còn hợp lệ.

`qa_report.qa_run_id` trỏ agent_run của qa-compliance; finding giữ revision_task_id/resolved_in_version_id để QA xác nhận việc sửa. Approval có thể không có artifact_version cho email nhắc việc, nhưng luôn có task/project/action digest; decided_by_user dùng BIGINT. Approval APPROVED chỉ mở gate, không tự chuyển artifact thành PUBLISHED hoặc xác nhận gửi email/chạy ads.

## Event contract v0.1

Bắt buộc: contract_version, event_id, event_type, occurred_at, workspace_id, project_id, workflow_id, task_id, task_version, correlation_id, actor, payload.

Event đề xuất: task.created, task.state_changed, run.started, run.finished, artifact.version_created, qa.report_created, approval.requested, approval.decided, tool.operation_reconciled.

Không phát secret/raw prompt/nội dung nhạy cảm vào stream UI. Consumer xử lý event trùng; đọc snapshot khi thiếu version. Hướng mới ưu tiên SSE + REST command; WebSocket/polling là phương án nếu nhu cầu yêu cầu, chưa có transport MKT được triển khai. Activity phải ghi cùng transaction và phát sau commit, cursor/replay theo thiết kế bền vững ở 14/16.

## Error envelope v0.1

Bắt buộc: contract_version, error.code, error.message, error.retryable, error.details, correlation_id. Details có thể chứa field/expected/current version; không chứa secret hoặc dữ liệu scope khác.

Phân loại cần làm rõ: INVALID_INPUT, ACCESS_DENIED, VERSION_CONFLICT, DEPENDENCY_NOT_READY, QUOTA_EXCEEDED, TOOL_UNAVAILABLE, TOOL_OUTCOME_UNKNOWN. HTTP mapping chốt trong OpenAPI khi triển khai, không phải API đang có.

## API khi bắt đầu implementation

Chưa chốt endpoint/auth scope. Lập OpenAPI cho lát cắt cần làm: request/response, quyền project, pagination, version conflict, idempotency, async operation và lỗi.

Nhóm chức năng dự kiến: project, agent catalog, task/run, artifact/source, QA/approval và event. Không suy ra auth hiện tại đã hỗ trợ.

Nếu tạo Postman collection, chỉ định nguồn contract chuẩn và chiều đồng bộ. Các [ví dụ JSON](examples/content-task.json) là synthetic để review hợp đồng, không phải dữ liệu hoặc API live.
