# Kiến Trúc Hệ Thống EcoShop

> **Đồ án môn Lập Trình Hướng Đối Tượng (OOP)** — Học kỳ 1, Năm học 2026-2027  
> **Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng  
> **Dự án:** EcoShop — Ứng dụng Quản lý Cửa hàng & Mua sắm Trực tuyến  

---

## 1. Tổng Quan Kiến Trúc

Hệ thống **EcoShop** được thiết kế theo **Mô hình Phân lớp (Layered Architecture / N-Tier Architecture)** nhằm đảm bảo tính phân tách trách nhiệm (*Separation of Concerns*), tăng khả năng mở rộng, bảo trì và tái sử dụng mã nguồn. Các tầng trong kiến trúc tương tác với nhau theo một chiều từ trên xuống dưới, tuân thủ nguyên tắc đóng gói và trừu tượng hóa của Lập trình Hướng đối tượng.

```
┌─────────────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER (Giao diện)              │
│       EcoShopApp (Console UI - Giao diện Bảng Unicode)      │
└──────────────────────────────┬──────────────────────────────┘
                               │ Gọi nghiệp vụ
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              BUSINESS LOGIC LAYER (Nghiệp vụ)               │
│   • Domain Models: User, Customer, Admin, Product, Order... │
│   • Payment Service: PaymentProcessor                       │
└──────────────────────────────┬──────────────────────────────┘
                               │ Đọc / Ghi dữ liệu
                               ▼
┌─────────────────────────────────────────────────────────────┐
│               DATA ACCESS LAYER (Dữ liệu)                   │
│   • JsonDataManager (File I/O thuần Java, Custom Parser)    │
│   • Thư mục data/: users.json, products.json, orders.json   │
└─────────────────────────────────────────────────────────────┘
                               ▲
                               │ Bắt và xử lý lỗi xuyên suốt
┌──────────────────────────────┴──────────────────────────────┐
│              CROSS-CUTTING CONCERNS (Xuyên suốt)            │
│   • Exception Hierarchy: EcoShopException & Subclasses      │
└─────────────────────────────────────────────────────────────┘
```

### 1.1. Presentation Layer (Tầng Trình Bày / Giao Diện)
- **Thư mục:** `src/app/`
- **Thành phần:** `EcoShopApp.java` (giao diện dòng lệnh console trực quan với bảng Unicode, bắt lỗi Try-Catch chống crash, hỗ trợ UTF-8).
- **Trách nhiệm:**
  - Nhận tương tác trực tiếp từ người dùng (nhập lựa chọn menu, nhập thông tin sản phẩm, số lượng, phương thức thanh toán).
  - Kiểm tra sơ bộ tính hợp lệ của dữ liệu đầu vào (bọc trong `readInt`, `readDouble`, bắt `NumberFormatException` và các ngoại lệ).
  - Hiển thị kết quả, hóa đơn, bảng danh mục sản phẩm, giỏ hàng rõ ràng, thẩm mỹ.
  - Hoàn toàn độc lập với chi tiết lưu trữ dữ liệu (không trực tiếp đọc ghi file mà thông qua Data Manager).

### 1.2. Business Logic Layer (Tầng Nghiệp Vụ)
- **Thư mục:** `src/model/`, `src/service/` (phần xử lý thanh toán)
- **Thành phần:**
  - Các thực thể nghiệp vụ (*Domain Entities*): `Product`, `CartItem`, `ShoppingCart`, `Order`, `OrderStatus`.
  - Cây phân cấp người dùng: `User` $\rightarrow$ `Admin`, `Customer` $\rightarrow$ `RegularCustomer`, `VIPCustomer`.
  - Giao diện trừu tượng nghiệp vụ: `Discountable`, `JsonSerializable`.
  - Xử lý nghiệp vụ thanh toán: `PaymentProcessor`.
- **Trách nhiệm:**
  - Đóng gói toàn bộ quy tắc nghiệp vụ (*Business Rules*): kiểm tra số lượng tồn kho trước khi đặt hàng, trừ tồn kho, tính tổng tiền giỏ hàng, áp dụng cơ chế giảm giá theo hạng khách hàng (*đa hình*), tích lũy điểm thưởng cho khách hàng thông thường.
  - Xử lý xác thực quy trình thanh toán qua các phương thức Tiền mặt, Thẻ ngân hàng, Ví điện tử.

