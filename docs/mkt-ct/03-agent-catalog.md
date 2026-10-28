# 03 — Danh mục agent

Tên và nhiệm vụ chính: USER REQUIREMENT. ID/skill ID, quy trình và điều kiện chi tiết: PROPOSED.

Cập nhật 2026-10-28 theo 11 profile và `_common.md` người dùng gửi. Đọc [luật runtime chung](agents/_common.md) trước profile; [17](17-agent-spec-integration.md) ghi mapping với bản gửi. Số 00–10 là thứ tự tài liệu; code trong bảng là định danh chuẩn, không seed thêm code viết tắt.

| Agent ID | Vai trò | Hồ sơ |
| --- | --- | --- |
| mkt-orchestrator | MKT Orchestrator | [Chi tiết](agents/00-mkt-orchestrator.md) |
| research-intelligence | Research Intelligence | [Chi tiết](agents/01-research-intelligence.md) |
| strategic-planner | Strategic Planner | [Chi tiết](agents/02-strategic-planner.md) |
| case-study-analyst | Case Study Analyst | [Chi tiết](agents/03-case-study-analyst.md) |
| editorial-writer | Editorial Writer | [Chi tiết](agents/04-editorial-writer.md) |
| social-localization | Social & Localization | [Chi tiết](agents/05-social-localization.md) |
| creative-designer | Creative Designer | [Chi tiết](agents/06-creative-designer.md) |
| performance-specialist | Performance Specialist | [Chi tiết](agents/07-performance-specialist.md) |
| conversion-specialist | Conversion Specialist | [Chi tiết](agents/08-conversion-specialist.md) |
| marketing-coordinator | Marketing Coordinator | [Chi tiết](agents/09-marketing-coordinator.md) |
| qa-compliance | QA & Compliance | [Chi tiết](agents/10-qa-compliance.md) |

## Cấu hình chung

AgentDefinition cần agent_id, display_name, config_version, instructions_version, skill_refs, tool_policy_ref, knowledge_scope, input/output contract, acceptance_rules và run_limits.

Profile v0.2 bổ sung phân bậc model tham khảo, input/output chuyên môn, ràng buộc và checklist từ bản gửi. Model tier không chọn provider hoặc chứng minh chất lượng. Tool allowlist ghi các code như `web.search`, `source.register`, `email.draft`; các code này là TOOL dự kiến, không thay các business skill code hiện có.

Skill là quy trình chuyên môn; tool là thao tác thực hiện. Một skill gọi nhiều tool, cùng tool có thể được cấp cho nhiều agent với scope khác nhau.

## Ánh xạ registry/runtime dự kiến

- `agent` có **11 dòng cấu hình nền**, code đúng cột Agent ID; không seed 10 gồm Orchestrator. `agent.id` UUID là khóa nội bộ; ID trong profile/JSON v0.1 là code, cần resolve theo mapping 05.
- Một `AgentRunner` chung nạp prompt/schema/model/context, không có 11 subclass hoặc 11 microservice. `agent_prompt_version` lưu version bất biến, pin vào mỗi agent_run, chỉ một current cho run mới.
- Skill ID trong profile là BUSINESS_SKILL. Tool thực thi có registry entry kind TOOL và agent_skill grant riêng; gateway kiểm từng call. Có grant business skill không đồng nghĩa có mọi tool/provider scope mà nó có thể sử dụng.
- `agent_skill` là allowlist dữ liệu; backend vẫn lấy phần giao của quyền user/workspace, integration account/scope, policy và QA/approval trước side effect. `requires_approval` không tự triển khai bước duyệt.
- Registry là cấu hình dùng chung; trạng thái phòng agent suy ra từ task/run theo project, không dùng một trạng thái WORKING/IDLE toàn cục cho nhiều khách hàng.
- Prompt seed dự kiến `resources/agents/<code>/system.md` + schema; DB version là bản run đã pin. Import/activate/rollback có kiểm soát, không sync đè mỗi startup.

Chi tiết bảng/constraint ở [14](14-relational-data-model.md), package/import ở [15](15-source-code-structure.md), nghiệm thu REG/RUN ở [16](16-implementation-handoff.md). Profile là input để viết prompt sau này, chưa phải prompt hoặc credential đã được nạp vào runtime.

Tên công cụ trong hồ sơ là nhu cầu, không xác nhận có connector hoặc credential. Hồ sơ chỉ là đặc tả sản phẩm, không tạo agent runtime hay tự cấp quyền cho coding agent.

## Ranh giới

- Orchestrator: phân công, phụ thuộc, tổng hợp. Coordinator: lịch/status, tài liệu, stakeholder và hồ sơ duyệt.
- Research: nguồn/thị trường. Case Study: proposal/case/số liệu cụ thể.
- Strategy: định hướng. Editorial: phát triển bài. Social: chuyển thể theo kênh/ngôn ngữ.
- Performance: thu hút traffic. Conversion: offer/trang đích/chuyển đổi.
- Designer: tài sản trực quan từ copy/brand.
- QA: đánh giá artifact version; backend kiểm soát quyền; approver quyết định theo policy.
