# Cấu Trúc Dữ Liệu — EcoShop

> **Học phần:** Lập trình Hướng đối tượng (OOP) — Học kỳ 1, 2026-2027  
> **Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng  
> **Dự án:** Hệ thống quản lý bán hàng EcoShop  
> **Tài liệu:** Đặc tả chi tiết lược đồ dữ liệu JSON (Database Schema Specification)

---

## Tổng Quan Kiến Trúc Lưu Trữ

Hệ thống **EcoShop** được thiết kế theo hướng phi cơ sở dữ liệu quan hệ (No-RDBMS), sử dụng định dạng tập tin **JSON thuần (Plain JSON)** để lưu trữ toàn bộ dữ liệu nghiệp vụ.

- **Vị trí lưu trữ:** Thư mục `data/` nằm tại thư mục gốc của dự án.
- **Tập tin dữ liệu:**
  1. `data/users.json` — Danh sách người dùng hệ thống (Quản trị viên & Khách hàng).
  2. `data/products.json` — Danh mục và kho sản phẩm.
  3. `data/orders.json` — Danh sách đơn hàng và lịch sử giao dịch.
- **Tự động khởi tạo:** Khi ứng dụng khởi chạy lần đầu, nếu thư mục `data/` hoặc các file JSON chưa tồn tại, hệ thống sẽ tự động tạo cấu trúc thư mục và nạp dữ liệu mẫu ban đầu (seed data).
- **Trình xử lý dữ liệu (Parser):** Lớp `JsonDataManager.java` đóng vai trò là custom parser/serializer được hiện thực bằng Java chuẩn (`java.io`, `java.util`, `java.util.regex`), **hoàn toàn không sử dụng thư viện bên ngoài** (như Jackson, Gson hay Fastjson) nhằm đáp ứng triệt để yêu cầu học phần OOP.

```
EcoShop/
├── data/
│   ├── users.json       <-- Polymorphic User Collection
│   ├── products.json    <-- Product Catalog Collection
│   └── orders.json      <-- Nested Orders Collection
└── src/
    └── service/
        └── JsonDataManager.java
```

---

## 1. `users.json` — Người Dùng (Polymorphic Array)

Tập tin `users.json` triển khai mô hình **Polymorphic Array (Mảng đa hình)**. Toàn bộ các đối tượng kế thừa từ lớp trừu tượng `User` (`Admin`, `RegularCustomer`, `VIPCustomer`) được lưu trữ chung trong một danh sách JSON duy nhất. 

Trường `"type"` đóng vai trò là **Type Discriminator (Thuộc tính phân biệt loại)** giúp bộ phân tích dữ liệu tái tạo chính xác đối tượng lớp con tương ứng trong bộ nhớ.

### 1.1. Admin (Quản trị viên hệ thống)

Quản trị viên chịu trách nhiệm vận hành hệ thống, quản lý danh mục sản phẩm, duyệt và xử lý trạng thái đơn hàng, cũng như quản lý người dùng.

```json
{
  "type": "Admin",
  "userId": "A001",
  "username": "admin",
  "password": "admin123",
  "adminLevel": 1,
  "permissions": [
    "MANAGE_PRODUCTS",
    "VIEW_ORDERS",
    "MANAGE_USERS",
    "CHANGE_ORDER_STATUS"
  ]
}
```

> [!NOTE]
> - `adminLevel = 1`: **Super Admin** — Toàn quyền hệ thống, bao gồm cả quyền cập nhật trạng thái đơn hàng (`CHANGE_ORDER_STATUS`).
> - `adminLevel = 2`: **Admin thường** — Quyền hạn giới hạn (chỉ quản lý sản phẩm, xem đơn hàng, hỗ trợ người dùng cơ bản).
> - Mảng `permissions`: Danh sách chuỗi đại diện cho các quyền chức năng đã cấp.

---

### 1.2. RegularCustomer (Khách hàng thông thường)

Khách hàng tiêu chuẩn tích lũy điểm thưởng qua mỗi lần mua sắm để được hưởng ưu đãi hoặc quy đổi quyền lợi.

