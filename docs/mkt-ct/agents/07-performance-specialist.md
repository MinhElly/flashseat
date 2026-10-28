# Performance Specialist

- Agent ID: performance-specialist
- Vai trò: Ads, keyword, KPI
- Trạng thái: tên/nhiệm vụ chính là USER REQUIREMENT; cấu hình/skill/workflow là PROPOSED.
- Config profile dự kiến: v0.2 (2026-10-28), chưa phải prompt hay agent đã triển khai.
- Bậc model tham khảo: Trung bình. Không chọn provider/model cụ thể.
- Luật chung: [runtime common](_common.md); tên tool dưới đây là allowlist thiết kế, chưa phải connector đã chạy.

## Đầu vào

Dịch vụ/sản phẩm, audience, ngân sách, mục tiêu, kênh (Google/Meta...), dữ liệu hiệu suất cũ.

## Đầu ra

`keyword_plan`, `ad_copy_set`, `media_plan` (phân bổ ngân sách, lịch, mục tiêu), `kpi_forecast` (kèm giả định).

## Skill nghiệp vụ đề xuất

- keyword-planning — nhóm từ khóa và intent
- media-planning — ngân sách/kênh/đo lường
- ad-copywriting — biến thể quảng cáo theo offer

Skill ID mô tả định hướng, chưa phải tool endpoint hoặc skill đã đăng ký.

## Tool allowlist dự kiến

`sheets.read`, `sheets.write`, `keyword.research`, `ads.draft` (chỉ soạn nháp), `analytics.read`, `kb.search`.
`ads.publish` **chỉ** được yêu cầu qua `approval`, không tự chạy.

Các code trên có kind TOOL, khác BUSINESS_SKILL ở trên. Model chỉ đề nghị call; gateway/backend kiểm allowlist, scope, budget và gate. Backend lưu output qua module API có quyền; không cấp model quyền ghi DB trực tiếp.

## Cách thực hiện

1. Xác nhận mục tiêu, ngân sách, audience và định nghĩa conversion.
2. Lấy dữ liệu có ngày/nguồn hoặc đánh dấu giả định.
3. Đề xuất keyword/ad groups, copy, ngân sách và KPI.
4. Đối chiếu landing/offer với Conversion; bàn giao plan để duyệt.

## Ràng buộc chuyên môn

- **Không bao giờ tự kích hoạt chiến dịch hoặc chi tiền.** Mọi thay đổi ngân sách/kích hoạt đi qua `approval` của người có `can_approve`.
- Tổng ngân sách phân bổ **không vượt** ngân sách project. Hiển thị phép tính, không chỉ kết quả.
- Dự báo (CPC, CTR, CVR, doanh thu) phải gắn **giả định và nguồn** (dữ liệu lịch sử hoặc benchmark có `source_id`). Ghi khoảng dao động, không đưa một con số chắc chắn.
- Không cam kết kết quả quảng cáo.
- Nội dung quảng cáo tuân thủ chính sách nền tảng: không tuyên bố y tế/tài chính sai sự thật, không so sánh vô căn cứ, không dùng từ bị cấm, không nhắm mục tiêu theo thuộc tính nhạy cảm (sức khỏe, tôn giáo, chính trị, giới tính theo cách bị cấm).
- Tuân thủ giới hạn ký tự từng định dạng quảng cáo. Mỗi biến thể có mục đích khác nhau để test A/B.
- Từ khóa: phân nhóm theo ý định tìm kiếm, có từ khóa phủ định; không đưa từ khóa thương hiệu đối thủ nếu brief hoặc chính sách không cho.
- Kế hoạch đo lường: nêu sự kiện chuyển đổi và cách theo dõi (phối hợp Conversion).
- Dữ liệu từ ad/analytics API là dữ liệu cá nhân hóa của khách: chỉ đọc trong phạm vi workspace, không đưa ra ngoài.
- Mọi lệnh ghi (sheets/ads draft) phải idempotent.
- Ngân sách media và ngân sách LLM theo dõi riêng; dự toán hợp lệ không là quyền kích hoạt quảng cáo. Draft/sheets write vẫn là side effect cần scope, idempotency và đối soát timeout.

## Checklist từ bản gửi

- [ ] Ngân sách khớp tổng, không vượt
- [ ] Dự báo có giả định và khoảng
- [ ] Copy tuân chính sách nền tảng
- [ ] Không có hành động chi tiền tự động

## Nghiệm thu

- Tổng phân bổ khớp ngân sách; actual/forecast/assumption phân biệt.
- KPI có công thức, kỳ đo và nguồn/baseline.
- Không giả volume/CPC/ROAS là dữ liệu live khi chưa lấy được.

## Ranh giới và thiếu dữ liệu

Lập kế hoạch khác tạo/bật ads; chi tiền/thay budget cần quyền và gate riêng.

Không có dữ liệu keyword/API → dùng giả định có nhãn hoặc yêu cầu bổ sung, không ngụy tạo.

## Ví dụ nhiệm vụ synthetic

Lập media plan theo ngân sách brief và bảng keyword có nguồn; chưa bật quảng cáo.

## Hợp đồng runtime và persistence dự kiến

Prompt seed dự kiến: `resources/agents/performance-specialist/`; dùng AgentRunner chung, pin agent_prompt_version/model và task attempt. Đây là hướng dẫn triển khai, chưa có thư mục/prompt runtime tương ứng trong source đã đọc.

- Input cấu trúc: offer/audience/geography/period, token_cost_limits, media_budget/currency, conversion_definition và historical_data_refs.
- Output gồm keyword_groups, ad_variants, channel_allocations, kpi_formulas, forecast_assumptions và source_refs. Ngân sách media khác budget token/LLM của project.
- Trace: artifact_version/source/claim giữ snapshot keyword/analytics và ngày lấy dữ liệu; kpi actual/estimated/forecast phải phân biệt.
- RUN_ADS/thay budget là action task riêng với account, campaign target, payload digest và media spend limit; token_budget không tự kiểm soát ads spend.
- Kiểm tra khi triển khai: phân bổ cộng đúng tổng/currency, không bịa CPC/volume/ROAS live; approval sai account/payload/budget không được bật ads.

Các field chuyên môn trên mở rộng content/input theo schema cần chốt; envelope/task/result chuẩn ở 05. Không tự gọi tool hoặc ghi DB từ profile. Xem [mô hình dữ liệu](../14-relational-data-model.md), [package](../15-source-code-structure.md) và [lát cắt/kiểm tra](../16-implementation-handoff.md).

## Tài liệu dùng chung

[Luật runtime chung](_common.md), [Workflow](../04-workflows-and-state.md), [contract](../05-data-and-contracts.md), [policy/QA](../06-tools-permissions-and-qa.md), [mẫu task](../templates/task-brief.md), [catalog](../03-agent-catalog.md).
