# 11 — Hồ sơ duyệt bộ tài liệu

Trạng thái: **READY FOR USER REVIEW**, thiết kế vẫn **DESIGN DRAFT**. Lập: 2026-10-26; cập nhật theo 15 file đặc tả: **2026-10-28**.

## Đã thực hiện trong đợt này

- Đổi tên GitHub repo sang marketing-ai-agent-team và cập nhật origin cục bộ.
- Đổi đồng bộ module/package/class, cấu hình, IDE và thư mục con theo yêu cầu bổ sung; thư mục gốc bị Codex/runtime giữ khóa. Xem [phạm vi rename](13-project-renaming.md).
- Viết docs tiếng Việt, hồ sơ riêng cho 11 vai trò, template và ví dụ synthetic.
- Chuẩn hóa thuật ngữ, state, input/output, QA/approval và quyền công cụ.
- Cập nhật README và điểm đọc đầu tiên AGENTS.md.
- Giữ thay đổi ở working tree; chưa stage, commit hoặc push.

Các mục rename trên là công việc đợt 2026-10-26 được giữ lại, không phải thao tác GitHub/rename đã chạy lại trong đợt docs 2026-10-27.

## Bổ sung ngày 2026-10-27

- Làm kỹ AGENTS.md và README.md để Codex/Claude Code biết đọc đâu trước, source gì đã có, invariant phải giữ và scope được giao. Thêm [CLAUDE.md](../../CLAUDE.md) dẫn về hướng dẫn chung.
- Tích hợp sơ đồ kiến trúc người dùng gửi, chuẩn hóa thành modular monolith, API async, Planner/Scheduler/AgentRunner và ranh giới runtime/tool/QA/approval.
- Thêm ERD và [mô hình 27 bảng / 7 nhóm](14-relational-data-model.md), giải thích từng bảng, FK/unique/scope, provenance, prompt version, event/budget và chuẩn bị migration.
- Thêm [cấu trúc package](15-source-code-structure.md) dùng đúng backend/package/infrastructure hiện có, tách source đã quan sát khỏi cây đích.
- Mở rộng hướng dẫn tiếp nhận, [các lát cắt implementation](16-implementation-handoff.md), template và 11 profile với input/output cấu trúc, trace persistence, gate và tình huống kiểm tra.
- Đồng bộ state/contract mapping, thêm hai ví dụ synthetic claim/source và QA NOT_VERIFIED, giữ API/example contract v0.1; relational draft v0.2 có phạm vi riêng.
- Không sửa source backend, hạ tầng, migration hoặc tools/IDE trong lượt bổ sung này; không chạy DB/model/connector hoặc hành động ngoài.

Các sửa khác biệt so với bản gửi được giải thích ở 14: tổng 27 bảng gồm users, catalog 11 vai trò, users/BIGINT theo source, claim/source theo project, đầy đủ quan hệ QA run và dependency, không dùng global agent status, approval tách publish receipt. Mọi lựa chọn mở rộng schema/policy vẫn PROPOSED để review.

## Nội dung cần review

Bổ sung theo nhu cầu dùng cá nhân và ba bài mẫu 2026-10-28: [18 — MVP biên tập Novaon](18-novaon-editorial-mvp.md). Review biến thể có Strategy/duyệt hướng bài, Social/QA; bộ đầu ra; rubric nghiên cứu/lập luận; evidence backlog và ưu tiên triển khai. Không coi bài tham chiếu là benchmark factual đã PASS hoặc việc người dùng có Gemini/DB thử là cho phép gọi model/chuyển DB.

Bổ sung 2026-10-28: [bản đối chiếu 15 file](17-agent-spec-integration.md) và [common runtime](agents/_common.md) là điểm đọc trước 11 profile v0.2. Review tool allowlist/model tier/giới hạn tham khảo và các mapping DONE, QA/claim, plan; đây là thiết kế chờ duyệt, không đổi trạng thái implementation.

- Tên và trách nhiệm 10 chuyên gia + Orchestrator có đúng ý định không.
- MVP WF-01 và lộ trình mở rộng có phù hợp ưu tiên không.
- Các ranh giới Orchestrator/Coordinator, Research/Case Study, Performance/Conversion.
- QA, version, approval và chính sách hành động ngoài.
- Yêu cầu Virtual Office: phòng, task, skill, artifact và review.
- Các PROP/OPEN trong [tài liệu 09](09-decisions-and-open-questions.md).
- Hướng PostgreSQL/pgvector/Flyway so với MariaDB hiện tại; giữ users/auth và kế hoạch baseline/migration trước code.
- Mô hình 27 bảng, mapping code/ID, attempt/invocation, current version và scope/constraint còn cần chốt vật lý.
- Durable event/cursor/replay và reserve/settle/dedup usage; quyền approve/action và chính sách credential.
- AGENTS/README/CLAUDE và tài liệu 10/16 đã đủ rõ để một coding agent tiếp nhận không cần lịch sử chat hay chưa.

