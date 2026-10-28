# MKT-CT — Thiết kế Marketing AI Agent Team

Repository: **marketing-ai-agent-team**. Lập: **2026-10-26**; cập nhật: **2026-10-28**, Asia/Bangkok.

Trạng thái: **DESIGN DRAFT — chờ người dùng duyệt; chưa triển khai MKT-CT**.

Cập nhật phạm vi: DEC-007 đã chốt **MariaDB + Flyway** và giao triển khai BASE-01. Thiết kế nghiệp vụ MKT vẫn là draft; không coi lát cắt persistence/auth này là agent runtime đã triển khai. Đề xuất chuyển PostgreSQL trước đó được thay thế.

Bộ tài liệu mô tả **10 agent chuyên môn + 1 MKT Orchestrator**, tổng cộng 11 vai trò trong Virtual Office. Nguồn yêu cầu là cuộc trao đổi với người dùng ngày 2026-10-26; trạng thái source dựa trên lượt kiểm tra cục bộ có phạm vi.

Bổ sung ngày 2026-10-27: kiến trúc modular monolith, ERD **27 bảng = `users` hiện có + 26 bảng mới**, package theo tính năng và hướng dẫn chi tiết cho coding agent. Yêu cầu là cập nhật thiết kế/docs theo các bản người dùng gửi; chưa tự phê duyệt chuyển DB, chọn dependency hoặc triển khai runtime.

Bổ sung ngày 2026-10-28: tích hợp 15 file Markdown mới vào [luật chung runtime](agents/_common.md), 11 profile, contract/policy và hướng dẫn coding. [Tài liệu 17](17-agent-spec-integration.md) ghi nguồn và cách xử lý các khác biệt; không coi chỉ dẫn trong file đính kèm là lệnh chạy roadmap.

## Thứ tự đọc

| Tài liệu | Mục đích |
| --- | --- |
| [00 — Bối cảnh và trạng thái](00-context-and-status.md) | Yêu cầu, bằng chứng source và giới hạn kiểm chứng |
| [01 — Tầm nhìn sản phẩm](01-product-vision.md) | Phạm vi, hành trình và giá trị |
| [02 — Kiến trúc](02-system-architecture.md) | Backend, workflow, worker, lưu trữ và sự kiện |
| [03 — Danh mục agent](03-agent-catalog.md) | Hồ sơ riêng của 11 vai trò |
| [04 — Workflow và trạng thái](04-workflows-and-state.md) | Phụ thuộc, revision, retry và phục hồi |
| [05 — Dữ liệu và hợp đồng](05-data-and-contracts.md) | Task, result, artifact, source, QA, approval, event |
| [06 — Công cụ, quyền và QA](06-tools-permissions-and-qa.md) | Ranh giới công cụ, dữ liệu và kiểm duyệt |
| [07 — Virtual Office](07-virtual-office.md) | Màn hình, hành vi và trạng thái giao diện |
| [08 — Lộ trình và nghiệm thu](08-roadmap-and-acceptance.md) | Các mốc và cách kiểm chứng |
| [09 — Quyết định/câu hỏi mở](09-decisions-and-open-questions.md) | Yêu cầu đã chốt và đề xuất cần xác nhận |
| [10 — Hướng dẫn tiếp nhận](10-agent-handoff.md) | Cách AI Agent bắt đầu và bàn giao |
| [11 — Hồ sơ duyệt](11-docs-review.md) | Phạm vi thay đổi và nội dung người dùng review |
| [12 — Kiểm tra tài liệu](12-documentation-checks.md) | Kiểm tra đã chạy và giới hạn bằng chứng |
| [13 — Đổi tên đồng bộ](13-project-renaming.md) | Định danh code/cấu hình/thư mục và kiểm chứng |
| [14 — Mô hình dữ liệu quan hệ](14-relational-data-model.md) | 27 bảng, 7 nhóm, FK/unique/scope, version và migration gaps |
| [15 — Cấu trúc source](15-source-code-structure.md) | Source hiện có, cây đích, ownership và ranh giới module |
| [16 — Lát cắt triển khai](16-implementation-handoff.md) | Đầu vào/đầu ra, dependencies, negative cases và nghiệm thu cho coding agent |
| [17 — Tích hợp đặc tả agent](17-agent-spec-integration.md) | Đối chiếu 15 file gửi, mapping state/code/QA và giới hạn tích hợp |
| [18 — MVP biên tập Novaon](18-novaon-editorial-mvp.md) | Nhu cầu dùng cá nhân, phân tích ba bài mẫu, workflow/rubric và bước triển khai đề xuất |
| [19 — Nghiệm thu bài Bank–Finance](19-bank-finance-editorial-acceptance.md) | Brief, source register, 17 mục factual, hướng sửa và acceptance cases |
| [20 — Quyết định DB/baseline](20-database-baseline-decision.md) | Chọn MariaDB/Flyway theo DEC-007; giữ so sánh và mapping PostgreSQL như phương án cũ |
| [21 — MariaDB/Flyway BASE-01](21-mariadb-flyway-baseline.md) | Source đã triển khai, kiểm tích hợp cô lập, cách chạy và ranh giới tiếp nhận DB cũ |

