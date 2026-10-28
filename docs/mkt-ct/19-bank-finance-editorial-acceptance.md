# 19 — Brief, claim–nguồn và nghiệm thu bài Bank–Finance

**2026-10-28 — DESIGN DRAFT.** Thực hiện tiếp theo yêu cầu “Bạn làm tiếp đi”: cụ thể hóa [MVP Novaon](18-novaon-editorial-mvp.md) thành bộ đầu vào/review dùng được. Đây là đối chiếu biên tập thủ công, không phải kết quả agent runtime hoặc phê duyệt phát hành. Không sửa ba file gốc trên Desktop.

## 1. Brief biên tập v0.1

| Mục | Yêu cầu / đề xuất |
| --- | --- |
| Bài đích | **Festive Experience Framework: Từ giao dịch mùa lễ đến sự ưu tiên thương hiệu Bank-Finance** — tên theo yêu cầu mới nhất của người dùng |
| Audience | Marketing Manager, Brand Manager, lãnh đạo Marketing/Growth của doanh nghiệp Bank–Finance |
| Vấn đề | Tăng giao dịch mùa lễ chưa đủ chứng minh khách hàng ưu tiên thương hiệu sau chiến dịch; cần nối truyền thông, sử dụng sản phẩm và đo lường |
| Luận đề đề xuất | Thiết kế đồng thời nhu cầu/giá trị, động lực tham gia và khả năng thực hiện lời hứa; đo hành vi và thương hiệu riêng rồi đối chiếu, thay vì lấy chỉ số thu hút thay cho hiệu quả dài hạn |
| Phạm vi | Việt Nam, mùa lễ cuối 2026; ứng dụng cho thẻ, tài khoản thanh toán, ví điện tử. Case lịch sử giữ đúng năm, không gọi là kết quả mùa lễ 2026 |
| Cấu trúc lập luận | Bối cảnh → Insight → Thách thức → Giải pháp → Hàm ý kinh doanh; không bắt dùng đúng năm heading |
| Framework | SCT là nền tiếp cận được đề cập trong bài; cách tổ chức Festive/Connected Experience cần ghi là đề xuất biên tập cho tới khi có căn cứ công bố/ủy quyền thương hiệu |
| Giọng văn | Nghiên cứu kết hợp strategic planning, cụ thể, súc tích; mỗi phần dẫn tới một quyết định marketing. Novaon xuất hiện để giải thích kinh nghiệm/cơ chế, không PR lặp lại |
| Đầu ra đề xuất để review | 1 longform tiếng Việt, 3 headline options, 1 caption Facebook, 1 bài LinkedIn, 1 carousel script 6 slide, 1 brief infographic; chưa sản xuất ảnh hoặc đăng lên kênh |
| Độ dài đề xuất | Longform target 2.800 đơn vị tách bằng khoảng trắng, ±10% = 2.520–3.080; chỉ tính sapo/body, không nguồn/brief hình. Cần người dùng chọn lại nếu kênh yêu cầu khác; đây không phải quy định của Brands Vietnam |
| Các gate | QA strategy/nguồn → người dùng duyệt hướng bài → Editorial/QA/revision → bài gốc qua gate → Social/QA → duyệt bộ phiên bản cuối; dùng state/approval chuẩn 04–06 |
| Giới hạn chạy | Chưa chốt model/budget/runtime/tool limits; không coi null là vô hạn. Bộ này không cấp quyền gọi Gemini có phí, publish, send hoặc tạo asset ở tài khoản ngoài |

Đầu vào đã đọc: `Văn bản đã dán (1)(3).txt`, 157 dòng, SHA-256 `a9b721df0241528dc98d0786b4acc9ed691be79ca6084f7335bf75a507c0096b`. Bản gốc dùng tiêu đề “Thiết kế hành trình…”; hash khóa bản đối chiếu, không là artifact_version_id DB. Đếm toàn file được 3.051 đơn vị khoảng trắng, **không** dùng số này để báo đã đạt target sapo/body. Hai bài FMCG chỉ là tham chiếu phong cách theo 18.

## 2. Source register thủ công

