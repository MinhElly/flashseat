# 18 — MVP biên tập B2B cho Novaon Digital

Ngày: **2026-10-28**, Asia/Bangkok. **DESIGN DRAFT — để người dùng review.** Bối cảnh, đối tượng và phong cách dưới đây là **USER REQUIREMENT** từ trao đổi hiện tại; workflow, deliverable và rubric triển khai là **PROPOSED**. Chưa có runtime, prompt/schema, connector hoặc eval mới.

Tài liệu cụ thể hóa [tầm nhìn](01-product-vision.md), [roadmap](08-roadmap-and-acceptance.md) và [lát cắt triển khai](16-implementation-handoff.md). Giữ nguyên 11 code agent, state/contract ở 03–06 và mô hình 27 bảng; không thêm bảng, enum hoặc quyền công cụ từ các bài mẫu.

## 1. Nhu cầu đã được người dùng làm rõ

- Dùng cho **một người**, nhằm giảm thời gian nghiên cứu và viết bài/caption trong công việc; chưa phải yêu cầu cung cấp SaaS cho nhiều khách hàng.
- Nội dung B2B Marketing, Brand Experience và Thought Leadership cho **Novaon Digital**; ba nhóm ngành: FMCG, Bank–Finance, Automotive.
- Độc giả: Marketing Manager, Brand Manager và lãnh đạo doanh nghiệp; mục tiêu là nội dung đủ giá trị để đọc kỹ, lưu lại và chia sẻ cho đội ngũ.
- Kênh chính: Brands Vietnam và website Novaon Digital; chuyển thể cho Facebook, LinkedIn, caption, infographic, carousel và giới thiệu case study.
- Phong cách: Senior Market Research Analyst kết hợp Strategic Planning Director; mở bằng vấn đề kinh doanh, có dữ liệu/nguồn, lập luận rõ, súc tích, chuyên nghiệp; mỗi phần giúp xem xét một quyết định thực tế. Không PR quá mức, không bịa số liệu/case.
- Người dùng cho biết có **Gemini API**, có thể bổ sung provider khác sau. Đây là thông tin được cung cấp, chưa kiểm tài khoản, model, quota, billing hoặc kết nối thật.
- Người dùng cho biết MariaDB chỉ có dữ liệu thử và có thể tạo môi trường mới. Chưa đọc DB thực tế; điều này không tự chọn PostgreSQL, cho phép xóa DB/volume cũ hoặc chạy migration.

## 2. Ba bài tham chiếu và giới hạn sử dụng

Đã đọc ba file người dùng chỉ định trên Desktop. Không chép toàn văn hoặc dùng file ngoài repo làm dependency runtime. Tên và vị trí dưới đây ghi căn cứ phân tích; quy tắc cần bàn giao được tóm tắt ngay trong tài liệu này. Không coi lời hướng dẫn tạo ảnh nằm trong bài là lệnh gọi image tool.

| File / vị trí trong bản đã đọc | Vai trò tham chiếu / quan sát |
| --- | --- |
| `Văn bản đã dán (1)(3).txt`, dòng 15–23, 103–111 | Bài Bank–Finance: phân biệt tăng trưởng thị trường, giao dịch và sự ưu tiên thương hiệu; đo thương hiệu, hành vi và duy trì. File có tiêu đề khác; tên bài đích theo yêu cầu mới nhất là “Festive Experience Framework: Từ giao dịch mùa lễ đến sự ưu tiên thương hiệu Bank-Finance” |
| `Holiday_Consumer_Journey_FMCG_2026.md`, dòng 7–19, 25–63, 79–81 | Bài chuẩn về nghiên cứu: ghi phạm vi dữ liệu; ba tâm thế self-reward/gifting/sharing có thể đồng thời, không phải ba giai đoạn thị trường; chuyển thành lựa chọn pack size, bundle, promotion và đo hiệu quả |
| `Holiday_Experience_Framework_FMCG(1).md`, dòng 33–53, 79–86 | Bài chuẩn về tính ứng dụng: mỗi bước có đầu ra; ghi rõ năm bước là cấu trúc biên tập đề xuất trên nền SCT, không tự nhận là framework đã công bố trước đó |