```json
{
  "type": "RegularCustomer",
  "userId": "C001",
  "username": "alice",
  "password": "pass123",
  "address": "123 Nguyễn Trãi, TP.HCM",
  "phoneNumber": "0901234567",
  "loyaltyPoints": 0
}
```

> [!TIP]
> **Cơ chế tích điểm:** Mỗi khi hoàn thành đơn hàng, `loyaltyPoints` được cộng dồn theo tỉ lệ:
> $$\text{Điểm thưởng cộng thêm} = \left\lfloor \frac{\text{Giá trị thanh toán cuối cùng}}{10} \right\rfloor$$
> Điểm thưởng có thể dùng để khấu trừ tiền cho các đơn hàng kế tiếp.

---

### 1.3. VIPCustomer (Khách hàng thân thiết VIP)

Khách hàng VIP nhận được chiết khấu trực tiếp trên từng đơn hàng dựa theo hạng thẻ thành viên.

```json
{
  "type": "VIPCustomer",
  "userId": "C002",
  "username": "bob",
  "password": "pass123",
  "address": "456 Lê Lợi, TP.HCM",
  "phoneNumber": "0912345678",
  "vipLevel": 2,
  "memberSince": "2026-10-01"
}
```

> [!IMPORTANT]
> **Phân hạng VIP và tỷ lệ giảm giá:**
> - `vipLevel = 1` (**Hạng Bạc**): Chiết khấu cố định **10%** tổng giá trị đơn hàng.
> - `vipLevel = 2` (**Hạng Vàng**): Chiết khấu cố định **15%** tổng giá trị đơn hàng.
> - `vipLevel = 3` (**Hạng Bạch Kim**): Chiết khấu cố định **20%** tổng giá trị đơn hàng.
> - `memberSince`: Định dạng ngày chuẩn ISO `YYYY-MM-DD` ghi nhận ngày kích hoạt thẻ VIP.

---

## 2. `products.json` — Sản Phẩm (Product Catalog)

Tập tin `products.json` lưu trữ danh mục các sản phẩm đang được bày bán trong cửa hàng. Dữ liệu này được đọc để hiển thị danh sách cho khách hàng và cập nhật số lượng tồn kho theo thời gian thực mỗi khi có đơn đặt hàng.

```json
{
  "productId": "P001",
  "name": "Laptop Dell XPS 15",
  "description": "Laptop hiệu năng cao",
  "price": 1299.99,
  "stockQuantity": 10,
  "category": "Điện tử"
}
```

### Các danh mục mặc định (`category`):
- **Điện Tử**: Thiết bị số, máy tính xách tay, điện thoại, phụ kiện công nghệ.
- **Giày Dép**: Giày thể thao, giày công sở, dép thời trang.
- **Quần Áo**: Thời trang nam, nữ, đồ bảo hộ thể thao.
- **Sách**: Sách giáo trình, văn học, kỹ năng sống, công nghệ thông tin.
- **Nhà Bếp**: Dụng cụ nấu nướng, gia dụng thông minh.
- **Thể Thao**: Dụng cụ rèn luyện thể thao, phụ kiện dã ngoại.

---

## 3. `orders.json` — Đơn Hàng (Nested Documents)

Tập tin `orders.json` lưu trữ toàn bộ các đơn hàng đã được khởi tạo trong hệ thống. Cấu trúc mỗi đơn hàng bao gồm thông tin thanh toán tổng hợp và một danh sách các mặt hàng lồng nhau (**Nested Document / Composition Pattern**).

```json
{
  "orderId": "DH-C002-1790871361374",
  "customerId": "C002",
  "customerName": "bob",
  "items": [
    {
      "productId": "P001",
      "productName": "Laptop Dell XPS 15",
      "quantity": 1,
      "subtotal": 29990000.00
    }
  ],
  "totalAmount": 29990000.00,
  "discountAmount": 4498500.00,
  "finalAmount": 25491500.00,
  "orderDate": "01/10/2026 23:16:01",
  "status": "CONFIRMED",
  "paymentMethod": "TIEN_MAT"
}
```