### 1.3. Data Access Layer (Tầng Truy Xuất Dữ Liệu)
- **Thư mục:** `src/service/`
- **Thành phần:** `JsonDataManager.java`
- **Trách nhiệm:**
  - Đóng vai trò là lớp Persistence Manager cho toàn bộ hệ sinh thái EcoShop.
  - Chịu trách nhiệm tuần tự hóa (*Serialization*) các đối tượng Java thành định dạng chuỗi JSON và ngược lại (*Deserialization*).
  - Tự tạo cấu trúc thư mục `data/` nếu chưa tồn tại; thực hiện ghi và đọc file bất đồng bộ/đồng bộ với bảng mã UTF-8.
  - Sử dụng toàn bộ thư viện chuẩn của Java SDK (`java.nio.file`, `java.io`), không dùng bất kỳ thư viện ngoài nào (như Jackson, Gson).

### 1.4. Cross-Cutting Concerns (Mối Quan Tâm Xuyên Suốt)
- **Thư mục:** `src/exception/`
- **Thành phần:** `EcoShopException` và các lớp ngoại lệ con chuyên biệt.
- **Trách nhiệm:**
  - Cung cấp cơ chế xử lý lỗi tập trung, phân loại lỗi theo mã (`errorCode`) và thông điệp tiếng Việt.
  - Đảm bảo ứng dụng không bao giờ bị dừng đột ngột (*crash*) khi người dùng nhập sai, hết hàng hoặc thanh toán thất bại.

---

## 2. Sơ Đồ Package & Cấu Trúc Mã Nguồn

```
src/
├── app/         → Lớp trình bày (Console + GUI Swing)
│   ├── EcoShopApp.java          (Console UI — Giao diện dòng lệnh tương tác văn bản)
│   └── EcoShopGUI.java          (GUI Swing — Giao diện đồ họa chính, 1557 dòng, thiết kế theo CardLayout)
├── exception/   → Xử lý lỗi tùy chỉnh (Custom Exception Hierarchy)
│   ├── EcoShopException.java          (Lớp ngoại lệ cơ sở, kế thừa java.lang.Exception)
│   ├── InvalidInputException.java     (Ném ra khi người dùng nhập sai định dạng/trống)
│   ├── ProductNotFoundException.java  (Ném ra khi không tìm thấy mã sản phẩm)
│   ├── InsufficientStockException.java(Ném ra khi số lượng đặt mua vượt quá tồn kho)
│   └── PaymentFailedException.java    (Ném ra khi quy trình thanh toán gặp lỗi)
├── model/       → Mô hình nghiệp vụ & Thực thể dữ liệu (Domain Models)
│   ├── User.java                (Lớp trừu tượng định nghĩa người dùng hệ thống)
│   ├── Admin.java               (Quản trị viên hệ thống: quản lý SP, đơn, người dùng)
│   ├── Customer.java            (Lớp trừu tượng cho khách hàng, chứa giỏ hàng & lịch sử)
│   ├── RegularCustomer.java     (Khách hàng thông thường: giảm giá 0–5% theo điểm tích lũy)
│   ├── VIPCustomer.java         (Khách hàng VIP: giảm giá 10–20% theo hạng Bạc/Vàng/Bạch Kim)
│   ├── Product.java             (Thực thể sản phẩm: mã, tên, danh mục, giá, tồn kho)
│   ├── CartItem.java            (Mục trong giỏ hàng: liên kết Product và số lượng mua)
│   ├── ShoppingCart.java        (Giỏ hàng của khách: tính tổng tiền, thêm/sửa/xóa mục)
│   ├── Order.java               (Đơn hàng: liên kết khách, danh sách CartItem, trạng thái)
│   ├── OrderStatus.java         (Enum trạng thái: PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED)
│   ├── Discountable.java        (Interface nghiệp vụ tính giảm giá: calculateDiscount)
│   └── JsonSerializable.java   (Interface tuần tự hóa: toJson)
└── service/     → Xử lý logic & Truy xuất dữ liệu
    ├── JsonDataManager.java    (Đọc/ghi JSON thuần Java, không sử dụng thư viện ngoài)
    └── PaymentProcessor.java   (Xử lý logic giao dịch thanh toán)
```

### Bảng Mô Tả Thành Phần & Vai Trò

