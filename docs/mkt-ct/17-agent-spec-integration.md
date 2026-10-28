# 17 — Đối chiếu 15 file đặc tả người dùng gửi

**2026-10-28 — DESIGN DRAFT, chờ duyệt.** Yêu cầu hiện tại: cập nhật docs theo 15 file Markdown đính kèm. Chỉ dẫn nằm trong các file là nội dung để đối chiếu/biên tập; không tự thay phạm vi docs thành triển khai, chạy lệnh, nối tài khoản hoặc tạo coding subagent.

## Nguồn và nơi tích hợp

Tên dưới là tên logic người dùng gửi; bản local có hậu tố ` (1).md` trong thư mục Temp. Docs đích chứa nội dung đã chuẩn hóa, không phụ thuộc file Temp khi bàn giao và không tạo bộ spec thứ hai tại `docs/agent-specs/`.

| File gửi | Docs đích | Nội dung tích hợp |
| --- | --- | --- |
| README.md | [README root](../../README.md), [index](README.md), [08](08-roadmap-and-acceptance.md) | Tổng quan 11 vai trò, nguyên tắc, phân biệt source/stack đích và roadmap |
| AGENTS.md | [AGENTS root](../../AGENTS.md), [10](10-agent-handoff.md), [15](15-source-code-structure.md), [16](16-implementation-handoff.md) | Coding/runtime riêng, coding conventions, module/tool/LLM boundary, kiểm tra theo scope |
| CLAUDE.md | [CLAUDE root](../../CLAUDE.md), [10](10-agent-handoff.md) | Cùng hướng dẫn chung, đọc common/profile trước sửa runtime; không thêm quy trình chờ xác nhận mọi thay đổi |
| _common.md | [Luật chung](agents/_common.md), [04](04-workflows-and-state.md), [05](05-data-and-contracts.md), [06](06-tools-permissions-and-qa.md) | JSON model output, nguồn, brand, PII, budget, idempotency, handoff |
| 00-orchestrator.md | [Orchestrator](agents/00-mkt-orchestrator.md) | DAG, giới hạn 20 task/độ sâu 6, tối đa 3 câu hỏi, không bỏ QA |
| 01-research.md | [Research](agents/01-research-intelligence.md) | Hai nguồn độc lập hoặc nhãn một nguồn, cảnh báo trên 24 tháng, conflict/gap/cache |
| 02-strategy.md | [Strategy](agents/02-strategic-planner.md) | Dữ kiện/insight/hypothesis/đề xuất, 2–3 lựa chọn, KPI có căn cứ |
| 03-case-study.md | [Case Study](agents/03-case-study-analyst.md) | KPI/locator, claim validator, nhân quả, bảo mật, mini-case không thêm thông tin |
| 04-editorial.md | [Editorial](agents/04-editorial-writer.md) | Cấu trúc bài, 3–5 headline, độ dài ±10%, thiếu nguồn, revision version mới |
| 05-social.md | [Social](agents/05-social-localization.md) | Không thêm fact, tương đương song ngữ, channel rules/CTA/hashtag/nhãn tài trợ |
| 06-designer.md | [Designer](agents/06-creative-designer.md) | usage_record/giấy phép, copy khớp 100%, người thật/IP, max_generations, kiểm preview |
| 07-performance.md | [Performance](agents/07-performance-specialist.md) | Phép tính budget, forecast có khoảng/nguồn, keyword intent/negative, không tự chi tiền |
| 08-conversion.md | [Conversion](agents/08-conversion-specialist.md) | Message match, consent, không dark pattern/khẩn cấp giả, tracking không PII, A/B test |
| 09-coordinator.md | [Coordinator](agents/09-marketing-coordinator.md) | Người nhận/Cc/Bcc, attachment version, báo tiến độ thật, nhắc tối đa 1/việc/ngày |
| 10-qa.md | [QA](agents/10-qa-compliance.md), [template QA](templates/qa-report.md) | Độc lập với tác giả, finding có mức độ/vị trí, fail closed, giới hạn sửa và report tái lập |

## Chuẩn hóa để không tạo hai hợp đồng