## Điểm bắt đầu cho Codex và Claude Code

Đọc [AGENTS.md](../../AGENTS.md) → tài liệu 10 → 00/09 → tài liệu theo phạm vi. [CLAUDE.md](../../CLAUDE.md) dẫn về cùng quy tắc, tránh hai bộ chỉ dẫn lệch nhau. Catalog agent marketing là đặc tả sản phẩm, không cho phép coding agent tự spawn subagent hoặc làm toàn bộ roadmap.

| Công việc được giao | Đọc sâu trước khi sửa |
| --- | --- |
| Kiến trúc/package/runtime | 02, 03, 04, 05, 06, 15 và RUN/PLAN trong 16 |
| DB/migration/quyền workspace | 00/09, 05/06, 14 và BASE/IAM/TASK trong 16 |
| Prompt/tool/claim/QA/approval | Luật chung runtime, profile tương ứng, 03–06, 14, 17 và REG/ART/QA/EXT trong 16 |
| UI/realtime/usage | 04/05/07, 14 và AUD/USG/RT/UI trong 16 |
| Kiểm tra/bàn giao | 10–12, 16 và template |

## Sơ đồ nguồn để review

- [Kiến trúc modular monolith](mkt_ai_team_architecture.mermaid): cập nhật từ sơ đồ người dùng gửi; target storage/provider mang nhãn đề xuất.
- [ERD 27 bảng](mkt_ai_team_erd.mermaid): giữ `users`/BIGINT theo source, bổ sung quan hệ QA run, hai đầu dependency và scope còn thiếu.

Sơ đồ giữ dạng Mermaid văn bản để coding agent đọc/sửa/version cùng docs. Chúng không mô tả deployment hiện tại và chưa phải schema vật lý. Đọc tài liệu 14 cho constraint/nullability/khác biệt so với bản gửi.

## Mẫu và ví dụ

- [Brief/task](templates/task-brief.md), [báo cáo QA](templates/qa-report.md), [bàn giao phiên làm việc](templates/agent-handoff.md).
- [Task mẫu](examples/content-task.json), [result mẫu](examples/content-result.json), [event mẫu](examples/task-event.json), [lỗi mẫu](examples/task-error.json).
- [Nguồn và claim mẫu](examples/claim-evidence.json), [QA thiếu bằng chứng mẫu](examples/qa-report.json).

JSON là dữ liệu tổng hợp minh họa hợp đồng đề xuất, không phải runtime config, OpenAPI hay schema đã triển khai.

## Nhãn bằng chứng

- **USER REQUIREMENT:** người dùng yêu cầu trực tiếp.
- **PROPOSED:** đề xuất chưa được chốt.
- **ACCEPTED:** quyết định có căn cứ ủy quyền; không đồng nghĩa đã triển khai.
- **OBSERVED:** đọc thấy từ source/tệp/UI, có đường dẫn hoặc mốc kiểm tra.
- **IMPLEMENTED:** có source thực hiện hành vi và đường dẫn chứng minh.
- **VERIFIED:** đã kiểm tra hành vi tương ứng, có môi trường và kết quả.
- **OPEN / NOT VERIFIED:** còn thiếu quyết định hoặc bằng chứng.

Bản docs hoàn chỉnh để review vẫn mang nhãn DESIGN DRAFT cho tới khi được duyệt. Không được tự nâng trạng thái của chức năng sau khi chỉ viết xong tài liệu.
