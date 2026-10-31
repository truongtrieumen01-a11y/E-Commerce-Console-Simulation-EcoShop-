package model;

import exception.InsufficientStockException;
import exception.EcoShopException;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  Lớp trừu tượng Customer — Lớp con của User, đại diện cho Khách hàng.
 * ============================================================
 *
 * SƠ ĐỒ KẾ THỪA:
 *   User (abstract)
 *    └── Customer (abstract)  ← bạn đang đọc file này
 *         ├── RegularCustomer (Khách thường — chiết khấu 0–5%)
 *         └── VIPCustomer     (Khách VIP   — chiết khấu 10–20%)
 *
 * TẠI SAO Customer CŨNG LÀ "abstract"?
 *   → Không có khách hàng nào chỉ là "Customer" chung chung.
 *   → Mỗi khách hàng phải là Regular hoặc VIP.
 *   → abstract class Customer bắt buộc lớp con phải định nghĩa:
 *       * calculateDiscount() — công thức chiết khấu riêng
 *       * getCustomerType()   — tên loại khách hàng
 *
 * OOP ĐƯỢC THỂ HIỆN:
 *  - Kế thừa (Inheritance)  : extends User + kế thừa thêm address, phone, cart
 *  - Trừu tượng (Abstraction): abstract calculateDiscount(), getCustomerType()
 *  - Đóng gói (Encapsulation): cart và orderHistory được bảo vệ qua getters
 *  - Đa hình (Polymorphism) : calculateDiscount() được lớp con override khác nhau
 *
 * GIAO DIỆN Discountable:
 *   Customer implements Discountable → buộc phải có calculateDiscount(double).
 *   RegularCustomer và VIPCustomer sẽ override phương thức này theo cách riêng.
 */
public abstract class Customer extends User implements Discountable {

    // ============================================================
    // PHẦN 1 — THUỘC TÍNH RIÊNG CỦA CUSTOMER
    // ============================================================

    /**
     * Địa chỉ giao hàng của khách hàng.
     * protected → lớp con (RegularCustomer, VIPCustomer) có thể đọc trực tiếp.
     */
    protected String address;

    /**
     * Số điện thoại liên hệ.
     * protected → lớp con truy cập khi cần xây JSON.
     */
    protected String phoneNumber;

    /**
     * Giỏ hàng hiện tại của khách hàng.
     * Mỗi khách hàng có 1 giỏ hàng riêng (tạo tự động khi khởi tạo Customer).
     * protected → lớp con có thể thao tác giỏ hàng.
     */
    protected ShoppingCart cart;

    /**
     * Lịch sử đơn hàng đã đặt trong phiên làm việc.
     * Được ghi nhận mỗi khi khách gọi placeOrder() thành công.
     * protected → lớp con có thể đọc để in lịch sử.
     */
    protected List<Order> orderHistory;

    // ============================================================
    // PHẦN 2 — CONSTRUCTOR
    // ============================================================

    /**
     * Khởi tạo Customer với đầy đủ thông tin cơ bản.
     *
     * Bước thực hiện:
     *   1. super(userId, username, password) → gọi User cha để gán thông tin tài khoản
     *   2. Gán address và phoneNumber
     *   3. Tạo giỏ hàng mới (rỗng) cho khách hàng này
     *   4. Tạo danh sách lịch sử đơn hàng rỗng
     *
     * @param userId      Mã khách hàng (ví dụ: "C001")
     * @param username    Tên đăng nhập
     * @param password    Mật khẩu
     * @param address     Địa chỉ giao hàng
     * @param phoneNumber Số điện thoại liên hệ
     */
    public Customer(String userId, String username, String password,
                    String address, String phoneNumber) {
        // Gọi constructor lớp cha User — bắt buộc là dòng đầu tiên trong constructor
        super(userId, username, password);
        this.address     = address;
        this.phoneNumber = phoneNumber;
        // Mỗi Customer có 1 giỏ hàng riêng được tạo mới (trống)
        this.cart        = new ShoppingCart();
        // Lịch sử đơn hàng ban đầu rỗng, sẽ được thêm khi khách đặt hàng
        this.orderHistory = new ArrayList<>();
    }

    // ============================================================
    // PHẦN 3 — PHƯƠNG THỨC TRỪU TƯỢNG (Abstract Methods)
    // ============================================================

    /**
     * ĐA HÌNH (Polymorphism) — Công thức tính chiết khấu PHẢI được lớp con định nghĩa.
     *
     * Tại sao abstract?
     *   → RegularCustomer và VIPCustomer tính chiết khấu theo công thức KHÁC NHAU.
     *   → Không thể viết 1 công thức chung cho cả 2 loại.
     *
     * Ví dụ kết quả:
     *   RegularCustomer (500 điểm).calculateDiscount(100.0) → 3.0  (giảm 3%)
     *   VIPCustomer (cấp 2).calculateDiscount(100.0)         → 15.0 (giảm 15%)
     *
     * Cùng 1 lời gọi phương thức, nhưng kết quả KHÁC NHAU → Đây là đa hình!
     *
     * @param totalAmount Tổng tiền trước khi giảm giá ($)
     * @return Số tiền được giảm ($) — không phải phần trăm
     */
    @Override
    public abstract double calculateDiscount(double totalAmount);