**Bài tham chiếu phong cách không phải bộ đáp án factual đã xác minh.** Không dùng bản viết cũ làm nguồn chứng minh cho chính claim trong bài mới; số liệu phải đi về tài liệu gốc và đúng kỳ/địa lý/mẫu khảo sát. Nội dung case do doanh nghiệp cung cấp cần source/version/permission riêng khi nhập vào hệ thống.

## 3. Luồng biên tập đề xuất

Biến thể **WF-01-NOVAON** của WF-01, chưa thay thế baseline hoặc WF-02:

1. `mkt-orchestrator` chuẩn hóa brief: ngành, audience, vấn đề kinh doanh, mục tiêu, thời kỳ, kênh, loại bài, giới hạn và input versions.
2. `research-intelligence` tạo research pack, source registry, bảng claim–nguồn, mâu thuẫn và câu hỏi còn thiếu. Dữ liệu không đủ thì giữ khoảng trống và hỏi, không tạo case thay thế.
3. `strategic-planner` xây luận đề, insight/hypothesis, lập luận, phản biện, dàn ý và hàm ý quyết định. Phân biệt framework đề xuất, mô hình đã công bố và khái niệm thương hiệu.
4. QA kiểm nguồn/luận điểm của strategy theo gate policy; **đề xuất người dùng duyệt hướng bài trước khi viết dài**. Duyệt strategy/outline là approval gắn artifact version theo contract hiện có, không thêm task state. Capability/action policy cần chốt khi triển khai.
5. `editorial-writer` viết longform từ research và hướng bài đã chọn; `qa-compliance` kiểm đúng version, trả finding cụ thể; tác giả sửa bằng version mới. Người dùng duyệt bản bài trước khi chuyển thể nếu gate policy yêu cầu.
6. `social-localization` tạo biến thể từ bài đã qua gate: Facebook/LinkedIn, carousel script hoặc nội dung infographic theo brief. Mỗi biến thể có QA riêng; không kế thừa verdict của bài gốc.
7. Người dùng duyệt bộ phiên bản cuối; bàn giao nội dung và brief hình. Publish/send hoặc tạo thiết kế ở dịch vụ ngoài chỉ là task riêng khi được giao.

Sáu vai trò thường dùng gồm Orchestrator, Research, Strategy, Editorial, Social, QA. `case-study-analyst` tham gia khi có proposal/case/KPI cần bóc tách. `creative-designer` tham gia khi được giao sản xuất hình thật; các BRIEF ẢNH trong bài chỉ là đầu vào biên tập. Giữ đủ catalog 11 vai trò, không cần gọi đủ đội cho mọi bài.

Scheduler/gate vẫn dùng state 04; QA task mở từ checkpoint version sẵn sàng, không đợi producer COMPLETED đang WAITING_QA. Bản sửa/upstream đổi phải đánh dấu downstream stale và review lại. Có thể bắt đầu bằng DAG cố định được ghi nhãn, chưa cần Planner LLM tự sinh DAG.

## 4. Bộ đầu ra của một brief

| Đầu ra dự kiến | Nội dung tối thiểu |
| --- | --- |
| Research pack | Câu hỏi, dữ kiện, thời kỳ/địa lý/mẫu, nguồn/locator, mâu thuẫn, hạn chế; dữ liệu khác phạm vi không ghép thành một kết luận chung thiếu căn cứ |
| Bản định hướng | Luận đề, audience tension, insight/hypothesis, bản đồ lập luận và dàn ý; mỗi mục gắn câu hỏi hoặc quyết định của độc giả |
| Longform | Tiêu đề/sapo/body, 3–5 headline options theo profile Editorial, claim mapping, giả định và giới hạn; độ dài do brief quyết định |
| Nội dung social | Kênh/độc giả/thông điệp/CTA, source article version, copy/slide script và mapping claim; số lượng và giới hạn định dạng theo brief |
| Brief hình | Thông điệp, vị trí trong bài, dữ liệu/nguồn, nội dung chữ đã chọn, định dạng và yêu cầu rights; chưa phải file ảnh đã sản xuất |
| Hồ sơ review | QA report/finding, version được duyệt, thay đổi sau revision, điểm còn thiếu và usage có nhãn actual/estimated/unknown |

