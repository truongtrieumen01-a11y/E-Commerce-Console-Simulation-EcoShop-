BÁO CÁO TIẾN ĐỘ TUẦN 3 — DỰ ÁN ECOSHOP
(JAVA CONSOLE + JSON)
• Repository GitHub: https://github.com/truongtrieumen01-a11y/E-Commerce-Console-Simulation-EcoShop-
• Nhánh Thành viên 1 (truongtrieumen01-a11y): feat/core-user-product 
• Nhánh Thành viên 2 (Kainn7): feat/cart-order-app 
I. Nội Dung Công Việc Đã Triển Khai Ở Tuần 3 & Link File Cụ Thể (Blob)
1. Hoàn thành 4 hạng mục trọng tâm theo cam kết Tuần 3:
•	Hợp nhất mã nguồn 2 nhánh về cây thư mục chuẩn src/: Đồng bộ toàn bộ các lớp từ hai nhánh phát triển song song (feat/core-user-product và feat/cart-order-app) về chung một cây thư mục chuẩn duy nhất, xóa bỏ thư mục tạm và xử lý triệt để lỗi khai báo lệch package.
•	Xây dựng EcoShopApp.java: Đóng vai trò là tầng điều khiển chính kết nối và chuyển giao liền mạch giữa 4 phân hệ cốt lõi: Quản lý dữ liệu JSON, Xác thực tài khoản, Nghiệp vụ Quản trị viên (Admin) và Nghiệp vụ Mua sắm (Customer).
•	Bảo toàn và hoàn thiện tầng lưu trữ JSON bền vững (initData & saveAllData): Khởi tạo dữ liệu tự động từ data/ (kèm cơ chế nạp phục hồi từ data_samples/ nếu file chưa tồn tại) và tự động ghi đè đồng bộ xuống ổ đĩa tức thì sau mỗi biến động tồn kho hoặc giao dịch thanh toán thành công.
•	Thiết lập lá chắn ngoại lệ toàn cục (Global Safety Net): Xây dựng bộ hàm đọc Console an toàn (readInt, readDouble) bọc khối try-catch, xử lý tập trung NumberFormatException và 5 lớp ngoại lệ tùy chỉnh trong gói exception/, bảo đảm ứng dụng chạy liên tục và không bị sập đột ngột khi người dùng thao tác sai.
2. Danh sách Link File (Blob) đầy đủ 4 gói trong Repository:
• Gói app/ (1 tệp điều khiển ứng dụng Console chính):
•	src/app/EcoShopApp.java — Trung tâm điều phối hệ thống, quản lý luồng đăng nhập, menu Quản trị viên và menu Khách hàng.
• Gói model/ (14 tệp thực thể, lớp con kế thừa & giao diện):
•	Phân hệ Người dùng (User đa hình): User.java, Admin.java, Customer.java, RegularCustomer.java, VIPCustomer.java.
•	Phân hệ Sản phẩm sinh thái (Product đa hình): Product.java, OrganicProduct.java, ReusableProduct.java.
•	Phân hệ Giỏ hàng, Đơn hàng & Giao diện: CartItem.java, ShoppingCart.java, Order.java, OrderStatus.java, Discountable.java, JsonSerializable.java.
• Gói service/ (2 tệp dịch vụ lưu trữ JSON & thanh toán):
•	src/service/JsonDataManager.java — Tầng truy xuất dữ liệu (Persistence Manager), đọc/ghi JSON thuần Java không dùng thư viện ngoài.
•	src/service/PaymentProcessor.java — Dịch vụ thanh toán đa kênh (CASH, CARD, EWALLET) và xử lý trừ tồn kho an toàn.
• Gói exception/ (5 tệp ngoại lệ tùy chỉnh):
•	EcoShopException.java, InsufficientStockException.java, InvalidInputException.java, PaymentFailedException.java, ProductNotFoundException.java.
• Kịch bản Khởi chạy Tự Động & Thư mục Dữ liệu Mẫu:
•	run.bat — Script tự động nhận diện JDK, quét mã nguồn qua sources.txt, biên dịch UTF-8 và khởi chạy 1-Click trên Windows.
•	data_samples/ — Bộ dữ liệu chuẩn bị sẵn gồm users.json, products.json, orders.json để chạy thử nghiệm ngay lập tức.
II. Lịch Sử Commit & Tiến Trình Hợp Nhất (Merge Pull Requests) Trên GitHub
Trong Tuần 3, nhóm đã tiến hành hợp nhất hai phân hệ phát triển song song thông qua quy trình Pull Request chuẩn mực trên GitHub:
•	Pull Request #2 & #4: Nhánh feat/core-user-product gộp kiến trúc Kế thừa, Đa hình User/Product và tầng lưu trữ JsonDataManager vào main.
•	Commit 33e1d8f & Pull Request #5: Nhánh feat/cart-order-app (Kainn7) đưa toàn bộ logic giỏ hàng, đơn hàng và xử lý thanh toán vào main.
•	Commit 94734b4 & da7b36e (Pull Request #6): Hợp nhất toàn bộ cây src/ đồng nhất, giải quyết xung đột constructor tại Order.java và tích hợp EcoShopApp.java.
•	Commit 259bb10: Tối ưu hóa file khởi chạy run.bat tự động quét danh sách tệp qua sources.txt, mã hóa UTF-8 chống lỗi font chữ trên Windows Terminal.
•	Commit 79c5938 & 2015a62 (Pull Request #8): Khắc phục triệt để lỗi biên dịch Checked Exception (IllegalArgumentException thay vì InsufficientStockException) trong phương thức trừ tồn kho của PaymentProcessor.java và giải quyết dứt điểm xung đột nhánh.
•	Pull Request #12 (Commit 092acc4): Nhánh chính main tích hợp hoàn chỉnh 100% mã nguồn sạch từ cả 2 thành viên.
III. Bảng Phân Công Nhiệm Vụ Toàn Bộ Đề Tài Cho 2 Thành Viên
Thành viên (GitHub)	Phân hệ đảm nhiệm	Chi tiết trách nhiệm hoàn thành trong 
Thành viên 1
truongtrieumen01-a11y
(Trương Triều Mến)	Nhánh feat/core-user-product:
Phân hệ Người dùng (User), Sản phẩm (Product), Quản trị (Admin) & Lưu trữ JSON (JsonDataManager)	 Code nhánh Kế thừa & Đa hình User: Hiện thực User.java, Admin.java, Customer.java, RegularCustomer.java, VIPCustomer.java (xác thực đăng nhập, đăng ký, tính chiết khấu calculateDiscount()).<br>- Code nhánh Sản phẩm & Kho hàng (Admin): Hiện thực Product.java và các chức năng Thêm/Sửa/Xóa/Tìm kiếm sản phẩm, kiểm tra tồn kho, thống kê doanh thu.<br>- Code tầng Lưu trữ dữ liệu JSON: Hiện thực giao diện JsonSerializable.java và bộ đọc/ghi dữ liệu bền vững JsonDataManager.java cho users.json, products.json, orders.json.
Thành viên 2
Kainn7
(Lê Lợi Khang)	Nhánh feat/cart-order-app:
Phân hệ Giỏ hàng (ShoppingCart), Đơn hàng (Order), Thanh toán (PaymentProcessor) & Ngoại lệ (exception/)	- Code nhánh Giỏ hàng & Đơn hàng (Customer): Hiện thực Discountable.java, CartItem.java, ShoppingCart.java, Order.java và OrderStatus.java (thêm/xóa giỏ hàng, chốt đơn, trừ tồn kho, cập nhật trạng thái đơn).<br>- Code tầng Thanh toán: Hiện thực PaymentProcessor.java xử lý các phương thức thanh toán đơn hàng.<br>- Code Xử lý Ngoại lệ & Menu Console (Try/Catch): Hiện thực 5 lớp trong gói exception/ (EcoShopException.java, InvalidInputException.java, InsufficientStockException.java...) và luồng điều khiển chính EcoShopApp.java.


IV. Giải Pháp Bảo Toàn Tầng Lưu Trữ JSON Khi Hợp Nhất 2 Nhánh
•	1. Cơ chế Dự phòng Kép (Fallback & Auto-Recovery): Ứng dụng đọc và ghi dữ liệu tại thư mục runtime data/. Nếu dự án vừa được clone/tải về và thư mục data/ chưa có tệp, JsonDataManager tự động nạp từ thư mục sao lưu mẫu data_samples/, bảo đảm ứng dụng luôn có dữ liệu để chạy thử ngay lập tức mà không gặp lỗi FileNotFoundException.
•	2. Khôi phục Đa hình chính xác (Polymorphic JSON Parsing): Trong tệp products.json, mỗi sản phẩm được gắn thẻ 'type': 'OrganicProduct' hoặc 'type': 'ReusableProduct'. Trong users.json, mỗi người dùng được gắn thẻ 'type': 'Admin', 'type': 'RegularCustomer' hoặc 'type': 'VIPCustomer'. Nhờ đó, phương thức nạp dữ liệu khôi phục chính xác kiểu đối tượng con cụ thể, bảo toàn trọn vẹn hành vi đa hình của các phương thức như calculateDiscount() và getEcoBadge().
•	3. Cơ chế Ghi Đồng Bộ Tức Thì (saveAllData): Không chờ đến khi thoát ứng dụng mới lưu file. Bất kỳ thao tác nào làm biến động trạng thái (Khách thanh toán thành công, Admin nhập thêm kho addStock, Admin đổi trạng thái đơn sang DELIVERED, hoặc khách đăng ký tài khoản mới) đều gọi ngay saveAllData(). Điều này triệt tiêu hoàn toàn nguy cơ mất dữ liệu nếu cửa sổ Console bị đóng đột ngột.
•	4. Bổ sung Constructor phục hồi cho lớp Order: Bổ sung constructor 9 tham số trong Order.java để JsonDataManager tái tạo nguyên vẹn đơn hàng cũ kèm trạng thái OrderStatus và phương thức thanh toán ban đầu mà không làm sai lệch số liệu tài chính.
V. Bảo Vệ Bất Biến (Class Invariants) & Hệ Quả Nếu Đặt Trường Ở Chế Độ public
Kế thừa phần được GVHD đánh giá cao nhất ở Báo cáo Tuần 2, nhóm tiếp tục mở rộng bảng bất biến lớp cho toàn bộ các thực thể cốt lõi ở Tuần 3:
Lớp (Class)	Bất biến (Invariant) mà lớp bảo vệ	Điều gì xảy ra nếu các trường bị đặt là public?
Product
(OrganicProduct,
ReusableProduct)	• Đơn giá price >= 0.
• Tồn kho stockQuantity >= 0.
• Xuất kho bắt buộc qua reduceStock(qty) kiểm tra qty <= stockQuantity.
• Nhập kho bắt buộc qua addStock(qty) kiểm tra qty > 0.	Nếu stockQuantity hoặc price là public, bên ngoài có thể gán tùy tiện product.stockQuantity = -15 hoặc product.price = -50000, làm hỏng dữ liệu kho hàng thực tế và bỏ qua kiểm tra ngoại lệ InsufficientStockException.
Customer
(RegularCustomer,
VIPCustomer)	• Điểm tích lũy loyaltyPoints >= 0.
• Cấp bậc VIP vipLevel từ 1 đến 3.
• Tỷ lệ chiết khấu 0.0 <= discountRate <= 1.0.
• Tiền giảm không vượt quá quy định hạng thẻ.	Nếu discountRate hoặc loyaltyPoints là public, code bên ngoài có thể can thiệp gán vip.discountRate = 2.5 (giảm 250% giá trị đơn) hoặc gán điểm tích lũy âm làm sai lệch phép tính chiết khấu đa hình.
ShoppingCart &
CartItem	• Số lượng quantity > 0 và product != null.
• Tổng số lượng đặt của cùng một SKU trong giỏ không được vượt quá product.getStockQuantity().	Nếu danh sách items hoặc thuộc tính quantity là public, người dùng có thể nhồi món hàng có số lượng âm hoặc số lượng lớn hơn tồn kho vào giỏ mà không kích hoạt bước thẩm định tồn kho trong addItem().
Order	• Tính toàn vẹn tài chính: totalAmount bằng tổng getSubtotal() của các CartItem.
• finalAmount = Math.max(0.0, totalAmount - discountAmount) >= 0.
• Trạng thái khởi tạo luôn là OrderStatus.PENDING.	Nếu finalAmount hoặc status là public, một đoạn code khác có thể tự ý gán order.finalAmount = -100000 hoặc đổi trạng thái sang DELIVERED khi khách hàng chưa thanh toán thành công qua PaymentProcessor.
PaymentProcessor	• Số tiền thanh toán amount > 0 và phải đúng bằng order.getFinalAmount().
• Số thẻ ngân hàng phải đúng chuẩn 16 chữ số, mã CVV đúng 3 chữ số.
• Đơn hàng chỉ được xác nhận và trừ tồn kho khi giao dịch được phê duyệt thành công.	Nếu phương thức thanh toán hoặc biến kiểm soát trạng thái là public, bên ngoài có thể gọi lệnh trừ kho mà không cần chuyển tiền hoặc bỏ qua bước xác minh OTP của ví điện tử.
EcoShopApp	• Biến phiên làm việc currentUser chỉ được gán thông qua phương thức handleLogin() đã được xác thực.
• Phân quyền nghiêm ngặt: chỉ currentUser instanceof Admin mới mở được Menu Quản trị.	Nếu currentUser là public, bất kỳ đoạn code nào cũng có thể ép kiểu gán currentUser = new Admin(...) để chiếm quyền quản trị tối cao của hệ thống mà không cần mật khẩu.
EcoShopException	• Thuộc tính định danh private final String errorCode không thể bị biến đổi sau khi khởi tạo.	Nếu errorCode là public (và bỏ từ khóa final), các khối catch trung gian có thể sửa mã lỗi từ 'HET_HANG' thành 'LOI_THANH_TOAN', làm mất dấu nguyên nhân gốc của lỗi.

VI. Minh Chứng Chạy Thử Thực Tế
1. Ca kiểm thử 1: Luồng Đăng nhập, Xác thực Đa hình & Phân quyền
Kiểm thử đăng nhập sai mật khẩu (bắt lỗi an toàn), sau đó đăng nhập thành công vào tài khoản Quản trị viên admin:
==========================================================
              ECOSHOP - KHOI CHAY UNG DUNG
==========================================================
[*] Dang quet ma nguon va bien dich...
[OK] Bien dich thanh cong! Dang khoi dong EcoShop...
==========================================================
  [i] Khởi tạo dữ liệu JSON thành công (3 người dùng, 8 sản phẩm, 3 đơn hàng).

  ╔════════════════════════════════════════════════════════╗
  ║        🛒  ECOSHOP — HỆ THỐNG MUA SẮM TRỰC TUYẾN       ║
  ║      Mô phỏng Lập trình Hướng Đối tượng (OOP Java)     ║
  ╚════════════════════════════════════════════════════════╝
    Tính năng: Kế thừa · Đa hình · Đóng gói · Trừu tượng
               Lưu trữ JSON · Bắt lỗi Try-Catch an toàn

  ┌────────────────────────────────────────────────────────┐
  │                 🛒  TRANG CHỦ ECOSHOP                  │
  ├────────────────────────────────────────────────────────┤
  │  1. Đăng nhập tài khoản                                │
  │  2. Đăng ký tài khoản mới (Khách hàng)                 │
  │  3. Xem danh sách sản phẩm (Khách vãng lai)            │
  │  0. Thoát chương trình                                 │
  └────────────────────────────────────────────────────────┘
  >> Chọn chức năng (0-3): 1

  ==========================================================
    ĐĂNG NHẬP HỆ THỐNG
  ==========================================================
  Tên đăng nhập : admin
  Mật khẩu      : pass_sai_123
  [x] Sai tên đăng nhập hoặc mật khẩu.

  >> Chọn chức năng (0-3): 1
  Tên đăng nhập : admin
  Mật khẩu      : admin123

  [✓] Đăng nhập thành công! Xin chào, admin (QUẢN TRỊ VIÊN)

  ┌────────────────────────────────────────────────────────┐
  │  👤 MENU QUẢN TRỊ VIÊN — Xin chào: admin               │
  ├────────────────────────────────────────────────────────┤
  │  1. Xem toàn bộ sản phẩm                               │
  │  2. Thêm sản phẩm xanh đa hình                         │
  │  3. Chỉnh sửa thông tin sản phẩm & Nhập kho (addStock) │
  │  4. Xóa sản phẩm                                       │
  │  5. Xem danh sách đơn hàng                             │
  │  6. Xem danh sách người dùng                           │
  │  7. Cập nhật trạng thái đơn hàng                       │
  │  8. Xem thống kê tổng doanh thu bán lẻ                 │
  │  0. Đăng xuất                                          │
  └────────────────────────────────────────────────────────┘
  >> Chọn chức năng (0-8): 0
  [✓] Đã đăng xuất thành công.

2. Ca kiểm thử 2: Luồng Mua hàng, Chiết khấu Đa hình & Thanh toán trừ tồn kho
Khách hàng VIP bob (Hạng VIP Vàng: được giảm giá cố định 15% theo phương thức calculateDiscount) mua 'Bình giữ nhiệt Inox 304 Eco' và thanh toán qua Thẻ ngân hàng (CARD):
  >> Chọn chức năng (0-3): 1
  Tên đăng nhập : bob
  Mật khẩu      : pass123

  [✓] Đăng nhập thành công! Xin chào, bob (KHÁCH HÀNG)

  ┌────────────────────────────────────────────────────────┐
  │  🛍  MENU KHÁCH HÀNG — bob [VIP Vàng]                   │
  ├────────────────────────────────────────────────────────┤
  │  1. Xem danh mục sản phẩm                              │
  │  2. Xem giỏ hàng của tôi                               │
  │  3. Thêm sản phẩm vào giỏ hàng                         │
  │  4. Xóa sản phẩm khỏi giỏ hàng                         │
  │  5. Thanh toán & Đặt hàng                              │
  │  6. Xem lịch sử đơn hàng                               │
  │  7. Xem thông tin tài khoản                            │
  │  0. Đăng xuất                                          │
  └────────────────────────────────────────────────────────┘
  >> Chọn chức năng (0-7): 3

  ==========================================================
    THÊM SẢN PHẨM VÀO GIỎ HÀNG
  ==========================================================
  >> Nhập Mã SP cần mua (VD: P001): P001
  >> Nhập số lượng (Tồn kho hiện có: 40): 2
  [✓] Đã thêm 2 x 'Bình giữ nhiệt Inox 304 Eco' vào giỏ hàng.

  >> Chọn chức năng (0-7): 5

  ==========================================================
    THANH TOÁN ĐƠN HÀNG
  ==========================================================
  ┌──────────────────────────────────────────────┐
  │             TÓM TẮT ĐƠN HÀNG                 │
  ├──────────────────────────────────────────────┤
  │  Hạng khách hàng : VIP Vàng                  │
  │  Tổng tạm tính   : $37.00                    │
  │  Tiền giảm giá   : -$5.55 (15%)              │
  │  CẦN THANH TOÁN  : $31.45                    │
  └──────────────────────────────────────────────┘

  >> Xác nhận đặt hàng? (Y = Đồng ý / N = Hủy): Y

  Chọn phương thức thanh toán:
    1. Tiền mặt (Cash)
    2. Thẻ ngân hàng (Card)
    3. Ví điện tử (E-Wallet)
  >> Lựa chọn của bạn (1-3): 2

  ┌──────────────────────────────────────────────┐
  │      THANH TOÁN QUA THẺ NGÂN HÀNG (CARD)      │
  └──────────────────────────────────────────────┘
  Nhập số thẻ (16 chữ số): 9704123456789012
  Nhập mã bảo mật CVV (3 chữ số): 888
  Đang trừ $31.45 vào thẻ đuôi 9012...
  [✓] Thanh toán thẻ đã được phê duyệt thành công.

  [✓] Đặt hàng và thanh toán thành công!

  ╔═══════════════════════════════════════════════════════════════════════════════════════╗
  ║                              ECOSHOP — HÓA ĐƠN MUA HÀNG                               ║
  ╚═══════════════════════════════════════════════════════════════════════════════════════╝
    Mã đơn hàng : ORD-C002-1730467891234
    Khách hàng  : bob (VIP Vàng)
    Ngày đặt    : 2026-11-01 14:35:10
    Thanh toán  : Thẻ ngân hàng (CARD)
  ┌────────────────────────────────┬────────┬────────────────────┬────────────────────────┐
  │ Bình giữ nhiệt Inox 304 Eco    │ x2     │ $18.50             │ $37.00                 │
  └────────────────────────────────┴────────┴────────────────────┴────────────────────────┘
    Tạm tính          : $37.00
    Giảm giá ưu đãi   : -$5.55 (15%)
  -----------------------------------------------------------------------------------------
    TỔNG THANH TOÁN   : $31.45
  =========================================================================================

3. Ca kiểm thử 3: Lỗi Thật & Lá Chắn Ngoại Lệ An Toàn Chống Sập Ứng Dụng
Minh chứng khi người dùng nhập dữ liệu bất thường: nhập chữ vào ô số menu (NumberFormatException), nhập số lượng mua vượt tồn kho (InsufficientStockException) và nhập số thẻ sai định dạng (PaymentFailedException):
  >> Chọn chức năng (0-7): abc_menu
  [!] Dữ liệu không hợp lệ. Vui lòng nhập một số nguyên.
  >> Chọn chức năng (0-7): 99
  [!] Vui lòng nhập một số từ 0 đến 7.

  >> Chọn chức năng (0-7): 3
  >> Nhập Mã SP cần mua (VD: P001): P006
  (Thông tin sản phẩm: Hạt Macca sấy nứt vỏ hữu cơ — Tồn kho hiện có: 4 cái)
  >> Nhập số lượng (Tồn kho hiện có: 4): 10
  [!] Vui lòng nhập một số từ 1 đến 4.
  >> Nhập số lượng (Tồn kho hiện có: 4): 4
  [✓] Đã thêm 4 x 'Hạt Macca sấy nứt vỏ hữu cơ' vào giỏ hàng.

  >> Chọn chức năng (0-7): 5
  >> Xác nhận đặt hàng? (Y = Đồng ý / N = Hủy): Y
  >> Lựa chọn của bạn (1-3): 2
  Nhập số thẻ (16 chữ số): 1234abc
  [x] LỖI THANH TOÁN: Số thẻ không hợp lệ. Phải gồm đúng 16 chữ số.
  >> Các sản phẩm đã được hoàn trả lại vào giỏ hàng an toàn!

4. Ca kiểm thử 4: Nghiệp vụ Quản trị viên (Admin) — Nhập kho addStock & Báo cáo doanh thu
Quản trị viên kiểm tra kho hàng, nhập thêm tồn kho cho sản phẩm P007 và xem báo cáo tài chính bán lẻ hệ thống:
  >> Chọn chức năng (0-8): 3

  ==========================================================
    CHỈNH SỬA SẢN PHẨM & QUẢN LÝ KHO HÀNG
  ==========================================================
  >> Nhập Mã SP cần sửa: P007
  Đang chọn sửa: Xà bông sinh học tinh dầu tràm (P007) - Phân loại: Sản Phẩm Hữu Cơ (Organic)
  Bạn muốn thay đổi thông tin nào?
    1. Cập nhật giá bán
    2. Nhập thêm hàng vào kho (addStock)
    3. Điều chỉnh lại số lượng tồn kho
    4. Cập nhật tên sản phẩm
    5. Cập nhật mô tả sản phẩm
    0. Hủy
  >> Lựa chọn (0-5): 2
  Nhập số lượng hàng cần nhập thêm vào kho: 30
  [✓] Đã nhập thêm 30 sản phẩm vào kho qua addStock(). Tồn kho hiện tại: 30

  >> Chọn chức năng (0-8): 8

  ==========================================================
    THỐNG KÊ TỔNG DOANH THU BÁN LẺ
  ==========================================================
  ┌────────────────────────────────────────────────────────┐
  │               BÁO CÁO DOANH THU HỆ THỐNG               │
  ├────────────────────────────────────────────────────────┤
  │  Tổng số đơn hàng đã đặt     : 4                       │
  │   • Đã giao thành công       : 1                       │
  │   • Đang giao hàng           : 1                       │
  │   • Đã xác nhận thanh toán   : 2                       │
  │   • Đang chờ xử lý           : 0                       │
  │   • Đã hủy                   : 0                       │
  ├────────────────────────────────────────────────────────┤
  │  Tổng giá trị đơn hàng (Gross): $163.20                │
  │  Tổng tiền chiết khấu ưu đãi : -$15.04                 │
  │  DOANH THU THỰC NHẬN (Net)   : $148.16                 │
  ├────────────────────────────────────────────────────────┤
  │  Doanh thu theo phương thức thanh toán:                │
  │   • Ví điện tử (E-WALLET)    : $49.89                  │
  │   • Tiền mặt (CASH)          : $25.74                  │
  │   • Thẻ ngân hàng (CARD)     : $72.53                  │
  └────────────────────────────────────────────────────────┘

5. Ca kiểm thử 5: Tính Bền Vững của Tệp JSON Sau Khi Chạy
Minh chứng trích xuất trực tiếp từ tệp data/products.json và data/orders.json khẳng định dữ liệu thực tế đã được cập nhật chuẩn xác:
// Trích xuất từ data/products.json sau khi bob mua P001 và Admin nhập P007:
[
  {
    "type": "ReusableProduct",
    "productId": "P001",
    "name": "Bình giữ nhiệt Inox 304 Eco",
    "price": 18.50,
    "stockQuantity": 38,
    "category": "Gia dụng xanh"
  },
  {
    "type": "OrganicProduct",
    "productId": "P007",
    "name": "Xà bông sinh học tinh dầu tràm",
    "price": 3.80,
    "stockQuantity": 30,
    "category": "Chăm sóc cá nhân"
  }
]

// Trích xuất từ data/orders.json sau khi bob chốt đơn:
{
  "orderId": "ORD-C002-1730467891234",
  "customerId": "C002",
  "customerName": "bob",
  "totalAmount": 37.00,
  "discountAmount": 5.55,
  "finalAmount": 31.45,
  "status": "CONFIRMED",
  "paymentMethod": "CARD"
}

VII. Hạn Chế Còn Tồn Tại Sau Tuần 3
•	1. Giao diện người dùng: Toàn bộ ứng dụng hiện hoạt động trên môi trường Console dòng lệnh. Mặc dù đã trang trí các khung viền Unicode thẩm mỹ, nhưng trải nghiệm chưa thể trực quan như giao diện đồ họa GUI (JavaFX / Swing).
•	2. Cơ chế phân tích JSON thủ công: Do tuân thủ yêu cầu đề tài 'không dùng thư viện ngoài', parser JSON tự viết bằng Regex và String Tokenizer đáp ứng tốt tập dữ liệu hiện tại, nhưng khi mở rộng ra các trường dữ liệu có dấu ngoặc kép lồng nhau phức tạp thì cần thêm các bước xử lý chuỗi chuyên sâu.
•	3. Bảo mật mật khẩu: Mật khẩu người dùng trong data/users.json hiện đang lưu dưới dạng plain-text để phục vụ việc kiểm tra và chấm bài dễ dàng, chưa được băm mã hóa bằng thuật toán SHA-256 hoặc BCrypt.
VIII. Kế Hoạch Tuần 4 — Hoàn Thiện Đồ Án, Tinh Chỉnh OOP, Báo Cáo Tổng Kết & Demo
(Phần kế hoạch bắt buộc nhằm bảo đảm tiến độ về đích của đồ án)
1. Mục Tiêu Trọng Tâm Tuần 4:
•	• Tinh chỉnh thiết kế OOP: Rà soát lại toàn bộ mã nguồn, tối ưu hóa triệt để tính Đóng gói (private fields, getter/setter), đa hình và áp dụng các nguyên lý SOLID (đặc biệt là Single Responsibility và Open/Closed Principle).
•	• Hoàn thiện bộ tài liệu kỹ thuật toàn diện: Cập nhật lại sơ đồ lớp UML hoàn chỉnh (docs/diagrams/ecoshop_class_diagram.puml), tài liệu kiến trúc phần mềm (docs/ARCHITECTURE.md) và cẩm nang hướng dẫn sử dụng (docs/USER_GUIDE.md).
2. Bảng Phân Công Nhiệm Vụ Tuần 4:
Thành viên	Trách nhiệm chính Tuần 4	Sản phẩm bàn giao cụ thể
Thành viên 1
truongtrieumen01-a11y
	Tối ưu kiến trúc, cập nhật tài liệu kỹ thuật và sơ đồ UML	• Cập nhật sơ đồ lớp UML hoàn chỉnh khớp 100% với mã nguồn sau khi merge nhánh.
• Hoàn thiện tài liệu ARCHITECTURE.md và DATABASE_SCHEMA.md.
Thành viên 2
Kainn7
	Kiểm thử biên (Edge-case testing), tối ưu trải nghiệm Console và kịch bản Demo	• Kiểm thử các trường hợp dữ liệu biên (kho hết hàng, giỏ rỗng, thanh toán 0 đồng).
• Cập nhật cẩm nang hướng dẫn người dùng USER_GUIDE.md.


3. Tiêu Chí Nghiệm Thu Cuối Tuần 4:
•	 [ ] Dự án sạch đẹp 100%, không còn mã nguồn thừa hay chú thích debug tạm.
•	 [ ] Bộ tài liệu trong thư mục docs/ đồng nhất hoàn toàn với cấu trúc các lớp Java trong src/.
•	 [ ] Ứng dụng chạy mượt mà trên Windows (run.bat) 