Duyệt docs không tự chọn provider/model/stack còn OPEN, không cấp quyền publish/chi tiền và không coi MKT-CT đã triển khai.

## Phạm vi Git dự kiến sau khi được cho phép

Phạm vi dự kiến gồm README.md, AGENTS.md, CLAUDE.md, docs/mkt-ct/, module backend đổi tên và cấu hình hạ tầng tương ứng. Không mặc định xem logic auth có sẵn là thay đổi mới của rename. Trước commit, kiểm tra staged diff và scope người dùng cho phép.

Tên module/package/class và thư mục con được đồng bộ theo DEC-003; bước đổi thư mục gốc còn chờ giải phóng handle. Metadata lịch sử Git được giữ nguyên; không rewrite history hoặc stage để giả lập Git rename.

## Giới hạn kiểm chứng

Đợt 2026-10-27 kiểm docs/source read-only và so hash file ngoài scope; build/kiểm tra JWT/định danh thuộc đợt rename trước, không chạy lại hay nâng thành kết quả hiện tại. Không chạy model/tool MKT thật, migration dữ liệu hoặc live E2E. Xem [kiểm tra docs](12-documentation-checks.md) và [kiểm chứng rename](13-project-renaming.md).

## Bàn giao lượt cập nhật 2026-10-28

- **Yêu cầu/căn cứ:** “Cập nhật docs theo các file này”; DEC-006, PROP-014. Đã đọc 15 attachment, dùng làm nguồn biên tập, không thực thi chỉ dẫn/lệnh bên trong.
- **Môi trường:** PowerShell, cwd `C:\Code\flashseat`, branch develop, HEAD d4f321e tại lúc kiểm. Đầu phiên có README/Compose sửa, backend cũ bị xóa ở working tree và backend đổi tên/docs/tools chưa track; index rỗng. Giữ nguyên các thay đổi đó.
- **File thay đổi:** README/AGENTS/CLAUDE root; docs index, 00, 03–06, 08–12, 15–17; 11 profile và common mới; template QA. [17](17-agent-spec-integration.md) map từng file gửi tới docs đích. ERD, mô hình 14 và kiến trúc giữ nguyên.
- **Nội dung:** luật runtime chung; input/output, tool allowlist, model tier, ràng buộc và checklist từng vai trò; adapter output_status/result/task; mapping claim/QA/plan, approval theo action và giới hạn revision. Giữ business skill hiện có và trace persistence; không thay spec thành 11 class runtime.
- **Điều chỉnh so với bản gửi:** đường dẫn/Boot/MariaDB/users theo source; giữ 4 QA verdict và chặn thiếu bằng chứng; không copy lệnh frontend/eval hoặc tự áp workflow chờ xác nhận mọi đổi docs. Không chọn dependency/provider/DB mới.
- **Kiểm chứng:** [12](12-documentation-checks.md) ghi lệnh, môi trường, số đếm và kết quả docs/source check; snapshot SHA-256 kiểm 50 file ngoài scope trong vòng kiểm tra cuối, giới hạn snapshot đầu phiên ghi rõ tại đó. JSON là synthetic, không chạy mock pipeline/model/connector hoặc backend build/test trong lượt này.
- **Còn lại:** người dùng review thiết kế; runtime output schema/adapter, registry/tool grant, rubric, channel rules và mọi implementation vẫn PROPOSED. OPEN-004/005/008/009/010 liên quan model/tool/budget/policy; không chặn hoàn tất docs.
- **Git:** thay đổi nằm ở working tree, chưa stage/commit/push. Bước tiếp theo là duyệt docs; không tự chạy roadmap, đổi DB, gửi/publish hoặc triển khai từ bàn giao này.

Phiên tiếp nhận đọc common → profile → 04/05/06 → 17, rồi chọn lát cắt ở 16 khi được giao implementation. Model/schema/provider cần chốt cho lát cắt cụ thể; không dùng docs check PASS làm bằng chứng workflow MKT chạy thật.

