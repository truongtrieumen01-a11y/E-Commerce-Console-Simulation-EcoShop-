# Báo Cáo Tuần 2: Hiện Thực Thân Hàm, Đa Hình Chiết Khấu, Tách Cây Kế Thừa Sản Phẩm & Hoàn Thiện Lưu Trữ JSON — EcoShop

> **Học phần:** Lập Trình Hướng Đối Tượng (OOP) — Học kỳ 1, Năm học 2026-2027  
> **Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng  
> **Đề tài:** Hệ Thống Quản Lý Bán Hàng & Mua Sắm Trực Tuyến Sinh Thái EcoShop  
> **Giai đoạn:** Tuần 2 — Hiện thực logic nghiệp vụ (Method Body), công thức chiết khấu đa hình, tách kế thừa `Product`, hoàn thiện đọc/ghi JSON và nghiệp vụ Quản trị viên  

---

## 👥 Bảng Phân Công Công Việc & Khắc Phục Góp Ý Tuần 1

> [!NOTE]
> **Khắc phục lưu ý của GVHD ở Tuần 1:** Bảng phân công dưới đây đã hiển thị đầy đủ, chính xác họ tên và tài khoản GitHub của cả 2 thành viên trong nhóm, khắc phục lỗi hiển thị định dạng `****` ở báo cáo trước.

