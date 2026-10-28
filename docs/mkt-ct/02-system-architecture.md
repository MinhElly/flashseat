# 02 — Kiến trúc hệ thống

Cập nhật 2026-10-27 theo ERD, giải thích và sơ đồ người dùng cung cấp. **PROPOSED — hướng thiết kế để duyệt**, không phải sơ đồ runtime hiện có.

[Sơ đồ kiến trúc Mermaid](mkt_ai_team_architecture.mermaid) · [ERD](mkt_ai_team_erd.mermaid) · [Mô hình dữ liệu](14-relational-data-model.md) · [Cấu trúc source](15-source-code-structure.md).

**Cập nhật DEC-007, 2026-10-28:** persistence cho BASE-01 đã chọn MariaDB + Flyway. Bảng công nghệ quan sát 2026-10-27 phía dưới là lịch sử; PostgreSQL/pgvector không còn là lựa chọn đích mặc định. Vector/knowledge cần mapping và quyết định riêng khi làm KB-01; toàn bộ runtime MKT vẫn là thiết kế.

## Modular monolith và ranh giới trách nhiệm

Đề xuất mở rộng một ứng dụng Spring Boot thành **modular monolith**, tổ chức package theo tính năng. Task, artifact, QA, approval và event cần giao dịch nhất quán; giai đoạn đầu chưa có bằng chứng cần tách microservice. 10 chuyên gia + Orchestrator là **11 vai trò cấu hình**, dùng chung runtime/pool worker, không phải 11 ứng dụng thường trực.

| Tầng / module dự kiến | Trách nhiệm | Dữ liệu chính |
| --- | --- | --- |
| Client — Virtual Office | Brief, phòng agent, board, sản phẩm, duyệt, activity | Snapshot và event đã lọc quyền |
| API | REST, JWT identity, workspace access, validation, SSE | Command/query, không chạy LLM trong request |
| `auth`, `user` | Đăng ký/đăng nhập/JWT; giữ nền hiện có | `users` hiện có |
| `workspace`, `project` | Thành viên/quyền, brand, mục tiêu, deadline | Workspace/member/project/brand |
| `orchestration` | Planner LLM đề xuất DAG; Scheduler/deadline/merge xác định bằng code | Kế hoạch lưu trong task/dependency |
| `task` | State machine, dependency, claim lease, retry, checkpoint, cancellation | Task/dependency |
| `runtime` | `AgentRunner`, context, gọi LLM, validate output, ghi lần gọi | Agent run/tool call |
| `agent`, `skill`, `integration` | Registry, prompt version, tool policy/adapter, credential | Agent/prompt/skill/agent_skill/credential |
| `artifact`, `qa`, `approval` | Version, source/claim, report/finding, quyết định của người | Artifact/source/QA/approval |
| `knowledge` | Ingest, retrieval, project memory theo quyền | Document/chunk/memory |
| `audit`, `realtime`, `usage` | Event bền vững/replay, snapshot UI, budget/reservation/settlement | Activity log/usage ledger |

Tên class/module là mục tiêu thiết kế; kiểm tra source trước khi viện dẫn như khả năng đã có. Package hiện có là `com.vts.minh.marketingaiagentteam` trong `marketing-ai-agent-team-backend/`.

## Luồng xử lý một yêu cầu

1. API xác thực identity và membership, kiểm project/brand/giới hạn. Trong transaction tạo **task gốc** cho yêu cầu và activity event; trả receipt sau commit. `POST /api/projects/{id}/requests` và HTTP 202 là **hợp đồng dự kiến**, chưa phải endpoint hiện có.
2. Worker nhận task lập kế hoạch. Planner gọi LLM qua cùng runtime với cấu hình `mkt-orchestrator`; trả cấu trúc task/dependency, không tự ghi tùy ý hoặc cấp quyền.
3. Backend validate schema, agent/skill, cùng scope, không self-edge/cycle, input/gate/budget. Kế hoạch hợp lệ lưu transaction thành task con + dependency. Nếu thiếu dữ liệu → WAITING_INPUT; kế hoạch không hợp lệ không được đưa vào lịch chạy.
4. Scheduler xét input version, dependency COMPLETED, quota và policy rồi đưa task READY vào hàng đợi. Worker claim lease bằng thao tác nguyên tử; queue chỉ báo có việc, DB là nguồn chính thức.
5. `AgentRunner` nạp prompt version/schema bất biến, brand snapshot, RAG/memory trong scope và skill được phép. Mỗi lần gọi LLM ghi một `agent_run`; mỗi tool call kiểm quyền lại tại gateway.
6. Kết quả được validate trước khi ghi `artifact_version`, claim/source và input provenance. Chỉ attempt có lease/fencing token hợp lệ được ghi kết quả chính thức. Ghi state/event/usage settlement nhất quán; event trùng không tạo version/chi phí trùng.
7. Sản phẩm vào WAITING_QA. QA review version cụ thể, ghi report/findings; FAIL sinh revision task/attempt có giới hạn. QA không tự publish và không tự approve thay người.
8. QA đủ điều kiện → chờ approval theo gate policy. Người có quyền duyệt đúng version/action; nội dung hoặc payload đổi làm approval cũ mất hiệu lực. Task sản xuất COMPLETED là sản phẩm được chấp nhận.
9. Nếu yêu cầu có publish/send/ads: tạo task hành động riêng. Gateway recheck scope, QA/approval và budget rồi mới gọi provider; chỉ ghi thành công sau receipt/đối soát. UI nhận event đã lọc qua SSE; reconnect dùng cursor/replay hoặc snapshot.