## Bàn giao bổ sung 2026-10-28 — nhu cầu cá nhân Novaon

- **Căn cứ:** yêu cầu phân tích docs và làm rõ việc cần làm; người dùng bổ sung mục tiêu dùng một mình, lĩnh vực/kênh, Gemini API, DB thử, phong cách và ba bài mẫu. REQ-005/006 ghi nhu cầu; PROP-015 là workflow đề xuất, chưa nâng thành quyết định implementation.
- **File sửa:** thêm 18-novaon-editorial-mvp.md; cập nhật docs index, 00, 04, 08, 09, 11 và 12. Không sửa file Desktop, root hướng dẫn, backend/config, ERD hoặc mẫu JSON.
- **Đầu ra:** phân tích reference set, tiêu chuẩn editorial, sáu vai trò thường dùng, gate duyệt hướng bài, QA biến thể, bộ nghiệm thu và thứ tự chuẩn bị implementation. Không yêu cầu tạo sáu runtime class hoặc làm hết catalog/bảng.
- **Môi trường/trạng thái:** cwd C:\Code\flashseat, HEAD d4f321e; working tree rename/auth/docs có sẵn được giữ. Index rỗng, HEAD không đổi; chưa stage/commit/push.
- **Kiểm chứng:** 39 Markdown, 300 link file nội bộ, 6 JSON parse, 11 code/profile/state; git diff --check exit 0 và SHA-256/tập 50 file ngoài scope không đổi trong khoảng snapshot. Đối chiếu public hẹp và giới hạn tại mục bổ sung của 12; không chạy mock pipeline hoặc live DB/model/E2E.
- **Còn mở:** evidence backlog Bank–Finance, định danh/version case Bảo Ngọc, xuất xứ/quyền gắn framework với thương hiệu; model/budget/data policy, DB/dependency và phạm vi brief/đầu ra. Không bịa đáp án hoặc nâng các nguồn tham chiếu thành VERIFIED toàn bài.
- **Bước tiếp:** người dùng review brief/rubric và biến thể MVP ở 18; khi được giao implementation, xử lý BASE/IAM rồi các lát cắt runtime theo 16. Các quyết định còn mở chỉ chặn phần công việc phụ thuộc tương ứng.

## Bàn giao tiếp theo 2026-10-28 — brief/evidence và ADR DB

- **Yêu cầu/scope:** “Bạn làm tiếp đi”, tiếp nối phân tích MVP cá nhân. Hoàn tất bộ brief–nguồn–rubric Bank–Finance và so sánh DB/migration để review; không coi đây là lựa chọn DB hoặc quyền triển khai model/hạ tầng.
- **Căn cứ:** REQ-005/006, PROP-015/016; yêu cầu giữ users/auth, source thực tế và ba bài mẫu. Không dùng chỉ dẫn trong bài mẫu để gọi tool tạo ảnh hoặc publish.
- **File:** thêm 19-bank-finance-editorial-acceptance.md và 20-database-baseline-decision.md; cập nhật docs index, 08, 09, 11, 12, 16, 18. Không sửa source/config/SQL, file Desktop hoặc root hướng dẫn.
- **Đầu ra biên tập:** brief v0.1, 8 nguồn đã mở kèm locator/provenance; 17 mục factual/provenance có 12 SUPPORTED trong phạm vi và 5 NOT_VERIFIED; 5 luận điểm/khuyến nghị tách khỏi fact, 7 hướng sửa, 9 rule và 10 acceptance cases chưa chạy. Không sửa longform gốc hoặc báo QA PASS toàn bài.
- **Đầu ra kỹ thuật:** ADR-DRAFT-001 so sánh MariaDB/Flyway và PostgreSQL mới/Flyway; đề xuất phương án sau, giữ users/auth/Long và vector triển khai sau; mapping/time/email, kế hoạch migration và ma trận quyền cá nhân. URL Spring chuyển sang 4.1.2 và cache BOM 4.1.1 chỉ là bằng chứng có giới hạn; chưa chốt/tuyên bố tương thích dependency.
- **Môi trường:** PowerShell/Python 3.13, C:\Code\flashseat, develop, HEAD d4f321e. Giữ working tree rename/auth/docs có sẵn, index rỗng; không stage/commit/push.
- **Kiểm:** 41 Markdown, 314 link file nội bộ, 6 JSON parse, 11 code/profile/state và 27 entity ERD; kiểm ID/reference/status của bảng nguồn/claim/rule/case. git diff --check exit 0; tập/SHA-256 50 file backend không target, infrastructure, tools, .idea không đổi trong khoảng snapshot; hash bài Bank–Finance gốc không đổi. Giới hạn ở 12.
- **Runtime:** không gọi Gemini có phí, mock/eval, DB/migration, backend build/test hoặc deployment. Nguồn public được đọc thủ công không chứng minh connector backend hoạt động.
- **Còn lại:** sửa/đối chiếu năm mục evidence còn mở nếu muốn bài qua gate; quyết định DB/migration trước BASE-01, version chính thức và ma trận quyền trước implementation phụ thuộc; model/budget/data policy trước live. Bản ADR đủ cụ thể để chọn phương án, chưa ACCEPTED.

