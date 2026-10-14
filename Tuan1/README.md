# Tuần 1: Khởi Tạo Dự Án & Thiết Kế Khung Kiến Trúc (Skeleton) — EcoShop

> **Học phần:** Lập Trình Hướng Đối Tượng (OOP) — Học kỳ 1, Năm học 2026-2027  
> **Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng  
> **Đề tài:** Hệ thống Quản lý Bán hàng & Mua sắm Trực tuyến EcoShop  
> **Giai đoạn:** Tuần 1 — Khảo sát yêu cầu, thiết kế Class Diagram, phân tích cấu trúc dữ liệu JSON/CSV & xây dựng Skeleton  

---

## 🎯 Mục Tiêu Trọng Tâm Tuần 1

1. **Khảo sát & Phân tích Đề tài:**
   - Nắm vững yêu cầu đề bài của giảng viên: phân cấp người dùng (`User` $\rightarrow$ `Admin`, `Customer`), cơ chế chiết khấu đa hình (`calculateDiscount()`), lưu trữ dữ liệu bền vững (JSON/CSV), bắt lỗi ngoại lệ không crash.
2. **Thiết kế Class Diagram (PlantUML):**
   - Phác thảo cấu trúc cây phân cấp kế thừa, quan hệ hợp thành (`Composition`), kết hợp (`Association`), hiện thực giao diện (`Realization`).
   - Đảm bảo thể hiện trọn vẹn 4 trụ cột OOP: **Đóng gói (Encapsulation)**, **Trừu tượng (Abstraction)**, **Kế thừa (Inheritance)**, **Đa hình (Polymorphism)**.
3. **Phân tích Cấu Trúc Dữ Liệu Mẫu (JSON & CSV):**
   - Đánh giá ưu và nhược điểm giữa định dạng dữ liệu cây lồng nhau (JSON) và định dạng bảng phẳng (CSV).
   - Xây dựng các tệp dữ liệu mẫu chuẩn (`data_samples/`) cho người dùng, sản phẩm và đơn hàng.
   - Thiết kế giải pháp lưu trữ đa hình (Type Discriminator) và quan hệ 1-N trong JSON vs CSV.
4. **Xây Dựng Khung Kiến Trúc Tổng Quan (Skeleton — Chưa Code):**
   - Định nghĩa toàn bộ Interface, Abstract Class, Concrete Classes, Custom Exceptions, Service/DAO.
   - Hoàn thiện signature (chữ ký phương thức), phạm vi truy cập (`private`, `protected`, `public`), giá trị trả về, tham số và tài liệu Javadoc chi tiết.
   - Thân hàm để trống hoặc đánh dấu `// TODO: Hiện thực trong Tuần 1` làm nền tảng chuẩn bị cho việc viết mã thực tế.

---

## 📂 Cấu Trúc Thư Mục Tuần 1

```
Tuan1/
├── README.md                          <-- Tài liệu tổng quan Tuần 1 (Tệp này)
├── PHAN_TICH_DU_LIEU_JSON_CSV.md      <-- Khảo sát & Đánh giá so sánh cấu trúc JSON vs CSV
├── KE_HOACH_TUAN_1.md                 <-- Kế hoạch công việc & Tiêu chí nghiệm thu
│
├── data_samples/                      <-- Dữ liệu mẫu JSON chuẩn phục vụ kiểm thử & thiết kế parser
│   ├── users.json                     <-- Mẫu người dùng đa hình (Admin, Khách thường, VIP)
│   ├── products.json                  <-- Mẫu danh mục kho sản phẩm
│   └── orders.json                    <-- Mẫu đơn hàng lồng ghép (Nested Order & Items)
│
└── skeleton/                          <-- Khung mã nguồn tổng quan chuẩn OOP (Chưa code thân hàm)
    ├── app/
    │   └── EcoShopApp.java            <-- Skeleton ứng dụng điều khiển Console chính
    ├── exception/
    │   ├── EcoShopException.java      <-- Lớp ngoại lệ cơ sở (Base Exception)
    │   ├── InsufficientStockException.java
    │   ├── InvalidInputException.java
    │   ├── PaymentFailedException.java
    │   └── ProductNotFoundException.java
    ├── model/
    │   ├── Discountable.java          <-- Giao diện tính chiết khấu
    │   ├── JsonSerializable.java      <-- Giao diện tuần tự hóa JSON
    │   ├── User.java                  <-- Lớp trừu tượng Người dùng
    │   ├── Admin.java                 <-- Skeleton Quản trị viên
    │   ├── Customer.java              <-- Lớp trừu tượng Khách hàng
    │   ├── RegularCustomer.java       <-- Skeleton Khách hàng thường (Điểm tích lũy)
    │   ├── VIPCustomer.java           <-- Skeleton Khách hàng VIP (Cấp bậc)
    │   ├── Product.java               <-- Skeleton Sản phẩm
    │   ├── CartItem.java              <-- Skeleton Mục giỏ hàng
    │   ├── ShoppingCart.java          <-- Skeleton Giỏ hàng
    │   ├── Order.java                 <-- Skeleton Đơn hàng
    │   └── OrderStatus.java           <-- Enum Trạng thái đơn hàng
    └── service/
        ├── JsonDataManager.java       <-- Skeleton xử lý lưu trữ tệp JSON thuần Java SE
        └── PaymentProcessor.java      <-- Skeleton xử lý thanh toán đa phương thức
```

---

## 📌 Các Liên Kết Tài Liệu Hữu Ích
- [Phân Tích Cấu Trúc Dữ Liệu JSON/CSV](file:///c:/Users/Lenovo/Documents/OOP/EcoShop/Tuan1/PHAN_TICH_DU_LIEU_JSON_CSV.md)
- [Kế Hoạch & Tiêu Chí Nghiệm Thu Tuần 1](file:///c:/Users/Lenovo/Documents/OOP/EcoShop/Tuan1/KE_HOACH_TUAN_1.md)
- [Biểu Đồ Lớp PlantUML](file:///c:/Users/Lenovo/Documents/OOP/EcoShop/ecoshop_class_diagram.puml)
- [Tài Liệu Phân Tích 4 Trụ Cột OOP](file:///c:/Users/Lenovo/Documents/OOP/EcoShop/docs/OOP_ANALYSIS.md)
