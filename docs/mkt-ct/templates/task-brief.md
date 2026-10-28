# Template brief/task

Trạng thái: PROPOSED; thay placeholder bằng dữ liệu phù hợp, không đưa secret vào fixture.

## Mục tiêu

- Task / workflow / project / workspace:
- Agent / skill / config version:
- Agent code / DB ID đã resolve; prompt version / model snapshot:
- Task kind / parent task / idempotency key + payload hash:
- Mục tiêu / audience / ngành / địa lý / thời kỳ:
- Offer / thông điệp / CTA:
- Kênh / ngôn ngữ / tone:
- Deadline có timezone:

## Input và output

- Artifact IDs + versions / source locator:
- Brand/glossary version:
- Depends_on / dữ liệu còn thiếu:
- Checkpoint artifact-ready nếu task QA; không phụ thuộc producer COMPLETED đang chờ QA:
- Loại và số lượng đầu ra:
- Định dạng / kích thước / yêu cầu nguồn:
- Tiêu chí nghiệm thu / QA / approval capability:
- Claim output bắt buộc (Editorial/Case Study): claim_text/type/content_locator + source refs/excerpt/locator/status:

## Giới hạn

- Tool / integration binding / scope:
- Hành động được ủy quyền:
- Nếu là action: target/provider/account/payload digest, artifact version, approver/expiry/budget:
- Runtime / budget / retry / revision:
- Cách xử lý thiếu dữ liệu và lỗi:

## Bàn giao

- Artifact versions / nguồn / QA:
- Giả định / phần chưa xác minh:
- Đề nghị bước tiếp theo:

Contract chuẩn ở [05](../05-data-and-contracts.md); ràng buộc bảng ở [14](../14-relational-data-model.md). Không điền password/token vào brief, chỉ binding reference đã được backend resolve.
