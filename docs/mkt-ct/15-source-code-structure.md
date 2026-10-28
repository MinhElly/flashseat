# 15 — Cấu trúc source và ranh giới module

**PROPOSED — 2026-10-27.** Cây dưới là định hướng triển khai, không phải danh sách module đã tồn tại hoặc lệnh tạo toàn bộ thư mục. [02](02-system-architecture.md) mô tả luồng; [14](14-relational-data-model.md) mô tả bảng; [16](16-implementation-handoff.md) chia lát cắt.

## Source đang có

Cập nhật DEC-007, 2026-10-28: đã bổ sung Flyway và migration fresh tại `resources/db/migration/mariadb/V1__create_users.sql`; config dùng Flyway + Hibernate validate. Hai SQL V1/V2 cũ giữ nguyên, ngoài location được scan. Package auth/user không tái cấu trúc; các module MKT dưới đây vẫn là mục tiêu.

Quan sát trong `marketing-ai-agent-team-backend/`: Java 21, Spring Boot parent 4.1.1; package `com.vts.minh.marketingaiagentteam`, main `MarketingAiAgentTeamApplication`. Các package đã thấy là `auth`, `user`, `config`, `share/exeption`; resources hiện là `application.yaml` và hai file SQL V1/V2. Chưa thấy runtime MKT hoặc frontend.

Không di chuyển `SecurityConfig`/`JwtConfig` hay đổi tên `share/exeption` chỉ để khớp cây đề xuất. Auth có sẵn dùng resource-server JWT và converter claim role; không giả có `JwtFilter` tự viết. Cần giữ hành vi auth/user và bổ sung kiểm quyền workspace riêng khi được giao.

## Cây đích theo tính năng

```text
marketing-ai-agent-team/                         # root đích; kiểm tra cwd thực tế
├── AGENTS.md                                    # hướng dẫn chung coding agent
├── CLAUDE.md                                    # Claude Code đọc AGENTS.md
├── README.md
├── marketing-ai-agent-team-backend/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/vts/minh/marketingaiagentteam/
│       │   │   ├── MarketingAiAgentTeamApplication.java
│       │   │   ├── auth/                        # có source; giữ luồng JWT
│       │   │   ├── user/                        # có User -> users, Long ID
│       │   │   ├── config/                      # config nền hiện có
│       │   │   ├── share/exeption/              # đường dẫn hiện có
│       │   │   ├── workspace/                  # member, access checker
│       │   │   ├── project/                    # project, brand profile
│       │   │   ├── agent/                      # registry, prompt versions
│       │   │   ├── skill/                      # registry, guard, tool adapters
│       │   │   ├── integration/                # credential, provider clients
│       │   │   ├── task/                       # state, dependency, lease, worker
│       │   │   ├── orchestration/              # planner, scheduler, merge
│       │   │   ├── runtime/                    # AgentRunner, context, LLM, output
│       │   │   ├── artifact/                   # artifact/version, source/claim, storage
│       │   │   ├── qa/                         # report, finding, checkers
│       │   │   ├── approval/                   # decision, action binding
│       │   │   ├── knowledge/                  # ingest, chunk, retrieval, memory
│       │   │   ├── audit/                      # activity log, durable events
│       │   │   ├── realtime/                   # SSE projection/replay/snapshot
│       │   │   └── usage/                      # budget guard, reserve, settle
│       │   └── resources/
│       │       ├── application.yaml            # có source; không đổi DB ở đợt docs
│       │       ├── db/migration/               # kiểm baseline/location trước Flyway
│       │       └── agents/<agent-code>/         # seed dự kiến cho 11 code ở catalog
│       │           ├── system.md
│       │           ├── input.schema.json
│       │           └── output.schema.json
│       └── test/
│           ├── java/                           # unit, integration, architecture
│           └── resources/eval/                 # synthetic prompts + acceptance rules
├── frontend/                                   # dự kiến; chưa chọn React/Next.js
│   └── src/
│       ├── features/                           # office, tasks, artifacts, approvals, projects
│       ├── components/
│       └── lib/                                # API/SSE client theo contract
├── infrastructure/                             # tên thư mục thật; không đổi thành infra/
│   └── docker-compose.yml                      # hiện MariaDB; stack đích chưa triển khai
└── docs/mkt-ct/                                 # đặc tả, profile, template, sơ đồ
```

Module mới có thể dùng `api/` (controller, request/response DTO), `domain/` (entity, enum, invariant), `repository/`, `service/` (use case/public module API). Thêm `worker/`, `storage/`, `ingest/` hoặc `retrieval/` khi lát cắt cần. Không tạo lớp/thư mục trống hoặc chuyển toàn bộ auth/user từ cấu trúc hiện tại trong một task MKT.

## Chủ sở hữu module và hợp đồng

