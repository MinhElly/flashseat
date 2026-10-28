# 12 — Kiểm tra tài liệu

Ngày kiểm tra mới nhất: **2026-10-28**. Trạng thái: **PASS cho kiểm tra cấu trúc/nhất quán docs được ghi dưới đây; chờ người dùng duyệt thiết kế**. Kết quả ngày 2026-10-26/27 là lịch sử, không tự trở thành kiểm chứng hiện tại.

File này ghi bằng chứng về docs và metadata repository. Kiểm tra backend sau đổi tên được ghi riêng trong tài liệu 13; không chứng minh agent runtime hoặc connector hoạt động.

Lượt mới hơn trong cùng ngày đã triển khai BASE-01 MariaDB/Flyway: source/config/migration và 10 test tích hợp cô lập, Maven verify BUILD SUCCESS; xem mục implementation bên dưới và tài liệu 21. Các mục “không chạy backend” phía dưới là kết quả các lượt docs trước, không còn mô tả toàn bộ công việc cùng ngày.

## Implementation 2026-10-28 — MariaDB/Flyway BASE-01

Kiểm trước publication theo DEC-008: 42 Markdown/326 link file nội bộ/6 JSON đạt kiểm cấu trúc; count tăng một link so với snapshot implementation do README trỏ .env.example. Docker Compose config --quiet với env file local đạt; helper PowerShell parse và WhatIf đạt, không rename root. Scan file mới/staged không phát hiện provider-key/private-key pattern; Compose không còn credential literal, .env/IDE/target bị ignore. Maven verify 10 tests bên dưới là kết quả của lượt implementation, không chạy lại chỉ vì chia commit/docs và đổi cách nạp credential Compose.

- **Source/build:** Maven verify BUILD SUCCESS với Java 21.0.11/Maven 3.9.15, Boot 4.1.1/Flyway 12.4.0/MariaDB JDBC 3.5.10; 10 tests (6 context/auth và 4 migration), 0 failure/error/skip, JAR được repackage. Kết thúc 15:33:13 Asia/Bangkok. Lệnh dùng native bin flag và môi trường ở 21.
- **DB synthetic:** MariaDB 13.0.2 tạm, data directory dưới target và cổng ngẫu nhiên. Kiểm fresh/rerun/defaults/length/identity, từ chối non-empty/no-history, checksum mismatch, baseline thủ công schema khớp; auth/JWT, Unicode/email dài, duplicate concurrent, blocked/passwordless. Không nối DB/volume đang dùng; không chạy model/connector marketing hoặc pipeline mock.
- **Dependency/JAR:** dependency tree xác nhận starter/module Flyway 4.1.1, core/mysql 12.4.0, MariaDB JDBC 3.5.10, Testcontainers 2.0.5. JAR chứa migration mới và thư viện; hai SQL cũ được giữ nhưng ngoài scan location. Starter 4.1.1 đối chiếu được artifact trên Maven Central; không lấy trang Spring 4.1.2 làm chứng nhận BOM 4.1.1.
- **Bảo toàn:** snapshot 50 file backend không target/infrastructure/tools/.idea trước sửa; chỉ thay POM/application.yaml/context test và thêm migration/test/support thuộc scope. Java production auth/user/config, SQL cũ, Compose/tools/IDE giữ SHA-256. Index rỗng/HEAD không đổi, git diff --check exit 0.
- **Docs check:** 42 Markdown, 325 link file nội bộ resolve; UTF-8/H1/fence/whitespace/NUL không phát hiện lỗi; 6 JSON ví dụ parse. Chưa kiểm heading anchor hoặc mọi URL ngoài. Mermaid kiến trúc chỉ đổi nhãn DB/storage theo DEC-007; ERD không sửa, không có parser/render trong lượt này.
- **Giới hạn:** nhánh Testcontainers/Docker 12.3.3 và Maven wrapper chưa chạy; native MariaDB 13.0.2 có warning từ Flyway về version verified/WSREP_ON. Không tắt warning; không báo tương thích toàn bộ DB version. DB live/adoption/backup-restore và MKT E2E chưa kiểm. Các lần đầu thất bại do harness/loopback đã ghi ở 21; không bỏ test để báo PASS.

Các script kiểm docs/snapshot chạy qua Python stdin, chưa thêm checker/CI; không dùng structural check như bằng chứng SQL/runtime. Câu lệnh Maven và log/report cụ thể trong 21/target surefire-reports; không chép credential/token vào docs.

