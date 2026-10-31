package model;

import java.time.LocalDate;

/**
 * ============================================================
 *  Lớp VIPCustomer — Khách hàng VIP (cụ thể, không abstract).
 * ============================================================
 *
 * SƠ ĐỒ KẾ THỪA (3 cấp):
 *   User (abstract)
 *    └── Customer (abstract)
 *         └── VIPCustomer  ← bạn đang đọc file này
 *
 * ĐẶC ĐIỂM:
 *   Khách hàng VIP có cấp độ cố định và được hưởng chiết khấu CAO HƠN Regular.
 *   Chiết khấu dựa vào VIP Level (không cần tích điểm):
 *     ┌──────────────┬──────────┬─────────────────┐
 *     │ VIP Level    │ Tên hạng │ Chiết khấu      │
 *     ├──────────────┼──────────┼─────────────────┤
 *     │ 1 (Silver)   │ VIP Bạc  │ 10%             │
 *     │ 2 (Gold)     │ VIP Vàng │ 15%             │
 *     │ 3 (Platinum) │ VIP Bạch Kim │ 20%         │
 *     └──────────────┴──────────┴─────────────────┘
 *
 * OOP ĐƯỢC THỂ HIỆN:
 *  - Kế thừa (Inheritance)  : extends Customer (3 cấp: User→Customer→VIPCustomer)
 *  - Đóng gói (Encapsulation): vipLevel private, kiểm tra hợp lệ (1–3) khi gán
 *  - Đa hình (Polymorphism) : calculateDiscount() hoàn toàn khác RegularCustomer
 *  - Trừu tượng (Abstraction): implements JsonSerializable (thông qua kế thừa User)
 *
 * SO SÁNH VỚI RegularCustomer (minh chứng đa hình):
 *   Cùng phương thức calculateDiscount(100.0):
 *     RegularCustomer (0 điểm)  → 0.0   (0%)
 *     VIPCustomer (cấp 1)       → 10.0  (10%)
 *     VIPCustomer (cấp 3)       → 20.0  (20%)
 */
public class VIPCustomer extends Customer {

    // ============================================================
    // PHẦN 1 — THUỘC TÍNH RIÊNG
    // ============================================================

    /**
     * Cấp độ VIP — chỉ nhận giá trị 1, 2, hoặc 3.
     * Được kiểm tra hợp lệ trong constructor: nếu ngoài khoảng → mặc định là 1.
     *
     * private → bên ngoài chỉ thay đổi qua setVipLevel() (có kiểm tra hợp lệ).
     */
    private int vipLevel;

    /**
     * Ngày trở thành thành viên VIP.
     * Được gán tự động = ngày hôm nay khi tạo tài khoản (LocalDate.now()).
     * Dùng để hiển thị và tính thâm niên thành viên.
     *
     * private → chỉ đọc qua getMemberSince(), không cho phép sửa từ ngoài.
     */
    private LocalDate memberSince;

    // ============================================================
    // PHẦN 2 — CONSTRUCTOR
    // ============================================================

    /**
     * Khởi tạo Khách hàng VIP.
     *
     * Kiểm tra hợp lệ: vipLevel phải trong khoảng [1, 3].
     * Nếu truyền vào giá trị ngoài khoảng (ví dụ 0 hoặc 5) → mặc định = 1 (Silver).
     *
     * @param userId      Mã khách hàng (ví dụ: "C002")
     * @param username    Tên đăng nhập
     * @param password    Mật khẩu
     * @param address     Địa chỉ giao hàng
     * @param phoneNumber Số điện thoại
     * @param vipLevel    Cấp độ VIP (1, 2, hoặc 3)
     */
    public VIPCustomer(String userId, String username, String password,
                       String address, String phoneNumber, int vipLevel) {
        // Gọi constructor lớp cha Customer
        super(userId, username, password, address, phoneNumber);

        // Toán tử 3 ngôi: nếu vipLevel hợp lệ thì dùng, không thì dùng 1
        // Cú pháp: (điều_kiện) ? giá_trị_nếu_đúng : giá_trị_nếu_sai
        this.vipLevel    = (vipLevel >= 1 && vipLevel <= 3) ? vipLevel : 1;

        // Ghi nhận ngày đăng ký = ngày hiện tại
        this.memberSince = LocalDate.now();
    }

    // ============================================================
    // PHẦN 3 — OVERRIDE: CÔNG THỨC CHIẾT KHẤU (Key Polymorphism)
    // ============================================================