Task gốc giữ trạng thái tổng hợp, chỉ hoàn thành khi deliverables/gate đủ; không thêm cạnh child → root gây cycle. Chi tiết checkpoint QA và dependency ở [04](04-workflows-and-state.md).

## Planner, Scheduler và AgentRunner

- **Planner:** phần suy luận bằng LLM; chọn chuyên gia và đề xuất DAG. Mỗi kế hoạch phải truy được input và prompt/model đã dùng.
- **Scheduler:** code xác định được; tính readiness, deadline, fairness, concurrency và retry. Không gọi LLM để quyết định quyền, lease hoặc hạn mức.
- **AgentRunner:** chạy một invocation theo contract. Một task attempt có thể gồm nhiều vòng LLM/tool; mọi `agent_run` thuộc attempt đó nhưng không thay thế lease của task.
- **Result merger:** tổng hợp version đã được chọn theo gate; không lấy `latest` trong lúc chạy hoặc sửa artifact đã duyệt.

Prompt/schema trong `resources/agents/<agent-code>/` là bản seed dự kiến. Import thành prompt version trong DB; lúc chạy pin version, không tự ghi đè `is_current` mỗi lần khởi động. Rollback chọn version cũ cho run mới, giữ nguyên lịch sử.

## Công nghệ: source hiện tại và hướng đề xuất

| Hạng mục | OBSERVED ngày 2026-10-27 | Hướng đề xuất / việc phải chốt |
| --- | --- | --- |
| Java / Spring Boot | POM: Java 21, Boot parent 4.1.1 | Kế thừa nền này; không hạ xuống Boot 3.x từ bản mẫu |
| LLM adapter | Chưa thấy dependency Spring AI trong POM | `LlmClient` interface; đánh giá Spring AI/version/provider tương thích khi triển khai |
| Database | MariaDB driver, JDBC MariaDB, Compose MariaDB | PostgreSQL + pgvector cho JSONB/vector; cần migration plan và kiểm auth/dữ liệu cũ |
| Migration | Có V1/V2 SQL; POM chưa khai báo Flyway/Liquibase | Đề xuất Flyway sau chọn DB; phải kiểm location/naming/baseline thực tế |
| Queue / cache / lock | Chưa thấy trong phạm vi kiểm tra | Redis là ứng viên; task/event DB bền vững, lease/fencing không chỉ dựa khóa Redis |
| File | Chưa thấy kho MKT | MinIO local / S3-compatible khi phù hợp; file private, URL ký có hạn |
| Realtime | Chưa thấy API MKT | Ưu tiên SSE + REST command; WebSocket chỉ khi có nhu cầu hai chiều |
| Scheduling | Chưa thấy scheduler MKT | Spring Scheduler ban đầu; Quartz nếu cần persistent scheduling riêng |
| Quan sát | Chưa xác minh stack cho MKT | Correlation/metrics trước; đánh giá Micrometer/Prometheus/Grafana và trace LLM như Langfuse sau |
| Frontend | Chưa thấy Virtual Office | React/Next.js là ứng viên, chưa chọn một trong hai |

Sơ đồ lưu trữ/dịch vụ ngoài là **kiến trúc đích đề xuất**. OpenAI/Claude/Gemini, search provider, Google, Canva, ads và CMS là ứng viên tích hợp, không phải tài khoản hay connector đã được cấp. Không dùng tài liệu này làm bằng chứng tương thích phiên bản; khi chọn dependency phải xác minh tài liệu chính thức hiện hành.

## Giao dịch, phục hồi và realtime

- Transaction tạo task/kết quả phải ghi activity event cùng thay đổi state. Trong baseline 27 bảng, đề xuất dùng `activity_log` với metadata phát/replay như outbox; không coi in-memory `ApplicationEventPublisher` là giao hàng bền vững.
- Dispatcher chỉ phát sau commit; restart đọc bản ghi chưa phát. Worker/SSE consumer chịu at-least-once, xử lý event ID trùng/đến trễ, snapshot khi cursor hết retention. Thiết kế delivery/cursor cuối cùng cần chốt trước code.
- Claim task có task_version, lease owner/expiry và fencing token. Worker mất lease/cancel không được nhận kết quả mới. Queue trùng hoặc khóa cache mất không tạo hai kết quả chính thức.
- Side effect có idempotency key theo action/payload và external operation ID. Timeout có thể là provider đã nhận; chuyển trạng thái chờ đối soát, không retry mù.
- Budget reserve nguyên tử theo project trước dispatch; settlement theo usage thực tế/ước tính có nhãn, release phần dư. `usage_ledger`/`agent_run` không được tính hai lần một khoản.
- Chỉ chạy song song task độc lập trong scope và quota; retention/backup/concurrency/hosting vẫn cần quyết định.

## Quyền và dữ liệu

Quyền MKT nằm ở workspace membership, không thay `users.role` hoặc tự tin vào role JWT. API, scheduler, runtime context, retrieval, file và gateway cùng kiểm scope. `agent_skill` chỉ là allowlist nền, chưa đủ để gọi tool nếu thiếu credential/quyền người dùng/gate.

Credential mã hóa hoặc secret reference; key không ở cùng dữ liệu DB, không đưa token vào model/UI/log. Raw request/response và tool arguments phải redaction/retention; trace không mặc định giữ toàn bộ dữ liệu khách hàng. Nguồn web/PDF/email là input không đáng tin cậy.

Ứng viên tách sau này là ingest tài liệu nặng hoặc runtime cần scale riêng. Đây là khả năng mở rộng, chưa phải công việc được giao.