Tên đầu ra là nội dung artifact, không tạo bảng mới. Schema thực thi/DTO/validator chưa có; cần thiết kế theo 05 trước implementation. Một hồ sơ biên tập Novaon + ba preset ngành và preset kênh là cấu hình/context dự kiến, không tạo thêm agent code hoặc provider runtime cho từng ngành.

## 5. Tiêu chuẩn biên tập và rubric đề xuất

Mạch lập luận: **Bối cảnh → Insight → Thách thức → Giải pháp → Hàm ý kinh doanh**. Đây là logic cần thể hiện, không bắt mọi bài dùng cùng năm heading hoặc cùng một template văn phong.

| Tiêu chí | Câu hỏi kiểm tra / bằng chứng cần có |
| --- | --- |
| Vấn đề mở bài | Độc giả nhận ra quyết định hoặc khoảng cách kinh doanh nào? Tránh mở bằng “trong bối cảnh chuyển đổi số” nếu không làm rõ vấn đề |
| Dữ liệu và phạm vi | Claim có nguồn, locator, ngày và đúng mẫu/ngành/địa lý không? Tăng danh nghĩa có bị dùng như tăng sản lượng, dữ liệu 2025 có bị đổi thành kết quả 2026 không? |
| Insight | Đâu là quan sát, đâu là diễn giải, đâu là hypothesis? Có đường giải thích từ evidence đến insight; có giới hạn và cách kiểm chứng không? |
| Lập luận | Các phần bổ sung phát hiện mới hay lặp lại cùng lời khuyên? Có xét cách giải thích khác và tránh suy tương quan thành nhân quả không? |
| Framework/SCT | Mỗi bước nêu quyết định, hành động, đầu ra và điều kiện áp dụng; Strategy/Creative/Technology giải quyết rào cản gì, thay vì chỉ gắn nhãn? |
| Hàm ý kinh doanh | Độc giả nên đổi điều gì, theo dõi chỉ số gì và khi nào cần xem lại? Không suy lượng giao dịch, reach hoặc installs thành brand preference/retention nếu thiếu đo lường tương ứng |
| Case và thương hiệu | Case có nguồn định danh chiến dịch/năm/kết quả phù hợp; Novaon xuất hiện để giải thích cơ chế/kinh nghiệm, không thay lập luận bằng lời quảng bá năng lực |
| Súc tích và chuyển thể | Mỗi đoạn có vai trò, thuật ngữ được giải thích; caption/slide giữ nghĩa và dữ kiện, không thêm lời hứa mạnh hơn bài gốc |

QA vẫn dùng PASS/FAIL/NOT_VERIFIED/NOT_APPLICABLE theo 05/06. Sai claim đã đối chiếu được → FAIL; chưa kiểm được → NOT_VERIFIED và đóng gate bắt buộc. Các rubric trên cần rule_set_version và policy criterion khi triển khai; không thêm category QA mới ngoài FACT/BRAND/COPYRIGHT/COMPLIANCE hoặc tự tạo điểm tổng để bù lỗi blocker. Người dùng đánh giá giá trị chiến lược, mức cần sửa và khả năng sử dụng; model không tự chứng nhận bài “đáng chia sẻ”.

**Phân biệt ví dụ ứng dụng với case thật:** người dùng không cho phép case/số liệu giả định. Bài tham chiếu có các gợi ý minh họa triển khai; hệ thống không biến chúng thành chiến dịch đã xảy ra. Chỉ đưa ví dụ giả định vào bản mới khi brief cho phép rõ và có nhãn; nếu thiếu case thật thì nêu khoảng trống, không bịa nhãn hàng/KPI.

## 6. Kiểm tra nguồn có phạm vi ngày 2026-10-28

Đây là đối chiếu thủ công một số nguồn public để thiết kế acceptance cases, **không phải QA PASS cho ba bài**, không phải connector/backend đã chạy. Không gửi toàn văn bài hoặc tài liệu nội bộ cho model/provider. Các nguồn dưới có thể thay đổi; khi sản xuất cần lưu snapshot/locator/checksum phù hợp.

