package skeleton.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Admin — Kế thừa từ User, đại diện cho Quản trị viên cửa hàng.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class Admin extends User {

    private int adminLevel;
    private List<String> permissions;

    public Admin(String userId, String username, String password, int adminLevel) {
        super(userId, username, password);
        this.adminLevel = adminLevel;
        this.permissions = new ArrayList<>();
        // TODO: Khởi tạo danh sách quyền mặc định
    }

    @Override
    public String getRole() {
        return "QUẢN TRỊ VIÊN";
    }

    public boolean hasPermission(String permission) {
        // TODO: Kiểm tra quyền trong danh sách permissions
        return false;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public List<String> getPermissions() {
        // TODO: Áp dụng Defensive Copying trả về bản sao danh sách
        return new ArrayList<>(permissions);
    }

    @Override
    public String toJson() {
        // TODO: Hiện thực chuyển đổi sang JSON chuỗi
        return "";
    }
}
