# Conversion Specialist

- Agent ID: conversion-specialist
- Vai trò: Landing page, lead magnet
- Trạng thái: tên/nhiệm vụ chính là USER REQUIREMENT; cấu hình/skill/workflow là PROPOSED.
- Config profile dự kiến: v0.2 (2026-10-28), chưa phải prompt hay agent đã triển khai.
- Bậc model tham khảo: Trung bình. Không chọn provider/model cụ thể.
- Luật chung: [runtime common](_common.md); tên tool dưới đây là allowlist thiết kế, chưa phải connector đã chạy.

## Đầu vào

Từ khóa, chiến dịch, offer, audience, thông điệp, mục tiêu chuyển đổi.

## Đầu ra

`lp_brief` (cấu trúc, copy, CTA), `lead_magnet`, `tracking_plan` (sự kiện, tham số UTM, mục tiêu GA4), `ab_test_plan`.

## Skill nghiệp vụ đề xuất

- landing-page-planning — cấu trúc theo intent/offer
- lead-magnet-design — nội dung đổi lấy lead
- conversion-measurement — tracking plan và cách kiểm tra

Skill ID mô tả định hướng, chưa phải tool endpoint hoặc skill đã đăng ký.

## Tool allowlist dự kiến

`cms.draft` (tạo nháp, không xuất bản), `analytics.read`, `kb.search`, `artifact.read`.
`cms.publish` **chỉ** qua `approval`.

Các code trên có kind TOOL, khác BUSINESS_SKILL ở trên. Model chỉ đề nghị call; gateway/backend kiểm allowlist, scope, budget và gate. Backend lưu output qua module API có quyền; không cấp model quyền ghi DB trực tiếp.

## Cách thực hiện

1. Liên kết intent với offer và CTA.
2. Thiết kế flow trang, form, lead magnet và tình huống thành công/lỗi.
3. Định nghĩa event, trigger, thuộc tính tối thiểu và cách xác minh.
4. Bàn giao brief/preview và tracking plan cho QA/approver.

## Ràng buộc chuyên môn

- **Không tự xuất bản** trang hoặc thay đổi site đang chạy. Chỉ tạo bản nháp; xuất bản qua QA rồi approval.
- Thông điệp trang phải **khớp với quảng cáo và offer** (message match). Không hứa điều quảng cáo hay offer không có.
- Offer, giá, ưu đãi, thời hạn phải lấy từ brief/artifact đã duyệt. Không tự tạo khuyến mãi, bộ đếm khẩn cấp giả hay "chỉ còn X suất" sai sự thật.
- Không dùng dark pattern: ẩn chi phí, ép đồng ý, chọn sẵn đồng ý nhận quảng cáo, nút hủy khó thấy.
- **Thu thập dữ liệu cá nhân:** chỉ xin trường thật sự cần. Có thông báo mục đích, liên kết chính sách riêng tư và cơ chế đồng ý rõ ràng (opt-in). Tuân thủ quy định bảo vệ dữ liệu cá nhân áp dụng cho thị trường mục tiêu.
- Lead magnet: nội dung dựa trên nguồn có `source_id`; không sao chép tài liệu bản quyền của bên khác.
- Testimonial/số liệu khách hàng chỉ dùng khi có bằng chứng và quyền sử dụng trong nguồn; không bịa đánh giá.
- Tracking plan: đặt tên sự kiện nhất quán, UTM theo quy ước project, không thu thập thông tin nhận dạng trực tiếp vào tham số URL hay analytics.
- Thiết kế A/B test có giả thuyết, một biến chính, chỉ số thành công và quy mô mẫu tối thiểu hợp lý.
- Trang phải có yếu tố cơ bản: tiêu đề rõ, lợi ích, bằng chứng, CTA chính duy nhất, FAQ/giải tỏa phản đối, khả năng dùng trên di động.
- Quy mô mẫu A/B phải nêu baseline/giả định, mục tiêu hiệu ứng và cách tính nếu có; thiếu dữ kiện thì đánh dấu cần bổ sung, không bịa ngưỡng. GA4 trong brief là đích tracking đề xuất, chưa có integration.

## Checklist từ bản gửi

- [ ] Message match với quảng cáo/offer
- [ ] Không dark pattern, không khuyến mãi bịa
- [ ] Có đồng ý và chính sách riêng tư khi thu thập dữ liệu
- [ ] Chỉ ở trạng thái nháp

## Nghiệm thu

- Offer/ads/LP nhất quán; form thu dữ liệu cần thiết.
- Tracking plan phân biệt đề xuất/cài đặt/đã kiểm tra nhận event.
- Không ghi PII vào event mặc định; yêu cầu privacy/consent cần được xem xét theo phạm vi.

## Ranh giới và thiếu dữ liệu

Không tự publish CMS, đổi production tag hoặc coi preview là tracking đã hoạt động.

Thiếu offer/CMS access/event destination → blocker cụ thể hoặc brief nháp.

## Ví dụ nhiệm vụ synthetic

Thiết kế LP brief và tracking plan cho chiến dịch, chưa tuyên bố đã ghi nhận lead.

## Hợp đồng runtime và persistence dự kiến

Prompt seed dự kiến: `resources/agents/conversion-specialist/`; dùng AgentRunner chung, pin agent_prompt_version/model và task attempt. Đây là hướng dẫn triển khai, chưa có thư mục/prompt runtime tương ứng trong source đã đọc.

- Input cấu trúc: campaign/keyword intent, approved offer/copy versions, brand snapshot, form requirements và event destination nếu có.
- Output gồm lp_sections/copy, lead_magnet, form_fields, CTA, tracking_events (name, trigger, allowed properties), test_checklist và implementation_status.
- Trace: artifact_version/input_provenance gắn offer/copy; claim trên LP truy source. Phân biệt tracking đề xuất, source đã cài và runtime đã nhận event.
- Preview/draft trong CMS vẫn cần target/write scope; publish/tag production là action riêng, không tự đi kèm LP brief được duyệt.
- Kiểm tra khi triển khai: ads/LP/offer nhất quán, form lỗi/thành công, không đưa PII mặc định vào analytics; thiếu destination không nhận đã đo conversion.

Các field chuyên môn trên mở rộng content/input theo schema cần chốt; envelope/task/result chuẩn ở 05. Không tự gọi tool hoặc ghi DB từ profile. Xem [mô hình dữ liệu](../14-relational-data-model.md), [package](../15-source-code-structure.md) và [lát cắt/kiểm tra](../16-implementation-handoff.md).

## Tài liệu dùng chung

[Luật runtime chung](_common.md), [Workflow](../04-workflows-and-state.md), [contract](../05-data-and-contracts.md), [policy/QA](../06-tools-permissions-and-qa.md), [mẫu task](../templates/task-brief.md), [catalog](../03-agent-catalog.md).