    /**
     * ĐA HÌNH — Override calculateDiscount() từ interface Discountable (qua Customer).
     *
     * Công thức chiết khấu theo cấp VIP:
     *   Level 1 (Bạc)     → 10%  (totalAmount * 0.10)
     *   Level 2 (Vàng)    → 15%  (totalAmount * 0.15)
     *   Level 3 (Bạch Kim)→ 20%  (totalAmount * 0.20)
     *
     * Ví dụ:
     *   bob (VIP cấp 2) mua hàng $500:
     *   calculateDiscount(500.0) → 500.0 * 0.15 = 75.0 (giảm $75!)
     *
     * So sánh với RegularCustomer:
     *   alice (100 điểm) mua hàng $500:
     *   calculateDiscount(500.0) → 500.0 * 0.01 = 5.0 (chỉ giảm $5)
     *
     * Cùng tên phương thức → kết quả hoàn toàn khác = ĐA HÌNH!
     *
     * @param totalAmount Tổng tiền hàng trước giảm giá ($)
     * @return Số tiền được giảm ($)
     */
    @Override
    public double calculateDiscount(double totalAmount) {
        // switch-case dựa vào vipLevel để chọn phần trăm giảm
        switch (vipLevel) {
            case 1: return totalAmount * 0.10;   // Silver → 10%
            case 2: return totalAmount * 0.15;   // Gold   → 15%
            case 3: return totalAmount * 0.20;   // Platinum → 20%
            default: return totalAmount * 0.10;  // Dự phòng (không xảy ra vì đã kiểm tra trong constructor)
        }
    }

    /**
     * Trả về tên hạng VIP theo cấp độ.
     *
     * Ví dụ:
     *   getCustomerType() với vipLevel=2 → "VIP Vàng"
     *
     * @return Chuỗi tên hạng VIP
     */
    @Override
    public String getCustomerType() {
        switch (vipLevel) {
            case 1: return "VIP Bạc";
            case 2: return "VIP Vàng";
            case 3: return "VIP Bạch Kim";
            default: return "VIP";
        }
    }

    /**
     * Override getRole() từ abstract User.
     * Cùng giá trị với RegularCustomer — đều là "KHÁCH HÀNG".
     * Phân biệt 2 loại bằng getCustomerType().
     */
    @Override
    public String getRole() { return "KHÁCH HÀNG"; }

    // ============================================================
    // PHẦN 4 — GETTER & SETTER (Đóng gói — Encapsulation)
    // ============================================================

    /** Getter — Trả về cấp độ VIP (1, 2, hoặc 3) */
    public int getVipLevel() { return vipLevel; }

    /**
     * Setter — Cập nhật cấp độ VIP (ví dụ: nâng cấp từ Silver lên Gold).
     * Kiểm tra hợp lệ: chỉ chấp nhận 1, 2, 3.
     *
     * @param level Cấp độ VIP mới (phải trong khoảng 1–3)
     */
    public void setVipLevel(int level) {
        // Bảo vệ: bỏ qua nếu giá trị ngoài khoảng hợp lệ
        if (level >= 1 && level <= 3) this.vipLevel = level;
    }

    /**
     * Tính tỉ lệ chiết khấu dạng thập phân (0.10, 0.15, 0.20).
     * Dùng trong toString() và viewProfile() để hiển thị %.
     *
     * @return Tỉ lệ chiết khấu (ví dụ: 0.15 = 15%)
     */
    public double getDiscountRate() {
        switch (vipLevel) {
            case 1: return 0.10;
            case 2: return 0.15;
            case 3: return 0.20;
            default: return 0.10;
        }
    }

    /** Getter — Ngày đăng ký thành viên VIP */
    public LocalDate getMemberSince() { return memberSince; }

    // ============================================================
    // PHẦN 5 — TRIỂN KHAI GIAO DIỆN JsonSerializable
    // ============================================================

    /**
     * Chuyển VIPCustomer thành chuỗi JSON để lưu vào file users.json.
     *
     * Ví dụ output:
     * {
     *   "type":"VIPCustomer",
     *   "userId":"C002",
     *   "username":"bob",
     *   "password":"pass123",
     *   "address":"456 Nguyễn Huệ, Quận 1",
     *   "phoneNumber":"0912345678",
     *   "vipLevel":2,
     *   "memberSince":"2026-10-25"
     * }
     *
     * memberSince.toString() → chuyển LocalDate (2026-10-25) thành chuỗi ISO-8601.
     * JsonDataManager.loadUsers() sẽ đọc lại "type":"VIPCustomer" để tái tạo đúng đối tượng.
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"type\":\"VIPCustomer\",\"userId\":\"%s\",\"username\":\"%s\"," +
            "\"password\":\"%s\",\"address\":\"%s\"," +
            "\"phoneNumber\":\"%s\",\"vipLevel\":%d,\"memberSince\":\"%s\"}",
            userId, username, password,
            address.replace("\"", "\\\""),  // Escape nháy đôi trong địa chỉ
            phoneNumber, vipLevel,
            memberSince.toString()          // "2026-10-25" (định dạng ISO-8601)
        );
    }

    // ============================================================
    // PHẦN 6 — HIỂN THỊ
    // ============================================================

    /**
     * Override toString() — in thông tin VIP Customer dạng bảng.
     *
     * Ví dụ output:
     *   [VIP Vàng          ] bob          | Mã: C002  | Giảm giá: 15% | Ngày tham gia: 2026-10-25 | SĐT: 091...
     *
     * getDiscountRate() * 100 → chuyển 0.15 thành 15 để hiển thị %
     */
    @Override
    public String toString() {
        return String.format("[%-18s] %-12s | Mã: %-5s | Giảm giá: %2.0f%% | Ngày tham gia: %s | SĐT: %s",
            getCustomerType(), username, userId, getDiscountRate() * 100, memberSince, phoneNumber);
    }
}