    /**
     * Trả về tên loại khách hàng để hiển thị trên màn hình.
     *
     * Ví dụ:
     *   RegularCustomer → "Khách Thường"
     *   VIPCustomer (cấp 1) → "VIP Bạc"
     *   VIPCustomer (cấp 2) → "VIP Vàng"
     *   VIPCustomer (cấp 3) → "VIP Bạch Kim"
     *
     * @return Chuỗi mô tả loại khách hàng
     */
    public abstract String getCustomerType();

    // ============================================================
    // PHẦN 4 — NGHIỆP VỤ ĐẶT HÀNG (Business Logic)
    // ============================================================

    /**
     * Tạo đơn hàng từ những sản phẩm trong giỏ hàng hiện tại.
     *
     * LUỒNG XỬ LÝ:
     *   Bước 1: Kiểm tra giỏ hàng không rỗng
     *          → nếu rỗng, ném EcoShopException (không đặt hàng được)
     *
     *   Bước 2: Kiểm tra tồn kho từng sản phẩm
     *          → nếu thiếu, ném InsufficientStockException
     *          (ưu tiên báo lỗi sớm trước khi trừ tồn kho)
     *
     *   Bước 3: Trừ tồn kho các sản phẩm đã kiểm tra đủ
     *          → product.reduceStock(quantity)
     *
     *   Bước 4: Tạo đối tượng Order và tính tổng tiền
     *
     *   Bước 5: Lưu order vào lịch sử và xóa giỏ hàng
     *
     * XỬ LÝ NGOẠI LỆ (Exception Handling):
     *   - EcoShopException         : giỏ hàng trống
     *   - InsufficientStockException: tồn kho không đủ (lớp con của EcoShopException)
     *
     * @return Order vừa được tạo
     * @throws EcoShopException nếu giỏ hàng trống hoặc tồn kho không đủ
     */
    public Order placeOrder() throws EcoShopException {
        // ── Bước 1: Kiểm tra giỏ hàng có sản phẩm không ──
        if (cart.isEmpty()) {
            // Ném ngoại lệ với mã lỗi "GIO_HANG_TRONG" để dễ tra cứu
            throw new EcoShopException("Không thể đặt hàng: Giỏ hàng đang trống.", "GIO_HANG_TRONG");
        }

        // ── Bước 2: Kiểm tra tồn kho TRƯỚC khi trừ (tránh trừ một nửa rồi fail) ──
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            // Nếu số lượng trong giỏ > tồn kho thực tế → báo lỗi
            if (item.getQuantity() > p.getStockQuantity()) {
                throw new InsufficientStockException(p.getName(), item.getQuantity(), p.getStockQuantity());
            }
        }

        // ── Bước 3: Tất cả OK → Trừ tồn kho ──
        for (CartItem item : cart.getItems()) {
            // reduceStock() trừ số lượng tồn kho trong Product
            item.getProduct().reduceStock(item.getQuantity());
        }

        // ── Bước 4: Tạo đơn hàng ──
        // Tạo mã đơn hàng duy nhất: "ORD-C001-1698212345678"
        String orderId = "ORD-" + userId + "-" + System.currentTimeMillis();

        // Chụp snapshot giỏ hàng (tạo bản sao) — tránh giỏ bị xóa làm Order rỗng
        List<CartItem> snapshot = new ArrayList<>(cart.getItems());

        // Tạo đơn hàng mới với danh sách sản phẩm đã snapshot
        Order order = new Order(orderId, this, snapshot);

        // Tính tổng tiền cuối cùng (áp dụng chiết khấu đa hình)
        order.calculateFinal();

        // ── Bước 5: Lưu lịch sử và xóa giỏ hàng ──
        orderHistory.add(order);   // Thêm vào lịch sử đơn hàng
        cart.clear();              // Xóa toàn bộ giỏ hàng sau khi đặt thành công
        return order;              // Trả về đơn hàng để EcoShopApp xử lý thanh toán
    }

    // ============================================================
    // PHẦN 5 — GETTER & SETTER (Đóng gói — Encapsulation)
    // ============================================================

    /** Getter — Địa chỉ giao hàng */
    public String getAddress()    { return address;     }

    /** Getter — Số điện thoại */
    public String getPhoneNumber(){ return phoneNumber; }

    /**
     * Getter — Trả về giỏ hàng để thao tác (thêm/xóa sản phẩm).
     * Trả về trực tiếp (không copy) vì cần thao tác thật trên giỏ hàng.
     */
    public ShoppingCart getCart() { return cart; }

    /**
     * Getter — Trả về BẢN SAO lịch sử đơn hàng để hiển thị.
     * new ArrayList<>(orderHistory) → tạo bản sao bảo vệ dữ liệu gốc.
     */
    public List<Order> getOrderHistory() { return new ArrayList<>(orderHistory); }

    /** Setter — Cập nhật địa chỉ giao hàng */
    public void setAddress(String address)       { this.address     = address;   }

    /** Setter — Cập nhật số điện thoại */
    public void setPhoneNumber(String phone)     { this.phoneNumber = phone;     }

    /**
     * Thêm đơn hàng vào lịch sử (gọi từ bên ngoài khi cần).
     * Khác với placeOrder() — đây chỉ ghi nhận, không tạo đơn mới.
     */
    public void addOrderToHistory(Order order)   { this.orderHistory.add(order); }
}
