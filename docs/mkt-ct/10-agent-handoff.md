# 10 — AI Agent tiếp nhận công việc

Hướng dẫn cho **Codex, Claude Code và coding agent** tiếp nhận repository, cập nhật 2026-10-27. Các agent marketing là cấu hình sản phẩm dự kiến; không phải coding subagent đang chạy. Tài liệu này không phải lệnh tự triển khai roadmap.

## Bắt đầu

1. Đọc root AGENTS.md, index, tài liệu 00 và 09.
2. Kiểm tra git status và hướng dẫn áp dụng trong thư mục sửa.
3. Xác định yêu cầu hiện tại, scope được giao và tiêu chí nghiệm thu.
4. Đọc [luật runtime chung](agents/_common.md), profile ở catalog 03, workflow 04, contract 05; xem [17](17-agent-spec-integration.md) cho mapping bản đặc tả gửi 2026-10-28.
5. Đọc 06 cho tool/quyền/QA; 07 cho UI; 08 cho roadmap.
6. Kiểm tra source thực tế, không giả có API/connector từ tên trong docs.
7. Đọc 14 cho bảng/constraint/migration, 15 cho ownership/package, 16 cho lát cắt và kiểm tra; chỉ chọn phần liên quan yêu cầu hiện tại.

Repository là marketing-ai-agent-team; workspace đích C:\Code\marketing-ai-agent-team chưa được đổi vì Codex/runtime đang giữ handle. Kiểm tra đường dẫn thực tế trước khi chạy lệnh. Backend là marketing-ai-agent-team-backend, package com.vts.minh.marketingaiagentteam. Định danh trong repo đã đổi theo yêu cầu; giữ nguyên logic auth và công việc ngoài phạm vi.

Claude Code bắt đầu từ [CLAUDE.md](../../CLAUDE.md), dẫn về [AGENTS.md](../../AGENTS.md). Không nhân bản một bộ quy tắc độc lập có thể lệch với hướng dẫn chung.

Lượt cập nhật ngày 2026-10-28 chỉ tích hợp docs từ file đính kèm. Quy tắc coding hữu ích được biên tập về root AGENTS; lệnh mẫu backend/frontend/eval, câu “ask first mọi spec” và gợi ý tạo subagent trong file gửi không trở thành lệnh thực thi. Với implementation sau này, theo scope người dùng đã giao, dùng kiểm tra phù hợp và ghi trạng thái tooling thật.

## Kiểm tra đầu phiên và xác định scope

Các lệnh PowerShell đọc trạng thái từ **root thực tế**, không tự đổi cwd sang root đích chưa tồn tại:

```powershell
Get-Location
git status --short
git diff --stat
git diff --cached --name-only
rg --files -g AGENTS.md -g CLAUDE.md
rg --files marketing-ai-agent-team-backend/src
```

Đọc diff/file sẽ sửa, kể cả file untracked không xuất hiện trong `git diff`. Đợt rename làm Git hiển thị xóa backend cũ + backend mới untracked trước stage; không reset hoặc stage chỉ để làm status đẹp. Không tự tạo branch/worktree, chép source hoặc đổi root khi yêu cầu chỉ chỉnh docs.

Trước hành động, lập bản tiếp nhận ngắn: mục tiêu người dùng, in/out of scope, tài liệu/decision áp dụng, source đã quan sát, file định sửa và bằng chứng cần có để hoàn tất. Nếu người dùng chỉ yêu cầu giải thích/audit/docs thì không tự viết source/runtime từ backlog.

## Bản đồ đọc theo công việc

| Công việc | Source cần đối chiếu | Docs chuẩn / đầu ra kỳ vọng |
| --- | --- | --- |
| Auth / workspace | User/entity/enums, SecurityConfig, auth/JWT service/controller | 00/06/09/14; giữ users/BIGINT, bổ sung authority theo scope nếu được giao |
| DB / migration | POM, application.yaml, V1/V2 SQL, Compose, schema live nếu được phép | 14, BASE-01; chọn DB/baseline, không tự chạy migration |
| Registry / prompt / tools | Kiểm có module thực tế chưa, không coi profile là code | 03, profile, 05/06/15; version, grant, credential binding |
| Task / runtime / planner | Source mới nếu có; kiểm lease/state/transaction thực sự | 02/04/05/15/16; async receipt, DAG, invocation và gate |
| Artifact / QA / approval | Endpoint/entity/service thực tế khi đã có | 05/06/14; fixed version, source/claim, report, action digest |
| UI / event / usage | Frontend/API/dispatcher/budget guard nếu đã có | 07/14/16; snapshot/replay theo scope, reservation/dedup |

Nếu source được agent khác/người dùng bổ sung sau mốc 00, cập nhật quan sát thay vì khẳng định “chưa có” từ docs cũ. Tên dự kiến `AgentRunner`, `DagScheduler`, `WorkspaceAccessChecker` không được viện dẫn như class hiện hữu khi chưa tìm thấy.

## Các bất nhất đã được xử lý trong bản thiết kế

