# Hướng Dẫn Sử Dụng — EcoShop

Hệ thống quản lý và mua sắm cửa hàng sản phẩm thân thiện với môi trường **EcoShop**.  
Dự án môn học Lập trình hướng đối tượng (OOP) — Học kỳ 1, Năm học 2026-2027.  
**Giảng viên hướng dẫn:** ThS. Huỳnh Xuân Phụng.

---

## 1. Yêu Cầu Hệ Thống

Để khởi chạy và sử dụng phần mềm EcoShop một cách mượt mà nhất, hệ thống máy tính của bạn cần đáp ứng các điều kiện sau:

- **Bộ công cụ Java (JDK):** Java SE Development Kit phiên bản **21 trở lên** (khuyến nghị JDK 21 LTS).
- **Biến môi trường:** Đường dẫn `JAVA_HOME` và `Path` đã trỏ chính xác đến thư mục `bin` của JDK (kiểm tra bằng lệnh `javac -version` hoặc `java -version`).
- **Hệ điều hành:**
  - **Hệ điều hành chính:** Microsoft Windows 10/11 (hỗ trợ tập lệnh chạy nhanh `.bat`).
  - **Hệ điều hành khác:** Linux, macOS (hoàn toàn tương thích và chạy được thông qua lệnh `javac` / `java` nếu đã cài đặt JDK).
- **Độ phân giải màn hình:** Tối thiểu 1024x768 (khuyến nghị 1280x800 hoặc cao hơn để trải nghiệm giao diện tốt nhất).

---

## 2. Cách Chạy Ứng Dụng

EcoShop được đóng gói kèm tập lệnh chạy tự động rất tiện lợi:

1. **Mở thư mục dự án:** Mở thư mục `EcoShop/` trong File Explorer (hoặc Terminal/Command Prompt).
2. **Khởi chạy ứng dụng:**
   - Nhấn đúp chuột vào tệp `run.bat` (hoặc mở CMD/PowerShell và gõ `.\run.bat`).
3. **Biên dịch và khởi động:** Hệ thống sẽ tự động quét toàn bộ các tệp mã nguồn Java, biên dịch sang bytecode với mã hóa UTF-8 (`chcp 65001`) và khởi động giao diện bảng Console trực quan của EcoShop.

> [!NOTE]
> Khi khởi chạy lần đầu tiên, hệ thống sẽ tự động nạp dữ liệu mẫu ban đầu (danh sách sản phẩm, tài khoản mặc định) để người dùng có thể trải nghiệm ngay lập tức mà không cần cấu hình phức tạp.

---

## 3. Danh Sách Tài Khoản Mặc Định

Hệ thống đã chuẩn bị sẵn các tài khoản thử nghiệm tương ứng với các nhóm đối tượng và phân quyền khác nhau:

| Tên đăng nhập (Username) | Mật khẩu (Password) | Loại | Ghi chú & Đặc quyền |
|---|---|---|---|
| `admin` | `admin123` | **Quản Trị Viên** | Super Admin — Toàn quyền quản trị: Quản lý kho sản phẩm, quản lý đơn hàng và người dùng. |
| `alice` | `pass123` | **Khách Thường** | Khách hàng thông thường, khởi đầu với 0 điểm tích lũy. |
| `bob` | `pass123` | **VIP Vàng** | Hạng VIP Vàng, tự động nhận ưu đãi giảm giá cố định **15%**. |

---

## 4. Hướng Dẫn Theo Vai Trò

### 4.1. Khách Hàng (Customer)

Khách hàng có thể trải nghiệm toàn bộ quy trình mua sắm từ duyệt sản phẩm đến đặt hàng và theo dõi đơn mua:

1. **Đăng nhập:** 
   - Nhập chính xác *Tên đăng nhập* và *Mật khẩu* tại màn hình đăng nhập.
   - Nhấn nút **Đăng Nhập** để vào hệ sinh thái mua sắm.
2. **Xem sản phẩm:**
   - Click chọn mục **Sản Phẩm** trên thanh điều hướng bên trái (sidebar).
   - Danh sách các sản phẩm thân thiện với môi trường sẽ hiển thị chi tiết (mã, tên, phân loại, giá bán, số lượng tồn kho).
   - Sử dụng bộ lọc theo danh mục nếu cần tìm kiếm nhanh nhóm sản phẩm mong muốn.
3. **Thêm vào giỏ hàng:**
   - Chọn một sản phẩm trong bảng danh sách.
   - Nhấn nút **Thêm Vào Giỏ Hàng**.
   - Nhập số lượng cần mua vào hộp thoại (số lượng không được vượt quá số tồn kho hiện có).
4. **Thanh toán:**
   - Vào mục **Giỏ Hàng** trên thanh sidebar để kiểm tra các sản phẩm đã chọn, số lượng và tổng tiền.
   - Nhấn nút **Thanh Toán**.
   - Chọn phương thức thanh toán phù hợp (Tiền mặt, Thẻ ngân hàng, Ví điện tử), nhập thông tin cần thiết và nhấn **Xác nhận**.
   - Hóa đơn hoàn chỉnh sẽ được tạo và cập nhật trạng thái "Chờ xử lý".