| Quan sát | Phạm vi bằng chứng / điều cần làm |
| --- | --- |
| PwC có 517 phản hồi Việt Nam; trang nêu 47% coi giá là yếu tố chính khi chọn thực phẩm | Đã mở [trang PwC](https://www.pwc.com/vn/vn/publications/vietnam-publications/voice-of-consumer-2025.html), mục Giới thiệu và Giá thực phẩm. Chỉ đối chiếu các claim này; chưa xác minh toàn bộ số liệu khác của bài. Không mở rộng mẫu thực phẩm thành toàn bộ hành vi FMCG |
| NIQ nêu Tết khoảng 20% giá trị FMCG năm, tăng trưởng giá trị Tết 2025 là 5%, kênh truyền thống được 84% người mua sắm Tết lựa chọn | Đã mở [bài NIQ ngày 30/6/2025](https://nielseniq.com/global/en/insights/analysis/2025/winning-vietnams-tet-holiday-optimizing-fmcg-growth-for-the-next-season/), các mục Consumers are spending smarter và Channel strategy. Giữ mốc dữ liệu 2025 và định nghĩa chỉ số; đây không phải số đo mùa lễ 2026 |
| Hai bài FMCG mô tả case Bảo Ngọc khác thông điệp/hoạt động | [Trang dự án Novaon](https://novaondigital.com/vi/banh-trung-thu-bao-ngoc/) có thông điệp trao tặng và thiệp tùy chỉnh; phần bối cảnh và giải pháp ghi năm khác nhau. Chưa tìm được căn cứ trong trang đã mở để xác nhận thông điệp ở bản Consumer Journey. Không kết luận case đó sai hoặc hai bài cùng một chiến dịch; cần source/version riêng và đối chiếu |
| Bản Bank–Finance nêu VietnamPlus, Visa, VNBA, VnExpress, VTV và Viettel Money nhưng file không chứa URL/locator tương ứng | Tạo evidence backlog cho từng claim. Tra cứu có tìm thấy các trang liên quan, chưa đối chiếu toàn bộ phát biểu/điều kiện/quyền sử dụng; giữ NOT VERIFIED cho phần chưa đủ bằng chứng, không coi tên publisher là citation đầy đủ |
| Framework 5 bước trong bài FMCG tự ghi là cấu trúc biên tập đề xuất | Đây là quan sát từ file, không phải xác minh framework độc lập. Bài Bank–Finance cũng phải ghi rõ xuất xứ/phiên bản và thẩm quyền gắn framework với Novaon trước khi dùng như tài sản thương hiệu đã công bố |

## 7. Bộ nghiệm thu đề xuất

Ba bài là **reference set về phong cách và tình huống QA**, chưa là benchmark đã chạy hoặc dữ liệu huấn luyện đã cấp quyền. Khi được giao kiểm thử, tạo brief synthetic từ đặc điểm tác vụ, đáp án kiểm claim tách khỏi input và nguồn được phép dùng; không sao chép dữ liệu khách hàng vào fixture. Bộ nhỏ gồm:

1. Bài Bank–Finance: phân biệt tăng giao dịch với brand preference; claim chưa có locator phải bị chặn; framework đề xuất không thành mô hình đã kiểm chứng.
2. Consumer Journey: không biến ba tâm thế thành ba giai đoạn tuyến tính; không đổi mẫu khảo sát thực phẩm thành đại diện mọi ngành hàng.
3. Framework FMCG: từng bước có đầu ra; nguồn ghi năm không nhất quán không được tự chọn một năm; case khác mô tả không bị trộn.
4. Biến thể social: giữ số liệu, qualifier và source article version; nguồn đúng vẫn không cho phép Social thêm fact ngoài bài gốc.
5. Revision: người dùng đổi thesis/dữ kiện sau duyệt outline; bản bài/social phụ thuộc bị stale và review lại, không dùng approval cũ.

Trước pilot, người dùng ghi thời gian nghiên cứu/viết/sửa của cách làm hiện tại. Khi chạy thật, đo thời gian người dùng trực tiếp thao tác, mức chỉnh sửa đến khi chấp nhận, số claim bắt buộc chưa xác minh, vòng revision, latency và chi phí mỗi brief. Có thể dùng 5–10 brief đại diện; số lượng và ngưỡng tiết kiệm/chất lượng/chi phí là đề xuất cần chốt, chưa có kết quả đo.

## 8. Việc cần làm tiếp theo

| Thứ tự | Đầu ra cần hoàn tất | Phụ thuộc / giới hạn |
| --- | --- | --- |
| 1 — Chốt brief và rubric | Một brief Bank–Finance dùng làm ca đầu, hướng bài, độ dài, số biến thể, bộ nguồn/case được phép, điều kiện duyệt | Có đủ bối cảnh để soạn; nguồn gốc framework, case bất nhất và tiêu chí còn thiếu phải được ghi OPEN |
| 2 — BASE-01/IAM-01 | DEC-007 đã chọn MariaDB/Flyway; BASE-01 fresh schema/auth được kiểm ở 21, quyền workspace cá nhân chưa triển khai | Giữ users/auth/Long; không xóa/tác động DB đang dùng; IAM cần giao lát cắt/quyền tương ứng |
| 3 — REG/TASK/ART/QA/AUD/USG/RUN | Schema/prompt versions, task/claim/source/artifact, gate và usage; kiểm luồng bằng synthetic fixture | Chỉ implementation khi được giao; không cần tạo hết 27 bảng hoặc 11 runtime class cùng lúc; fixture không là live E2E |
| 4 — Gemini và nguồn thật | Adapter qua LlmClient, model ID/SDK tương thích đã kiểm, tool grants, phép tìm/đọc nguồn và giới hạn chạy | Model cụ thể, API mode/tài khoản/quota/billing, dữ liệu được gửi và budget còn OPEN; không gọi paid model để tự chọn ngưỡng |
| 5 — UI tối thiểu và pilot | Nhập brief, duyệt outline, đọc claim/nguồn, xem diff/QA, sửa/duyệt, xuất bộ nội dung | Giữ yêu cầu Virtual Office dài hạn; ưu tiên thao tác biên tập trước phần đồ họa; publish/ads/email thật chưa nằm trong lát cắt |

Provider đầu tiên theo nhu cầu là Gemini; kiến trúc giữ adapter thay được. Không yêu cầu nhiều provider để bắt đầu; QA có context/rule/evidence độc lập, dùng cùng provider không tự chứng minh tính độc lập của kết luận. Việc bổ sung provider khác dựa trên eval và yêu cầu cụ thể sau này.

Các quyết định chưa chốt: model/budget/data policy; DB và dependency; nguồn/ownership framework; tài liệu case/asset rights; kênh/độ dài/số biến thể cụ thể; quyền và action policy; môi trường triển khai/retention. Các điểm này không chặn hoàn thiện docs; chúng phải được giải quyết trước phần implementation/live phụ thuộc tương ứng.

## 9. Đầu ra cụ thể của bước tiếp theo

Theo yêu cầu “Bạn làm tiếp đi”, lượt trước đã cụ thể hóa [brief/claim–nguồn/rubric Bank–Finance ở 19](19-bank-finance-editorial-acceptance.md) và [ADR DB/baseline ở 20](20-database-baseline-decision.md). Kết quả nguồn có phạm vi trong mục 6 ở trên là lượt đầu; 19 ghi chi tiết phát biểu nào đã đối chiếu được, phát biểu nào cần sửa hoặc thêm nguồn. Không nâng thành QA PASS toàn bài. Khi đó phương án PostgreSQL mới + Flyway chỉ là khuyến nghị PROPOSED, trước DEC-007 bên dưới.

Cập nhật sau đó cùng ngày: **DEC-007 đã chọn MariaDB + Flyway**, thay khuyến nghị PostgreSQL; [21](21-mariadb-flyway-baseline.md) ghi implementation BASE-01 và kiểm auth/migration trên DB tạm. Brief/rubric/workflow ở tài liệu này vẫn là đề xuất; chưa có runtime agent/LLM hoặc QA nội dung thật.
