# 07 — Virtual Office

Phòng riêng, nhiệm vụ và skill: USER REQUIREMENT. Chi tiết màn hình/hành vi: PROPOSED. Chưa chốt stack hoặc phong cách đồ họa.

## Khu vực chính

| Khu vực | Nội dung |
| --- | --- |
| Tổng quan văn phòng | 11 vai trò, task đang chạy, blocker và deadline |
| Phòng agent | Hồ sơ, skill, quyền/công cụ, hàng đợi, artifact |
| Bảng chiến dịch | Mục tiêu, task, phụ thuộc, milestone, owner |
| Chi tiết task | Brief, input versions, run/attempt, QA/approval và lịch sử |
| Thư viện sản phẩm | Preview/download theo quyền, phiên bản và nguồn |
| Hàng đợi phê duyệt | Đối tượng cần duyệt, diff, QA, tác động |
| Cấu hình workspace | Brand/knowledge, thành viên, connector và quota |

## Phòng agent

Hiển thị đang rảnh/chạy/chờ input/chờ QA/chờ duyệt/bị chặn/tạm dừng/lỗi, tổng hợp từ task/run. Một phòng có nhiều task nên vẫn hiển thị trạng thái từng task.

Registry agent là cấu hình dùng chung cho 11 vai trò; trạng thái phòng phải lọc workspace/project đang xem, không ghi một status global trong agent rồi hiển thị cho mọi khách hàng. Membership/role/can_approve quyết định thao tác hợp lệ ở backend; không lấy users.role/JWT role làm quyền approve nội dung.

Thao tác theo quyền: giao task, bổ sung tài liệu, yêu cầu sửa, pause/cancel/resume. Skill thiếu connector có nhãn chưa khả dụng và nguyên nhân.

Phân biệt tool được phép dùng với tool đã dùng. Activity chung không lộ credential hoặc nội dung riêng tư.

## Tiến độ và event

Backend là nguồn trạng thái; animation không được tự đánh dấu xong. Sau reconnect đọc snapshot và bù event, xử lý trùng/đến trễ.

Hướng đề xuất ưu tiên SSE từ activity_log sau transaction commit. Stream chỉ chứa payload đã lọc scope/secret, có event ID/cursor; hết retention hoặc thiếu task_version thì lấy snapshot. Activity feed không tự cung cấp notification read/unread hay đảm bảo email nhắc đã gửi. Xem AUD/RT trong 16 cho nghiệm thu.

Ưu tiên số bước/task hoàn thành; phần trăm/ETA cần cơ sở tính. Usage ghi actual/estimated/unknown. Deadline theo timezone người dùng, mốc lưu có offset.

## Duyệt và revision

Màn hình duyệt hiển thị artifact version/action digest, QA, điểm chưa xác minh, đích/account và budget khi áp dụng. Backend kiểm tra quyền tại thao tác approve.

Artifact đổi trong lúc đang xem phải báo stale/version conflict và tải bản mới. Phân biệt duyệt nội dung, strategy và hành động phát hành.

## Các trạng thái UX bắt buộc

- Empty: chưa có project/task/knowledge; chỉ rõ bước bắt đầu.
- Loading: giữ input người dùng và trạng thái đang xử lý.
- Blocked: thiếu quyền/connector/quota/phụ thuộc; có hướng giải quyết.
- Error: retry an toàn, xem đối soát và giữ lịch sử.
- Stale: đầu vào/sản phẩm cũ; chỉ ra upstream thay đổi.
- Access denied: không lộ nội dung ngoài scope.

Có nhãn chữ cho trạng thái, hỗ trợ bàn phím/focus, không chỉ phân biệt bằng màu. Có thể bắt đầu bằng card/list; đồ họa nhân vật bổ sung sau luồng nghiệp vụ chạy thật.