| Package | Class / Interface | Vai Trò & Trách Nhiệm |
|:---|:---|:---|
| **app** | `EcoShopGUI` | Cửa sổ JFrame chính, sử dụng `CardLayout` chứa toàn bộ các panel màn hình: `LoginPanel`, `RegisterPanel`, `CustomerPanel` (bao gồm `SanPhamPanel`, `GioHangPanel`, `LichSuPanel`, `HoSoPanel`), `AdminPanel` (bao gồm `AdminSanPhamPanel`, `AdminDonHangPanel`, `AdminNguoiDungPanel`) và `PaymentDialog`. |
| **app** | `EcoShopApp` | Ứng dụng Console chạy qua dòng lệnh tương tác bàn phím, điều hướng theo vòng lặp menu. |
| **model** | `User` *(abstract)* | Định nghĩa các thuộc tính cơ bản của người dùng (`userId`, `username`, `password`), cung cấp hàm `login()` và phương thức thuần ảo `getRole()`. Triển khai `JsonSerializable`. |
| **model** | `Admin` | Kế thừa `User`, xác định vai trò `"ADMIN"`. Quản lý dữ liệu người dùng, sản phẩm và duyệt đơn hàng. |
| **model** | `Customer` *(abstract)* | Kế thừa `User`, hiện thực `Discountable`. Quản lý `address`, `phoneNumber`, `ShoppingCart` và `orderHistory`. Định nghĩa phương thức trừu tượng `calculateDiscount()` và `getCustomerType()`. |
| **model** | `RegularCustomer` | Kế thừa `Customer`. Tính chiết khấu dựa trên điểm thưởng tích lũy (tỉ lệ 0%, 1%, 3%, 5%). |
| **model** | `VIPCustomer` | Kế thừa `Customer`. Tính chiết khấu cố định theo cấp độ VIP (`SILVER` 10%, `GOLD` 15%, `PLATINUM` 20%). |
| **model** | `Product` | Đại diện sản phẩm gồm mã `productId`, `name`, `category`, `price`, `stockQuantity`. Cung cấp phương thức trừ kho an toàn `reduceStock()`. |
| **model** | `CartItem` | Đại diện cho một dòng sản phẩm trong giỏ hàng, tính thành tiền `getSubtotal()`. |
| **model** | `ShoppingCart` | Quản lý danh sách `CartItem`, tính tổng tiền tạm tính, cung cấp hàm dọn giỏ hàng `clear()`. |
| **model** | `Order` | Lưu thông tin đơn đặt hàng, ngày giờ tạo, phương thức thanh toán, trạng thái. Gọi hàm tính tiền cuối cùng `calculateFinal()` tích hợp chiết khấu đa hình. |
| **model** | `OrderStatus` | Kiểu liệt kê enum mô tả trạng thái đơn hàng (`PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`). |
| **model** | `Discountable` | Interface quy định hành vi tính giảm giá `calculateDiscount(double totalAmount)`. |
| **model** | `JsonSerializable` | Interface quy định hành vi chuyển đổi đối tượng thành định dạng JSON string `toJson()`. |
| **service** | `JsonDataManager` | Đọc và ghi dữ liệu ra đĩa cứng tại thư mục `data/` (`users.json`, `products.json`, `orders.json`). Chứa bộ phân tích cú pháp JSON nhẹ tự viết. |
| **service** | `PaymentProcessor` | Xác thực số tiền thanh toán, phân nhánh xử lý theo phương thức thanh toán (Tiền mặt, Thẻ ngân hàng, Ví điện tử), cập nhật điểm tích lũy. |
| **exception** | `EcoShopException` | Lớp cơ sở ngoại lệ kiểm tra (*checked exception*), mang mã lỗi `errorCode` định danh. |
| **exception** | `*Exception` | Các lớp con (`InvalidInputException`, `ProductNotFoundException`, `InsufficientStockException`, `PaymentFailedException`) mô tả chi tiết lỗi phát sinh. |

---

## 3. Luồng Dữ Liệu & Quy Trình Xử Lý Nghiệp Vụ

