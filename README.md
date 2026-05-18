# Bluromatic App

**Bluromatic** là một ứng dụng Android minh họa cách sử dụng **WorkManager** để thực hiện các tác vụ chạy ngầm, cụ thể là làm mờ (blur) hình ảnh.

## Tính năng chính

- **Làm mờ hình ảnh**: Cho phép người dùng chọn mức độ làm mờ và áp dụng hiệu ứng lên hình ảnh.
- **Xử lý ngầm với WorkManager**: Sử dụng WorkManager để đảm bảo các tác vụ xử lý hình ảnh được thực hiện ngay cả khi ứng dụng bị đóng hoặc thiết bị khởi động lại.
- **Chuỗi công việc (Work Chaining)**: Kết hợp nhiều Worker để thực hiện một quy trình hoàn chỉnh:
    1. `CleanupWorker`: Dọn dẹp các tệp tạm thời cũ.
    2. `BlurWorker`: Thực hiện làm mờ hình ảnh.
    3. `SaveImageToFileWorker`: Lưu hình ảnh đã xử lý vào bộ nhớ.
- **Trạng thái UI thời gian thực**: Cập nhật giao diện người dùng dựa trên trạng thái của công việc (Đang chạy, Hoàn thành, Mặc định).
- **Hủy tác vụ**: Người dùng có thể dừng quá trình xử lý bất cứ lúc nào.

## Kiến trúc

Ứng dụng tuân thủ kiến trúc Android hiện đại:
- **UI**: Jetpack Compose.
- **ViewModel**: Quản lý trạng thái giao diện và giao tiếp với Repository.
- **Repository**: Quản lý việc lập lịch các công việc thông qua WorkManager.
- **Data Layer**: Lưu trữ dữ liệu và các Worker thực hiện logic nghiệp vụ.

## Công nghệ sử dụng

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

## Cách cài đặt

1. Clone repository này.
2. Mở project trong **Android Studio**.
3. Chạy ứng dụng trên trình giả lập hoặc thiết bị thật.