| Bản gửi | Chuẩn giữ trong repository / lý do |
| --- | --- |
| `backend/`, `infra/`, `com.yourcompany.mktai`, `docs/agent-specs/` | Backend/package/infrastructure thực ở 00/15; profile ở `docs/mkt-ct/agents/`. Không đổi tên lại hoặc tạo source từ cây mẫu |
| Boot 3.x; PostgreSQL/pgvector/Flyway; frontend, `.env.example`, `agent-eval` có thể chạy | POM quan sát vẫn Boot 4.1.1/MariaDB; stack đích PROPOSED. Không copy lệnh frontend/eval hoặc tuyên bố ArchUnit/Modulith/CI đã có |
| UUID cho tất cả primary key | Giữ `users.id` Long/BIGINT và auth/role/status hiện có; UUID chỉ là hướng bảng mới |
| `status: DONE / NEEDS_INPUT / BLOCKED / FAILED` trong model JSON | Dùng `output_status` ở common; model output khác result v0.1, run_status và 11 task states. Mapping ở 05 |
| `agent_code: RESEARCH` và code ngắn | Dùng code catalog `research-intelligence`; số 00–10 là thứ tự profile, không phải ID DB. Không seed thêm alias agent |
| Plan có `temp_id`, `depends_on`, `requires_qa`, `requires_approval` | Map sang local_key + dependencies và gate_policy ở 05; backend kiểm rồi persist task/dependency. Không thêm bảng plan |
| “Skill” như `web.search`, `email.draft` | Đây là TOOL capability code; BUSINESS_SKILL hiện có vẫn riêng. Allowlist cụ thể ở từng profile, gateway kiểm mỗi call |
| QA chỉ PASS/FAIL; mặc định FAIL khi không chắc | Giữ PASS/FAIL/NOT_VERIFIED/NOT_APPLICABLE; vi phạm đã thấy → FAIL, chưa đủ khả năng kiểm → NOT_VERIFIED. Cả hai chặn gate bắt buộc, không PASS có điều kiện |
| Claim VERIFIED / UNSUPPORTED / CONTRADICTED / OUTDATED | Mapping SUPPORTED / NOT_VERIFIED / CONTRADICTED ở 05; unsupported/outdated ghi finding/reason, không tạo enum DB thứ hai |
| QA `status: DONE`, `result`, `scores` mặc định 0 | `output_status` nói model xong review; `verdict` nói QA. Backend resolve artifact_version_id; scores chỉ khi có rubric/đo được, chưa biết dùng null |
| Agent “không có skill ghi” nhưng cần artifact/report/registry | Model trả JSON, backend persist qua service có quyền; source.register/doc.write và external draft là write có kiểm soát. QA không sửa artifact tác giả |
| Tất cả run/tool_call append-only | Lịch sử đã kết thúc không ghi đè; run/call đang chạy cần lifecycle/measurement update có kiểm soát. Ledger append settlement; artifact version bất biến, không đổi thiết kế thành log chỉ INSERT mà không xử lý completion |
| Ask first mọi đổi spec/schema/auth; Claude luôn plan rồi đợi | Yêu cầu hiện tại đã cho phép sửa docs/spec; giữ quy tắc scope ở root. Không nhập một approval workflow mới từ tài liệu đính kèm |

## Phần còn mở và nghiệm thu

Tool/model matrix là đề xuất cần hiện thực bằng registry/guard; chưa có output.schema.json runtime, adapter hoặc connector. Threshold fact/brand/rights, channel rules, benchmark, market policy và cấu hình giới hạn phải version hóa khi được giao implementation. Không coi nội dung policy trong spec là xác minh pháp lý/chính sách nền tảng hiện hành.

Không thêm bảng/cột/migration từ các field chuyên môn; schema nội dung và mapping còn là thiết kế. Giữ ERD 27 bảng/7 nhóm, 11 code, state/contract chuẩn và logic auth/user/config. Kết quả kiểm tra lần này ở [12](12-documentation-checks.md), scope review ở [11](11-docs-review.md), decision ở [09](09-decisions-and-open-questions.md).