| Thành viên (Họ & Tên) | GitHub Username | Phân hệ đảm nhiệm xuyên suốt đề tài | Chi tiết các phần code & tệp phụ trách Tuần 2 |
| :--- | :--- | :--- | :--- |
| **Trương Triều Mến** *(thành viên ở hàng 1)* | [`truongtrieumen01-a11y`](https://github.com/truongtrieumen01-a11y) (`neMuierT`) | **Phân hệ Người dùng (`User`), Sản phẩm (`Product`), Nghiệp vụ Admin & Lưu trữ JSON** | **1. Nhánh Kế thừa & Đa hình User:** Hiện thực hoàn chỉnh thân hàm `User.java`, `Admin.java`, `Customer.java`, `RegularCustomer.java`, `VIPCustomer.java` (xác thực đăng nhập, đăng ký, công thức chiết khấu đa hình `calculateDiscount()`).<br>**2. Nhánh Kế thừa & Đa hình Product:** Tách `Product.java` thành `abstract class` và bổ sung 2 lớp con `ReusableProduct.java` & `OrganicProduct.java` với phương thức đa hình `getProductType()`, `getEcoBadge()`, `getSpecificInfo()`.<br>**3. Nghiệp vụ Admin & Kho hàng:** Hiện thực Thêm/Sửa/Xóa sản phẩm, Tìm kiếm & Lọc sản phẩm nâng cao, Kiểm tra tồn kho & Cảnh báo hàng sắp hết, Báo cáo thống kê doanh thu toàn diện & Top sản phẩm bán chạy.<br>**4. Tầng Lưu trữ dữ liệu JSON:** Nâng cấp `JsonDataManager.java` đọc/ghi đa hình (`type discriminator`) cho cả `User` và `Product`, khôi phục toàn vẹn danh sách `Order` cùng các `CartItem` lồng nhau. |
| **Khang** | [`Kainn77`](https://github.com/Kainn77) | **Phân hệ Giỏ hàng (`ShoppingCart`), Đơn hàng (`Order`), Thanh toán, Ngoại lệ (`exception/`) & Giao diện Customer** | **1. Nhánh Giỏ hàng & Đơn hàng:** Hiện thực `CartItem.java`, `ShoppingCart.java`, `Order.java`, `OrderStatus.java`.<br>**2. Tầng Thanh toán:** Hiện thực `PaymentProcessor.java` (Tiền mặt CASH, Thẻ ngân hàng CARD, Ví điện tử EWALLET).<br>**3. Gói Ngoại lệ:** 5 lớp trong gói `exception/` (`EcoShopException`, `InvalidInputException`, `InsufficientStockException`, `PaymentFailedException`, `ProductNotFoundException`).<br>**4. Giao diện & Luồng đặt hàng:** Luồng điều khiển giỏ hàng, chốt đơn trừ kho trong `EcoShopApp.java`. |

---

## 🎯 4 Hạng Mục Trọng Tâm Đã Hoàn Thành Ở Tuần 2

Tuân thủ nghiêm túc kết luận duyệt báo cáo Tuần 1 của GVHD ThS. Huỳnh Xuân Phụng:
> *"Tuần 2 các em tập trung 4 hạng mục đã liệt kê: viết thân hàm, công thức chiết khấu đa hình, đọc/ghi JSON, luồng đặt hàng và trừ tồn kho, cùng tách Product thành cây kế thừa như em đã dự kiến."*

### 1. Viết thân hàm thực thi (Method Body) & Đa hình Chiết khấu (`calculateDiscount`)
- Loại bỏ toàn bộ ghi chú `// TODO`, hiện thực 100% logic cho các lớp trong mô hình.
- **Tính Đa hình (Polymorphism) tại `calculateDiscount(double amount)`:**
  - **Khách hàng Thường (`RegularCustomer`):** Giảm giá lũy tiến dựa trên điểm thưởng (`loyaltyPoints`):
    $$\text{Discount} = \begin{cases} 5\% \times \text{amount} & \text{nếu } \text{points} \ge 1000 \\ 3\% \times \text{amount} & \text{nếu } 500 \le \text{points} < 1000 \\ 1\% \times \text{amount} & \text{nếu } 100 \le \text{points} < 500 \\ 0\% & \text{nếu } \text{points} < 100 \end{cases}$$
  - **Khách hàng VIP (`VIPCustomer`):** Giảm giá cố định dựa trên cấp bậc thành viên (`vipLevel`):
    - Cấp 1 (VIP Bạc - Silver): Chiết khấu $10\%$
    - Cấp 2 (VIP Vàng - Gold): Chiết khấu $15\%$
    - Cấp 3 (VIP Bạch Kim - Platinum): Chiết khấu $20\%$
  - Khi chốt đơn tại `Order.calculateFinal()`, phương thức `customer.calculateDiscount(totalAmount)` được gọi qua tham chiếu kiểu `Customer` trừu tượng; Java runtime tự động kích hoạt phiên bản của đối tượng thực tế mà không cần bất kỳ câu lệnh `if (customer instanceof ...)` nào.

---

### 2. Tách Sản Phẩm (`Product`) thành Cây Kế Thừa Sinh Thái
Khắc phục triệt để hạn chế Tuần 1, nâng cấp `Product` từ một concrete class đơn lẻ thành cây kế thừa phong phú, đúng tôn chỉ cửa hàng xanh **EcoShop**:

```
Product (abstract class - implements JsonSerializable)
 ├── ReusableProduct (Sản phẩm Tái sử dụng)
 │    ├── Thuộc tính riêng: material, estimatedReuseCount, ecoCertificate
 │    └── Đa hình: getProductType(), getEcoBadge() [♻️ Tái sử dụng...], getSpecificInfo()
 └── OrganicProduct (Sản phẩm Hữu cơ / Sinh học)
      ├── Thuộc tính riêng: expiryDate, organicCertification, storageInstruction
      └── Đa hình: getProductType(), getEcoBadge() [🌿 Hữu cơ...], getSpecificInfo()
```

- **Thể hiện trọn vẹn 4 trụ cột OOP trên nhánh Sản phẩm:**
  - **Đóng gói (Encapsulation):** Toàn bộ thuộc tính `private`/`protected`, setter kiểm tra `price >= 0`, `stockQuantity >= 0`.
  - **Trừu tượng (Abstraction):** `Product` là `abstract class`, định nghĩa các phương thức thuần ảo bắt buộc lớp con cài đặt.
  - **Kế thừa (Inheritance):** `ReusableProduct` và `OrganicProduct` tái sử dụng mã nguồn lớp cha, mở rộng thuộc tính sinh thái.
  - **Đa hình (Polymorphism):** Danh mục sản phẩm lưu dạng `List<Product>`. Khi duyệt in bảng Console hoặc xuất JSON, mỗi đối tượng tự sinh thông tin và nhãn xanh riêng biệt.

---

### 3. Nghiệp Vụ Quản Trị Viên (Admin) Nâng Cao
Hiện thực trọn vẹn bộ công cụ quản lý chuyên nghiệp cho Admin:
1. **Thêm sản phẩm đa hình (`adminAddProduct`):** Tùy chọn thêm Sản phẩm Tái sử dụng hoặc Sản phẩm Hữu cơ với các thông số sinh thái tương ứng.
2. **Chỉnh sửa thông tin sản phẩm (`adminEditProduct`):** Cho phép sửa giá, tồn kho, tên, mô tả VÀ cả các thuộc tính đặc thù của từng lớp con.
3. **Xóa sản phẩm (`adminRemoveProduct`):** Xóa khỏi kho và đồng bộ hóa tệp JSON.
4. **Tìm kiếm & Lọc sản phẩm (`searchProducts`):**
   - Tìm kiếm theo tên / từ khóa (không phân biệt hoa thường).
   - Lọc theo khoảng giá ($min - max).
   - Lọc theo dòng sinh thái (Tái sử dụng vs Hữu cơ).
5. **Kiểm tra tồn kho & Cảnh báo (`adminCheckInventory`):**
   - Thống kê tổng số SKU, tổng sản phẩm trong kho, tổng vốn hàng tồn ($).
   - Phát hiện và cảnh báo các mặt hàng đã hết hàng (`tồn kho = 0`).
   - Cảnh báo các mặt hàng sắp hết (`tồn kho <= 5 cái`).
   - Chức năng nhập hàng nhanh (Quick restock) ngay tại màn hình kiểm tra.
6. **Thống kê doanh thu & Báo cáo kinh doanh (`adminViewRevenueStatistics`):**
   - Thống kê tổng doanh thu thực nhận (loại trừ các đơn bị hủy).
   - Tổng tiền chiết khấu đã tri ân khách hàng.
   - Doanh thu phân bổ theo phương thức thanh toán (Tiền mặt CASH, Thẻ ngân hàng CARD, Ví điện tử EWALLET).
   - Thống kê số lượng đơn theo từng trạng thái (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
   - Bảng xếp hạng Top sản phẩm bán chạy nhất theo số lượng và doanh số.

---

### 4. Tầng Lưu Trữ JSON Bền Vững (`JsonDataManager`)
- **Đọc/Ghi Người dùng (`users.json`):** Áp dụng Type Discriminator (`"type":"Admin"`, `"type":"RegularCustomer"`, `"type":"VIPCustomer"`).
- **Đọc/Ghi Sản phẩm (`products.json`):** Tuần tự hóa và giải tuần tự hóa đa hình cho `ReusableProduct` và `OrganicProduct`. Hỗ trợ cơ chế tương thích ngược (fallback) chống crash nếu gặp dữ liệu cũ.
- **Đọc/Ghi Đơn hàng (`orders.json`):** Khôi phục hoàn chỉnh danh sách `Order`, phân tích mảng con lồng ghép `items: [...]` thành các đối tượng `CartItem`, liên kết lại với đối tượng `Customer` và `Product` trong bộ nhớ, tự động nạp vào lịch sử mua hàng cá nhân của khách hàng khi đăng nhập.

---

## 🛠️ Hướng Dẫn Biên Dịch & Chạy Thử Ứng Dụng

Ứng dụng được viết thuần Java SE 11+, hỗ trợ UTF-8 đầy đủ trên cả Windows Terminal và Linux.

```bash
# Cách 1: Chạy trực tiếp qua kịch bản run.bat (trên Windows)
run.bat

# Cách 2: Biên dịch thủ công bằng lệnh javac
javac -encoding UTF-8 -d out src/exception/*.java src/model/*.java src/service/*.java src/app/*.java

# Khởi chạy chương trình
java -Dfile.encoding=UTF-8 -cp out app.EcoShopApp
```

### Tài khoản kiểm thử có sẵn trong hệ thống:
- **Quản trị viên (Admin):** `admin` / `admin123`
- **Khách hàng Thường (RegularCustomer):** `alice` / `pass123` (đã tích lũy điểm thưởng)
- **Khách hàng VIP (VIPCustomer):** `bob` / `pass123` (hạng VIP Vàng - chiết khấu 15%)