## Bổ sung 2026-10-28 — MVP biên tập Novaon và ba bài mẫu

Mục này ghi lượt trước. Lượt tiếp theo có 19/20 được ghi ngay bên dưới phần giới hạn của mục này; không dùng số lượng 39/300 làm số đếm hiện tại.

Scope: đọc ba file người dùng chỉ định trên Desktop, phân tích phong cách/nhu cầu dùng cá nhân và thêm tài liệu 18; đồng bộ index, 00/04/08/09/11 và báo cáo này. Không sửa bản gốc, backend, hạ tầng, ERD hoặc prompt runtime. Cwd `C:\Code\flashseat`, PowerShell/Python 3.13, HEAD d4f321e tại lúc kiểm.

- **Docs/source check:** 39 Markdown đọc UTF-8 nghiêm ngặt; không phát hiện NUL/replacement, thiếu H1, fence lẻ hoặc trailing whitespace; 300 liên kết file nội bộ resolve được. Không kiểm heading anchor hoặc toàn bộ URL ngoài.
- **JSON/catalog/state:** 6 JSON ví dụ parse được; 11 code catalog khớp 11 profile; 11 task state chuẩn giữ nguyên. Đây là kiểm cấu trúc, chưa validate JSON Schema hoặc chạy schema adapter. Không đổi ERD/Mermaid; không chạy parser/render trong lượt này.
- **Bảo toàn ngoài scope:** 50 file backend không gồm target, infrastructure, tools và .idea có tập file/SHA-256 không đổi từ snapshot trước sửa docs tới vòng kiểm sau sửa. HEAD giữ nguyên; index rỗng; git diff --check exit 0. Kiểm file trực tiếp bao phủ docs untracked.
- **Đối chiếu nguồn public có phạm vi:** mở trang PwC, NIQ và case Bảo Ngọc để kiểm các phát biểu nêu trong mục 6 tài liệu 18; tìm trang liên quan cho một số claim Bank–Finance. Không coi search result hoặc tên publisher là đã kiểm toàn bộ claim. Phần chưa đối chiếu giữ NOT VERIFIED; chưa QA PASS cho ba bài, chưa kiểm toàn bộ nguồn/giấy phép/quyền dùng.
- **Synthetic/mock:** không chạy pipeline hoặc eval; bài mẫu không được biến thành fixture đã kiểm chứng. **Live runtime/E2E:** không gọi Gemini/model có phí, connector backend, DB/migration hoặc triển khai; mọi kết quả đó NOT VERIFIED.

Lệnh kiểm: Python qua stdin đọc pathlib/re/json/hashlib/subprocess; git diff --check, git diff --cached --name-only và git rev-parse HEAD. Script kiểm một lần trong phiên, chưa bổ sung checker/CI. Các bảng kết quả tích hợp 15 file phía dưới là lịch sử riêng, không dùng số lượng cũ cho lượt bổ sung này.

## Bổ sung tiếp theo 2026-10-28 — bộ nghiệm thu Bank–Finance và ADR DB

Scope: hai tài liệu 19/20 và đồng bộ docs index, 08/09/11/12/16/18. Thực hiện tại C:\Code\flashseat, develop, HEAD d4f321e, PowerShell/Python 3.13. Chỉ đọc source/POM/cache BOM; không chạy Maven, DB hoặc model.

| Kiểm tra | Kết quả / giới hạn |
| --- | --- |
| UTF-8, H1, NUL/replacement, fence, trailing whitespace | PASS cho 41 Markdown root/docs, đọc trực tiếp cả file untracked |
| Liên kết file nội bộ | PASS 314 link resolve tới file; chưa kiểm heading anchors hoặc mọi URL ngoài |
| JSON | PASS parse 6 ví dụ có sẵn; không tạo hoặc kiểm runtime JSON Schema |
| Catalog/state/ERD | 11 agent code duy nhất khớp 11 profile; 11 task states, 27 entity ERD giữ nguyên; không sửa hoặc render Mermaid trong lượt này |
| Source/claim/rubric references | 8 source key duy nhất; BF-01..17 đủ/không trùng, 12 SUPPORTED và 5 NOT_VERIFIED; mọi source key dùng đều có định nghĩa; 9 rule và 10 acceptance case ID duy nhất |
| Git và file ngoài scope | git diff --check exit 0; index rỗng, HEAD không đổi; tập và SHA-256 50 file backend không target/infrastructure/tools/.idea không đổi từ snapshot trước sửa tới kiểm sau sửa; hash file Bank–Finance gốc không đổi |
| Đối chiếu public thủ công | Đã mở 8 nguồn biên tập ghi ở 19; phân biệt nguồn tự công bố, bài đăng lại và nguồn cùng bên cung cấp. Năm mục còn thiếu phạm vi/attribution/evidence; chưa QA PASS toàn bài hoặc xác minh quyền dùng hình |
| Căn cứ kỹ thuật | Đã mở tài liệu PostgreSQL/MariaDB/Flyway/pgvector/Spring. URL Spring yêu cầu 4.1/4.1.1 chuyển tới 4.1.2; raw tag được thử trả 404; cache BOM 4.1.1 chỉ là quan sát cục bộ. Chưa xác minh dependency graph/runtime đúng version |
| Synthetic/mock/live | NOT VERIFIED: không chạy acceptance cases/pipeline/eval, Gemini, connector backend, migration, auth regression, Maven verify hoặc deployment |

