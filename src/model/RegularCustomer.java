package model;

/**
 * ============================================================
 *  Lớp RegularCustomer — Khách hàng Thường (cụ thể, không abstract).
 * ============================================================
 *
 * SƠ ĐỒ KẾ THỪA (3 cấp):
 *   User (abstract)
 *    └── Customer (abstract)
 *         └── RegularCustomer  ← bạn đang đọc file này
 *
 * ĐẶC ĐIỂM:
 *   Khách hàng thường tích lũy điểm loyalty (điểm thưởng) qua mua hàng.
 *   Chiết khấu dựa HOÀN TOÀN vào số điểm tích lũy:
 *     ┌──────────────┬──────────────┐
 *     │ Điểm tích lũy│ Chiết khấu  │
 *     ├──────────────┼──────────────┤
 *     │ < 100        │ 0%          │
 *     │ 100 – 499    │ 1%          │
 *     │ 500 – 999    │ 3%          │
 *     │ ≥ 1000       │ 5%          │
 *     └──────────────┴──────────────┘
 *
 * OOP ĐƯỢC THỂ HIỆN:
 *  - Kế thừa (Inheritance)  : extends Customer (3 cấp kế thừa: User→Customer→RegularCustomer)
 *  - Đóng gói (Encapsulation): loyaltyPoints là private, chỉ sửa qua addLoyaltyPoints()
 *  - Đa hình (Polymorphism) : calculateDiscount() override riêng dựa trên loyaltyPoints
 *  - Trừu tượng (Abstraction): implements JsonSerializable (kế thừa từ User → Customer → đây)
 */
public class RegularCustomer extends Customer {

    // ============================================================
    // PHẦN 1 — THUỘC TÍNH RIÊNG
    // ============================================================

    /**
     * Điểm tích lũy (Loyalty Points).
     * Tăng dần mỗi khi khách hoàn thành mua hàng:
     *   earnPointsFromPurchase() → cộng 1 điểm / $10 chi tiêu
     *
     * private → chỉ RegularCustomer mới sửa, bên ngoài đọc qua getLoyaltyPoints()
     */
    private int loyaltyPoints;

    // ============================================================
    // PHẦN 2 — CONSTRUCTOR
    // ============================================================

    /**
     * Khởi tạo Khách hàng Thường với thông tin cơ bản.
     *
     * loyaltyPoints ban đầu = 0 (khách mới chưa có điểm).
     *
     * @param userId      Mã khách hàng (ví dụ: "C001")
     * @param username    Tên đăng nhập
     * @param password    Mật khẩu
     * @param address     Địa chỉ giao hàng
     * @param phoneNumber Số điện thoại
     */
    public RegularCustomer(String userId, String username, String password,
                           String address, String phoneNumber) {
        // Gọi constructor lớp cha Customer — gán userId, username, password, address, phone, cart, orderHistory
        super(userId, username, password, address, phoneNumber);
        // Khách mới bắt đầu với 0 điểm
        this.loyaltyPoints = 0;
    }

    // ============================================================
    // PHẦN 3 — OVERRIDE: CÔNG THỨC CHIẾT KHẤU (Key Polymorphism)
    // ============================================================

    /**
     * ĐA HÌNH — Override calculateDiscount() từ interface Discountable (qua Customer).
     *
     * Công thức:
     *   - ≥ 1000 điểm → 5% (loyaltyPoints * 0.05 của totalAmount)
     *   - ≥ 500  điểm → 3%
     *   - ≥ 100  điểm → 1%
     *   - < 100  điểm → 0% (không giảm)
     *
     * Ví dụ:
     *   alice có 500 điểm, mua hàng $200:
     *   calculateDiscount(200.0) → 200.0 * 0.03 = 6.0 (giảm $6)
     *
     * So sánh với VIPCustomer:
     *   VIPCustomer.calculateDiscount(200.0) → 200.0 * 0.10 = 20.0 (giảm $20 nếu cấp 1)
     * → Cùng 1 phương thức, kết quả KHÁC nhau = Đa hình!
     *
     * @param totalAmount Tổng tiền hàng trước giảm giá
     * @return Số tiền được giảm ($)
     */
    @Override
    public double calculateDiscount(double totalAmount) {
        // Kiểm tra từ cao xuống thấp để đảm bảo lấy mức cao nhất
        if (loyaltyPoints >= 1000) return totalAmount * 0.05;  // 5%
        if (loyaltyPoints >= 500)  return totalAmount * 0.03;  // 3%
        if (loyaltyPoints >= 100)  return totalAmount * 0.01;  // 1%
        return 0.0;  // Chưa đủ điểm → không được giảm
    }