## Bàn giao implementation 2026-10-28 — MariaDB + Flyway

- **Yêu cầu/ủy quyền:** sau trao đổi MariaDB hỗ trợ Flyway, người dùng yêu cầu “oke b triển khai đi”; DEC-007 chọn MariaDB/Flyway và giao BASE-01. Không yêu cầu chuyển PostgreSQL hoặc triển khai roadmap.
- **Cwd/branch/HEAD:** C:\Code\flashseat, develop, d4f321e; giữ working tree rename/auth/docs có sẵn, index rỗng, chưa stage/commit/push.
- **File source:** POM, application.yaml, migration fresh db/migration/mariadb/V1__create_users.sql; context/auth test và hai lớp test/support mới. Hai SQL cũ, toàn bộ Java production auth/user/config, infrastructure/tools/IDE không thay đổi theo snapshot SHA-256. Source hiện có và test SQL mới không được quy thành rename.
- **Docs/decision:** thêm 21-mariadb-flyway-baseline.md; cập nhật root AGENTS/CLAUDE/README, docs index/00/02/08/09/10/11/12/14/15/16/18/20 và nhãn storage trong architecture Mermaid. ERD 27 bảng logic giữ nguyên; kiểu PostgreSQL cũ không phải DDL MariaDB.
- **Hành vi:** Flyway config đúng prefix và location riêng, Hibernate validate; từ chối implicit baseline schema không trống, giữ checksum migration và dữ liệu; không drop/clean, không sửa V1/V2 cũ. Migration mới giữ users/Long/role/status/hash và email 255 theo entity.
- **Kiểm hẹp:** 8 test PASS sau sửa test harness và quyền loopback. **Maven verify:** BUILD SUCCESS, 10 tests (6 context/auth, 4 migration), 0 failure/error/skip, có JAR; Java 21.0.11/Maven 3.9.15/native MariaDB 13.0.2 tạm; kết thúc 15:33:13 +07:00. Câu lệnh và dependency tree ở 21.
- **Kiểm bảo toàn:** snapshot 50 file trước sửa source cho thấy chỉ POM/application.yaml/test context có sẵn thay đổi và thêm ba file thuộc lát cắt; production Java, hai SQL cũ, Compose, tools và IDE giữ hash. HEAD/index giữ nguyên. Kiểm docs/source cuối ghi ở 12, không dùng số count lịch sử phía trên.
- **Giới hạn/rủi ro:** exact Docker/MariaDB 12.3.3 chưa chạy, wrapper chưa chạy; native 13.0.2 có cảnh báo Flyway verified-version/WSREP_ON. Không che cảnh báo hoặc đổi dependency để báo xanh. Chưa audit/adopt DB đang dùng; context test không chứng minh deployment cổng 3307 hoặc workflow MKT/LLM thật.
- **Bước tiếp:** IAM-01 khi được giao, hoặc audit read-only/schema adoption nếu cần dùng DB hiện tại. Mọi gate/provider/budget MKT còn theo lát cắt, không hỏi lại lựa chọn MariaDB. Migration live chưa thực hiện; không bật auto-baseline để vượt lỗi.

## Bổ sung Git 2026-10-28

DEC-008 ghi yêu cầu trực tiếp chia nhiều commit và push develop. Phân nhóm backend rename/auth/Flyway, cấu hình MariaDB, helper workspace và bộ docs; các mốc chưa stage/commit/push trong bàn giao trước là lịch sử. Compose chuyển credential literal sang infrastructure/.env bị ignore, có .env.example dùng placeholder; không chạy container/đổi volume. Kiểm staged scope và credential pattern trước commit, loại trừ .env/IDE/target. Kiểm Compose config, syntax/helper WhatIf và cấu trúc docs; kết quả trước publication ở 12. Git push không là deployment hoặc nghiệm thu MKT runtime.