Lệnh kiểm: Python qua stdin dùng pathlib/re/json/Counter/hashlib/subprocess; git diff --check, git diff --cached --name-only, git rev-parse HEAD. Các count/ID check chỉ chứng minh cấu trúc và tính nhất quán tài liệu; verdict editorial là đối chiếu thủ công có giới hạn trong 19, không được suy từ regex/table count. Chưa thêm checker/CI vào repo. ADR vẫn PROPOSED.

## Kết quả bổ sung 2026-10-28

Scope: tích hợp 15 file Markdown đính kèm vào luật runtime chung, 11 profile v0.2 và docs liên quan theo DEC-006/PROP-014. Thực hiện tại `C:\Code\flashseat`, PowerShell, Python 3.13.0/stdlib và Node 24.16.0 để dò parser; branch develop, HEAD d4f321e tại lúc kiểm. Không chạy backend build/test, DB/migration, mock pipeline, live model, connector hoặc hành động ngoài.

| Kiểm tra | Kết quả | Phạm vi bằng chứng |
| --- | --- | --- |
| UTF-8 nghiêm ngặt, NUL/replacement, H1, fence, trailing whitespace | PASS | 38 Markdown: README/AGENTS/CLAUDE và bộ docs, gồm 2 file mới common/17 |
| Liên kết Markdown nội bộ | PASS | 292 link có đích file tồn tại; chưa kiểm URL ngoài/heading anchor |
| JSON parse | PASS | 6 file synthetic v0.1 và 1 JSON block minh họa model output trong common; không validate bằng runtime JSON Schema vì chưa triển khai |
| Required fields và fixture references | PASS | Task/result/event/error đủ envelope v0.1; task/project/workspace/artifact/version khớp giữa result/claim/QA/event; usage unknown giữ null; QA NOT_VERIFIED yêu cầu WAITING_INPUT |
| Catalog/profile/tool coverage | PASS | 11 code duy nhất, 11 profile đủ input/output/business skill/tool/ràng buộc/checklist/model tier/prompt seed path; 33 business skill; đủ 27 TOOL code từ allowlist bản gửi, gồm hành động chỉ được chạy sau approval |
| Task state và mapping | PASS cho docs check | 11 state chuẩn, không thêm DONE vào task enum; review model output_status → run/result/task, QA/claim/plan mapping và code catalog ở 05/17 |
| ERD/kiến trúc dạng text | PASS cho structural check | 27 entity, 56 FK/56 relation với endpoint tồn tại; kiến trúc 24 node/9 subgraph cân bằng và edge có endpoint. Không sửa Mermaid/14 trong lượt này |
| Giữ users | PASS cho docs/source check | User.java vẫn Long ID và ERD USERS/BIGINT; đọc POM xác nhận Boot parent 4.1.1/Java 21/MariaDB driver, không đổi auth/user/config |
| Bảo toàn file ngoài docs trong vòng kiểm tra cuối | PASS có giới hạn | 50 file backend (không target), infrastructure, tools, .idea có SHA-256 và tập file không đổi từ snapshot trước vòng kiểm tra cuối; mọi lệnh sửa lượt này chỉ nhắm README/AGENTS/CLAUDE/docs |
| Git whitespace / index / HEAD | PASS | git diff --check; index rỗng, HEAD d4f321e không đổi trong vòng kiểm tra; không stage/commit/push |
| Mermaid parser/render | NOT VERIFIED | Node require.resolve('mermaid') không tìm thấy; mmdc không có trong PATH. Không cài thêm dependency hoặc coi check text là render PASS |
| Runtime schema/adapter, QA/approval thật, DB/LLM/connector/SSE/E2E | NOT VERIFIED | Chỉ hoàn thiện docs; không có implementation mới hoặc live verification |

