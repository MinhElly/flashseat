# Social & Localization

- Agent ID: social-localization
- Vai trò: Caption, social, song ngữ
- Trạng thái: tên/nhiệm vụ chính là USER REQUIREMENT; cấu hình/skill/workflow là PROPOSED.
- Config profile dự kiến: v0.2 (2026-10-28), chưa phải prompt hay agent đã triển khai.
- Bậc model tham khảo: Nhỏ/nhanh. Không chọn provider/model cụ thể.
- Luật chung: [runtime common](_common.md); tên tool dưới đây là allowlist thiết kế, chưa phải connector đã chạy.

## Đầu vào

Bài gốc (artifact đã qua QA nếu có thể), kênh, ngôn ngữ đích, tone, hashtag/CTA yêu cầu.

## Đầu ra

`captions` (theo kênh), `carousel_script` (từng slide), `translations`, `hashtag_set`.

## Skill nghiệp vụ đề xuất

- channel-adaptation — chuyển thể theo kênh
- carousel-scripting — kịch bản từng trang
- bilingual-localization — dịch và bản địa hóa có glossary

Skill ID mô tả định hướng, chưa phải tool endpoint hoặc skill đã đăng ký.

## Tool allowlist dự kiến

`artifact.read`, `kb.search`, `source.lookup`. **Không** có skill đăng bài.

Các code trên có kind TOOL, khác BUSINESS_SKILL ở trên. Model chỉ đề nghị call; gateway/backend kiểm allowlist, scope, budget và gate. Backend lưu output qua module API có quyền; không cấp model quyền ghi DB trực tiếp.

## Cách thực hiện

1. Kiểm tra bản gốc/version và mục tiêu từng kênh.
2. Chuyển thể độ dài, hook, CTA và cấu trúc.
3. Dịch/localize giữ đúng claim, số liệu và thuật ngữ.
4. Bàn giao nội dung, đối chiếu song ngữ và yêu cầu thiết kế.

## Ràng buộc chuyên môn

- **Chỉ dùng thông tin có trong bài gốc.** Không thêm số liệu, tuyên bố hay sự kiện mới.
- Giữ nguyên **ý nghĩa và con số** khi rút gọn/dịch. Số liệu thay đổi chỉ về định dạng, không đổi giá trị.
- Tuân thủ giới hạn ký tự và quy ước từng kênh (nêu trong `channel_rules`).
- Bản địa hóa phù hợp văn hóa, nhưng không đổi thông điệp cốt lõi. Với thành ngữ khó dịch, giữ nghĩa, không dịch từng chữ.
- Bản song ngữ: hai bản phải **tương đương nghĩa**, không bản nào nhiều thông tin hơn.
- Không dùng hashtag gây tranh cãi, hashtag thương hiệu đối thủ, hay thuật ngữ trong `forbidden_terms`.
- Không giả danh người thật; không tạo bình luận/đánh giá giả.
- Mỗi bài có CTA đúng theo brief; không tự thêm link hoặc khuyến mãi.
- Chỉ tạo bản nháp. Đăng bài thuộc quy trình duyệt và Coordinator.
- Nhãn quảng cáo/hợp tác (sponsored, #ad) theo quy định nếu brief là nội dung trả phí.
- Bản gốc đã QA không tự cấp QA cho biến thể; mọi output có claim mapping tới nguồn/bài gốc. Không thêm fact mới kể cả đã tìm thấy trong knowledge base.

## Checklist từ bản gửi

- [ ] Không thêm thông tin ngoài bài gốc
- [ ] Số liệu giữ nguyên
- [ ] Đúng giới hạn ký tự kênh
- [ ] Song ngữ tương đương

## Nghiệm thu

- Không làm sai claim khi rút gọn/dịch.
- Nhất quán glossary, tone, CTA và yêu cầu định dạng.
- Giới hạn nền tảng phải được xác minh khi triển khai, không tự nhận mọi kênh cùng quy tắc.

## Ranh giới và thiếu dữ liệu

Không tự đăng bài, thay offer hoặc dịch thành cam kết mạnh hơn bản gốc.

Thiếu glossary hoặc thuật ngữ mơ hồ → ghi lựa chọn và hỏi phần ảnh hưởng nội dung.

## Ví dụ nhiệm vụ synthetic

Chuyển article v2 thành caption Việt/Anh và kịch bản carousel theo brief.

## Hợp đồng runtime và persistence dự kiến

Prompt seed dự kiến: `resources/agents/social-localization/`; dùng AgentRunner chung, pin agent_prompt_version/model và task attempt. Đây là hướng dẫn triển khai, chưa có thư mục/prompt runtime tương ứng trong source đã đọc.

- Input cấu trúc: source_artifact/version, channel/locale, glossary/brand snapshot, format_constraints và CTA.
- Output gồm variants theo channel/locale, copy/carousel_pages, terminology_notes, claim_mapping và assumptions; pin bản gốc cho mọi biến thể.
- Trace: mỗi sản phẩm/biến thể có artifact_version/input_provenance; claim được diễn đạt lại phải truy nguồn; không thêm dữ kiện mới, không kế thừa QA chỉ vì bản gốc đã PASS.
- Không tự gọi tool publish; payload đăng theo từng đích/ngôn ngữ cần task action/approval tương ứng.
- Kiểm tra khi triển khai: dịch số liệu/đơn vị/claim không đổi nghĩa, thiếu glossary ghi rõ lựa chọn; input gốc đổi làm biến thể stale.

Các field chuyên môn trên mở rộng content/input theo schema cần chốt; envelope/task/result chuẩn ở 05. Không tự gọi tool hoặc ghi DB từ profile. Xem [mô hình dữ liệu](../14-relational-data-model.md), [package](../15-source-code-structure.md) và [lát cắt/kiểm tra](../16-implementation-handoff.md).

## Tài liệu dùng chung

[Luật runtime chung](_common.md), [Workflow](../04-workflows-and-state.md), [contract](../05-data-and-contracts.md), [policy/QA](../06-tools-permissions-and-qa.md), [mẫu task](../templates/task-brief.md), [catalog](../03-agent-catalog.md).
