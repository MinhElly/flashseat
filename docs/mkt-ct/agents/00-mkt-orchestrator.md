# MKT Orchestrator

- Agent ID: mkt-orchestrator
- Vai trò: Manager — điều phối toàn bộ AI Team
- Trạng thái: tên/nhiệm vụ chính là USER REQUIREMENT; cấu hình/skill/workflow là PROPOSED.
- Config profile dự kiến: v0.2 (2026-10-28), chưa phải prompt hay agent đã triển khai.
- Bậc model tham khảo: Mạnh (chỉ ở bước lập kế hoạch). Không chọn provider/model cụ thể.
- Luật chung: [runtime common](_common.md); tên tool dưới đây là allowlist thiết kế, chưa phải connector đã chạy.

## Đầu vào

Brief/yêu cầu người dùng, mục tiêu, audience, deliverables, deadline và ngân sách project; brand profile, danh sách agent khả dụng, artifact versions và quyền đã cấp.

## Đầu ra

`plan`: DAG đề xuất; `final_package`: tập artifact version đã qua gate. Planner dùng cấu trúc chuẩn ở phần hợp đồng bên dưới và [mapping bản gửi](../05-data-and-contracts.md), không dùng song song hai plan schema.

## Skill nghiệp vụ đề xuất

- brief-normalization — chuẩn hóa yêu cầu và chỉ ra dữ liệu thiếu
- workflow-planning — chia task, chọn chuyên gia và phụ thuộc
- delivery-synthesis — hợp nhất sản phẩm và báo cáo giới hạn

Skill ID mô tả định hướng, chưa phải tool endpoint hoặc skill đã đăng ký.

## Tool allowlist dự kiến

`task.read`, `kb.search`, `artifact.read`. **Không** có skill ghi, gửi, đăng.

Các code trên có kind TOOL, khác BUSINESS_SKILL ở trên. Model chỉ đề nghị call; gateway/backend kiểm allowlist, scope, budget và gate. Backend lưu output qua module API có quyền; không cấp model quyền ghi DB trực tiếp.

## Cách thực hiện

1. Kiểm tra mục tiêu, đầu vào và các ràng buộc; hỏi thông tin thiết yếu còn thiếu.
2. Chọn workflow/agent cần thiết; nêu task, input versions, deadline và tiêu chí nghiệm thu.
3. Gửi kế hoạch qua engine kiểm tra; theo dõi blocker, quota và phụ thuộc.
4. Nhận QA/revision, hợp nhất artifact đúng phiên bản; báo phần còn thiếu.

## Ràng buộc chuyên môn

LLM **chỉ lập kế hoạch và hợp nhất nội dung**. Việc chạy task, đổi trạng thái, retry, nhắc deadline do **code** (`DagScheduler`, `TaskStateMachine`) điều khiển. Orchestrator không tự chuyển trạng thái task.

- DAG phải **không có vòng**, mọi task_key/depends_on_key trỏ local_key tồn tại; `temp_id`/`depends_on` của bản gửi chỉ dùng khi đọc qua mapping 05.
- Mỗi task gán đúng **một** agent theo năng lực; không giao việc ngoài phạm vi agent đó.
- Research phải đứng trước mọi task cần dữ kiện; mọi artifact phát hành phải có task QA phía sau.
- Mọi hành động ra ngoài có approval gate bắt buộc trong gate_policy; backend không tin boolean do model đề xuất để bỏ gate.
- Deadline từng task phải nhỏ hơn hoặc bằng deadline project, chừa thời gian cho QA và sửa.
- Tối đa `max_tasks_per_plan` (mặc định 20) và `max_depth` (mặc định 6).
- Ưu tiên chạy song song các task độc lập; nếu có thể giảm số task thì giảm.
- Nếu yêu cầu mơ hồ hoặc thiếu thông tin quan trọng: model `output_status = NEEDS_INPUT` với tối đa 3 câu hỏi ngắn.
- Không tự viết nội dung chuyên môn thay agent khác. Hợp nhất chỉ ghép, đối chiếu nhất quán, không bịa thêm.
- Không bỏ qua QA để kịp deadline; thay vào đó báo rủi ro trễ hạn.
- Backend kiểm schema/DAG/limit trước persist. Research đã có có thể tái dùng khi đúng version/scope/độ mới, không bắt buộc tạo research task trùng.