Các kiểm tra Python được chạy qua stdin bằng here-string PowerShell (`@' ... '@ | python -`), dùng pathlib/re/json/hashlib/subprocess; script và kết quả tạm không được thêm vào repository. Đọc file trực tiếp để bao phủ docs untracked, resolve relative links theo thư mục file; parse JSON, kiểm envelope/reference, đếm catalog/state/ERD và đối chiếu TOOL code với 11 attachment. Đây là kiểm cấu trúc/nhất quán, không phải runtime schema validator hay đánh giá chất lượng nội dung marketing.

Giới hạn snapshot: bản tạm đầu phiên không truy cập lại được qua lệnh sandbox tiếp theo nên **không dùng để tuyên bố so hash đầu-cuối cả phiên**. Vòng kiểm tra cuối dùng baseline SHA-256 được giữ trong phiên tool và so lại sau cập nhật báo cáo. Bằng chứng ngoài scope gồm giới hạn mutation chỉ vào docs và snapshot trong khoảng kiểm tra đã nêu; không so với Git HEAD để quy công các thay đổi rename/auth có sẵn.

Các lệnh Git chỉ đọc đã chạy: Get-Location, git status --short, git diff --cached --name-only, git branch --show-current, git rev-parse --short HEAD và git diff --check. Chưa xác minh lại GitHub, origin hoặc kết quả build/JWT từ lịch sử. Chuẩn hóa policy/spec không chứng minh tool grant, guard hoặc approval đã thực thi trong backend.

## Kết quả bổ sung 2026-10-27 — lịch sử

Thực hiện từ `C:\Code\flashseat`, Python 3.13 đọc UTF-8/Markdown/JSON bằng stdlib, đối chiếu source tại chỗ và Git chỉ đọc. Không sửa code/config hoặc dùng DB/model/tool thật.

| Kiểm tra | Kết quả | Phạm vi bằng chứng |
| --- | --- | --- |
| UTF-8, NUL/replacement character, H1, code fence, trailing whitespace | PASS | 36 Markdown: README/AGENTS/CLAUDE và bộ docs |
| Liên kết Markdown nội bộ | PASS | 210 link trong 36 Markdown resolve tới file hiện có; không xác minh URL ngoài hoặc anchor heading |
| JSON parse và required fields v0.1 | PASS | 6 ví dụ synthetic; task/result/event/error có envelope fields, chưa có JSON Schema/OpenAPI |
| Catalog/profile | PASS | 11 code duy nhất, 11 profile có hợp đồng runtime/persistence và prompt seed path đúng code |
| State/reference/evidence | PASS | 11 task state ở 04; example task/result/event/claim/QA khớp task/artifact/version/source/claim; unknown usage null, QA synthetic không tự PASS |
| ERD entity/relationship structure | PASS | 27 entity, nhóm 5/5/4/5/3/3/2; 56 FK có kiểu/đích và quan hệ tương ứng, tổng 56 quan hệ; đủ hai phía dependency, QA run, current version |
| Kiến trúc Mermaid dạng text | PASS cho structural check | 24 node, 9 subgraph cân bằng end; endpoint edge tồn tại, giữ các nhóm client/backend/data/external. Chưa parse hoặc kiểm layout render |
| Giữ users | PASS cho docs/source check | ERD dùng USERS/BIGINT và đủ cột đang có trong User.java; không sửa entity hoặc auth/JWT |
| Mô hình logic/persistence mapping | REVIEWED | Code agent/skill khác UUID DB, version pair resolve version ID, attempt khác invocation; source unique theo project, QA/approval pin version, scope và event/budget gaps ghi rõ |
| Bảo toàn phần ngoài scope lượt bổ sung | PASS | SHA-256 trước/sau: 50 file trong backend (không target), infrastructure, tools, .idea không thay đổi; không phải so với Git HEAD |
| Git whitespace / index | PASS | `git diff --check`; index rỗng, HEAD vẫn d4f321e; không stage/commit/push |
| Mermaid parser/render | NOT VERIFIED | Chỉ kiểm cấu trúc text/entity/edge; chưa có Mermaid parser/CLI trong môi trường đã dò. Không coi structural check là render PASS |
| Migration / DB / LLM / connector / SSE / E2E | NOT VERIFIED | Không triển khai/chạy trong scope docs |