### 3.1. Luồng Khởi Động & Tải Dữ Liệu (Application Startup)
1. Ứng dụng (`EcoShopGUI` hoặc `EcoShopApp`) được kích hoạt.
2. Khởi tạo đối tượng `JsonDataManager("data")`. Nếu thư mục `data/` chưa tồn tại, hệ thống tự động tạo mới qua `Files.createDirectories()`.
3. `JsonDataManager` đọc nội dung các file `users.json`, `products.json`, `orders.json`:
   - Nếu file chưa có: nạp tập dữ liệu mẫu mặc định (*Seed Data*) gồm tài khoản Admin, tài khoản Customer (Alice - Regular, Bob - VIP) và 8 mặt hàng mẫu.
   - Nếu file đã có: bộ parser phân tích cú pháp, khôi phục danh sách các đối tượng `User` (đa hình giữa `Admin`, `RegularCustomer`, `VIPCustomer`), `Product` và `Order`.
4. Giao diện mở màn hình đăng nhập (`LoginPanel`).

```
[Start App] ──> [New JsonDataManager] ──> [Kiểm tra data/] 
                     │
                     ├── Chưa có file ──> [Load Seed Data] ──> [Save data/]
                     │
                     └── Đã có file   ──> [Parse JSON]     ──> [Load into Memory]
                                                                     │
                                                                     ▼
                                                             [Mở Login Screen]
```

### 3.2. Luồng Xác Thực Người Dùng (Authentication & Routing)
1. Người dùng nhập `username` và `password` trên giao diện.
2. `EcoShopGUI` duyệt danh sách `users` và gọi phương thức `user.login(password)`.
3. Nếu thông tin khớp:
   - Lưu tham chiếu người dùng hiện tại vào `currentUser`.
   - Kiểm tra `instanceof Admin`: chuyển sang `AdminPanel` với thanh điều hướng màu tối (quản lý sản phẩm, đơn hàng, khách hàng).
   - Kiểm tra `instanceof Customer`: chuyển sang `CustomerPanel` với thanh điều hướng màu xanh lá (mua sắm, xem giỏ hàng, xem lịch sử đơn, thông tin cá nhân).
4. Nếu thất bại: ném hoặc hiển thị thông báo lỗi, không cho phép truy cập.

### 3.3. Luồng Đặt Hàng & Thanh Toán (Order & Payment Processing)

Đây là luồng nghiệp vụ quan trọng nhất, thể hiện rõ sự phối hợp giữa **Presentation**, **Model**, **Service** và **Đa hình OOP**:

```
[Khách hàng]
     │
     │ 1. Chọn sản phẩm & Thêm vào giỏ
     ▼
[ShoppingCart] ───────── Thêm CartItem(Product, Quantity)
     │
     │ 2. Nhấn nút "Đặt Hàng & Thanh Toán"
     ▼
[Customer.placeOrder()]
     │
     ├──> Kiểm tra giỏ hàng rỗng? ──> [Ném EcoShopException]
     │
     ├──> Kiểm tra tồn kho từng món?
     │         └──> Nếu thiếu hàng: [Ném InsufficientStockException]
     │
     ├──> Trừ số lượng tồn kho: Product.reduceStock(quantity)
     │
     └──> Tạo đối tượng Order(orderId, this, items)
               │
               │ 3. Tính tiền cuối cùng
               ▼
         [Order.calculateFinal()]
               │
               ├──> Tính tổng tạm: totalAmount = Σ(item.getSubtotal())
               │
               └──> Gọi ĐA HÌNH: customer.calculateDiscount(totalAmount)
                         │
                         ├─ RegularCustomer: Chiết khấu 0–5% theo points
                         └─ VIPCustomer:     Chiết khấu 10–20% theo VIP level
                               │
                               ▼
               [finalAmount = totalAmount - discountAmount]
                               │
                               │ 4. Xử lý thanh toán
                               ▼
                   [PaymentProcessor.processPayment()]
                               │
                               ├──> Kiểm tra số tiền hợp lệ (> 0)
                               ├──> Xử lý qua Tiền mặt / Thẻ / Ví điện tử
                               ├──> Set OrderStatus.CONFIRMED
                               └──> Nếu RegularCustomer: Tích điểm thưởng
                               │
                               │ 5. Lưu trữ & Hiển thị
                               ▼
                   [JsonDataManager.saveData()]
                               │
                               ├──> Ghi users.json, products.json, orders.json
                               └──> In / Hiển thị Hóa Đơn (Receipt)
```

---

## 4. Công Nghệ Sử Dụng & Quyết Định Kỹ Thuật

