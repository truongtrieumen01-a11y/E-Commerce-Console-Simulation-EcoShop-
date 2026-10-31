# Báo Cáo Tuần 3: Xây Dựng EcoShopApp (Trung Tâm Điều Phối Hệ Thống), Hợp Nhất Nhánh, Quản Lý Lưu Trữ JSON & Phân Hệ Quản Trị Viên

> **Học phần:** Lập Trình Hướng Đối Tượng (OOP) — Học kỳ 1, Năm học 2026-2027  
> **Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng  
> **Đề tài:** Hệ Thống Quản Lý Bán Hàng & Mua Sắm Trực Tuyến Sinh Thái EcoShop  
> **Giai đoạn:** Tuần 3 — Hợp nhất nhánh `feat/core-user-product` và `feat/cart-order-app`, xây dựng trung tâm điều khiển `EcoShopApp`, hiện thực cặp hàm nạp/lưu dữ liệu `initData()` & `saveAllData()`, luồng xác thực và toàn bộ phân hệ Quản trị viên (`Admin`).

---

## 👥 1. Bảng Phân Công Thực Hiện Tuần 3

| Thành viên (Họ & Tên) | GitHub Username | Trách nhiệm chính Tuần 3 | Sản phẩm bàn giao cụ thể |
| :--- | :--- | :--- | :--- |
| **Trương Triều Mến** *(Thành viên 1)* | [`truongtrieumen01-a11y`](https://github.com/truongtrieumen01-a11y) (`neMuierT`) | **Hợp nhất nhánh, quản lý dữ liệu JSON, luồng xác thực và phân hệ Quản trị viên** | • Cây thư mục `src/` đồng nhất sau khi merge nhánh, biên dịch 100% không lỗi.<br>• Các hàm nạp/lưu dữ liệu bền vững (`initData()`, `saveAllData()`).<br>• Luồng xác thực đăng nhập qua `User.authenticate(password)`, đăng ký tài khoản mới.<br>• Toàn bộ Menu Quản trị `Admin` (Kho hàng đa hình, cảnh báo hết hàng, nhập kho `addStock`, duyệt đơn hàng `OrderStatus`, báo cáo doanh thu). |
| **Lê Lợi Khang** *(Thành viên 2)* | [`Kainn7`](https://github.com/Kainn7) (`Kainn77`) | **Xử lý nhập liệu an toàn, phân hệ Khách hàng và kiểm thử tích hợp** | • Bộ hàm đọc Console an toàn bọc `try-catch` (`readInt`, `readDouble`) bắt lỗi `NumberFormatException`.<br>• Menu khách hàng, luồng giỏ hàng (`ShoppingCart`), thanh toán (`PaymentProcessor`) và trừ tồn kho (`reduceStock`).<br>• Kịch bản kiểm thử tích hợp toàn luồng (End-to-End Test). |

---

## 🎯 2. Các Hạng Mục Trọng Tâm Đã Hoàn Thành Ở Tuần 3

Tuân thủ chặt chẽ nhận xét duyệt của GVHD ThS. Huỳnh Xuân Phụng:
> *"Khi merge 2 nhánh em nhớ giữ nguyên tầng lưu trữ JSON, đó là chỗ dễ vỡ nhất lúc hợp nhất."*

### 2.1. Hợp nhất nhánh & Đồng nhất cây thư mục `src/`
- Đồng bộ toàn bộ các lớp từ 2 nhánh phát triển về chung cây thư mục chuẩn `src/`, phân bổ vào 4 gói (package) rõ ràng:
  - `app/`: [`EcoShopApp.java`](file:///d:/EcoShop/src/app/EcoShopApp.java) (Entry point & Controller trung tâm).
  - `model/`: Toàn bộ thực thể người dùng, sản phẩm đa hình, giỏ hàng, hóa đơn và interface (`User`, `Admin`, `Customer`, `RegularCustomer`, `VIPCustomer`, `Product`, `ReusableProduct`, `OrganicProduct`, `CartItem`, `ShoppingCart`, `Order`, `OrderStatus`, `Discountable`, `JsonSerializable`).
  - `service/`: Tầng dịch vụ lưu trữ bền vững [`JsonDataManager.java`](file:///d:/EcoShop/src/service/JsonDataManager.java) và thanh toán [`PaymentProcessor.java`](file:///d:/EcoShop/src/service/PaymentProcessor.java).
  - `exception/`: 5 lớp ngoại lệ nghiệp vụ chống dừng đột ngột chương trình.
- Dự án biên dịch thành công 100% qua lệnh `javac` và kịch bản `run.bat` / `run.sh`.

### 2.2. Tầng Quản lý Lưu trữ Dữ liệu JSON Bền vững (`initData` & `saveAllData`)
- **Khởi chạy (`initData()`):** Tự động nạp toàn bộ danh sách `User`, `Product`, `Order` từ các tệp `data/*.json`. Nếu hệ thống mới được clone về và chưa có dữ liệu runtime, `JsonDataManager` tự động khôi phục dữ liệu từ thư mục mẫu `data_samples/` hoặc nạp bộ Seed Data chuẩn OOP.
- **Đồng bộ sau giao dịch (`saveAllData()`):** Gọi đồng thời việc ghi dữ liệu cho cả 3 phân hệ ngay sau khi:
  - Khách hàng đăng ký tài khoản mới (`handleRegister`).
  - Quản trị viên thêm/sửa/xóa sản phẩm hoặc nhập thêm hàng tồn kho (`adminAddProduct`, `adminEditProduct`, `adminRemoveProduct`, `adminCheckInventory`).
  - Khách hàng chốt đơn thanh toán thành công (`checkout`).
  - Quản trị viên đổi trạng thái đơn hàng (`adminChangeOrderStatus`).

### 2.3. Luồng Xác thực & Phân quyền Người dùng
- Chuẩn hóa phương thức xác thực `User.authenticate(String password)`: Đóng gói việc kiểm tra mật khẩu bên trong lớp cha trừu tượng `User`.
- Luồng điều phối đa hình tại vòng lặp chính (`run()`):
  ```java
  if (currentUser == null) {
      running = showMainMenu();
  } else if (currentUser instanceof Admin) {
      showAdminMenu();       // Điều hướng phiên làm việc của Quản trị viên
  } else if (currentUser instanceof Customer) {
      showCustomerMenu();    // Điều hướng phiên làm việc của Khách hàng
  }
  ```

### 2.4. Phân hệ Quản trị viên (`Admin`) Hoàn Chỉnh
1. **Xem kho hàng sinh thái:** Bảng hiển thị thông tin đa hình `getEcoBadge()` tự động phân biệt nhãn xanh `[♻️ Tái sử dụng...]` và `[🌿 Hữu cơ...]`.
2. **Thêm sản phẩm mới đa hình:** Cho phép lựa chọn thêm `ReusableProduct` (vật liệu, số lần tái sử dụng, chứng chỉ) hoặc `OrganicProduct` (hạn dùng, chứng nhận USDA/VietGAP, bảo quản).
3. **Chỉnh sửa & Xóa sản phẩm:** Cập nhật giá bán, tồn kho, tên, mô tả và các đặc tính con; xóa và đồng bộ hóa tức thì.
4. **Kiểm tra tồn kho & Cảnh báo an toàn:**
   - Cảnh báo các mặt hàng **đã hết hàng** (`stockQuantity == 0`).
   - Cảnh báo các mặt hàng **sắp hết hàng** (`stockQuantity <= 5`).
   - Nhập hàng nhanh ngay tại màn hình kiểm tra qua phương thức bảo vệ bất biến `product.addStock(quantity)`.
5. **Duyệt chu trình đơn hàng:** Xem toàn bộ đơn hàng và cập nhật trạng thái `OrderStatus` (`CONFIRMED` $\rightarrow$ `SHIPPED` $\rightarrow$ `DELIVERED` $\rightarrow$ `CANCELLED`).
6. **Báo cáo doanh thu toàn diện:** Thống kê doanh thu theo 3 kênh thanh toán (Tiền mặt CASH, Thẻ CARD, Ví điện tử EWALLET) và xếp hạng Top sản phẩm bán chạy nhất.

---

## 🖥️ 3. Minh Chứng Chạy Thử Thực Tế: "Lỗi Thật & Output Thật"

*(Thực hiện nghiêm túc theo lưu ý của GVHD để minh chứng quá trình tự làm và kiểm thử của nhóm)*

### 3.1. Output Thật: Cảnh báo tồn kho & Nhập hàng nhanh
Khi Quản trị viên chọn chức năng `6. Kiểm tra tồn kho & Cảnh báo hàng sắp hết`:

```text
  ==========================================================
    BÁO CÁO TỒN KHO & CẢNH BÁO HẾT HÀNG
  ==========================================================
  ┌────────────────────────────────────────────────────────┐
  │               📊 TỔNG QUAN KHO HÀNG ECOSHOP             │
  ├────────────────────────────────────────────────────────┤
  │  Tổng số danh mục mặt hàng (SKU) : 8                   │
  │  Tổng số lượng sản phẩm trong kho : 309                 │
  │  Tổng giá trị vốn tồn kho         : $2772.00            │
  │  Sản phẩm Tái sử dụng (Reusable)  : 4                   │
  │  Sản phẩm Hữu cơ (Organic)        : 4                   │
  └────────────────────────────────────────────────────────┘

  ❌ CẢNH BÁO: CÓ SẢN PHẨM ĐÃ HẾT HÀNG (TỒN KHO = 0):
    • [P007] Xà bông sinh học tinh dầu tràm | Danh mục: Chăm sóc cá nhân

  ⚠️ CẢNH BÁO: CÁC MẶT HÀNG SẮP HẾT (TỒN KHO <= 5 CÁI):
    • [P006] Hạt Macca sấy nứt vỏ hữu cơ    | Còn lại: 4 cái | Giá: $14.50

  Bạn có muốn nhập hàng nhanh cho một sản phẩm? [1] Có  [0] Không
  >> Lựa chọn: 1
  Nhập Mã SP muốn nhập thêm hàng: P007
  Nhập số lượng bổ sung: 25
  [✓] Đã bổ sung thành công! Tồn kho hiện tại của 'Xà bông sinh học tinh dầu tràm' là: 25
```

### 3.2. Lỗi Thật & Cơ chế Lá chắn Ngoại lệ Chống Crash
Khi người dùng nhập chuỗi văn bản vào mục nhập số lựa chọn menu, bộ hàm Console an toàn bọc `try-catch` phát hiện `NumberFormatException` và in hướng dẫn thân thiện, không làm sập ứng dụng:

```text
  ┌────────────────────────────────────────────────────────┐
  │                 🛒  TRANG CHỦ ECOSHOP                  │
  ├────────────────────────────────────────────────────────┤
  │  1. Đăng nhập tài khoản                                │
  │  2. Đăng ký tài khoản mới (Khách hàng)                 │
  │  3. Xem toàn bộ danh mục sản phẩm                      │
  │  4. Tìm kiếm & Lọc sản phẩm sinh thái                  │
  │  0. Thoát chương trình                                 │
  └────────────────────────────────────────────────────────┘
  >> Chọn chức năng (0-4): abc
  [!] Dữ liệu không hợp lệ. Vui lòng nhập một số nguyên hợp lệ.
  >> Chọn chức năng (0-4): 99
  [!] Vui lòng nhập số trong khoảng từ 0 đến 4.
  >> Chọn chức năng (0-4): 1
  ==========================================================
    ĐĂNG NHẬP HỆ THỐNG ECOSHOP
  ==========================================================
  Tên đăng nhập : admin
  Mật khẩu      : sai_mat_khau
  [x] Sai tên đăng nhập hoặc mật khẩu. Vui lòng kiểm tra lại!
```

---

## 🏆 4. Tiêu Chí Nghiệm Thu Cuối Tuần 3

- [x] **Biên dịch & Chạy 1-Click:** Đầy đủ script `run.bat` (Windows) và `run.sh` (Linux/macOS), mã hóa UTF-8 hoàn chỉnh.
- [x] **Cây thư mục `src/` đồng nhất:** Không còn tệp phân mảnh ở các nhánh rời, 100% mã nguồn nằm trong gói chuẩn `src/app`, `src/model`, `src/service`, `src/exception`.
- [x] **Bền vững dữ liệu JSON:** Cặp hàm `initData()` và `saveAllData()` hoạt động ổn định, có cơ chế sao lưu tự động chống vỡ cấu trúc.
- [x] **Phân hệ Admin & Xác thực:** Hoàn thành trọn vẹn 10 nghiệp vụ của Quản trị viên và luồng đăng nhập/đăng ký.
