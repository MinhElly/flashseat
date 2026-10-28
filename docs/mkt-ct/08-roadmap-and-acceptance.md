# 08 — Lộ trình và nghiệm thu

Trạng thái: PROPOSED. Chưa cam kết timeline/ngân sách; chưa có bằng chứng triển khai các mốc MKT-CT.

Đối chiếu README mẫu gửi 2026-10-28: các giai đoạn 0–6 là tham khảo, giữ các mốc P0–P4 và WF-01 ở đây. QA/approval/usage/quyền phải đi cùng lát cắt chạy đầu tiên; không nghiệm thu artifact phát hành ở giai đoạn 1 rồi đợi giai đoạn 4 mới có QA. React/Next.js, connector và live eval chưa được chọn hoặc cho chạy từ roadmap.

Đọc [16 — Lát cắt bàn giao](16-implementation-handoff.md) cho dependency/input/output/negative cases từng phần; roadmap này không cấp quyền triển khai. Kiến trúc đích là modular monolith theo 02/15, ERD baseline 27 bảng theo 14; không cần tạo hết bảng/module ở một lần.

## P0 — Chốt nền tảng

Cập nhật DEC-007: MariaDB + Flyway đã được chọn và lát cắt BASE-01 fresh schema/auth có [bàn giao 21](21-mariadb-flyway-baseline.md). IAM/runtime/provider và các phần khác của P0 chưa hoàn tất; không yêu cầu chọn PostgreSQL để tiếp tục.

Đầu ra: chốt cách mở rộng source nền hiện có, scope MVP, runtime/provider, quyền workspace, DB/queue/storage và contract API cho lát cắt đầu. Tên module/package đã thống nhất; không cần hỏi lại việc đổi định danh.

Ưu tiên BASE-01/IAM-01: giữ users/Long/BIGINT/auth hiện có; chốt hướng PostgreSQL/pgvector/Flyway so với MariaDB hiện tại và ma trận membership/action. Không downgrade Boot 4.1.1 từ bản mẫu 3.x; xác minh SDK/version trước thêm dependency.

Nghiệm thu: có decision record và task cụ thể; không mặc định viết lại backend; có fixture synthetic và bộ rule QA để đo.

Đầu ra review đã cụ thể hóa: [ADR-DRAFT-001 ở 20](20-database-baseline-decision.md) so sánh DB, mapping users và migration/quyền cá nhân; [19](19-bank-finance-editorial-acceptance.md) có brief, rubric và case nghiệm thu biên tập chưa chạy. ADR chưa ACCEPTED; tài liệu/source check không thay nghiệm thu DB hoặc model.

## P1 — WF-01 chạy trọn vẹn

Orchestrator, Research, Editorial, QA; task state, artifact/source/version, approval nội bộ và UI tối thiểu.

**Biến thể ưu tiên cá nhân PROPOSED:** [WF-01-NOVAON tại 18](18-novaon-editorial-mvp.md) đưa Strategy/duyệt hướng bài và Social/QA biến thể vào lát cắt đầu theo nhu cầu nghiên cứu, longform và caption của người dùng. Baseline WF-01 vẫn giữ; P2 là mở rộng các vai trò còn lại nếu chọn biến thể này, không làm Strategy/Social lần thứ hai. Gemini là provider người dùng đang có, chưa chốt model/budget hoặc kiểm kết nối thật.

Nghiệm thu:

- Từ brief tạo research pack và bài viết có nguồn đối chiếu được.
- QA PASS/FAIL/NOT_VERIFIED dẫn tới đúng trạng thái.
- Revision tạo version mới; không tái dùng verdict/approval cũ.
- Restart/worker chết không mất task đã xác nhận.
- Kết quả đến muộn sau cancel/hết lease không ghi đè.
- User khác workspace/project không đọc task, source hoặc tải file.
- Usage có nhãn actual/estimated/unknown.
- Task/run/prompt version/source/claim/QA/approval truy được xuyên suốt; database constraint và service scope cùng được kiểm.
- Event ghi cùng transaction, sau restart không mất sự kiện; usage reservation/settlement không vượt budget hoặc cộng trùng khi chạy đồng thời.

Fixture chỉ chứng minh orchestration với fixture. Ghi riêng live model/tool E2E, tài khoản/môi trường và giới hạn kiểm chứng.

## P2 — Đủ 11 vai trò

Thêm Strategy, Case Study, Social, Designer, Performance, Conversion, Coordinator; WF-02 và invalidation downstream.

Nghiệm thu: mỗi agent có skill/contract hữu ích; task độc lập chạy song song trong quota; upstream đổi làm stale sản phẩm liên quan; không gọi chuyên gia không cần.

## P3 — Connector ưu tiên

Chọn theo nhu cầu: source/Drive; document/sheet; image/Canva; email/task; CMS/analytics; ads.

Nghiệm thu từng connector: quyền thực tế, timeout/quota/lỗi, idempotency/đối soát, secret redaction, approval đúng scope. Tên tool/nút UI không chứng minh kết nối.

## P4 — Virtual Office và vận hành

Hoàn thiện phòng, task board, artifact library, review queue, thông báo, accessibility và phục hồi UI.

Nghiệm thu: UI khớp backend sau reconnect; pause/cancel/revision hoạt động; truy nguồn/diff/approval; activity không lộ dữ liệu. Chốt ngưỡng quality/latency/cost bằng workload đo được.

## Tình huống kiểm tra cần có

| Tình huống | Mong đợi |
| --- | --- |
| Brief thiếu audience/offer thiết yếu | WAITING_INPUT, câu hỏi cụ thể |
| Claim thiếu nguồn | NOT_VERIFIED, chặn gate liên quan |
| PDF chứa chỉ dẫn lấy secret | Không mở rộng quyền/công cụ |
| QA yêu cầu sửa | Version mới và review lại |
| Tool đọc timeout | Retry hữu hạn hoặc lỗi rõ |
| Email timeout sau provider nhận | Đối soát trước retry |
| Event/callback lặp | Không lặp artifact hoặc hành động |
| Input đã duyệt thay đổi | Downstream stale, chặn bản cũ |
| Hai worker cùng task | Chỉ lease hợp lệ ghi kết quả |
| Actor không có quyền approve | Backend từ chối |
| Vượt runtime/budget | Dừng theo policy, báo phần đã làm |

Mỗi mốc báo source/contract, kiểm tra, môi trường, kết quả, rủi ro và phần chưa xác minh. Không gộp IMPLEMENTED với VERIFIED hoặc MOCK với LIVE.
