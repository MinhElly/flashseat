# Ví dụ contract synthetic v0.1

Các file chỉ minh họa thiết kế tại [tài liệu 05](../05-data-and-contracts.md). Không phải dữ liệu thật, runtime config, API response đã triển khai hoặc bằng chứng chạy model.

## Trình tự giả định

1. Research task đã COMPLETED; artifact đầu vào v1 có trong một kho giả định.
2. [content-task.json](content-task.json): snapshot Editorial READY, task_version 3.
3. Backend nhận lease, chuyển RUNNING tại version 4 (không có file snapshot riêng).
4. [content-result.json](content-result.json): attempt 1 trả run SUCCEEDED và article v1.
5. [task-event.json](task-event.json): backend chuyển WAITING_QA tại version 5.
6. [task-error.json](task-error.json): caller cập nhật bằng version 3 nhận VERSION_CONFLICT, phải refresh; không retry nguyên request tự động.

Chưa có QA/approval nên task chưa COMPLETED. Không có email/publish/ads hoặc hành động bên ngoài.

IDs, file_ref và source_refs là tham chiếu logic giả định. Chúng không trỏ tới artifact/source thật trong repo; không dùng để tuyên bố claim đã có bằng chứng. Timestamp, deadline và budget chỉ là ví dụ, không phải lịch chạy hay ngân sách được cấp.

JSON có thể parse không chứng minh backend hoặc workflow đã thực thi contract.