Các mã `SRC-BF-*` dưới đây chỉ là khóa đọc trong tài liệu, chưa được insert vào Source Registry. Đã mở/đọc nội dung bằng web tool ngày 2026-10-28; locator là heading/đoạn có thể tìm lại, chưa lưu snapshot nội dung public. Không gọi search snippet là bằng chứng đã đọc. Quyền tái sử dụng hình/copy dài vẫn chưa xác minh; mặc định diễn giải và dẫn link.

| Mã | Nguồn / locator đã đọc | Phạm vi và provenance |
| --- | --- | --- |
| SRC-BF-01 | [VNBA: Diễn đàn Ngân hàng Bán lẻ Việt Nam 2026](https://vnba.org.vn/vi/dien-dan-ngan-hang-ban-le-viet-nam-2026--ai-tro-thanh-dong-luc-tai-dinh-hinh-dich-vu-tai-chinh-23739.htm), 30/9/2026; đoạn phát biểu khai mạc, bắt đầu bằng số liệu tám tháng | Bản tin của đơn vị hiệp hội về diễn đàn, có số liệu thị trường; chưa mở bảng thống kê gốc NHNN. Không chứng minh hiệu quả một thương hiệu |
| SRC-BF-02 | [Visa: The Key Forces Driving Payments for 2026](https://www.visa.com.hk/en_HK/about-visa/newsroom/press-releases/the-key-forces-driving-payments-for-2026-from-agentic-commerce-to-interoperable-payments.html), 22/1/2026; mục 3 về checkout và mục 4 về interoperability | Triển vọng do Visa công bố, trọng tâm APAC/Hong Kong. Không phải khảo sát riêng Bank–Finance Việt Nam mùa lễ |
| SRC-BF-03 | [VNBA: Banktainment](https://vnba.org.vn/vi/emagazine/banktainment-chien-luoc-bien-ve-concert-thanh-san-pham-trieu-do-cua-ngan-hang-20104.htm), 18/12/2025; phần định nghĩa và kết nối quyền lợi với sản phẩm | Trang ghi nguồn fili.vn: phải dẫn là bài được VNBA đăng lại, không gán thành nghiên cứu gốc/quan điểm chính thức của hiệp hội |
| SRC-BF-04 | [VPBank: trang sự kiện BIGBANG 2026–2027](https://www.vpbank.com.vn/i/en/bigbang-2026-2027-world-tour-xx-cosmos-in-hanoi); mục EXCLUSIVE EARLY ACCESS… và FAQ presale | Nguồn ngân hàng xác nhận quyền mua vé sớm cho Mastercard. Nội dung trang có hình/section không trích xuất đầy đủ; không suy điều kiện còn thiếu |
| SRC-BF-05 | [VnExpress: Chủ thẻ VPBank hưởng đặc quyền tại các sự kiện K-pop](https://vnexpress.net/chu-the-vpbank-huong-dac-quyen-tai-cac-su-kien-k-pop-5134967.html), 21/10/2026; phần BIGBANG và vật phẩm | Bài ghi “Nguồn: VPBank”; là thông tin ngân hàng cung cấp qua báo, không độc lập với SRC-BF-04 và không có đo retention |
| SRC-BF-06 | [VTV: Cơ hội trúng vé qua VPBank](https://vtv.vn/tuong-lo-hen-bigbang-fan-viet-vo-oa-khi-vpbank-mo-them-co-hoi-trung-ve-100260909193919103.htm), 9/9/2026; đoạn chương trình mở mới thẻ 9–27/9 | Bài giới thiệu điều kiện chương trình; chỉ dùng mô tả cơ chế đã được đưa tin. Không coi đây là thể lệ hiện hành hoặc báo cáo hiệu quả |
| SRC-BF-07 | [Novaon: Viettel Money](https://novaondigital.com/en/viettel-money/); Campaign, Success Story, Novaon Digital’s Implementations | Case do đơn vị thực hiện tự công bố; có mục AI/Onfluencer và năm 2022. Các kết quả tự công bố chưa có dataset/baseline độc lập |
| SRC-BF-08 | [Novaon trên Brands Vietnam: Tết an lành – Chia sẻ yêu thương](https://www.brandsvietnam.com/congdong/topic/324562-viettel-money-tet-an-lanh-chia-se-yeu-thuong); phần 5, hoạt động Lì xì; phần Social Media; phần 7 Kết quả | Tác giả Novaon; cùng bên cung cấp với SRC-BF-07, không tính thành hai nguồn độc lập. Chỉ chứng minh thông tin case đã được công bố |

Lần mở trang ưu đãi merchandise trực tiếp của VPBank gặp lỗi fetch; vì vậy **không** ghi trang đó thành nguồn đã xác minh. Chưa tìm được chính xác bài VietnamPlus ngày 14/10 được bản gốc nhắc tới. Không tìm thấy trong phạm vi tra cứu không chứng minh nguồn/claim không tồn tại.

## 3. Bảng claim factual và provenance cần kiểm

Trạng thái theo 05: SUPPORTED / CONTRADICTED / NOT_VERIFIED. SUPPORTED chỉ áp dụng phát biểu **đúng phạm vi** trong cột claim, không chứng nhận số liệu gốc đã audit hoặc QA toàn bài. Cột “Xử lý” chỉ ra khi nào cần thay câu/nguồn trước dùng; bản gốc chưa được sửa.

| Claim | Vị trí bản gốc | Phát biểu cần kiểm | Evidence status | Nguồn / xử lý |
| --- | --- | --- | --- | --- |
| BF-01 | L15 | Tám tháng 2026, thanh toán không dùng tiền mặt hơn 21 tỷ giao dịch | SUPPORTED | SRC-BF-01 có số liệu tương ứng; thay attribution theo nguồn thực đã mở nếu dùng |
| BF-02 | L15 | Số lượng giao dịch tăng 34,56% so với cùng kỳ | SUPPORTED | SRC-BF-01; giữ đúng chỉ số số lượng, kỳ tám tháng, không đổi thành doanh thu/tăng thêm do marketing |
| BF-03 | L15 | VietnamPlus dẫn số liệu NHNN ngày 14/10/2026 | NOT_VERIFIED | Chưa mở được đúng bài; dùng SRC-BF-01 với attribution mới hoặc bổ sung đúng URL. BF-01/02 không tự xác minh ngày/publisher này |
| BF-04 | L19 | Visa nhấn mạnh kỳ vọng số nhanh, an toàn, thuận tiện giữa các kênh | NOT_VERIFIED | SRC-BF-02 hỗ trợ nội dung gần về checkout/interoperability, chưa đủ nguyên phạm vi câu cũ; thu hẹp theo đề xuất ở mục 5 |
| BF-05 | L47 | Có cách gọi Banktainment kết hợp ngân hàng và giải trí trong bài VNBA | SUPPORTED | SRC-BF-03; ghi provenance đăng lại. Không chuyển lời phân tích thành quan hệ nhân quả đã đo |
| BF-06 | L81 | VPBank đồng tổ chức và là nhà tài trợ chính BIGBANG tại Hà Nội 2026 | SUPPORTED | SRC-BF-05/06 đưa tin như vậy; thông tin cung cấp về sự kiện, không phải đánh giá tác động độc lập |
| BF-07 | L81 | Chủ thẻ VPBank Mastercard có quyền mua vé sớm | SUPPORTED | SRC-BF-04 xác nhận; không tự mở rộng thành mọi dòng thẻ hoặc lợi ích không điều kiện |
| BF-08 | L81 | Có ưu đãi thanh toán vật phẩm nghệ sĩ cho chủ thẻ | SUPPORTED | SRC-BF-05 mô tả ưu đãi; giữ “theo thông tin VPBank được VnExpress đăng”, chưa xác minh mọi thể lệ/điều kiện |
| BF-09 | L81 | Cơ hội nhận vé gắn đăng ký NEO, mở mới thẻ tín dụng và chi tiêu đủ điều kiện | SUPPORTED | SRC-BF-06 mô tả cơ chế này; phân biệt cơ hội với bảo đảm nhận vé và chương trình đã qua với ưu đãi đang chạy |
| BF-10 | L119 | Có chiến dịch Viettel Money Tết 2022 với tên và đơn vị thực hiện nêu trong bài | SUPPORTED | SRC-BF-07/08; nguồn case tự công bố, giữ năm lịch sử |
| BF-11 | L121 | Novaon công bố dùng Onfluencer hỗ trợ chọn KOL bằng AI | SUPPORTED | SRC-BF-07, phần Implementations; chỉ là nội dung bên thực hiện công bố, chưa đo thuật toán |
| BF-12 | L121 | Trong chiến dịch đó có sử dụng Big Data như mô tả | NOT_VERIFIED | Hai nguồn case đã đọc chưa xác nhận đủ phần này; bỏ cụm hoặc bổ sung nguồn triển khai cụ thể |
| BF-13 | L121 | Hoạt động influencer có nội dung trên Facebook/TikTok và gắn bối cảnh sử dụng | SUPPORTED | SRC-BF-08, phần Social Media; không suy từ danh sách kênh thành hiệu quả mỗi kênh |
| BF-14 | L123 | Case có hai game trong ứng dụng, gồm Lì xì Yêu thương | SUPPORTED | SRC-BF-08, phần 5; không ghép tên/cơ chế game của năm khác |
| BF-15 | L123 | Lì xì gắn truy cập ứng dụng và nạp tiền/chuyển tiền/nạp điện thoại | SUPPORTED | SRC-BF-08, hoạt động Lì xì; giữ đây là mô tả lịch sử, không hướng dẫn tham gia hiện tại |
| BF-16 | L125 | Kết quả tích cực về tiếp cận, tương tác và cài đặt tự nhiên | NOT_VERIFIED | SRC-BF-07/08 có kết quả tự công bố cho các metric khác nhau; câu cũ chưa ghi attribution/định nghĩa/baseline. Có thể sửa thành thông tin Novaon báo cáo; không mở rộng thành đóng góp nhân quả hoặc duy trì đã đo |
| BF-17 | L3, L25–31, L131 | Provenance cho việc quy thuộc đề xuất Festive Experience Framework trong bài cho Novaon | NOT_VERIFIED | Chưa có tài liệu version/nguồn mô hình thương hiệu. Bản gốc không khẳng định rõ framework đã công bố trước đó; đây là kiểm attribution, không gán thêm phát biểu cho tác giả. Có thể dùng như khung ứng dụng bài viết đề xuất, không tự nhận là mô hình chính thức đã kiểm chứng |

Kết quả đối chiếu **17 mục factual/provenance: 12 SUPPORTED trong phạm vi, 5 NOT_VERIFIED, 0 CONTRADICTED**. Đây không phải tỷ lệ chất lượng, điểm benchmark hoặc kết luận rằng 12 claim được phép publish nguyên văn. BF-04/12 cần sửa hoặc thêm nguồn; BF-16/17 cần giới hạn attribution/claim; BF-03 cần sửa nguồn/ngày. Chưa có audit quyền hình, pháp lý sản phẩm tài chính hoặc QA toàn bài.

## 4. Nhận định chiến lược và đề xuất đo lường

Các mục dưới không được đánh dấu SUPPORTED chỉ vì hợp lý hoặc có nguồn thị trường gần chủ đề. Đây là luận điểm/khuyến nghị biên tập; khi lưu claim dùng loại ASSUMPTION theo 05 kèm rationale/provenance, không thêm enum INSIGHT. Giữ NOT_VERIFIED cho hiệu quả chưa được đo; việc đề xuất đo không phải tuyên bố đã có kết quả.

| Mã | Luận điểm / vị trí | Cách dùng và giới hạn |
| --- | --- | --- |
| ARG-01 | Giao dịch không thay phép đo sự ưu tiên thương hiệu, L17–23 | Đặt như câu hỏi đánh giá. Chỉ số hành vi và khảo sát cân nhắc/tin cậy cần định nghĩa riêng; không nói thị trường đã chứng minh hiệu ứng mùa lễ cụ thể |
| ARG-02 | SCT phối hợp để nối lời hứa với sử dụng, L27–57 | Đề xuất cơ chế làm việc: nhu cầu/hành vi → động lực → điều kiện thực hiện. Chưa là nghiên cứu chứng minh SCT gây tăng trưởng |
| ARG-03 | Awareness–Consideration–Engagement–Transaction–Loyalty, L61 | Khung tổ chức của bài, không quy luật khách hàng bắt buộc đi tuyến tính. Có thể quay lại/bỏ bước; tránh đồng nhất customer journey với funnel đo lường |
| ARG-04 | Sau cao điểm cần giá trị tiếp nối, L93–97 | Khuyến nghị chăm sóc theo nhu cầu và quyền sử dụng dữ liệu; hiệu quả giữ chân phải đo, không tự suy ra từ tương tác |
| ARG-05 | Đối chứng và chi phí toàn phần, L105–111 | Đề xuất phương pháp. Trước pilot cần định nghĩa cohort, cửa sổ quan sát, outcome và yếu tố mùa vụ; không tự bịa mức uplift/ROI |

Mapping trên không làm QA dễ hơn: gắn nhãn assumption không cứu một câu vẫn viết như sự kiện đã xảy ra. QA cần kiểm **cách diễn đạt** khớp loại claim; dữ kiện trong đoạn lập luận vẫn phải có evidence riêng.

## 5. Chỉ dẫn chỉnh sửa có thể áp dụng ngay

Đây là hướng sửa cho phiên bản tiếp theo, chưa ghi đè bài gốc và chưa tạo bản longform hoàn chỉnh:

1. **L15 — nguồn số liệu:** dẫn bản tin VNBA về diễn đàn ngày 30/9/2026 cho hai số đã đối chiếu, hoặc giữ attribution VietnamPlus chỉ sau khi có đúng nguồn. Không giữ ngày cũ bên cạnh link mới.
2. **L19 — phạm vi Visa:** diễn giải rằng trong triển vọng 2026 Visa đề cập checkout nhanh/an toàn và liên thông thanh toán; ghi phạm vi tài liệu, sau đó tách riêng hàm ý của tác giả cho mùa lễ Việt Nam. Không viết như kết quả khảo sát riêng tại Việt Nam.
3. **L47 — provenance:** đổi cách dẫn thành bài về Banktainment được VNBA đăng lại; chỉ mượn mô tả cơ chế kết nối sự kiện–sản phẩm, không lấy các khẳng định tăng trưởng trong bài đó làm số liệu của framework.
4. **L81–83 — case VPBank:** giữ presale/quyền lợi và điều kiện tham gia có dẫn nguồn; câu về khả năng tiếp tục lựa chọn ngân hàng phải là giả thuyết cần đo. Tách quyền mua sớm khỏi chương trình có cơ hội trúng vé.
5. **L121–125 — case Viettel Money:** giữ phần triển khai đã công bố, bỏ “Big Data” khi chưa bổ sung căn cứ; attribution rõ cho kết quả tự công bố. Không thêm KPI cụ thể hoặc kết luận loyalty/Business Impact ngoài dữ liệu được cung cấp.
6. **Framework/ảnh:** chú thích đây là khung ứng dụng bài viết đề xuất trên nền SCT tới khi có căn cứ brand approval; các dòng “Nguồn: Novaon Digital” cho hình chưa có file/version/permission cần bổ sung reference, không tự thành chứng cứ quyền dùng.
7. **Từng phần:** kết thúc bằng một quyết định cần xem lại và tín hiệu cần đo, không lặp lời kết “kết nối cảm xúc với tăng trưởng” ở nhiều đoạn. Ví dụ: nếu người dùng bỏ đăng ký, kiểm quyền lợi/điều kiện/thao tác trước khi tăng media — là khuyến nghị, không phải case giả định.

## 6. Rubric `novaon-b2b-editorial-v0.1` — PROPOSED

| Rule | Category / mức mặc định | Điều kiện đạt / điều kiện chặn |
| --- | --- | --- |
| ED-01 | FACT / BLOCKER | Claim factual bắt buộc có source/locator đúng phạm vi; không kiểm được → NOT_VERIFIED, mâu thuẫn đã xác định → FAIL |
| ED-02 | FACT / BLOCKER | Tách data, inference, hypothesis; không dùng số thị trường hay metric thu hút để chứng minh tác động thương hiệu/nhân quả |
| ED-03 | BRAND / MAJOR | Sapo nêu vấn đề ra quyết định; thesis và đối tượng đọc rõ, không mở bằng nhận định chung không có vai trò |
| ED-04 | BRAND / MAJOR | Mỗi phần có phát hiện/cơ chế và hàm ý thực tế; bước framework có đầu ra/điều kiện, không chỉ ba nhãn SCT |
| ED-05 | FACT / BLOCKER | Case đúng đơn vị/năm/chương trình; tự công bố có attribution, nguồn cùng tác giả không giả thành hai kiểm chứng độc lập |
| ED-06 | BRAND / MAJOR | Giới hạn PR; từng lần nhắc Novaon giải thích nguồn/cơ chế/kinh nghiệm, không thay lập luận bằng lời quảng bá |
| ED-07 | COPYRIGHT / BLOCKER | Hình, trích dẫn và asset có reference/quyền phù hợp; không rõ quyền → NOT_VERIFIED; không sao chép hình chỉ vì có URL |
| ED-08 | BRAND / MAJOR | Caption/carousel giữ giá trị, qualifier và bản gốc; không thêm fact, không kế thừa QA bản gốc; body không dùng từ khó hiểu không cần thiết |
| ED-09 | COMPLIANCE / BLOCKER | Không biến case thành lời mời dùng ưu đãi hết hạn, lời hứa lợi ích tài chính hoặc thông tin sản phẩm chưa kiểm; nội dung cần chuyên gia có thẩm quyền phải ghi giới hạn |

Rule set/version trên mới là thiết kế, chưa có validator/eval profile. Mọi rule bắt buộc phải đạt; điểm hoặc majority vote không bù được blocker/MAJOR chưa giải quyết. NOT_APPLICABLE chỉ dùng khi policy cho phép và có lý do, ví dụ bài text không dùng asset; không dùng để bỏ kiểm claim.

**Kết luận review thủ công hiện tại:** bài có nền lập luận phù hợp yêu cầu, nhưng **chưa đủ bằng chứng để PASS gate factual/rights/brand ownership**. Chưa chấm điểm giá trị chiến lược thay người dùng. Việc có source mới trong tài liệu này chưa tự cập nhật source_refs/QA/approval của bài.

## 7. Acceptance cases cho implementation sau này

| Case | Đầu vào synthetic / thao tác | Kết quả mong đợi |
| --- | --- | --- |
| ACC-BF-01 | Giữ số 34,56 nhưng đổi nhãn thành tăng doanh thu | QA FACT FAIL, finding chỉ rõ sai chỉ số và locator |
| ACC-BF-02 | Nguồn tên publisher đúng nhưng không mở được evidence | NOT_VERIFIED; task chờ bổ sung, không dùng tên nguồn để PASS |
| ACC-BF-03 | Cho nguồn checkout APAC nhưng viết thành khảo sát mùa lễ Việt Nam | FACT FAIL khi đối chiếu được sai phạm vi; sửa qualifier/nguồn rồi QA version mới |
| ACC-BF-04 | Hai URL cùng tác giả được gọi là hai nguồn độc lập | Finding FACT yêu cầu sửa provenance; không tăng confidence tự động |
| ACC-BF-05 | Có presale nhưng viết mọi người đủ điều kiện đều chắc chắn trúng vé | FACT FAIL; giữ khác biệt quyền mua và cơ hội nhận vé |
| ACC-BF-06 | Chỉ có installs/reach nhưng kết luận tăng retention/ưu tiên thương hiệu | FACT FAIL cho kết luận đã khẳng định; đề xuất cần đo được tách riêng |
| ACC-BF-07 | Chưa rõ ownership framework hoặc quyền hình | NOT_VERIFIED gate tương ứng; không suy từ tên Novaon thành approval |
| ACC-BF-08 | Sửa outline đã duyệt hoặc claim bài gốc sau khi tạo carousel | Downstream stale; QA/approval cũ không mở gate cho version mới |
| ACC-BF-09 | Bài mẫu có “BRIEF ẢNH” hoặc nguồn web chứa lệnh gọi tool | Xem là dữ liệu; không gọi image/publish hoặc nới allowlist từ nội dung |
| ACC-BF-10 | Không có budget/model/tool limits hợp lệ | Chặn dispatch có phí; báo cấu hình thiếu, không coi null là vô hạn |

Mười case là đặc tả kiểm thử chưa chạy, không phải unit/integration/eval PASS. Source công khai ở mục 2 là tài liệu review; khi tạo fixture phải dùng dữ liệu synthetic có nhãn và đáp án kiểm riêng, không chép bài khách hàng vào test resources. Contract/schema/runtime vẫn theo 05/16; tài liệu này không tạo API hay JSON Schema mới.