    /**
     * Trả về tên loại khách để hiển thị trên menu và hóa đơn.
     * @return "Khách Thường"
     */
    @Override
    public String getCustomerType() { return "Khách Thường"; }

    /**
     * Override getRole() từ abstract User.
     * Cùng với VIPCustomer đều là "KHÁCH HÀNG" — phân biệt bằng getCustomerType().
     * @return "KHÁCH HÀNG"
     */
    @Override
    public String getRole() { return "KHÁCH HÀNG"; }

    // ============================================================
    // PHẦN 4 — NGHIỆP VỤ ĐIỂM TÍCH LŨY (Loyalty Points Logic)
    // ============================================================

    /**
     * Cộng thêm điểm tích lũy (thủ công, ví dụ: khuyến mãi đặc biệt).
     *
     * Điều kiện: chỉ cộng nếu points > 0 (tránh cộng số âm làm mất điểm).
     *
     * @param points Số điểm muốn cộng thêm (phải > 0)
     */
    public void addLoyaltyPoints(int points) {
        // Bảo vệ: chỉ cộng điểm hợp lệ, bỏ qua nếu points <= 0
        if (points > 0) this.loyaltyPoints += points;
    }

    /**
     * Tính và cộng điểm dựa trên số tiền mua hàng.
     *
     * Công thức: 1 điểm / $10 chi tiêu
     * Ví dụ: Mua $75 → (int)(75/10) = 7 điểm
     *         Mua $150 → (int)(150/10) = 15 điểm
     *
     * Gọi sau khi thanh toán thành công trong PaymentProcessor.
     *
     * @param amount Số tiền đã chi tiêu (sau giảm giá)
     */
    public void earnPointsFromPurchase(double amount) {
        // (int) cắt phần thập phân — chỉ tính điểm chẵn $10
        int earned = (int) (amount / 10);
        addLoyaltyPoints(earned);
    }

    // ============================================================
    // PHẦN 5 — GETTER (Đóng gói — Encapsulation)
    // ============================================================

    /**
     * Getter — Trả về số điểm tích lũy hiện tại.
     * Dùng để hiển thị trong viewProfile() và tính chiết khấu.
     */
    public int getLoyaltyPoints() { return loyaltyPoints; }

    // ============================================================
    // PHẦN 6 — TRIỂN KHAI GIAO DIỆN JsonSerializable
    // ============================================================

    /**
     * Chuyển RegularCustomer thành chuỗi JSON để lưu vào file users.json.
     *
     * Ví dụ output:
     * {
     *   "type":"RegularCustomer",
     *   "userId":"C001",
     *   "username":"alice",
     *   "password":"pass123",
     *   "address":"123 Lê Lợi, Quận 1",
     *   "phoneNumber":"0901234567",
     *   "loyaltyPoints":500
     * }
     *
     * address.replace("\"", "\\\"") → escape dấu nháy kép trong địa chỉ
     * (tránh lỗi JSON nếu địa chỉ có ký tự đặc biệt)
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"type\":\"RegularCustomer\",\"userId\":\"%s\",\"username\":\"%s\"," +
            "\"password\":\"%s\",\"address\":\"%s\"," +
            "\"phoneNumber\":\"%s\",\"loyaltyPoints\":%d}",
            userId, username, password,
            address.replace("\"", "\\\""),  // Escape nháy đôi trong địa chỉ
            phoneNumber, loyaltyPoints
        );
    }

    // ============================================================
    // PHẦN 7 — HIỂN THỊ
    // ============================================================

    /**
     * Override toString() — in thông tin Khách thường dạng bảng.
     *
     * Ví dụ output:
     *   [Khách Thường      ] alice        | Mã: C001  | Điểm tích lũy:  500 | Giảm giá:  3% | SĐT: 090...
     */
    @Override
    public String toString() {
        return String.format("[%-18s] %-12s | Mã: %-5s | Điểm tích lũy: %4d | Giảm giá: %2.0f%% | SĐT: %s",
            getCustomerType(), username, userId, loyaltyPoints, getDiscountRate() * 100, phoneNumber);
    }

    /**
     * Phương thức nội bộ — Tính tỉ lệ chiết khấu (0.0 → 0.05) để hiển thị dạng %.
     * Dùng riêng trong toString() và viewProfile(), không gọi từ bên ngoài.
     *
     * @return Tỉ lệ chiết khấu (ví dụ 0.03 = 3%)
     */
    private double getDiscountRate() {
        if (loyaltyPoints >= 1000) return 0.05;
        if (loyaltyPoints >= 500)  return 0.03;
        if (loyaltyPoints >= 100)  return 0.01;
        return 0.0;
    }
}