| Module | API/lớp dự kiến | Sở hữu / giới hạn |
| --- | --- | --- |
| `workspace` / `project` | `WorkspaceAccessChecker`, project/brand service | Membership/tenant; role hệ thống không thay role workspace |
| `agent` | `AgentRegistry`, `PromptVersionService` | Agent/prompt; pin version, import seed có kiểm soát |
| `skill` | `SkillRegistry`, `SkillPermissionGuard`, tool executor | Skill/agent_skill; mọi adapter đi qua guard |
| `integration` | Credential resolver, provider client | Credential theo workspace/account; không trả secret sang runtime prompt |
| `task` | `TaskService`, `TaskStateMachine`, `TaskWorker` | Task/dependency, claim lease, checkpoint và gate; chỉ module này đổi task state |
| `orchestration` | `PlannerService`, `DagScheduler`, `DeadlineMonitor`, `ResultMerger` | Đề xuất/validate plan, chọn task đủ điều kiện; không ghi trực tiếp bảng module khác |
| `runtime` | `AgentRunner`, `ContextBuilder`, `LlmClient`, `StructuredOutputValidator` | Agent run/tool call; nhận invocation context, không phụ thuộc scheduler |
| `artifact` | Artifact/source/claim service, `FileStorage` | Ghi bất biến/provenance; không tự approve hoặc public file |
| `qa` / `approval` | QA report service, approval decision service | Report/finding và decision riêng; trả gate evidence cho task |
| `knowledge` | Parser, chunker, embedder, retrieval service | Document/chunk/memory; lọc workspace/project trước retrieval |
| `audit` / `realtime` | Activity writer, durable dispatcher, SSE controller | Event sau commit, scope, replay/snapshot; không sở hữu task state |
| `usage` | `BudgetGuard`, reservation/settlement service | Sổ usage duy nhất, chống cộng trùng/vượt đồng thời |

Các tên trên chưa tồn tại trong source đã kiểm tra; chúng là hướng dẫn đặt ranh giới khi triển khai.

## Quy tắc phụ thuộc

1. Controller gọi use case/service của module mình; không ghép transaction bằng cách gọi repository nhiều module ở controller.
2. `orchestration` dùng public API của `task` và `runtime` cho lập kế hoạch; `task` worker gọi runtime bằng invocation contract. `runtime` không gọi ngược orchestration/task scheduler, chỉ trả outcome.
3. `runtime` đọc snapshot/config qua public API của agent, knowledge, project; gọi gateway của skill/integration; gửi output qua artifact service và usage service. Không truyền JPA entity/repository qua các ranh giới này.
4. QA/approval nhận artifact version qua query API, trả report/decision. Task gate dùng dữ liệu này qua public API để quyết định state. Không để `artifact → qa → task → runtime → artifact` trở thành vòng phụ thuộc Java; dùng event cho thông báo tiến độ giữa các module ngang hàng.
5. Nếu cần cùng transaction giữa task/artifact/usage/audit, đặt application use case điều phối rõ ở phía caller với module API, không để các module gọi ngược lẫn nhau. Event UI được ghi bền vững cùng transaction, chỉ phát sau commit.
6. `ApplicationEventPublisher` có thể làm thông báo trong process; delivery qua restart phải có outbox/activity dispatcher. Handler có dedup, không giả một event chỉ đến một lần.
7. Skill adapter chỉ qua guard với scope/action context. `LlmClient` và `FileStorage` là interface để provider/storage adapter thay được mà không đổi nghiệp vụ.
8. Common/shared chỉ chứa primitive/config/error envelope dùng chung; không đẩy task/workspace/approval logic vào đó để né ranh giới. Khi có code, có thể kiểm dependency bằng ArchUnit/Spring Modulith sau khi xác minh tương thích.

## Prompt và cấu hình là dữ liệu có version

Trước viết system.md/schema đọc [common](agents/_common.md) và profile chuẩn catalog, gồm tool allowlist/ràng buộc/checklist bổ sung 2026-10-28. Mapping model JSON → result, plan và QA nằm ở 05/17; không copy nguyên mẫu `status: DONE`, code RESEARCH hoặc output QA PASS/FAIL thành enum task/DB. `LlmClient` adapter chỉ ở runtime/llm; không import provider SDK xuyên module. Common/profile là docs, chưa phải resource đã nạp.

Folder dùng **code chuẩn catalog**, ví dụ `mkt-orchestrator/`, `research-intelligence/`, `editorial-writer/`, `qa-compliance/`; không tạo thêm alias `research`/`qa` thành agent riêng. DB `agent.id` là surrogate ID; code dùng ngoài API là định danh ổn định, xem mapping ở 05.

Import prompt/schema thành version mới, validate và chọn current có kiểm soát. Không sync đè DB tự động mỗi startup. Rollback chọn version cũ cho run mới. Resource nằm trong JAR thường cần đóng gói lại để thay nội dung; nếu muốn sửa không build, cần đường nhập cấu hình bên ngoài/DB và quyền quản trị riêng. “Agent là cấu hình” không có nghĩa mọi loại tool/schema mới đều không cần code.

## Bắt đầu triển khai sau khi được giao

Chọn lát cắt ở 16, chốt phần phụ thuộc ở 09, chỉ tạo module cần dùng. WF-01 tối thiểu vẫn cần quyền workspace/project, registry/prompt, task, artifact/source/claim, QA/approval, usage/audit; không bỏ gate vì mới tạo runtime. Frontend và connector thật có thể triển khai sau với contract/fixture ghi nhãn rõ.