### 3.1. Quy tắc sinh mã đơn hàng (`orderId`)
Định dạng: `DH-{userId}-{timestamp}`
- `DH`: Tiền tố viết tắt của "Đơn Hàng".
- `{userId}`: Mã định danh của khách hàng đặt mua (ví dụ: `C002`).
- `{timestamp}`: Dấu thời gian tính theo mili-giây (Unix Epoch Milliseconds) tại thời điểm khởi tạo, đảm bảo tính duy nhất và khả năng sắp xếp theo thời gian.

### 3.2. Trạng thái đơn hàng (`status`)
Hệ thống quản lý vòng đời đơn hàng qua các trạng thái (State Machine):
- `PENDING`: Đơn hàng mới lập, chờ nhân viên hoặc hệ thống duyệt.
- `CONFIRMED`: Đơn hàng đã được xác nhận hợp lệ, số lượng tồn kho của các sản phẩm tương ứng đã được trừ.
- `SHIPPED`: Đơn hàng đã xuất kho và bàn giao cho đơn vị vận chuyển.
- `DELIVERED`: Đơn hàng đã giao thành công tới khách hàng; hoàn tất giao dịch.
- `CANCELLED`: Đơn hàng đã hủy; số lượng hàng hóa được hoàn trả lại kho tương ứng nếu đã trừ trước đó.

### 3.3. Phương thức thanh toán (`paymentMethod`)
- `TIEN_MAT`: Thanh toán tiền mặt khi nhận hàng (COD).
- `THE_NGAN_HANG`: Chuyển khoản ngân hàng hoặc thanh toán qua thẻ ghi nợ/tín dụng.
- `VI_DIEN_TU`: Thanh toán qua ví điện tử tích hợp (Momo, ZaloPay, ViettelMoney).

---

## Bảng Tổng Hợp Chi Tiết Trường Dữ Liệu

### 1. Bảng thực thể Người Dùng (`users.json`)

| Tên trường | Kiểu dữ liệu | Bắt buộc | Áp dụng cho | Ý nghĩa & Quy tắc |
| :--- | :--- | :---: | :--- | :--- |
| `type` | `String` | Có | Tất cả | Phân loại người dùng: `"Admin"`, `"RegularCustomer"`, `"VIPCustomer"`. |
| `userId` | `String` | Có | Tất cả | Mã định danh duy nhất (VD: `A001`, `C001`, `C002`). |
| `username` | `String` | Có | Tất cả | Tên đăng nhập duy nhất không trùng lặp. |
| `password` | `String` | Có | Tất cả | Mật khẩu tài khoản (dạng chuỗi thuần hoặc mã hóa). |
| `adminLevel` | `int` | Có | `Admin` | Cấp bậc quản trị: `1` (Super Admin), `2` (Admin thường). |
| `permissions` | `String[]` | Có | `Admin` | Mảng chứa các quyền thao tác trong hệ thống. |
| `address` | `String` | Có | Khách hàng | Địa chỉ nhận hàng của khách hàng. |
| `phoneNumber` | `String` | Có | Khách hàng | Số điện thoại liên hệ (10 chữ số). |
| `loyaltyPoints` | `int` | Có | `RegularCustomer` | Số điểm thưởng tích lũy được qua các đơn hàng. |
| `vipLevel` | `int` | Có | `VIPCustomer` | Cấp độ thành viên VIP: `1` (Bạc - 10%), `2` (Vàng - 15%), `3` (Bạch Kim - 20%). |
| `memberSince` | `String` | Có | `VIPCustomer` | Ngày kích hoạt tài khoản VIP (`YYYY-MM-DD`). |

---

### 2. Bảng thực thể Sản Phẩm (`products.json`)

| Tên trường | Kiểu dữ liệu | Bắt buộc | Ràng buộc | Ý nghĩa & Diễn giải |
| :--- | :--- | :---: | :--- | :--- |
| `productId` | `String` | Có | Duy nhất | Mã sản phẩm (VD: `P001`, `P002`,...). |
| `name` | `String` | Có | Không rỗng | Tên thương mại của sản phẩm. |
| `description` | `String` | Không | Tùy chọn | Mô tả chi tiết tính năng, thông số sản phẩm. |
| `price` | `double` | Có | $\ge 0.0$ | Đơn giá niêm yết của sản phẩm (VND). |
| `stockQuantity`| `int` | Có | $\ge 0$ | Số lượng hàng tồn khả dụng trong kho. |
| `category` | `String` | Có | Danh mục hợp lệ| Phân loại hàng hóa (Điện Tử, Giày Dép, Quần Áo, Sách, Nhà Bếp, Thể Thao). |

