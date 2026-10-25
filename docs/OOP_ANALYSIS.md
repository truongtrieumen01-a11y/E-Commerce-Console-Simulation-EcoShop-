# Phân Tích Lập Trình Hướng Đối Tượng — EcoShop

> **Môn học:** Lập Trình Hướng Đối Tượng (OOP) — HK1 2026–2027  
> **Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng  
> **Dự án:** Hệ thống Cửa hàng Trực tuyến EcoShop  
> **Ngôn ngữ triển khai:** Java (Thuần Java SE, không dùng thư viện ngoài)

---

## Mục Lục
1. [Tổng Quan Kiến Trúc Hướng Đối Tượng](#tổng-quan-kiến-trúc-hướng-đối-tượng)
2. [Trụ Cột 1: Đóng Gói (Encapsulation)](#1-đóng-gói-encapsulation)
3. [Trụ Cột 2: Kế Thừa (Inheritance)](#2-kế-thừa-inheritance)
4. [Trụ Cột 3: Đa Hình (Polymorphism)](#3-đa-hình-polymorphism)
5. [Trụ Cột 4: Trừu Tượng (Abstraction)](#4-trừu-tượng-abstraction)
6. [Cơ Chế Xử Lý Ngoại Lệ (Error Handling)](#5-xử-lý-ngoại-lệ-error-handling)
7. [Sơ Đồ Quan Hệ & Giải Thích Chi Tiết](#6-sơ-đồ-quan-hệ-kèm-giải-thích)
8. [Tổng Kết & Đánh Giá Thiết Kế](#7-tổng-kết--đánh-giá-thiết-kế)

---

## Tổng Quan Kiến Trúc Hướng Đối Tượng

Dự án **EcoShop** được thiết kế dựa trên các nguyên lý chuẩn mực của Lập trình Hướng đối tượng (OOP). Hệ thống mô phỏng một sàn thương mại điện tử hoàn chỉnh với đầy đủ các phân hệ: quản lý sản phẩm, tài khoản phân quyền, giỏ hàng, đặt hàng, tính toán chiết khấu và xử lý thanh toán đa phương thức.

| Trụ Cột OOP | Mục Tiêu Thiết Kế Trong EcoShop | Vị Trí Triển Khai Tiêu Biểu |
| :--- | :--- | :--- |
| **Đóng Gói** *(Encapsulation)* | Che giấu trạng thái nội tại, bảo vệ tính toàn vẹn dữ liệu thông qua getters/setters và phòng thủ sao chép. | `Product`, `ShoppingCart`, `User`, `Order` |
| **Kế Thừa** *(Inheritance)* | Tái sử dụng mã nguồn, tổ chức phân cấp quan hệ *"is-a"* giữa các nhóm người dùng và hệ thống ngoại lệ. | `User` → `Customer`, `Admin`; `Customer` → `RegularCustomer`, `VIPCustomer`; `EcoShopException` hierarchy |
| **Đa Hình** *(Polymorphism)* | Cho phép cùng một thông điệp được thực thi với hành vi chuyên biệt tại thời điểm chạy (*Dynamic Dispatch*). | `calculateDiscount()`, `getRole()`, `getCustomerType()`, `toJson()` |
| **Trừu Tượng** *(Abstraction)* | Thiết lập các giao ước chung (interfaces) và lớp cơ sở trừu tượng (abstract classes), ẩn giấu chi tiết kỹ thuật phức tạp. | `Discountable`, `JsonSerializable`, `User`, `Customer`, `JsonDataManager`, `PaymentProcessor` |

---

## 1. Đóng Gói (Encapsulation)

### 1.1. Bản Chất và Nguyên Tắc Áp Dụng
Đóng gói là kỹ thuật che giấu thông tin (*Information Hiding*) và gói gọn dữ liệu (thuộc tính) cùng với các phương thức thao tác trên dữ liệu đó vào trong cùng một đơn vị (lớp). Trong EcoShop:
- **100% thuộc tính** trong các lớp mô hình (`model`) được khai báo với phạm vi truy cập `private` (hoặc `protected` đối với lớp cha cần chia sẻ cho lớp con kế thừa).
- Việc truy cập và sửa đổi trạng thái đối tượng **bắt buộc** phải đi qua các phương thức `getter` và `setter` có kèm logic kiểm tra tính hợp lệ (*Data Validation*).
- **Phòng thủ sao chép (Defensive Copying):** Các thuộc tính kiểu tập hợp (`List`) không bao giờ được trả về trực tiếp nhằm tránh việc đối tượng bên ngoài can thiệp làm thay đổi cấu trúc danh sách nội bộ.

### 1.2. Minh Họa Chi Tiết Qua Mã Nguồn

#### a. Kiểm soát giá và tồn kho trong `Product.java`
Biến `price` và `stockQuantity` được bảo vệ chặt chẽ, không cho phép nhận giá trị âm; việc giảm hàng tồn kho được ràng buộc bởi nghiệp vụ `reduceStock`:

```java
public class Product implements JsonSerializable {
    private String productId;
    private String name;
    private double price;
    private int    stockQuantity;

    // Chỉ cho phép cập nhật giá nếu giá trị không âm
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    // Nghiệp vụ trừ tồn kho an toàn: ném lỗi nếu không đủ hàng
    public void reduceStock(int quantity) {
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("Không đủ hàng trong kho: " + name);
        }
        stockQuantity -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity > 0) {
            stockQuantity += quantity;
        }
    }
}
```

#### b. Quản lý trạng thái danh sách giỏ hàng trong `ShoppingCart.java`
Lớp `ShoppingCart` sở hữu `private List<CartItem> items`. Bên ngoài không thể thêm trực tiếp một phần tử vào danh sách mà phải thông qua các phương thức nghiệp vụ, đồng thời phương thức `getItems()` trả về một bản sao độc lập:

```java
public class ShoppingCart {
    private List<CartItem> items; // Hoàn toàn private

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) throws InsufficientStockException {
        if (quantity <= 0) return;
        int currentInCart = getQuantityInCart(product.getProductId());
        int totalRequested = currentInCart + quantity;
        
        // Kiểm tra hợp lệ tồn kho trước khi nhận vào giỏ
        if (totalRequested > product.getStockQuantity()) {
            throw new InsufficientStockException(product.getName(), totalRequested, product.getStockQuantity());
        }

        // Nếu đã có trong giỏ thì chỉ cộng dồn số lượng
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    // Defensive Copying: Trả về bản sao để bảo vệ List gốc
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
}
```

#### c. Bảo mật mật khẩu người dùng trong `User.java`
Mật khẩu được lưu trữ an toàn, **không cung cấp phương thức `getPassword()` public**. Việc kiểm tra đăng nhập được đóng gói trọn vẹn trong phương thức `login`:

```java
public abstract class User implements JsonSerializable {
    protected String userId;
    protected String username;
    protected String password; // Được bảo vệ nội bộ

    // Xác thực mật khẩu đóng gói — không phơi bày chuỗi password gốc ra ngoài
    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getUserId()   { return userId;   }
    public String getUsername() { return username; }
    public void setPassword(String password) { this.password = password; }
    // Tuyệt đối không có public String getPassword()!
}
```

---

## 2. Kế Thừa (Inheritance)

### 2.1. Cấu Trúc Phân Cấp Kế Thừa
Kế thừa cho phép các lớp dẫn xuất (*subclasses*) tái sử dụng thuộc tính và phương thức từ lớp cơ sở (*superclass*), đồng thời mở rộng hoặc chuyên biệt hóa hành vi theo mối quan hệ *"là-một"* (*is-a*).

Hệ thống EcoShop xây dựng hai cây phân cấp kế thừa cốt lõi:

```
[1] Cây phân cấp Người dùng (User Hierarchy):
    User (Lớp cơ sở trừu tượng)
    ├── Admin (Người quản trị hệ thống)
    └── Customer (Khách hàng trừu tượng)
        ├── RegularCustomer (Khách hàng thành viên thông thường)
        └── VIPCustomer (Khách hàng thân thiết VIP)

[2] Cây phân cấp Ngoại lệ (Exception Hierarchy):
    java.lang.Exception
    └── EcoShopException (Ngoại lệ cơ sở của ứng dụng)
        ├── InvalidInputException (Lỗi nhập liệu sai)
        ├── ProductNotFoundException (Lỗi không tìm thấy sản phẩm)
        ├── InsufficientStockException (Lỗi không đủ tồn kho)
        └── PaymentFailedException (Lỗi thanh toán thất bại)
```

### 2.2. Phân Tích Kế Thừa và Tái Sử Dụng Mã Nguồn

| Lớp | Lớp Cha Trực Tiếp | Thuộc Tính Kế Thừa | Thuộc Tính Mở Rộng Bổ Sung |
| :--- | :--- | :--- | :--- |
| `Customer` | `User` | `userId`, `username`, `password` | `address`, `phoneNumber`, `cart`, `orderHistory` |
| `Admin` | `User` | `userId`, `username`, `password` | `adminLevel`, `permissions` |
| `RegularCustomer` | `Customer` | Kế thừa tất cả từ `Customer` & `User` | `loyaltyPoints` (Điểm tích lũy) |
| `VIPCustomer` | `Customer` | Kế thừa tất cả từ `Customer` & `User` | `vipLevel` (Cấp 1-3), `memberSince` |

#### Chuỗi Constructor Chain (`super`)
Mã nguồn tận dụng từ khóa `super` để truyền các thông số dùng chung lên lớp cha, tránh trùng lặp code khởi tạo:

```java
// Trong Customer.java
public Customer(String userId, String username, String password,
                String address, String phoneNumber) {
    super(userId, username, password); // Gọi constructor của User
    this.address      = address;
    this.phoneNumber  = phoneNumber;
    this.cart         = new ShoppingCart();
    this.orderHistory = new ArrayList<>();
}

// Trong VIPCustomer.java
public VIPCustomer(String userId, String username, String password,
                   String address, String phoneNumber, int vipLevel) {
    super(userId, username, password, address, phoneNumber); // Gọi constructor Customer
    this.vipLevel    = (vipLevel >= 1 && vipLevel <= 3) ? vipLevel : 1;
    this.memberSince = LocalDate.now();
}
```

---

## 3. Đa Hình (Polymorphism)

### 3.1. Phương Thức Đa Hình Trọng Tâm: `calculateDiscount()`
Đa hình lúc chạy (*Runtime Polymorphism / Dynamic Method Dispatch*) là điểm nhấn kiến trúc quan trọng nhất trong đề án EcoShop. Phương thức `calculateDiscount(double totalAmount)` được khai báo tại interface `Discountable` và được ghi đè (*override*) với thuật toán hoàn toàn khác nhau ở từng lớp con:

```
                           ┌───────────────────────────────┐
                           │   <<interface>> Discountable  │
                           ├───────────────────────────────┤
                           │ + calculateDiscount(double)   │
                           └──────────────┬────────────────┘
                                          ▲ implements
                           ┌──────────────┴────────────────┐
                           │   Customer (abstract class)   │
                           ├───────────────────────────────┤
                           │ + calculateDiscount(double)*  │
                           └───────┬───────────────┬───────┘
                                   │ extends       │ extends
          ┌────────────────────────┴────┐     ┌────┴────────────────────────┐
          │       RegularCustomer       │     │         VIPCustomer         │
          ├─────────────────────────────┤     ├─────────────────────────────┤
          │ Giảm 0% - 5% theo điểm      │     │ Giảm 10% - 20% theo cấp bậc │
          │ (0 / 100 / 500 / 1000 pts)  │     │ (Bạc / Vàng / Bạch Kim)     │
          └─────────────────────────────┘     └─────────────────────────────┘
```

#### Quy tắc tính giảm giá:
1. **`RegularCustomer`** (Dựa vào điểm tích lũy `loyaltyPoints`):
   - $\ge 1.000$ điểm: Giảm **5%**
   - $\ge 500$ điểm: Giảm **3%**
   - $\ge 100$ điểm: Giảm **1%**
   - $< 100$ điểm: **0%** (không giảm)

   ```java
   @Override
   public double calculateDiscount(double totalAmount) {
       if (loyaltyPoints >= 1000) return totalAmount * 0.05;
       if (loyaltyPoints >= 500)  return totalAmount * 0.03;
       if (loyaltyPoints >= 100)  return totalAmount * 0.01;
       return 0.0;
   }
   ```

2. **`VIPCustomer`** (Dựa vào cấp VIP `vipLevel`):
   - Cấp 1 (VIP Bạc): Giảm cố định **10%**
   - Cấp 2 (VIP Vàng): Giảm cố định **15%**
   - Cấp 3 (VIP Bạch Kim): Giảm cố định **20%**

   ```java
   @Override
   public double calculateDiscount(double totalAmount) {
       switch (vipLevel) {
           case 1: return totalAmount * 0.10;
           case 2: return totalAmount * 0.15;
           case 3: return totalAmount * 0.20;
           default: return totalAmount * 0.10;
       }
   }
   ```

#### Điểm kích hoạt đa hình tại `Order.calculateFinal()`:
Tại thời điểm biên dịch (*compile-time*), lớp `Order` chỉ tham chiếu đến kiểu dữ liệu cha trừu tượng là `Customer`. Nhưng khi chạy (*runtime*), máy ảo JVM tự động quyết định hàm giảm giá tương ứng của đối tượng thực tế:

```java
// Trong Order.java
public void calculateFinal() {
    totalAmount    = items.stream().mapToDouble(CartItem::getSubtotal).sum();
    
    // ĐA HÌNH TẠI ĐÂY: customer có thể là RegularCustomer hoặc VIPCustomer
    discountAmount = customer.calculateDiscount(totalAmount);
    
    finalAmount    = totalAmount - discountAmount;
}
```
> **Ý nghĩa:** Khi bổ sung thêm bất kỳ loại khách hàng mới nào (ví dụ: `PartnerCustomer`, `StudentCustomer`), mã nguồn của lớp `Order` **hoàn toàn không cần thay đổi hay viết thêm câu lệnh `if-else` nào** (tuân thủ nguyên lý *Open/Closed Principle*).

### 3.2. Các Biểu Hiện Đa Hình Khác

| Phương Thức | Nơi Khai Báo Ban Đầu | Biểu Hiện Trong Các Lớp Con | Ý Nghĩa Thực Tế |
| :--- | :--- | :--- | :--- |
| `getRole()` | `User` *(abstract)* | `Admin` $\rightarrow$ `"ADMIN"`<br>`Customer` $\rightarrow$ `"CUSTOMER"` | Phân quyền màn hình và menu chức năng |
| `getCustomerType()` | `Customer` *(abstract)* | `RegularCustomer` $\rightarrow$ `"Khách Thường"`<br>`VIPCustomer` $\rightarrow$ `"VIP Bạc" / "VIP Vàng" / "VIP Bạch Kim"` | Hiển thị thông tin định danh trên hóa đơn |
| `toJson()` | `JsonSerializable` *(interface)* | Mỗi lớp (`User`, `Product`, `Order`, `Admin`,...) tự định nghĩa chuỗi JSON tương ứng với các thuộc tính riêng của mình | Chuẩn hóa cơ chế tuần tự hóa dữ liệu (Serialization) |

---

## 4. Trừu Tượng (Abstraction)

### 4.1. Khái Niệm và Ứng Dụng Trong Dự Án
Trừu tượng là quá trình ẩn đi những chi tiết cài đặt phức tạp bên trong và chỉ phơi bày ra bên ngoài các tính năng cốt lõi (xác định *"đối tượng làm được gì"* thay vì *"làm bằng cách nào"*).

### 4.2. Khế Ước Giao Tiếp (Interfaces)
- **`Discountable`:** Khai báo một giao ước duy nhất cho việc chiết khấu:
  ```java
  public interface Discountable {
      double calculateDiscount(double totalAmount);
  }
  ```
- **`JsonSerializable`:** Khai báo phương thức tuần tự hóa đối tượng thành chuỗi JSON:
  ```java
  public interface JsonSerializable {
      String toJson();
  }
  ```

### 4.3. Lớp Cơ Sở Trừu Tượng (Abstract Classes)
- **`User`:** Đại diện cho một tài khoản trừu tượng trong hệ thống. Lớp này không thể khởi tạo trực tiếp (`new User(...)` sẽ báo lỗi biên dịch). Khai báo phương thức trừu tượng `public abstract String getRole();` buộc tất cả các tài khoản thực tế phải xác định rõ vai trò.
- **`Customer`:** Đóng vai trò là lớp cha nghiệp vụ cho mọi khách hàng. Nó hiện thực các hành vi dùng chung như đặt hàng (`placeOrder()`), sở hữu giỏ hàng, nhưng giữ nguyên tính trừu tượng của `calculateDiscount()` và `getCustomerType()` để giao cho các lớp con cấp dưới hiện thực.

### 4.4. Ẩn Giấu Chi Tiết Kỹ Thuật (Service Abstraction)
EcoShop trừu tượng hóa các tác vụ phức tạp thành các dịch vụ độc lập:
1. **`JsonDataManager` (Data Access Abstraction):**
   - Ẩn toàn bộ cơ chế đọc/ghi file `FileInputStream`, xử lý bảng mã `UTF-8`, thuật toán duyệt chuỗi và bóc tách cặp khóa/giá trị JSON thuần Java (không dùng Jackson/Gson).
   - Tầng giao diện chỉ cần gọi hàm trừu tượng đơn giản:
     ```java
     List<Product> products = dataManager.loadProducts();
     dataManager.saveProducts(products);
     ```
2. **`PaymentProcessor` (Business Logic Abstraction):**
   - Ẩn toàn bộ logic nghiệp vụ xử lý thanh toán từng phương thức:
     - `TIEN_MAT`: Tính tiền thừa thối lại cho khách.
     - `THE_NGAN_HANG`: Kiểm tra định dạng thẻ 16 chữ số và mã bí mật CVV 3 chữ số.
     - `VI_DIEN_TU`: Kiểm tra số điện thoại và xác thực mã OTP 6 chữ số.
   - Caller chỉ cần gọi duy nhất một phương thức tổng quát:
     ```java
     paymentProcessor.processPayment(order, paymentMethod, scanner);
     ```

---

## 5. Xử Lý Ngoại Lệ (Error Handling)

### 5.1. Cây Phân Cấp Ngoại Lệ Tùy Chỉnh (Custom Exceptions)
EcoShop xây dựng hệ thống ngoại lệ có cấu trúc rõ ràng bắt nguồn từ `EcoShopException` (kế thừa từ `java.lang.Exception` của Java):

```
                   ┌───────────────────────────────┐
                   │      java.lang.Exception      │
                   └──────────────┬────────────────┘
                                  ▲
                   ┌──────────────┴────────────────┐
                   │       EcoShopException        │
                   │    - errorCode : String       │
                   └──────────────┬────────────────┘
         ┌────────────────────────┼────────────────────────┬────────────────────────┐
         ▲                        ▲                        ▲                        ▲
┌────────┴────────┐      ┌────────┴────────┐      ┌────────┴────────┐      ┌────────┴────────┐
│  InvalidInput   │      │ ProductNotFound │      │InsufficientStock│      │  PaymentFailed  │
│    Exception    │      │    Exception    │      │    Exception    │      │    Exception    │
└─────────────────┘      └─────────────────┘      └─────────────────┘      └─────────────────┘
```

| Tên Ngoại Lệ | Error Code | Tình Huống Kích Hoạt |
| :--- | :--- | :--- |
| `EcoShopException` | `ECO_SHOP_ERROR` | Lớp cha cho mọi lỗi nghiệp vụ trong hệ thống |
| `InvalidInputException` | `INVALID_INPUT` | Người dùng nhập dữ liệu sai định dạng (chuỗi rỗng, số âm,...) |
| `ProductNotFoundException` | `PRODUCT_NOT_FOUND` | Tìm kiếm mã sản phẩm không tồn tại trong danh mục |
| `InsufficientStockException` | `INSUFFICIENT_STOCK` | Số lượng đặt mua vượt quá tồn kho hiện tại |
| `PaymentFailedException` | `PAYMENT_FAILED` | Phương thức thanh toán sai, thiếu tiền, thẻ lỗi hoặc OTP sai |

### 5.2. Nguyên Tắc An Toàn: Ứng Dụng Không Bao Giờ Bị Crash
Hệ thống được thiết kế để xử lý lỗi một cách duyên dáng (*Graceful Degradation*), bất kể người dùng nhập sai kiểu dữ liệu hay xảy ra sự cố logic:

#### a. Phòng thủ nhập liệu trong Console App (`EcoShopApp.java`)
Các hàm tiện ích nhập liệu sử dụng vòng lặp vô hạn `while (true)` kết hợp khối `try-catch` bắt `NumberFormatException`. Ứng dụng không bao giờ bị dừng đột ngột khi người dùng nhập ký tự chữ vào trường nhập số:

```java
private int readInt(String prompt, int min, int max) {
    while (true) {
        System.out.print(prompt);
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            if (value < min || value > max) {
                printError("Vui lòng nhập số từ " + min + " đến " + max + ".");
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            printError("Vui lòng nhập một số nguyên hợp lệ!");
        }
    }
}
```

#### b. Phòng thủ sự kiện trong Swing GUI (`EcoShopGUI.java`)
Trong giao diện đồ họa, toàn bộ các sự kiện nút bấm (`ActionListener`) đều được bọc trong các khối `try-catch`. Khi xảy ra ngoại lệ, hệ thống hiển thị hộp thoại cảnh báo `JOptionPane` thân thiện với người dùng và giữ nguyên tiến trình hoạt động của giao diện:

```java
btnCheckout.addActionListener(e -> {
    try {
        Order order = customer.placeOrder();
        paymentProcessor.processPayment(order, selectedMethod, null);
        JOptionPane.showMessageDialog(this, "Thanh toán thành công! Mã đơn: " + order.getOrderId(),
                "Thành Công", JOptionPane.INFORMATION_MESSAGE);
    } catch (InsufficientStockException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Hết Hàng", JOptionPane.WARNING_MESSAGE);
    } catch (EcoShopException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nghiệp Vụ [" + ex.getErrorCode() + "]", 
                JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
    }
});
```

---

## 6. Sơ Đồ Quan Hệ (Kèm Giải Thích)

### 6.1. Sơ Đồ Kiến Trúc Lớp (Class Architecture Diagram)

```
        <<interface>>                     <<interface>>
        Discountable                     JsonSerializable
             ▲                                ▲
             │ implements                     │ implements
             │                  ┌─────────────┴────────────────┐
             │                  │                              │
      ┌──────┴──────┐     ┌─────┴───────┐                ┌─────┴─────┐
      │  Customer   │────►│    User     │                │  Product  │
      │ (abstract)  │     │  (abstract) │                └─────▲─────┘
      └──────┬──────┘     └─────▲───────┘                      │
             │ extends          │ extends                      │
      ┌──────┴──────┐     ┌─────┴───────┐                      │
      │             │     │    Admin    │                      │
┌─────┴────────┐ ┌──┴────────────┐                             │
│RegularCustomer│ │ VIPCustomer  │                             │
└──────────────┘ └───────────────┘                             │
                                                               │
┌──────────────┐          ┌──────────────┐                     │
│ ShoppingCart │◆────────►│   CartItem   │─────────────────────┘
└──────────────┘          └──────────────┘
       ▲                         ▲
       │ composition             │ composition
┌──────┴───────┐          ┌──────┴───────┐
│   Customer   │◆────────►│    Order     │───► implements JsonSerializable
└──────────────┘ (history)└──────────────┘
```

### 6.2. Phân Tích Các Mối Quan Hệ Giữa Các Thực Thể

| Mối Quan Hệ | Ký Hiệu | Các Thực Thể Tham Gia | Giải Thích Bản Chất Thiết Kế |
| :--- | :---: | :--- | :--- |
| **Kế thừa** *(Inheritance)* | $\longrightarrow$ (rỗng) | `User` $\leftarrow$ `Admin`, `Customer`<br>`Customer` $\leftarrow$ `RegularCustomer`, `VIPCustomer`<br>`EcoShopException` $\leftarrow$ Sub-exceptions | Quan hệ *"là-một"* (*is-a*). Lớp con thừa hưởng toàn bộ trạng thái và hành vi của lớp cha, chuyên biệt hóa phương thức trừu tượng. |
| **Hiện thực hóa** *(Realization)* | $-\ - \triangleright$ | `Customer` $\rightarrow$ `Discountable`<br>`User`, `Product`, `Order` $\rightarrow$ `JsonSerializable` | Cam kết hiện thực đầy đủ các phương thức được định nghĩa trong Interface. |
| **Hợp thành** *(Composition)* | $\blacklozenge\!\!-$ | `Customer` $\blacklozenge\!\!-$ `ShoppingCart`<br>`ShoppingCart` $\blacklozenge\!\!-$ `CartItem`<br>`Order` $\blacklozenge\!\!-$ `CartItem` | Quan hệ sở hữu chặt chẽ (*strong has-a*). Vòng đời của `CartItem` gắn liền với `ShoppingCart` hoặc bản chụp hóa đơn `Order`. Khi giỏ hàng bị xóa, các `CartItem` bên trong cũng bị hủy. |
| **Thu nạp / Tập hợp** *(Aggregation)* | $\lozenge\!\!-$ | `Customer` $\lozenge\!\!-$ `Order` (Lịch sử đơn hàng) | Quan hệ chứa đựng lỏng lẻo. Đơn hàng thuộc về lịch sử của khách hàng nhưng tồn tại độc lập như một chứng từ kế toán trong hệ thống. |
| **Kết hợp** *(Association)* | $\longrightarrow$ | `CartItem` $\longrightarrow$ `Product`<br>`Order` $\longrightarrow$ `Customer` | Mỗi `CartItem` nắm giữ tham chiếu đến một đối tượng `Product` cụ thể để lấy thông tin đơn giá và tồn kho. |
| **Phụ thuộc** *(Dependency)* | $-\ - \rightarrow$ | `Order` $-\ - \rightarrow$ `Discountable`<br>`EcoShopApp` $-\ - \rightarrow$ `JsonDataManager` | Một lớp sử dụng dịch vụ hoặc phương thức của một lớp khác để thực hiện nghiệp vụ. |

---

## 7. Tổng Kết & Đánh Giá Thiết Kế

1. **Tính Đúng Đắn Về Mặt Học Thuật:**
   - Đồ án thể hiện trọn vẹn và chuẩn xác **4 trụ cột OOP** theo yêu cầu môn học do ThS. Huỳnh Xuân Phụng phụ trách.
   - Không lạm dụng biến toàn cục (`static`), bảo đảm tính độc lập và khả năng đóng gói của từng thực thể.
2. **Khả Năng Bảo Trì & Mở Rộng (Extensibility):**
   - Áp dụng tốt các nguyên lý **SOLID** (đặc biệt là Single Responsibility và Open/Closed Principle).
   - Khi cần thêm nhóm khách hàng mới (ví dụ: `WholesaleCustomer` — Khách sỉ), lập trình viên chỉ cần tạo lớp mới kế thừa `Customer` mà không phải chỉnh sửa bất kỳ dòng lệnh nào trong `Order`, `PaymentProcessor` hay `ShoppingCart`.
3. **Tính Ổn Định & Thực Tiễn:**
   - Hệ thống xử lý ngoại lệ chặt chẽ, bảo đảm ứng dụng không bao giờ bị dừng đột ngột.
   - Khả năng lưu trữ JSON tự xây dựng chứng minh năng lực hiểu sâu về cấu trúc dữ liệu, giải thuật duyệt chuỗi và cơ chế vào/ra dữ liệu (I/O Streams) trong Java SE.
