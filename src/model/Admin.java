package model;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  Lớp Admin — Lớp con của User, đại diện cho Quản trị viên.
 * ============================================================
 *
 * SƠ ĐỒ KẾ THỪA:
 *   User (abstract)
 *    └── Admin          ← bạn đang đọc file này
 *    └── Customer (abstract)
 *         ├── RegularCustomer
 *         └── VIPCustomer
 *
 * OOP ĐƯỢC THỂ HIỆN:
 *  - Kế thừa (Inheritance)  : extends User → tái sử dụng userId, username, login()
 *  - Đóng gói (Encapsulation): adminLevel và permissions là private → bên ngoài
 *                              không sửa trực tiếp, phải dùng hasPermission()
 *  - Đa hình (Polymorphism) : override getRole() để trả về "QUẢN TRỊ VIÊN"
 *  - Trừu tượng (Abstraction): implements JsonSerializable → cài đặt toJson()
 *                              để lớp service có thể ghi ra file mà không cần
 *                              biết Admin là gì
 *
 * CHỨC NĂNG CHÍNH:
 *   Admin quản lý toàn bộ hệ thống: thêm/sửa/xóa sản phẩm,
 *   xem danh sách người dùng, cập nhật trạng thái đơn hàng.
 */
public class Admin extends User {

    // ============================================================
    // PHẦN 1 — THUỘC TÍNH RIÊNG CỦA ADMIN
    // ============================================================

    /**
     * Cấp độ quản trị viên:
     *   - 1 = Super Admin (toàn quyền, kể cả đổi trạng thái đơn hàng)
     *   - 2 = Admin thường (quản lý sản phẩm và người dùng)
     *
     * private → chỉ lớp Admin đọc/ghi, bên ngoài phải dùng getAdminLevel()
     */
    private int adminLevel;

    /**
     * Danh sách quyền hạn của admin, ví dụ:
     *   ["MANAGE_PRODUCTS", "VIEW_ORDERS", "MANAGE_USERS", "CHANGE_ORDER_STATUS"]
     *
     * Dùng List<String> để linh hoạt thêm/bớt quyền sau này.
     * private → bên ngoài chỉ kiểm tra qua hasPermission(), không sửa trực tiếp.
     */
    private List<String> permissions;

    // ============================================================
    // PHẦN 2 — CONSTRUCTOR
    // ============================================================

    /**
     * Khởi tạo Admin với thông tin cơ bản và cấp độ.
     *
     * Bước thực hiện:
     *   1. super(...) → gọi constructor của User cha để gán userId, username, password
     *   2. Gán adminLevel
     *   3. Khởi tạo danh sách permissions rỗng
     *   4. Gọi initDefaultPermissions() để cấp quyền mặc định theo cấp
     *
     * @param userId     Mã admin (ví dụ: "A001")
     * @param username   Tên đăng nhập
     * @param password   Mật khẩu
     * @param adminLevel Cấp độ (1 = super, 2 = thường)
     */
    public Admin(String userId, String username, String password, int adminLevel) {
        // Gọi constructor lớp cha User — bắt buộc phải là dòng đầu tiên
        super(userId, username, password);
        this.adminLevel  = adminLevel;
        // Khởi tạo ArrayList rỗng trước khi gán quyền
        this.permissions = new ArrayList<>();
        // Cấp quyền mặc định dựa vào adminLevel
        initDefaultPermissions();
    }

    // ============================================================
    // PHẦN 3 — PHƯƠNG THỨC KHỞI TẠO QUYỀN HẠN (Private Helper)
    // ============================================================

    /**
     * Phương thức PRIVATE nội bộ — thiết lập quyền mặc định khi tạo Admin.
     *
     * Tất cả admin đều có 3 quyền cơ bản:
     *   - MANAGE_PRODUCTS : Thêm/Sửa/Xóa sản phẩm
     *   - VIEW_ORDERS     : Xem danh sách đơn hàng
     *   - MANAGE_USERS    : Xem danh sách người dùng
     *
     * Chỉ Super Admin (level 1) mới có thêm:
     *   - CHANGE_ORDER_STATUS : Cập nhật trạng thái đơn hàng
     *
     * private → chỉ gọi nội bộ trong Admin, bên ngoài không gọi được.
     */
    private void initDefaultPermissions() {
        // 3 quyền cơ bản cho mọi admin
        permissions.add("MANAGE_PRODUCTS");
        permissions.add("VIEW_ORDERS");
        permissions.add("MANAGE_USERS");

        // Quyền đặc biệt chỉ cho Super Admin (level 1)
        if (adminLevel == 1) {
            permissions.add("CHANGE_ORDER_STATUS");
        }
    }