| Hạng Mục | Lựa Chọn Kỹ Thuật | Lý Do Thiết Kế & Ưu Điểm |
|:---|:---|:---|
| **Nền tảng / Ngôn ngữ** | **Java 21 (JDK 21 LTS)** | Sử dụng phiên bản Java hiện đại, tận dụng các cải tiến của cú pháp chuỗi (`formatted`, `repeat`), luồng dữ liệu (`Stream API`), ngày giờ mới (`java.time.LocalDateTime`). |
| **Giao diện Người dùng** | **Java Swing & AWT** | Thư viện đồ họa tích hợp sẵn trong JDK chuẩn; không đòi hỏi cấu hình môi trường phức tạp; hỗ trợ đa nền tảng; cho phép tổ chức màn hình linh hoạt qua `CardLayout`, `BorderLayout`, `GridBagLayout`. |
| **Cơ chế Lưu trữ** | **File I/O Thuần Java** (`java.nio.file`) | Thao tác trực tiếp với các tệp `.json` thông qua `Files.readString()` và `Files.writeString()`, mã hóa UTF-8 chuẩn xác, đảm bảo hiển thị tiếng Việt trọn vẹn. |
| **Xử lý Dữ liệu JSON** | **Custom JSON Engine** | Tự xây dựng parser và serializer trong `JsonDataManager`: <br>• Khử bỏ hoàn toàn phụ thuộc vào thư viện bên ngoài (no Jackson/Gson). <br>• Thỏa mãn tiêu chí bài tập lớn đại học: hiểu sâu về cấu trúc dữ liệu và xử lý chuỗi. |
| **Mô hình Hướng đối tượng** | **Pure OOP (4 Trụ Cột)** | Triển khai triệt để Encapsulation, Inheritance, Polymorphism, Abstraction mà không bị pha tạp bởi các annotation hay framework ẩn logic. |

---

## 5. Ghi Chú Kỹ Thuật & Hướng Dẫn Vận Hành

### 5.1. Triết Lý Thiết Kế "Zero External Dependency"
- Toàn bộ dự án được xây dựng mà **không sử dụng bất kỳ công cụ quản lý thư viện nào như Maven hoặc Gradle**.
- Không phụ thuộc vào thư viện bên ngoài (JAR ngoài). Mọi tính năng từ GUI, xử lý dữ liệu, định dạng JSON cho đến quản lý ngày giờ đều tận dụng tối đa gói thư viện chuẩn `java.*` và `javax.*`.
- Giúp sinh viên và giảng viên dễ dàng kiểm tra, chấm bài trên bất kỳ máy tính nào cài đặt JDK mà không lo lỗi tải thư viện mạng.

### 5.2. Biên Dịch & Khởi Chạy

Dự án có thể được biên dịch và chạy trực tiếp thông qua trình biên dịch `javac` và máy ảo `java`:

```powershell
# Di chuyển vào thư mục gốc dự án
cd EcoShop

# Biên dịch toàn bộ mã nguồn vào thư mục out/
javac -encoding UTF-8 -d out src/exception/*.java src/model/*.java src/service/*.java src/app/*.java

# Khởi chạy giao diện đồ họa (Swing GUI - Chính)
java -cp out app.EcoShopGUI

# Hoặc khởi chạy giao diện dòng lệnh (Console)
java -cp out app.EcoShopApp
```

> Ngoài ra, người dùng Windows có thể khởi chạy trực tiếp bằng cách nhấn đúp chuột vào file kịch bản **`run_gui.bat`** hoặc **`run.bat`**.

### 5.3. Cơ Chế Lưu Trữ Dữ Liệu Tại Thư Mục `data/`
- Thư mục dữ liệu: `EcoShop/data/`
- Các tệp tin dữ liệu chính:
  1. `users.json`: Chứa mảng đối tượng người dùng. Điểm đặc biệt là mảng chứa các đối tượng có thuộc tính đa hình `"type": "ADMIN"`, `"REGULAR"`, hoặc `"VIP"`.
  2. `products.json`: Danh mục và số lượng tồn kho của các mặt hàng.
  3. `orders.json`: Lịch sử các đơn hàng đã thanh toán cùng chi tiết danh sách sản phẩm mua.
- Khi ứng dụng khởi chạy lần đầu trên máy tính mới chưa có thư mục `data/`, hệ thống sẽ tự động tạo thư mục và ghi dữ liệu mẫu (*seed data*) để người dùng có thể đăng nhập thử nghiệm ngay lập tức.