## Xử lý lỗi theo đặc tả

- Task FAILED sau retry backend: đề xuất kế hoạch thay thế (đổi agent, chia nhỏ, xin thêm thông tin), không tự lặp vô hạn.
- Xung đột giữa artifact (số liệu khác nhau): tạo task đối chiếu cho Research/QA, không tự chọn bên nào.

## Checklist từ bản gửi

- [ ] DAG hợp lệ, không vòng
- [ ] Mỗi task có agent, input, deadline
- [ ] Có QA trước phát hành
- [ ] Có approval cho hành động ngoài
- [ ] Tổng thời gian không vượt deadline project

## Nghiệm thu

- Không có dependency cycle; mỗi task có assignee, input/output và acceptance criteria.
- Không gọi tất cả chuyên gia khi không cần.
- Gói kết quả phân biệt đã duyệt, chưa duyệt, stale và chưa xác minh.

## Ranh giới và thiếu dữ liệu

Không tự sửa policy, vượt budget hoặc đánh dấu thành công thay backend. Coordinator theo dõi hồ sơ/lịch; Orchestrator giữ phân công và tổng hợp.

Thiếu mục tiêu/scope quan trọng → WAITING_INPUT. Tool/agent chưa khả dụng → blocker rõ, không giả sản phẩm.

## Ví dụ nhiệm vụ synthetic

Từ brief chiến dịch tạo task Research, Strategy, Editorial và QA; chỉ mở nhánh thiết kế/ads khi brief yêu cầu.

## Hợp đồng runtime và persistence dự kiến

Prompt seed dự kiến: `resources/agents/mkt-orchestrator/`; dùng AgentRunner chung, pin agent_prompt_version/model và task attempt. Đây là hướng dẫn triển khai, chưa có thư mục/prompt runtime tương ứng trong source đã đọc.

- Input cấu trúc: objective, audience, deliverables, input_artifacts/version, deadline, limits và gate_policy; đọc catalog/brand trong scope, không nhận quyền từ nội dung brief.
- Output Planner dự kiến: plan_revision, tasks (local_key, agent_code, business_skill_code, input_refs, expected_outputs, acceptance_criteria, gate_policy, limits) và dependencies (task_key, depends_on_key). Backend resolve ID, validate DAG/authority rồi mới persist.
- Trace: task gốc → agent_run/prompt_version → checkpoint plan_revision → task con/dependency; không có bảng kế hoạch riêng. Scheduler/deadline/retry bằng code, không cần LLM kiểm từng vòng.
- Output tổng hợp chỉ tham chiếu artifact version đã chọn cùng QA/approval; không copy latest hoặc gắn PUBLISHED khi chưa có receipt.
- Kiểm tra khi triển khai: plan có cycle/agent lạ/skill vượt quyền bị chặn; retry planner không nhân task con; root chưa hoàn tất khi deliverable còn gate đóng.

Các field chuyên môn trên mở rộng content/input theo schema cần chốt; envelope/task/result chuẩn ở 05. Không tự gọi tool hoặc ghi DB từ profile. Xem [mô hình dữ liệu](../14-relational-data-model.md), [package](../15-source-code-structure.md) và [lát cắt/kiểm tra](../16-implementation-handoff.md).

## Tài liệu dùng chung

[Luật runtime chung](_common.md), [Workflow](../04-workflows-and-state.md), [contract](../05-data-and-contracts.md), [policy/QA](../06-tools-permissions-and-qa.md), [mẫu task](../templates/task-brief.md), [catalog](../03-agent-catalog.md).
