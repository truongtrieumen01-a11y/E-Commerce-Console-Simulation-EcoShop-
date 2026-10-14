package skeleton.model;

/**
 * Lớp trừu tượng User — Lớp cơ sở đại diện cho người dùng hệ thống.
 *
 * Thể hiện 2 trụ cột OOP:
 *  - Đóng gói (Encapsulation): Thuộc tính protected, truy xuất qua getter/setter.
 *  - Kế thừa (Inheritance) & Trừu tượng (Abstraction): Lớp cơ sở cho Admin và Customer.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public abstract class User implements JsonSerializable {

    protected String userId;
    protected String username;
    protected String password;

    public User(String userId, String username, String password) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
    }

    /**
     * Phương thức trừu tượng trả về vai trò của người dùng.
     * Điểm nối cho tính Đa hình (Polymorphism) giữa các lớp con.
     */
    public abstract String getRole();

    /**
     * Xác thực thông tin đăng nhập với mật khẩu cung cấp.
     */
    public boolean login(String inputPassword) {
        // TODO: Hiện thực kiểm tra mật khẩu trong Tuần 1
        return false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        // TODO: Kiểm tra tính hợp lệ trước khi gán
        this.username = username;
    }

    public void setPassword(String password) {
        // TODO: Kiểm tra độ dài tối thiểu
        this.password = password;
    }

    @Override
    public String toString() {
        // TODO: Định dạng chuỗi hiển thị
        return String.format("[%s] %s (Mã: %s)", getRole(), username, userId);
    }
}
