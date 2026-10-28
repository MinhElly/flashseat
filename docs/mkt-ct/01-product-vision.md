# 01 — Tầm nhìn sản phẩm

Trạng thái: 11 vai trò và Virtual Office là USER REQUIREMENT; cơ chế chi tiết là PROPOSED.

## Mục tiêu

Một không gian làm việc cho đội marketing: nhận brief, điều phối chuyên gia, sản xuất artifact có nguồn và phiên bản, kiểm tra, sửa và duyệt trước bước bàn giao/phát hành theo quyền.

Agent riêng biệt có instructions, skill, tool policy, knowledge scope, phiên nhiệm vụ và tiêu chí nghiệm thu riêng. Không yêu cầu 11 model hay 11 máy chủ riêng.

## Người dùng dự kiến

| Vai trò ứng dụng | Trách nhiệm |
| --- | --- |
| Owner/Admin | Workspace, thành viên, tích hợp và chính sách |
| Manager | Chiến dịch, phân công và deadline |
| Contributor | Cung cấp tài liệu, chỉnh sửa, nhận xét |
| Reviewer/Approver | Kiểm tra/duyệt trong thẩm quyền |
| Viewer | Đọc dữ liệu được cấp quyền |

Vai trò tài khoản khác vai trò AI. Đây là đề xuất, chưa được xác nhận bởi auth source hiện tại.

## Hành trình

1. Tạo workspace/project; cung cấp brand guideline, sản phẩm, glossary và nguồn.
2. Giao brief cho Orchestrator hoặc trực tiếp một chuyên gia.
3. Nhận kế hoạch task, phụ thuộc, deadline, đầu ra và giới hạn chạy.
4. Theo dõi, bổ sung thông tin và xử lý blocker.
5. Xem artifact, phiên bản, nguồn, giả định và QA.
6. Yêu cầu sửa hoặc duyệt phiên bản cụ thể.
7. Bàn giao; hành động bên ngoài đi qua policy tương ứng.
8. Tra lại lịch sử, usage, lỗi và quyết định.

## Phạm vi bản đầu

WF-01: brief → Research → Editorial → QA → người dùng duyệt → bàn giao. Có task state, nguồn, artifact version, approval và history tối thiểu.

Bổ sung các chuyên gia còn lại theo chiến dịch thực tế. Một yêu cầu caption không cần huy động cả đội.

## Đo giá trị

Theo dõi tỷ lệ artifact qua QA, số vòng sửa, mức đầy đủ nguồn, thời gian xử lý và chi phí theo task/chiến dịch. Cần baseline trước khi chốt ngưỡng.

Không cam kết KPI marketing hoặc kết luận pháp lý chỉ từ đầu ra AI. Các dự báo/ước tính phải có nhãn và giả định.