    // ============================================================
    // PHẦN 4 — OVERRIDE PHƯƠNG THỨC TỪ LỚP CHA (Đa hình)
    // ============================================================

    /**
     * ĐA HÌNH — Override getRole() từ abstract class User.
     *
     * Khi code gọi:
     *   User u = new Admin(...);
     *   u.getRole(); // → "QUẢN TRỊ VIÊN"
     *
     * Java runtime biết u thực sự là Admin → gọi phiên bản này.
     * Đây là "runtime polymorphism" hay "dynamic dispatch".
     *
     * @return Chuỗi mô tả vai trò của Admin
     */
    @Override
    public String getRole() { return "QUẢN TRỊ VIÊN"; }

    // ============================================================
    // PHẦN 5 — NGHIỆP VỤ CỦA ADMIN (Business Logic)
    // ============================================================

    /**
     * Kiểm tra Admin có quyền thực hiện thao tác cụ thể không.
     *
     * Ví dụ sử dụng:
     *   if (admin.hasPermission("CHANGE_ORDER_STATUS")) { ... }
     *
     * @param permission Tên quyền cần kiểm tra (viết hoa, dùng hằng số)
     * @return true nếu admin có quyền đó, false nếu không
     */
    public boolean hasPermission(String permission) {
        // List.contains() kiểm tra xem phần tử có tồn tại trong danh sách không
        return permissions.contains(permission);
    }

    // ============================================================
    // PHẦN 6 — GETTER (Đóng gói — Encapsulation)
    // ============================================================

    /**
     * Getter — Trả về cấp độ admin để hiển thị hoặc kiểm tra logic.
     */
    public int getAdminLevel() { return adminLevel; }

    /**
     * Getter — Trả về BẢN SAO của danh sách quyền.
     *
     * VÌ SAO DÙNG "new ArrayList<>(permissions)" CHỨ KHÔNG TRẢ "permissions"?
     *   → Nếu trả về trực tiếp, bên ngoài có thể sửa list nội bộ (vi phạm đóng gói).
     *   → Tạo bản sao để bảo vệ dữ liệu gốc bên trong Admin.
     */
    public List<String> getPermissions() { return new ArrayList<>(permissions); }

    // ============================================================
    // PHẦN 7 — TRIỂN KHAI GIAO DIỆN JsonSerializable
    // ============================================================

    /**
     * Chuyển đối tượng Admin thành chuỗi JSON để lưu vào file users.json.
     *
     * Ví dụ output JSON:
     * {
     *   "type":"Admin",
     *   "userId":"A001",
     *   "username":"admin",
     *   "password":"admin123",
     *   "adminLevel":1,
     *   "permissions":["MANAGE_PRODUCTS","VIEW_ORDERS","MANAGE_USERS","CHANGE_ORDER_STATUS"]
     * }
     *
     * Trường "type" là bắt buộc → JsonDataManager.loadUsers() dùng "type"
     * để biết phải tạo đối tượng Admin hay RegularCustomer hay VIPCustomer.
     *
     * Lưu ý: KHÔNG dùng thư viện ngoài (Gson, Jackson...) — tự build chuỗi JSON thủ công.
     */
    @Override
    public String toJson() {
        // Bước 1: Xây dựng phần JSON của mảng permissions: ["P1","P2",...]
        StringBuilder permsJson = new StringBuilder("[");
        for (int i = 0; i < permissions.size(); i++) {
            // Thêm dấu nháy kép quanh tên quyền
            permsJson.append("\"").append(permissions.get(i)).append("\"");
            // Thêm dấu phẩy giữa các phần tử, trừ phần tử cuối
            if (i < permissions.size() - 1) permsJson.append(",");
        }
        permsJson.append("]");

        // Bước 2: Ghép toàn bộ thành chuỗi JSON hoàn chỉnh với String.format
        return String.format(
            "{\"type\":\"Admin\",\"userId\":\"%s\",\"username\":\"%s\"," +
            "\"password\":\"%s\",\"adminLevel\":%d,\"permissions\":%s}",
            userId, username, password, adminLevel, permsJson
        );
        // %s → thay chuỗi, %d → thay số nguyên
    }

    // ============================================================
    // PHẦN 8 — HIỂN THỊ
    // ============================================================

    /**
     * Override toString() để hiển thị thông tin Admin dạng bảng.
     *
     * Ví dụ output:
     *   [QUẢN TRỊ VIÊN    ] admin        | Mã: A001  | Cấp quản trị: 1
     *
     * %-18s → căn trái trong 18 ký tự (đảm bảo bảng thẳng hàng)
     */
    @Override
    public String toString() {
        return String.format("[%-18s] %-12s | Mã: %-5s | Cấp quản trị: %d",
            getRole(), username, userId, adminLevel);
    }
}