---

### 3. Bảng thực thể Đơn Hàng (`orders.json`)

#### Bảng thông tin đơn hàng chung (Order Root):

| Tên trường | Kiểu dữ liệu | Bắt buộc | Ràng buộc / Định dạng | Ý nghĩa & Diễn giải |
| :--- | :--- | :---: | :--- | :--- |
| `orderId` | `String` | Có | `DH-{userId}-{timestamp}` | Mã định danh đơn hàng duy nhất toàn hệ thống. |
| `customerId` | `String` | Có | Khớp `userId` | Mã người mua hàng. |
| `customerName` | `String` | Có | Không rỗng | Tên đăng nhập của người mua tại thời điểm đặt. |
| `items` | `Array` | Có | Ít nhất 1 phần tử | Danh sách các mặt hàng chi tiết trong đơn. |
| `totalAmount` | `double` | Có | $\sum \text{subtotal}$ | Tổng tiền hàng trước chiết khấu (VND). |
| `discountAmount`| `double` | Có | $\ge 0.0$ | Số tiền được giảm giá theo hạng khách hàng hoặc điểm. |
| `finalAmount` | `double` | Có | `totalAmount - discountAmount` | Số tiền thực thanh toán của đơn hàng (VND). |
| `orderDate` | `String` | Có | `dd/MM/yyyy HH:mm:ss` | Thời gian đặt hàng được ghi nhận. |
| `status` | `String` | Có | `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED` | Trạng thái xử lý của đơn hàng. |
| `paymentMethod`| `String` | Có | `TIEN_MAT`, `THE_NGAN_HANG`, `VI_DIEN_TU` | Phương thức thanh toán được chọn. |

#### Bảng chi tiết mặt hàng (`OrderItem` trong mảng `items`):

| Tên trường | Kiểu dữ liệu | Bắt buộc | Ràng buộc | Ý nghĩa & Diễn giải |
| :--- | :--- | :---: | :--- | :--- |
| `productId` | `String` | Có | Tham chiếu `products.json` | Mã định danh sản phẩm được mua. |
| `productName` | `String` | Có | Snapshot tại thời điểm mua | Tên sản phẩm hiển thị trên hóa đơn. |
| `quantity` | `int` | Có | $> 0$ | Số lượng đặt mua. |
| `subtotal` | `double` | Có | `đơn_giá \times quantity` | Tổng tiền cho riêng mặt hàng này trước giảm giá. |

---

## 4. Nguyên Lý Thiết Kế OOP Áp Dụng Cho Cấu Trúc Dữ Liệu

1. **Tính Đóng Gói (Encapsulation):**
   - Mỗi file JSON tương ứng với tập hợp các đối tượng độc lập (`User`, `Product`, `Order`).
   - Trạng thái bên trong của từng đối tượng chỉ được truy xuất và thay đổi qua các getter/setter và phương thức nghiệp vụ tương ứng trong code Java.

2. **Tính Kế Thừa (Inheritance) & Đa Hình (Polymorphism):**
   - Lớp cơ sở `User` được mở rộng bởi `Admin`, `RegularCustomer` và `VIPCustomer`.
   - Lưu trữ thống nhất trong `users.json` thông qua trường `"type"`. Khi đọc từ file, `JsonDataManager` phân giải linh hoạt để khởi tạo đúng thực thể cụ thể và gọi các phương thức đa hình (ví dụ tính toán chiết khấu `calculateDiscount()`).

3. **Tính Kết Tập & Hợp Thành (Aggregation & Composition):**
   - Mối quan hệ giữa `Order` và `OrderItem` là **Composition**: `OrderItem` nằm lồng trong mảng `items` của `Order` và không tồn tại độc lập bên ngoài vòng đời đơn hàng.
   - Mối quan hệ giữa `Order` và `User`, `Product` là liên kết tham chiếu lỏng (**Loose Coupling**) qua các trường ID (`customerId`, `productId`), giảm phụ thuộc tuần hoàn và dễ đồng bộ hóa dữ liệu.