- Tổng **27 bảng = users + 26 mới**, không 26 bảng tính cả user. **11 vai trò = 10 chuyên gia + Orchestrator**, không 10 gồm Orchestrator.
- User hiện là `users`, `Long/BIGINT`; ERD UUID chỉ áp dụng ID mới dự kiến, không đổi identity/auth/role hiện có.
- Source MariaDB/Boot 4.1.1 khác bản mẫu PostgreSQL/Boot 3.x. PostgreSQL/pgvector/Flyway là hướng mới PROPOSED; không tự đổi source hoặc downgrade Boot.
- Agent status UI tính theo task/run trong project, không dùng một cột global. Task dùng enum 04 (COMPLETED, không DONE); QA giữ NOT_VERIFIED/NOT_APPLICABLE.
- Attempt/lease ở task khác từng agent_run gọi LLM. DB agent/skill ID khác code đọc được trong JSON/profile; xem mapping 05.
- Artifact approval khác publish/send/ads receipt; notification/read-unread và plan table không nằm trong baseline.

## Quy trình thực hiện một lát cắt khi được giao code

1. Chọn acceptance cases và negative cases từ mục tương ứng ở [16](16-implementation-handoff.md). Liên kết REQ/PROP/DEC, không tự coi mọi PROP là ACCEPTED.
2. Chốt contract/state/authority/storage assumptions ảnh hưởng trực tiếp. Viết decision nếu có lựa chọn mới; tiếp tục phần độc lập trong lúc cần câu trả lời cho phần còn lại.
3. Xác minh phiên bản/dependency bằng tài liệu chính thức khi lựa chọn. Kế thừa nền hiện tại; tránh thêm framework/queue/vector chỉ vì xuất hiện trên sơ đồ.
4. Viết persistence/service/gateway theo ownership ở 15. Validation quyền/version/budget diễn ra ở backend; prompt chỉ hỗ trợ tác vụ chuyên môn.
5. Dùng synthetic fixture rõ nguồn, không token/customer data. Kiểm ID/FK/scope và tình huống timeout/cancel/retry có ý nghĩa; chỉ chạy live trong phạm vi được giao.
6. Kiểm diff và tài liệu liên quan, ghi bằng chứng lệnh/môi trường/kết quả. Không tiếp tục rollout/deploy/side effect/Git từ một task code nếu chưa nằm trong yêu cầu.

Nếu migration chưa được phép/chưa có DB dùng một lần, vẫn hoàn tất thiết kế/DDL review được giao và báo giới hạn. Không khẳng định schema đúng chỉ vì Hibernate ddl-auto hoặc file SQL tồn tại.

## Khi triển khai sau này

Chọn lát cắt nhỏ có thể review. Liên kết task với REQ/PROP/decision, nêu giả định và inputs. Chốt contract/state trước khi nối tool.

Dùng fixture synthetic có nhãn. Chạy tool/model thật khi có cấu hình và quyền phù hợp. Xác minh tài liệu chính thức của provider/SDK/API tại thời điểm chọn.

Nếu thiếu quyết định, tiếp tục phần độc lập và hỏi phần thực sự phụ thuộc. Roadmap không cấp quyền gửi email, publish, chạy ads hoặc đổi hạ tầng.

## Khi chỉnh docs

Agent ID chuẩn ở 03; state chuẩn ở 04; data contract chuẩn ở 05. Không tạo enum/ID mâu thuẫn trong profile hoặc ví dụ.

ERD/model chuẩn ở 14 và `mkt_ai_team_erd.mermaid`; topology ở 02 và `mkt_ai_team_architecture.mermaid`; tree/ownership ở 15. Sửa quan hệ/field phải cập nhật model, contract mapping, ví dụ và decision liên quan; không thêm bảng mà vẫn giữ nhãn “27 bảng”. Nếu chọn đổi baseline, ghi rõ lý do và số bảng mới.

Giữ các nhãn yêu cầu/đề xuất/triển khai/kiểm chứng. Cập nhật liên kết khi đổi file. Không ghi secret hoặc dữ liệu thật vào ví dụ.

## Bàn giao

Dùng [template](templates/agent-handoff.md): mục tiêu, phạm vi, file sửa, quyết định/giả định, kiểm tra/kết quả, phần chưa xác minh và bước tiếp theo.

Tách docs check, unit/integration, mock và live E2E. Hoàn tất docs không chứng minh runtime chạy được.

Bàn giao cần đủ để một AI khác tiếp tục mà không đọc lịch sử chat: working directory/branch/status, file đã sửa và file ngoài phạm vi, quyết định còn OPEN, command + exit/result, fixture/live boundary, blocker thực tế và bước tiếp theo có đầu vào/đầu ra. Không đưa credentials vào báo cáo hoặc gợi ý agent sau chạy side effect chưa được giao.

## Bước tiếp theo gợi ý

Sau khi được giao implementation: bắt đầu BASE-01/IAM-01 theo 16, chốt DB/migration và quyền MKT; thiết kế API/task/artifact WF-01 rồi triển khai lát cắt có fixture synthetic. OPEN-002 đã được giải quyết bằng yêu cầu đổi tên; DEC-005 giữ users/auth, không hỏi lại việc đổi user ID.

Đợt docs/rename vẫn chưa stage/commit/push. Sau đó người dùng đã giao BASE-01 MariaDB + Flyway theo DEC-007; [21](21-mariadb-flyway-baseline.md) ghi source/test và giới hạn DB tạm. Không hỏi lại lựa chọn engine hoặc biến scope docs cũ thành lý do từ chối implementation đã được giao. Khi người dùng cho phép bước Git tiếp theo, xác định scope file và phân biệt logic auth có sẵn với thay đổi định danh.