5. **Xem lịch sử:**
   - Click chọn mục **Lịch Sử Đơn** ở sidebar để theo dõi danh sách tất cả các đơn hàng đã đặt, tổng chi phí và trạng thái đơn hàng.
6. **Xem hồ sơ:**
   - Click chọn mục **Tài Khoản** để xem thông tin cá nhân: Họ tên, số điện thoại, địa chỉ, loại tài khoản (Thường / VIP) và điểm tích lũy hiện có.
7. **Đăng xuất:**
   - Nhấn nút **Đăng Xuất** ở góc dưới cùng thanh sidebar khi kết thúc phiên sử dụng.

---

### 4.2. Đăng Ký Tài Khoản Mới

Người dùng mới có thể chủ động tạo tài khoản trực tiếp trên ứng dụng:

1. Tại màn hình đăng nhập, nhấn nút hoặc liên kết **Tạo Tài Khoản Mới**.
2. Nhập đầy đủ các thông tin:
   - **Tên đăng nhập:** Viết liền không dấu, không trùng lặp với người dùng khác.
   - **Mật khẩu:** Độ dài $\ge 6$ ký tự nhằm đảm bảo an toàn.
   - **Số điện thoại:** Dùng để liên hệ giao nhận hàng hoặc xác thực giao dịch ví điện tử.
   - **Địa chỉ:** Địa chỉ giao nhận hàng mặc định.
3. **Chọn loại tài khoản:**
   - Chọn **Khách Thường** hoặc **VIP**.
   - Nếu chọn VIP, tiến hành chọn cấp bậc VIP mong muốn (*VIP Bạc*, *VIP Vàng*, hoặc *VIP Bạch Kim*).
4. Nhấn **Đăng Ký** để hoàn tất quá trình tạo tài khoản.

---

### 4.3. Quản Trị Viên (Admin)

Quản trị viên có toàn quyền kiểm soát các nghiệp vụ quản lý cửa hàng:

1. **Quản lý sản phẩm:**
   - **Xem danh sách:** Kiểm soát toàn bộ mặt hàng và số lượng tồn kho trong kho.
   - **Thêm sản phẩm:** Bổ sung mặt hàng mới vào danh mục kinh doanh.
   - **Sửa sản phẩm:** Điều chỉnh tên hàng, giá niêm yết hoặc cập nhật số lượng nhập kho.
   - **Xóa sản phẩm:** Loại bỏ các sản phẩm đã ngừng cung cấp ra khỏi hệ thống.
2. **Quản lý đơn hàng:**
   - Xem toàn bộ danh sách đơn đặt hàng từ mọi khách hàng.
   - Cập nhật luân chuyển trạng thái đơn hàng theo đúng quy trình nghiệp vụ:  
     $$\text{Chờ xử lý} \longrightarrow \text{Đã xác nhận} \longrightarrow \text{Đang giao} \longrightarrow \text{Đã giao / Đã hủy}$$
3. **Quản lý người dùng:**
   - Xem danh sách tất cả người dùng trong hệ thống (Khách thường, Khách VIP, Admin).
   - Kiểm tra thông tin định danh, điểm tích lũy và vai trò của từng thành viên.

---

## 5. Phương Thức Thanh Toán

EcoShop tích hợp 3 hình thức thanh toán đa dạng:

- **Tiền mặt (Cash):** Nhập số tiền khách đưa; hệ thống sẽ tự động tính toán số tiền thừa phải trả lại cho khách hàng.
- **Thẻ ngân hàng (Bank Card):** Nhập số thẻ ngân hàng (định dạng 16 chữ số) và mã CVV (3 chữ số bảo mật).
- **Ví điện tử (E-Wallet):** Nhập số điện thoại đăng ký ví điện tử và mã OTP xác thực (gồm 6 chữ số).

---

## 6. Hệ Thống Giảm Giá (Đa Hình — Polymorphism)

Một trong những ứng dụng quan trọng của tính đa hình trong dự án là cơ chế tính mức chiết khấu giảm giá khác nhau cho từng nhóm khách hàng:

### 6.1. Khách Thường (Normal Customer)
- Tự động cộng điểm tích lũy sau mỗi lần đặt hàng thành công.
- Tỷ lệ giảm giá được xác định tự động dựa trên mức điểm tích lũy hiện có:
  - **$< 100$ điểm:** Giảm **0%**
  - **$\ge 100$ điểm:** Giảm **1%**
  - **$\ge 500$ điểm:** Giảm **3%**
  - **$\ge 1000$ điểm:** Giảm **5%**

### 6.2. Khách Hàng VIP (VIP Customer)
Khách hàng hạng VIP được hưởng chiết khấu giảm giá cố định ngay lập tức dựa trên cấp bậc thẻ:
- **VIP Bạc (Silver VIP):** Giảm **10%** trên tổng hóa đơn.
- **VIP Vàng (Gold VIP):** Giảm **15%** trên tổng hóa đơn.
- **VIP Bạch Kim (Platinum VIP):** Giảm **20%** trên tổng hóa đơn.