Quy trình kiểm tra tài liệu đã dùng: đọc Markdown UTF-8 nghiêm ngặt, resolve link tương đối theo thư mục file, JSON parse + kiểm field/ID/state/version chéo fixture, đếm entity/group/edge ERD và so users với source. Đây là kiểm tra bằng script một lần trong phiên, **chưa thêm checker/CI vào repository**. Nếu sửa tiếp, chạy lại cùng các nhóm kiểm tra; không giữ nguyên nhãn PASS/số lượng khi dữ liệu đã đổi.

Các lệnh Git chỉ đọc có thể chạy lại từ root thực tế:

```powershell
git diff --check
git diff --cached --name-only
git rev-parse --short HEAD
```

`git diff --check` không bao phủ file untracked; vì docs hiện untracked, kiểm UTF-8/fence/whitespace/link thực hiện trực tiếp trên file. Không có lệnh build/test backend nào được chạy trong lượt docs này.

## Kết quả đợt 2026-10-26 — lịch sử

Các dòng sau là báo cáo từ đợt rename/docs trước, không được chạy lại đầy đủ ngày 2026-10-27. Không dùng các số đếm cũ cho bộ docs hiện tại hoặc coi GitHub/runtime đã được xác minh lại.

| Kiểm tra | Kết quả | Phạm vi bằng chứng |
| --- | --- | --- |
| Đọc UTF-8, ký tự lỗi/NUL, tiêu đề, code fence, trailing whitespace | PASS | 32 file Markdown, gồm README/AGENTS và docs |
| Liên kết Markdown nội bộ | PASS | 112 liên kết có đích tồn tại tại lúc kiểm tra |
| JSON parse | PASS | 4 ví dụ synthetic |
| Catalog và profile | PASS | Đủ 11 agent ID duy nhất; profile có input/output/skill/tool/quy trình/nghiệm thu/ranh giới |
| Contract/ví dụ | PASS | Trường bắt buộc, version 0.1, agent/skill/task/run/artifact references và timeline READY → RUNNING → WAITING_QA |
| Version conflict/usage | PASS | Ví dụ conflict khớp version; usage unknown giữ null |
| GitHub repository rename | VERIFIED qua UI | MinhElly/marketing-ai-agent-team; không đổi owner/visibility |
| Origin local | PASS | URL mới marketing-ai-agent-team.git |
| HEAD/index | PASS | HEAD vẫn d4f321e; không có file staged |
| Giữ logic/thay đổi code có sẵn | PASS | 10 file auth/user/config khớp SHA-256 baseline khi đảo phép đổi định danh; chỉ package/issuer/tên cấu hình thay đổi |
| Định danh code và file bên trong repo | PASS | Không còn tên dự án trước trong nội dung/đường dẫn tương đối, loại trừ lịch sử Git và sản phẩm build; 23 Java source/test có package khớp thư mục |
| Đổi thư mục gốc | BLOCKED | Windows khóa root bởi các tiến trình Codex/runtime; script đổi root đã kiểm tra syntax và -WhatIf, chưa rename thành công |

README gốc là UTF-16 LE; đã chuyển sang UTF-8 để hiển thị và chỉnh sửa Markdown bình thường. Git có thể hiển thị diff README là binary do bản cũ có NUL; nội dung mới đọc trực tiếp được.

Kiểm tra docs bằng script đọc file/JSON và lệnh Git chỉ đọc. Đợt đổi tên bổ sung cập nhật code/configuration, build backend và kiểm tra JWT bằng khóa synthetic; xem tài liệu 13 để phân biệt phạm vi.

## Không được suy ra

- Chưa chạy DB/server, model, connector hoặc live E2E. Build backend và kiểm tra JWT cô lập đã chạy trong đợt rename.
- Chưa kiểm tra mọi nhánh Git hoặc hệ thống bên ngoài.
- Liên kết nguồn bên ngoài không được coi là đã xác minh chỉ từ kiểm tra đích file nội bộ.
- Ví dụ JSON không được validate bằng JSON Schema hoặc OpenAPI vì các schema đó chưa được chốt/triển khai.
- Các yêu cầu quyền/QA trong docs chưa có backend thực thi chỉ vì docs đã PASS.
- Chưa stage, commit hoặc push; docs và quyết định kỹ thuật đề xuất còn chờ duyệt.
