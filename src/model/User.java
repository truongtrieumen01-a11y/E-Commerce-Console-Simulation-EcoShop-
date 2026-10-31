package model;

/**
 * ============================================================
 *  Lớp trừu tượng User — Lớp CHA gốc của toàn bộ hệ thống người dùng.
 * ============================================================
 *
 * VÌ SAO DÙNG "abstract"?
 *   → Vì trong thực tế KHÔNG có người dùng nào chỉ là "User" chung chung.
 *   → Mọi người dùng phải là Admin HOẶC Customer (lớp con cụ thể).
 *   → abstract class buộc các lớp con phải tự định nghĩa phương thức getRole().
 *
 * 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  ┌─────────────────┬────────────────────────────────────────────────────┐
 *  │ Encapsulation   │ Các field userId, username, password là protected  │
 *  │ (Đóng gói)      │ → chỉ lớp con đọc được, bên ngoài phải dùng getter│
 *  ├─────────────────┼────────────────────────────────────────────────────┤
 *  │ Abstraction     │ getRole() là abstract → lớp con BUỘC phải override │
 *  │ (Trừu tượng)    │ → mỗi loại user tự quyết định vai trò của mình     │
 *  ├─────────────────┼────────────────────────────────────────────────────┤
 *  │ Inheritance     │ Admin và Customer đều extends User                 │
 *  │ (Kế thừa)       │ → tái sử dụng code login(), getUsername()...       │
 *  ├─────────────────┼────────────────────────────────────────────────────┤
 *  │ Polymorphism    │ getRole() được override khác nhau ở mỗi lớp con    │
 *  │ (Đa hình)       │ → cùng gọi user.getRole() nhưng kết quả khác nhau  │
 *  └─────────────────┴────────────────────────────────────────────────────┘
 *
 * Giao diện JsonSerializable:
 *   User implements JsonSerializable → mỗi lớp con phải cài đặt toJson()
 *   để hỗ trợ lưu dữ liệu ra file users.json.
 */
public abstract class User implements JsonSerializable {

    // ============================================================
    // PHẦN 1 — THUỘC TÍNH (Fields / Attributes)
    // ============================================================

    /**
     * Mã định danh duy nhất của người dùng (ví dụ: "A001", "C001").
     * Dùng "protected" để lớp con (Admin, Customer...) có thể đọc trực tiếp
     * mà không cần gọi getter — giúp code gọn hơn trong lớp con.
     */
    protected String userId;

    /**
     * Tên đăng nhập — dùng để xác thực và hiển thị trên màn hình.
     * protected → lớp con và các lớp trong cùng package đọc được.
     */
    protected String username;

    /**
     * Mật khẩu — lưu dạng plaintext để đơn giản hóa.
     * Lưu ý: Trong ứng dụng thực tế, phải dùng hash (BCrypt, SHA-256...).
     * protected → lớp con có thể đổi mật khẩu thông qua setPassword().
     */
    protected String password;

    // ============================================================
    // PHẦN 2 — CONSTRUCTOR (Hàm khởi tạo)
    // ============================================================

    /**
     * Constructor — Gán giá trị ban đầu cho 3 thuộc tính cơ bản.
     *
     * Cách dùng:
     *   super(userId, username, password) ← gọi từ lớp con Admin/Customer
     *
     * @param userId   Mã người dùng (duy nhất trong hệ thống)
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     */
    public User(String userId, String username, String password) {
        // "this." để phân biệt thuộc tính của lớp với tham số truyền vào
        this.userId   = userId;
        this.username = username;
        this.password = password;
    }

    // ============================================================
    // PHẦN 3 — PHƯƠNG THỨC TRỪU TƯỢNG (Abstract Methods)
    // ============================================================

    /**
     * ĐA HÌNH (Polymorphism) — Phương thức trừu tượng BUỘC lớp con override.
     *
     * Tại sao abstract?
     *   → User không biết mình là "Admin" hay "Customer" nên không thể tự trả lời.
     *   → Lớp Admin sẽ return "QUẢN TRỊ VIÊN"
     *   → Lớp Customer sẽ return "KHÁCH HÀNG"
     *
     * Khi runtime gọi user.getRole(), Java tự động gọi đúng phiên bản
     * của lớp con — đây chính là đa hình (runtime polymorphism).
     *
     * @return Chuỗi mô tả vai trò (Admin, Customer...)
     */
    public abstract String getRole();

    // ============================================================
    // PHẦN 4 — PHƯƠNG THỨC CỤ THỂ (Concrete Methods)
    // ============================================================

    /**
     * Xác thực đăng nhập — so sánh mật khẩu nhập vào với mật khẩu đã lưu.
     *
     * Cách hoạt động:
     *   1. Người dùng nhập mật khẩu tại màn hình login
     *   2. EcoShopApp gọi user.login(inputPassword)
     *   3. Trả về true nếu khớp → cho phép vào hệ thống
     *   4. Trả về false nếu sai → báo lỗi đăng nhập
     *
     * @param inputPassword Mật khẩu mà người dùng vừa nhập
     * @return true nếu mật khẩu đúng, false nếu sai
     */
    public boolean login(String inputPassword) {
        // equals() so sánh nội dung chuỗi (không phải địa chỉ bộ nhớ như ==)
        return this.password != null && this.password.equals(inputPassword);
    }

    /**
     * Xác thực thông tin người dùng qua phương thức User.authenticate(password).
     * [YÊU CẦU TUẦN 3]: Chuẩn hóa hàm xác thực đăng nhập phục vụ luồng điều phối của EcoShopApp.
     * 
     * @param password Mật khẩu người dùng nhập vào từ bàn phím
     * @return true nếu mật khẩu khớp hoàn toàn, ngược lại trả về false
     */
    public boolean authenticate(String password) {
        return login(password);
    }

    // ============================================================
    // PHẦN 5 — GETTER & SETTER (Đóng gói — Encapsulation)
    // ============================================================

    /**
     * Getter — Trả về mã người dùng.
     * Không có setter vì userId không nên thay đổi sau khi tạo.
     */
    public String getUserId()   { return userId;   }

    /**
     * Getter — Trả về tên đăng nhập để hiển thị trên menu.
     */
    public String getUsername() { return username; }

    /**
     * Setter — Cho phép đổi tên đăng nhập (nếu cần tính năng sửa hồ sơ).
     * @param username Tên đăng nhập mới
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Setter — Cho phép đổi mật khẩu (dùng khi tính năng "đổi mật khẩu").
     * @param password Mật khẩu mới
     */
    public void setPassword(String password) { this.password = password; }

    // ============================================================
    // PHẦN 6 — HIỂN THỊ (Display)
    // ============================================================

    /**
     * Override toString() để in thông tin người dùng dạng dễ đọc.
     * Ví dụ output: "[QUẢN TRỊ VIÊN] admin (Mã: A001)"
     *
     * getRole() ở đây sẽ gọi phiên bản của lớp con (đa hình).
     */
    @Override
    public String toString() {
        return String.format("[%s] %s (Mã: %s)", getRole(), username, userId);
    }
}
