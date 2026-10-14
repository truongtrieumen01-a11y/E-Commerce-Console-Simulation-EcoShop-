package skeleton.app;

import skeleton.model.*;
import skeleton.service.*;
import java.util.List;
import java.util.Scanner;

/**
 * Lớp EcoShopApp — Điểm khởi động chính (Entry Point) của ứng dụng Console.
 *
 * Điều phối luồng chương trình, quản lý phiên đăng nhập hiện tại và menu chức năng.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class EcoShopApp {

    private final JsonDataManager dataManager;
    private final PaymentProcessor paymentProcessor;
    private final Scanner scanner;

    private List<User> users;
    private List<Product> products;
    private List<Order> orders;
    private User currentUser;

    public EcoShopApp() {
        this.dataManager = new JsonDataManager("data");
        this.paymentProcessor = new PaymentProcessor();
        this.scanner = new Scanner(System.in);
        // TODO: Khởi tạo danh sách và gọi loadData()
    }

    public static void main(String[] args) {
        // TODO: Khởi tạo EcoShopApp và gọi app.run()
        System.out.println("=== EcoShop Skeleton Tuần 1: Khung chương trình đã sẵn sàng! ===");
    }

    public void run() {
        // TODO: Hiện thực vòng lặp menu chính với cơ chế Try-Catch an toàn
    }

    private boolean showMainMenu() {
        // TODO: Hiển thị bảng menu chính (Đăng nhập, Đăng ký, Xem sản phẩm, Thoát)
        return false;
    }

    private void showAdminMenu() {
        // TODO: Hiển thị bảng chức năng cho Quản trị viên
    }

    private void showCustomerMenu() {
        // TODO: Hiển thị bảng chức năng cho Khách hàng
    }
}
