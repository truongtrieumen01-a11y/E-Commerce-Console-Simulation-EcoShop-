package app;

import exception.*;
import model.*;
import service.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ============================================================================
 *  ECOSHOP APP — CHƯƠNG TRÌNH ĐIỀU KHIỂN CHÍNH (Console Entry Point)
 * ============================================================================
 *
 * 🎯 VAI TRÒ TRONG ĐỀ TÀI:
 *  - Entry point điều khiển luồng ứng dụng mô phỏng bán hàng trực tuyến EcoShop.
 *  - Phối hợp chặt chẽ giữa Tầng Trình Bày (Presentation Layer), Tầng Nghiệp Vụ
 *    (Business Logic Layer) và Tầng Truy Xuất Dữ Liệu (Data Access Layer).
 *
 * 🛡️ 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 *  │ Đóng gói           │ Tất cả thuộc tính (dataManager, users, products, orders,     │
 *  │ (Encapsulation)    │ currentUser) đều là private, quản lý qua các phương thức nội │
 *  │                    │ bộ của lớp.                                                  │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Trừu tượng         │ EcoShopApp làm việc với các interface Discountable,          │
 *  │ (Abstraction)      │ JsonSerializable và các abstract class User, Product mà      │
 *  │                    │ không cần phụ thuộc vào chi tiết cài đặt bên trong.          │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Kế thừa            │ Quản lý đồng nhất cây phân cấp người dùng (User -> Admin,    │
 *  │ (Inheritance)      │ Customer -> RegularCustomer, VIPCustomer) và cây phân cấp    │
 *  │                    │ sản phẩm (Product -> ReusableProduct, OrganicProduct).       │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đa hình            │ 1. calculateDiscount(): Tự động áp dụng mức giảm giá chuẩn   │
 *  │ (Polymorphism)     │    cho từng loại khách hàng lúc đặt hàng.                    │
 *  │                    │ 2. getProductType() & getEcoBadge(): Tự động hiển thị nhãn   │
 *  │                    │    xanh tương ứng cho ReusableProduct và OrganicProduct.     │
 *  │                    │ 3. toJson(): Tuần tự hóa JSON đa hình theo từng đối tượng.   │
 *  └────────────────────┴──────────────────────────────────────────────────────────────┘
 *
 * ⚠️ XỬ LÝ NGOẠI LỆ (Error Handling & Robustness):
 *  - Mọi thao tác nhập số đều được bọc trong vòng lặp try-catch `readInt()`,
 *    `readDouble()` bắt lỗi `NumberFormatException` để ứng dụng KHÔNG BAO GIỜ bị dừng
 *    đột ngột (crash) khi người dùng nhập sai kiểu dữ liệu.
 *  - Các ngoại lệ nghiệp vụ (`InsufficientStockException`, `PaymentFailedException`,
 *    `InvalidInputException`, `ProductNotFoundException`) được bắt và hiển thị
 *    thông báo tiếng Việt thân thiện, rõ ràng.
 */
public class EcoShopApp {

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH ĐÓNG GÓI (Encapsulated Fields)
    // ========================================================================

    private final JsonDataManager  dataManager;
    private final PaymentProcessor paymentProcessor;
    private final Scanner          scanner;

    private List<User>    users;
    private List<Product> products;
    private List<Order>   orders;
    private User          currentUser;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Khởi tạo hệ thống)
    // ========================================================================

    public EcoShopApp() {
        this.dataManager      = new JsonDataManager("data");
        this.paymentProcessor = new PaymentProcessor();
        this.scanner          = new Scanner(System.in, StandardCharsets.UTF_8);
        this.orders           = new ArrayList<>();
        loadData();
    }

    // ========================================================================
    // PHẦN 3: ĐIỂM BẮT ĐẦU CHƯƠNG TRÌNH (main)
    // ========================================================================

    public static void main(String[] args) {
        // Thiết lập bảng mã hiển thị UTF-8 trên cửa sổ Console của Windows/Linux
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception ignored) {}

        EcoShopApp app = new EcoShopApp();
        app.run();
    }

    // ========================================================================
    // PHẦN 4: VÒNG LẶP CHÍNH (Main Event Loop)
    // ========================================================================

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            try {
                if (currentUser == null) {
                    running = showMainMenu();
                } else if (currentUser instanceof Admin) {
                    showAdminMenu();
                } else if (currentUser instanceof Customer) {
                    showCustomerMenu();
                }
            } catch (NoSuchElementException e) {
                // Thoát sạch khi luồng stdin kết thúc (piped input / EOF)
                running = false;
            } catch (Exception e) {
                // Tấm lưới an toàn toàn cục — đảm bảo ứng dụng không bao giờ bị dừng đột ngột
                System.out.println("\n  [x] Đã xảy ra lỗi không mong muốn: " + e.getMessage());
                System.out.println("  >> Đang quay lại menu chính...");
                currentUser = null;
            }
        }
        saveData();
        System.out.println("\n  ==========================================================");
        System.out.println("     Cảm ơn bạn đã mua sắm tại EcoShop! Hẹn gặp lại! 👋");
        System.out.println("  ==========================================================");
        scanner.close();
    }

    // ========================================================================
    // PHẦN 5: GIAO DIỆN CÁC MENU ĐIỀU HƯỚNG
    // ========================================================================

    /**
     * Hiển thị menu chính dành cho khách chưa đăng nhập.
     */
    private boolean showMainMenu() {
        System.out.println("\n  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │                 🛒  TRANG CHỦ ECOSHOP                  │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Đăng nhập tài khoản                                │");
        System.out.println("  │  2. Đăng ký tài khoản mới (Khách hàng)                 │");
        System.out.println("  │  3. Xem toàn bộ danh mục sản phẩm                      │");
        System.out.println("  │  4. Tìm kiếm & Lọc sản phẩm sinh thái                  │");
        System.out.println("  │  0. Thoát chương trình                                 │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-4): ", 0, 4);
        switch (choice) {
            case 1: handleLogin();        break;
            case 2: handleRegister();     break;
            case 3: browseProducts(null); break;
            case 4: searchProducts();     break;
            case 0: return false;
        }
        return true;
    }

    /**
     * Hiển thị Menu Quản trị viên (Admin Menu).
     * Phụ trách đầy đủ các nghiệp vụ quản lý sản phẩm, kho hàng, đơn hàng và thống kê.
     */
    private void showAdminMenu() {
        Admin admin = (Admin) currentUser;
        System.out.println("\n  ┌────────────────────────────────────────────────────────┐");
        System.out.printf("  │  👤 MENU QUẢN TRỊ VIÊN — Xin chào: %-19s │%n", admin.getUsername());
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Xem danh mục sản phẩm (Kèm nhãn sinh thái)         │");
        System.out.println("  │  2. Tìm kiếm & Lọc sản phẩm nâng cao                   │");
        System.out.println("  │  3. Thêm sản phẩm mới (Tái sử dụng / Hữu cơ)           │");
        System.out.println("  │  4. Chỉnh sửa thông tin sản phẩm                       │");
        System.out.println("  │  5. Xóa sản phẩm khỏi hệ thống                         │");
        System.out.println("  │  6. Kiểm tra tồn kho & Cảnh báo hàng sắp hết           │");
        System.out.println("  │  7. Xem danh sách đơn đặt hàng                         │");
        System.out.println("  │  8. Cập nhật trạng thái đơn hàng                       │");
        System.out.println("  │  9. Báo cáo thống kê doanh thu & Hiệu quả kinh doanh   │");
        System.out.println("  │ 10. Xem danh sách người dùng hệ thống                  │");
        System.out.println("  │  0. Đăng xuất tài khoản                                │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-10): ", 0, 10);
        switch (choice) {
            case 1: browseProducts(null);          break;
            case 2: searchProducts();              break;
            case 3: adminAddProduct();             break;
            case 4: adminEditProduct();            break;
            case 5: adminRemoveProduct();          break;
            case 6: adminCheckInventory();         break;
            case 7: adminViewOrders();             break;
            case 8: adminChangeOrderStatus();      break;
            case 9: adminViewRevenueStatistics();  break;
            case 10: adminViewUsers();             break;
            case 0:
                System.out.println("  [✓] Đã đăng xuất thành công.");
                currentUser = null;
                break;
        }
    }

    /**
     * Hiển thị Menu Khách hàng (Customer Menu).
     */
    private void showCustomerMenu() {
        Customer customer = (Customer) currentUser;
        String title = String.format("%s [%s]", customer.getUsername(), customer.getCustomerType());
        System.out.println("\n  ┌────────────────────────────────────────────────────────┐");
        System.out.printf("  │  🛍  MENU KHÁCH HÀNG — %-32s │%n", title);
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Xem danh mục sản phẩm sinh thái                    │");
        System.out.println("  │  2. Tìm kiếm & Lọc sản phẩm                            │");
        System.out.println("  │  3. Xem giỏ hàng của tôi                               │");
        System.out.println("  │  4. Thêm sản phẩm vào giỏ hàng                         │");
        System.out.println("  │  5. Xóa sản phẩm khỏi giỏ hàng                         │");
        System.out.println("  │  6. Thanh toán & Chốt đơn đặt hàng                     │");
        System.out.println("  │  7. Xem lịch sử mua hàng cá nhân                       │");
        System.out.println("  │  8. Xem thông tin tài khoản & Ưu đãi thành viên        │");
        System.out.println("  │  0. Đăng xuất                                          │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-8): ", 0, 8);
        try {
            switch (choice) {
                case 1: browseProducts(customer);   break;
                case 2: searchProducts();           break;
                case 3: viewCart(customer);         break;
                case 4: addToCart(customer);        break;
                case 5: removeFromCart(customer);   break;
                case 6: checkout(customer);         break;
                case 7: viewOrderHistory(customer); break;
                case 8: viewProfile(customer);      break;
                case 0:
                    System.out.println("  [✓] Đã đăng xuất thành công.");
                    currentUser = null;
                    break;
            }
        } catch (EcoShopException e) {
            System.out.println("\n  [!] LỖI HỆ THỐNG: " + e);
        }
    }

    // ========================================================================
    // PHẦN 6: XÁC THỰC NGƯỜI DÙNG (Đăng Nhập & Đăng Ký)
    // ========================================================================

    private void handleLogin() {
        printSectionHeader("ĐĂNG NHẬP HỆ THỐNG");
        System.out.print("  Tên đăng nhập : ");
        String username = scanner.nextLine().trim();
        System.out.print("  Mật khẩu      : ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("  [!] Tên đăng nhập và mật khẩu không được để trống.");
            return;
        }

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.login(password)) {
                currentUser = user;
                System.out.printf("%n  [✓] Đăng nhập thành công! Xin chào, %s (%s)%n",
                    user.getUsername(), user.getRole());
                return;
            }
        }
        System.out.println("  [x] Sai tên đăng nhập hoặc mật khẩu.");
    }

    private void handleRegister() {
        printSectionHeader("ĐĂNG KÝ TÀI KHOẢN KHÁCH HÀNG MỚI");
        try {
            System.out.print("  Tên đăng nhập: ");
            String username = scanner.nextLine().trim();
            if (username.isEmpty())
                throw new InvalidInputException("Tên đăng nhập không được để trống.");
            if (findUserByUsername(username) != null)
                throw new InvalidInputException("Tên đăng nhập '" + username + "' đã tồn tại trong hệ thống.");

            System.out.print("  Mật khẩu (tối thiểu 6 ký tự): ");
            String password = scanner.nextLine().trim();
            if (password.length() < 6)
                throw new InvalidInputException("Mật khẩu phải có ít nhất 6 ký tự để đảm bảo an toàn.");

            System.out.print("  Số điện thoại: ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty())
                throw new InvalidInputException("Số điện thoại không được để trống.");

            System.out.print("  Địa chỉ nhận hàng: ");
            String address = scanner.nextLine().trim();
            if (address.isEmpty())
                throw new InvalidInputException("Địa chỉ nhận hàng không được để trống.");

            System.out.println("  Loại tài khoản: [1] Khách hàng Thường (Tích điểm)   [2] Khách hàng VIP");
            int typeChoice = readInt("  >> Chọn loại tài khoản (1-2): ", 1, 2);

            String userId = "C" + String.format("%03d", users.size() + 1);
            Customer newCustomer;
            if (typeChoice == 2) {
                int vipLevel = readInt("  >> Chọn hạng VIP (1=Bạc [10%], 2=Vàng [15%], 3=Bạch Kim [20%]): ", 1, 3);
                newCustomer = new VIPCustomer(userId, username, password, address, phone, vipLevel);
            } else {
                newCustomer = new RegularCustomer(userId, username, password, address, phone);
            }

            users.add(newCustomer);
            currentUser = newCustomer;
            saveData();
            System.out.printf("%n  [✓] Đăng ký thành công! Chào mừng %s đến với EcoShop (%s)!%n",
                username, newCustomer.getCustomerType());

        } catch (InvalidInputException e) {
            System.out.println("  [!] LỖI NHẬP LIỆU: " + e.getMessage());
        }
    }

    // ========================================================================
    // PHẦN 7: HIỂN THỊ & TÌM KIẾM SẢN PHẨM (Đa hình Product)
    // ========================================================================

    /**
     * Xem danh mục sản phẩm kèm nhãn sinh thái đa hình.
     */
    private void browseProducts(Customer viewer) {
        printSectionHeader("DANH MỤC SẢN PHẨM SINH THÁI ECOSHOP");
        if (products.isEmpty()) {
            System.out.println("  [!] Hiện chưa có sản phẩm nào trong kho hàng.");
            return;
        }

        // Gom danh mục sản phẩm duy nhất
        Set<String> categories = new LinkedHashSet<>();
        for (Product p : products) categories.add(p.getCategory());

        System.out.println("  Lọc theo danh mục sản phẩm (Chọn 0 để xem tất cả):");
        int i = 1;
        List<String> catList = new ArrayList<>(categories);
        for (String cat : catList) System.out.printf("    %d. %s%n", i++, cat);
        System.out.print("  >> Chọn số danh mục (0 = Tất cả): ");
        String catInput = scanner.nextLine().trim();
        String filterCat = null;
        try {
            int catChoice = Integer.parseInt(catInput);
            if (catChoice >= 1 && catChoice <= catList.size()) {
                filterCat = catList.get(catChoice - 1);
            }
        } catch (NumberFormatException ignored) {}

        renderProductTable(products, filterCat);

        if (viewer != null) {
            System.out.printf("%n  [*] Ưu đãi hiện tại của bạn: Giảm $%.2f cho mỗi $100.00 mua sắm%n",
                viewer.calculateDiscount(100.0));
        }
    }

    /**
     * Tìm kiếm và lọc sản phẩm sinh thái theo nhiều tiêu chí.
     */
    private void searchProducts() {
        printSectionHeader("TÌM KIẾM & LỌC SẢN PHẨM");
        System.out.println("  Chọn tiêu chí tìm kiếm:");
        System.out.println("    1. Tìm theo tên hoặc từ khóa sản phẩm");
        System.out.println("    2. Lọc theo khoảng giá bán ($ min - max)");
        System.out.println("    3. Lọc theo dòng sinh thái (Tái sử dụng vs Hữu cơ)");
        System.out.println("    0. Quay lại");

        int choice = readInt("  >> Lựa chọn (0-3): ", 0, 3);
        List<Product> matched = new ArrayList<>();

        switch (choice) {
            case 1: {
                System.out.print("  Nhập từ khóa tìm kiếm: ");
                String kw = scanner.nextLine().trim().toLowerCase();
                if (kw.isEmpty()) {
                    System.out.println("  [!] Từ khóa không được để trống.");
                    return;
                }
                for (Product p : products) {
                    if (p.getName().toLowerCase().contains(kw) ||
                        p.getDescription().toLowerCase().contains(kw) ||
                        p.getProductId().toLowerCase().contains(kw)) {
                        matched.add(p);
                    }
                }
                break;
            }
            case 2: {
                double minPrice = readDouble("  Nhập giá thấp nhất ($): ", 0.0, 99999.0);
                double maxPrice = readDouble("  Nhập giá cao nhất ($): ", minPrice, 99999.0);
                for (Product p : products) {
                    if (p.getPrice() >= minPrice && p.getPrice() <= maxPrice) {
                        matched.add(p);
                    }
                }
                break;
            }
            case 3: {
                System.out.println("  Chọn dòng sản phẩm: [1] Sản phẩm Tái sử dụng   [2] Sản phẩm Hữu cơ");
                int typeChoice = readInt("  >> Lựa chọn (1-2): ", 1, 2);
                for (Product p : products) {
                    if (typeChoice == 1 && p instanceof ReusableProduct) {
                        matched.add(p);
                    } else if (typeChoice == 2 && p instanceof OrganicProduct) {
                        matched.add(p);
                    }
                }
                break;
            }
            case 0:
                return;
        }

        System.out.printf("%n  ==> Tìm thấy %d sản phẩm khớp với tiêu chuẩn:%n", matched.size());
        if (!matched.isEmpty()) {
            renderProductTable(matched, null);
        }
    }

    /**
     * Xuất bảng danh sách sản phẩm đẹp mắt, hiển thị đa hình nhãn sinh thái.
     */
    private void renderProductTable(List<Product> list, String categoryFilter) {
        System.out.println("\n  ┌────────┬────────────────────────────────┬────────────┬──────────┬──────────────────┬─────────────────────────────────────────────────┐");
        System.out.printf("  │ %-6s │ %-30s │ %-10s │ %-8s │ %-16s │ %-47s │%n",
            "Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho", "Danh mục", "Đặc tính sinh thái (Eco Badge)");
        System.out.println("  ├────────┼────────────────────────────────┼────────────┼──────────┼──────────────────┼─────────────────────────────────────────────────┤");

        int count = 0;
        for (Product p : list) {
            if (categoryFilter != null && !p.getCategory().equalsIgnoreCase(categoryFilter)) continue;

            // ĐA HÌNH: p.getEcoBadge() tự gọi phiên bản của ReusableProduct hoặc OrganicProduct
            String badge = p.getEcoBadge();
            if (badge.length() > 47) badge = badge.substring(0, 44) + "...";

            System.out.printf("  │ %-6s │ %-30s │ $%9.2f │ %8d │ %-16s │ %-47s │%n",
                p.getProductId(),
                (p.getName().length() > 30) ? p.getName().substring(0, 27) + "..." : p.getName(),
                p.getPrice(),
                p.getStockQuantity(),
                (p.getCategory().length() > 16) ? p.getCategory().substring(0, 13) + "..." : p.getCategory(),
                badge);
            count++;
        }
        System.out.println("  └────────┴────────────────────────────────┴────────────┴──────────┴──────────────────┴─────────────────────────────────────────────────┘");
        System.out.printf("  Tổng cộng: %d sản phẩm đang hiển thị%n", count);
    }

    // ========================================================================
    // PHẦN 8: NGHIỆP VỤ QUẢN TRỊ VIÊN (Admin Operations — Kho Hàng & Doanh Thu)
    // ========================================================================

    /**
     * Thêm sản phẩm sinh thái mới vào kho hàng (Hỗ trợ 2 nhánh kế thừa đa hình).
     */
    private void adminAddProduct() {
        printSectionHeader("THÊM SẢN PHẨM MỚI VÀO KHO");
        try {
            System.out.println("  Chọn phân loại sản phẩm sinh thái:");
            System.out.println("    1. Sản phẩm Tái sử dụng (ReusableProduct — Bình, Túi, Ống hút...)");
            System.out.println("    2. Sản phẩm Hữu cơ (OrganicProduct — Nông sản, Trà, Tinh dầu...)");
            int prodType = readInt("  >> Lựa chọn (1-2): ", 1, 2);

            String productId = "P" + String.format("%03d", products.size() + 1);
            System.out.print("  Tên sản phẩm: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) throw new InvalidInputException("Tên sản phẩm không được để trống.");

            System.out.print("  Mô tả chi tiết: ");
            String desc = scanner.nextLine().trim();

            double price = readDouble("  Giá bán ($): ", 0.01, 99999.99);
            int    stock = readInt("  Số lượng nhập kho ban đầu: ", 0, 99999);

            System.out.print("  Danh mục sản phẩm: ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) category = (prodType == 1) ? "Gia dụng xanh" : "Thực phẩm sinh thái";

            Product newProd;
            if (prodType == 1) {
                // Nhánh Sản phẩm Tái sử dụng
                System.out.print("  Chất liệu thân thiện môi trường: ");
                String material = scanner.nextLine().trim();
                int reuseCount = readInt("  Số lần tái sử dụng ước tính: ", 1, 100000);
                System.out.print("  Chứng chỉ môi trường / Tiêu chuẩn an toàn: ");
                String cert = scanner.nextLine().trim();

                newProd = new ReusableProduct(productId, name, desc, price, stock, category,
                    material, reuseCount, cert);
            } else {
                // Nhánh Sản phẩm Hữu cơ
                System.out.print("  Hạn sử dụng (YYYY-MM-DD): ");
                String expiry = scanner.nextLine().trim();
                if (expiry.isEmpty()) expiry = "2027-12-31";
                System.out.print("  Chứng nhận hữu cơ (USDA/EU/VietGAP): ");
                String orgCert = scanner.nextLine().trim();
                System.out.print("  Hướng dẫn bảo quản: ");
                String storage = scanner.nextLine().trim();

                newProd = new OrganicProduct(productId, name, desc, price, stock, category,
                    expiry, orgCert, storage);
            }

            products.add(newProd);
            saveData();
            System.out.printf("%n  [✓] Đã thêm sản phẩm thành công! Mã SP: %s | Loại: %s%n",
                productId, newProd.getProductType());

        } catch (InvalidInputException e) {
            System.out.println("  [!] LỖI NHẬP LIỆU: " + e.getMessage());
        }
    }

    /**
     * Chỉnh sửa thông tin sản phẩm (bao gồm cả thuộc tính đặc thù của lớp con).
     */
    private void adminEditProduct() {
        printSectionHeader("CHỈNH SỬA THÔNG TIN SẢN PHẨM");
        browseProducts(null);
        System.out.print("\n  >> Nhập Mã SP cần sửa: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        Product p = findProductById(productId);
        if (p == null) {
            System.out.println("  [!] Không tìm thấy sản phẩm có mã: " + productId);
            return;
        }

        System.out.printf("  Đang chọn: %s (%s) — Phân loại: %s%n",
            p.getName(), p.getProductId(), p.getProductType());
        System.out.println("  Chọn thông tin muốn cập nhật:");
        System.out.println("    1. Giá bán");
        System.out.println("    2. Số lượng tồn kho");
        System.out.println("    3. Tên sản phẩm");
        System.out.println("    4. Mô tả chi tiết");
        System.out.println("    5. Cập nhật thông số sinh thái đặc thù");
        System.out.println("    0. Hủy thao tác");

        int choice = readInt("  >> Lựa chọn (0-5): ", 0, 5);
        switch (choice) {
            case 1: {
                double newPrice = readDouble("  Nhập giá bán mới ($): ", 0.01, 99999.99);
                p.setPrice(newPrice);
                System.out.println("  [✓] Đã cập nhật giá bán.");
                break;
            }
            case 2: {
                int newStock = readInt("  Nhập số lượng tồn kho mới: ", 0, 99999);
                p.setStockQuantity(newStock);
                System.out.println("  [✓] Đã cập nhật số lượng tồn kho.");
                break;
            }
            case 3: {
                System.out.print("  Nhập tên sản phẩm mới: ");
                String newName = scanner.nextLine().trim();
                if (!newName.isEmpty()) {
                    p.setName(newName);
                    System.out.println("  [✓] Đã cập nhật tên sản phẩm.");
                }
                break;
            }
            case 4: {
                System.out.print("  Nhập mô tả mới: ");
                String newDesc = scanner.nextLine().trim();
                p.setDescription(newDesc);
                System.out.println("  [✓] Đã cập nhật mô tả.");
                break;
            }
            case 5: {
                // ĐA HÌNH / KẾ THỪA: Kiểm tra kiểu thực tế để cập nhật trường riêng
                if (p instanceof ReusableProduct) {
                    ReusableProduct rp = (ReusableProduct) p;
                    System.out.print("  Nhập chất liệu mới (Enter để giữ cũ): ");
                    String mat = scanner.nextLine().trim();
                    if (!mat.isEmpty()) rp.setMaterial(mat);

                    int reuses = readInt("  Số lần tái sử dụng mới: ", 1, 100000);
                    rp.setEstimatedReuseCount(reuses);

                    System.out.print("  Chứng chỉ mới (Enter để giữ cũ): ");
                    String cert = scanner.nextLine().trim();
                    if (!cert.isEmpty()) rp.setEcoCertificate(cert);

                    System.out.println("  [✓] Đã cập nhật đặc tính sản phẩm tái sử dụng.");
                } else if (p instanceof OrganicProduct) {
                    OrganicProduct op = (OrganicProduct) p;
                    System.out.print("  Hạn sử dụng mới YYYY-MM-DD: ");
                    String exp = scanner.nextLine().trim();
                    if (!exp.isEmpty()) op.setExpiryDate(exp);

                    System.out.print("  Chứng nhận hữu cơ mới: ");
                    String cert = scanner.nextLine().trim();
                    if (!cert.isEmpty()) op.setOrganicCertification(cert);

                    System.out.print("  Hướng dẫn bảo quản mới: ");
                    String st = scanner.nextLine().trim();
                    if (!st.isEmpty()) op.setStorageInstruction(st);

                    System.out.println("  [✓] Đã cập nhật đặc tính sản phẩm hữu cơ.");
                }
                break;
            }
            case 0:
                return;
        }
        saveData();
    }

    /**
     * Xóa sản phẩm khỏi kho hàng.
     */
    private void adminRemoveProduct() {
        printSectionHeader("XÓA SẢN PHẨM KHỎI KHO");
        browseProducts(null);
        System.out.print("\n  >> Nhập Mã SP cần xóa: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        Product target = findProductById(productId);

        if (target == null) {
            System.out.println("  [!] Không tìm thấy sản phẩm có mã: " + productId);
            return;
        }

        System.out.printf("  Bạn có chắc chắn muốn xóa '%s' khỏi hệ thống? [1] Có  [2] Hủy: ", target.getName());
        int confirm = readInt("", 1, 2);
        if (confirm == 1) {
            products.remove(target);
            saveData();
            System.out.println("  [✓] Đã xóa sản phẩm thành công khỏi kho dữ liệu.");
        } else {
            System.out.println("  [i] Đã hủy thao tác xóa.");
        }
    }

    /**
     * Kiểm tra tồn kho & Cảnh báo các mặt hàng sắp hết hàng.
     */
    private void adminCheckInventory() {
        printSectionHeader("BÁO CÁO TỒN KHO & CẢNH BÁO HẾT HÀNG");

        int totalStock = 0;
        double totalValue = 0.0;
        int reusableCount = 0;
        int organicCount = 0;
        List<Product> lowStock = new ArrayList<>();
        List<Product> outOfStock = new ArrayList<>();

        for (Product p : products) {
            totalStock += p.getStockQuantity();
            totalValue += p.getStockQuantity() * p.getPrice();
            if (p instanceof ReusableProduct) reusableCount++;
            if (p instanceof OrganicProduct)  organicCount++;

            if (p.getStockQuantity() == 0) {
                outOfStock.add(p);
            } else if (p.getStockQuantity() <= 5) {
                lowStock.add(p);
            }
        }

        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │               📊 TỔNG QUAN KHO HÀNG ECOSHOP             │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.printf("  │  Tổng số danh mục mặt hàng (SKU) : %-19d │%n", products.size());
        System.out.printf("  │  Tổng số lượng sản phẩm trong kho : %-19d │%n", totalStock);
        System.out.printf("  │  Tổng giá trị vốn tồn kho         : $%-18.2f │%n", totalValue);
        System.out.printf("  │  Sản phẩm Tái sử dụng (Reusable)  : %-19d │%n", reusableCount);
        System.out.printf("  │  Sản phẩm Hữu cơ (Organic)        : %-19d │%n", organicCount);
        System.out.println("  └────────────────────────────────────────────────────────┘");

        // Cảnh báo hết hàng
        if (!outOfStock.isEmpty()) {
            System.out.println("\n  ❌ CẢNH BÁO: CÓ SẢN PHẨM ĐÃ HẾT HÀNG (TỒN KHO = 0):");
            for (Product p : outOfStock) {
                System.out.printf("    • [%s] %-30s | Danh mục: %s%n",
                    p.getProductId(), p.getName(), p.getCategory());
            }
        } else {
            System.out.println("\n  [✓] Không có mặt hàng nào bị hết hàng hoàn toàn.");
        }

        // Cảnh báo sắp hết hàng (tồn <= 5)
        if (!lowStock.isEmpty()) {
            System.out.println("\n  ⚠️ CẢNH BÁO: CÁC MẶT HÀNG SẮP HẾT (TỒN KHO <= 5 CÁI):");
            for (Product p : lowStock) {
                System.out.printf("    • [%s] %-30s | Còn lại: %d cái | Giá: $%.2f%n",
                    p.getProductId(), p.getName(), p.getStockQuantity(), p.getPrice());
            }

            System.out.println("\n  Bạn có muốn nhập hàng nhanh cho một sản phẩm? [1] Có  [0] Không");
            int act = readInt("  >> Lựa chọn: ", 0, 1);
            if (act == 1) {
                System.out.print("  Nhập Mã SP muốn nhập thêm hàng: ");
                String pId = scanner.nextLine().trim().toUpperCase();
                Product target = findProductById(pId);
                if (target != null) {
                    int addQty = readInt("  Nhập số lượng bổ sung: ", 1, 9999);
                    target.addStock(addQty);
                    saveData();
                    System.out.printf("  [✓] Đã bổ sung thành công! Tồn kho hiện tại của '%s' là: %d%n",
                        target.getName(), target.getStockQuantity());
                } else {
                    System.out.println("  [!] Không tìm thấy mã sản phẩm.");
                }
            }
        } else {
            System.out.println("  [✓] Toàn bộ kho hàng đều duy trì mức tồn kho an toàn (> 5 cái).");
        }
    }

    /**
     * Báo cáo thống kê doanh thu và chỉ số kinh doanh toàn diện.
     */
    private void adminViewRevenueStatistics() {
        printSectionHeader("BÁO CÁO DOANH THU & CHỈ SỐ KINH DOANH");

        if (orders.isEmpty()) {
            System.out.println("  [!] Hiện chưa có đơn hàng nào trong lịch sử để thống kê.");
            return;
        }

        double totalRevenue = 0.0;
        double totalDiscountGiven = 0.0;
        double cashRevenue = 0.0;
        double cardRevenue = 0.0;
        double ewalletRevenue = 0.0;

        Map<OrderStatus, Integer> statusCount = new EnumMap<>(OrderStatus.class);
        for (OrderStatus st : OrderStatus.values()) statusCount.put(st, 0);

        Map<String, Integer> productQtySold = new HashMap<>();
        Map<String, Double> productRevenue = new HashMap<>();

        for (Order o : orders) {
            // Không tính các đơn đã hủy vào doanh thu thực
            if (o.getStatus() != OrderStatus.CANCELLED) {
                totalRevenue += o.getFinalAmount();
                totalDiscountGiven += o.getDiscountAmount();

                String pm = o.getPaymentMethod();
                if (pm.equalsIgnoreCase("CARD")) cardRevenue += o.getFinalAmount();
                else if (pm.equalsIgnoreCase("EWALLET")) ewalletRevenue += o.getFinalAmount();
                else cashRevenue += o.getFinalAmount();

                // Thống kê từng món hàng bán ra
                for (CartItem item : o.getItems()) {
                    String pName = item.getProduct().getName();
                    productQtySold.put(pName, productQtySold.getOrDefault(pName, 0) + item.getQuantity());
                    productRevenue.put(pName, productRevenue.getOrDefault(pName, 0.0) + item.getSubtotal());
                }
            }
            statusCount.put(o.getStatus(), statusCount.get(o.getStatus()) + 1);
        }

        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │             💰 CHỈ SỐ DOANH THU TỔNG QUAN              │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.printf("  │  Tổng số đơn hàng đã tiếp nhận   : %-19d │%n", orders.size());
        System.out.printf("  │  TỔNG DOANH THU THỰC NHẬN        : $%-18.2f │%n", totalRevenue);
        System.out.printf("  │  Tổng tiền chiết khấu tri ân KH  : $%-18.2f │%n", totalDiscountGiven);
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │          💳 DOANH THU THEO PHƯƠNG THỨC THANH TOÁN      │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.printf("  │  Tiền mặt (CASH)                 : $%-18.2f │%n", cashRevenue);
        System.out.printf("  │  Thẻ ngân hàng (CARD)            : $%-18.2f │%n", cardRevenue);
        System.out.printf("  │  Ví điện tử (E-WALLET)           : $%-18.2f │%n", ewalletRevenue);
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │             📦 TÌNH TRẠNG XỬ LÝ ĐƠN HÀNG               │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        for (OrderStatus st : OrderStatus.values()) {
            System.out.printf("  │  %-31s: %-19d │%n", st.getDisplayName(), statusCount.get(st));
        }
        System.out.println("  └────────────────────────────────────────────────────────┘");

        // Top 5 sản phẩm bán chạy nhất
        if (!productQtySold.isEmpty()) {
            System.out.println("\n  🔥 TOP SẢN PHẨM BÁN CHẠY NHẤT:");
            List<Map.Entry<String, Integer>> sortedSales = new ArrayList<>(productQtySold.entrySet());
            sortedSales.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            int rank = 1;
            for (Map.Entry<String, Integer> entry : sortedSales) {
                if (rank > 5) break;
                double rev = productRevenue.getOrDefault(entry.getKey(), 0.0);
                System.out.printf("    %d. %-32s | Đã bán: %3d món | Doanh thu: $%9.2f%n",
                    rank++, entry.getKey(), entry.getValue(), rev);
            }
        }
    }

    private void adminViewOrders() {
        printSectionHeader("DANH SÁCH TẤT CẢ ĐƠN ĐẶT HÀNG");
        if (orders.isEmpty()) {
            System.out.println("  [!] Hiện chưa có đơn đặt hàng nào trong hệ thống.");
            return;
        }
        System.out.println("  --------------------------------------------------------------------------------------------------------");
        for (Order o : orders) System.out.println("  " + o);
        System.out.println("  --------------------------------------------------------------------------------------------------------");
        System.out.printf("  Tổng số đơn hàng: %d%n", orders.size());
    }

    private void adminChangeOrderStatus() {
        printSectionHeader("CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG");
        if (orders.isEmpty()) {
            System.out.println("  [!] Chưa có đơn hàng nào.");
            return;
        }
        adminViewOrders();
        System.out.print("\n  >> Nhập Mã đơn hàng cần cập nhật: ");
        String orderId = scanner.nextLine().trim();
        Order target = null;
        for (Order o : orders) {
            if (o.getOrderId().equalsIgnoreCase(orderId)) { target = o; break; }
        }
        if (target == null) {
            System.out.println("  [!] Không tìm thấy đơn hàng với mã: " + orderId);
            return;
        }

        System.out.println("  Chọn trạng thái mới:");
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("    %d. %s (%s)%n", i + 1, statuses[i].getDisplayName(), statuses[i].name());
        }
        int choice = readInt("  >> Lựa chọn: ", 1, statuses.length);
        target.setStatus(statuses[choice - 1]);
        System.out.printf("  [✓] Đã cập nhật đơn hàng %s sang trạng thái: %s%n",
            orderId, statuses[choice - 1].getDisplayName());
        saveData();
    }

    private void adminViewUsers() {
        printSectionHeader("DANH SÁCH NGƯỜI DÙNG HỆ THỐNG");
        System.out.println("  --------------------------------------------------------------------------------------------------------");
        for (User u : users) System.out.println("  " + u);
        System.out.println("  --------------------------------------------------------------------------------------------------------");
        System.out.printf("  Tổng số người dùng: %d%n", users.size());
    }

    // ========================================================================
    // PHẦN 9: NGHIỆP VỤ KHÁCH HÀNG (Giỏ Hàng & Chốt Đơn)
    // ========================================================================

    private void viewCart(Customer customer) {
        printSectionHeader("GIỎ HÀNG CỦA BẠN");
        customer.getCart().printCart();
    }

    private void addToCart(Customer customer) throws EcoShopException {
        printSectionHeader("THÊM SẢN PHẨM VÀO GIỎ HÀNG");
        browseProducts(customer);

        System.out.print("\n  >> Nhập Mã sản phẩm muốn mua: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        Product product = findProductById(productId);

        if (product == null) {
            throw new ProductNotFoundException(productId);
        }
        if (!product.isAvailable()) {
            throw new InsufficientStockException(product.getName(), 1, 0);
        }

        System.out.printf("  Sản phẩm: %s | Tồn kho còn: %d cái | Giá: $%.2f%n",
            product.getName(), product.getStockQuantity(), product.getPrice());
        int quantity = readInt("  Nhập số lượng muốn mua: ", 1, product.getStockQuantity());

        customer.getCart().addItem(product, quantity);
        System.out.printf("  [✓] Đã thêm thành công %d x '%s' vào giỏ hàng!%n", quantity, product.getName());
    }

    private void removeFromCart(Customer customer) {
        printSectionHeader("XÓA SẢN PHẨM KHỎI GIỎ HÀNG");
        if (customer.getCart().isEmpty()) {
            System.out.println("  [!] Giỏ hàng của bạn đang trống.");
            return;
        }
        customer.getCart().printCart();
        System.out.print("\n  >> Nhập Mã sản phẩm cần xóa: ");
        String productId = scanner.nextLine().trim().toUpperCase();

        boolean removed = customer.getCart().removeItem(productId);
        if (removed) {
            System.out.println("  [✓] Đã xóa sản phẩm khỏi giỏ hàng.");
        } else {
            System.out.println("  [!] Không tìm thấy sản phẩm trong giỏ hàng.");
        }
    }

    private void checkout(Customer customer) throws EcoShopException {
        printSectionHeader("THANH TOÁN & CHỐT ĐƠN HÀNG");
        if (customer.getCart().isEmpty()) {
            throw new EcoShopException("Không thể thanh toán: Giỏ hàng đang trống.", "GIO_HANG_TRONG");
        }

        customer.getCart().printCart();

        // 1. Chốt đơn hàng & Trừ tồn kho qua Customer.placeOrder()
        Order order = customer.placeOrder();

        System.out.println("\n  Chọn phương thức thanh toán:");
        System.out.println("    1. Tiền mặt khi nhận hàng (CASH)");
        System.out.println("    2. Thẻ ngân hàng quốc tế / nội địa (CARD)");
        System.out.println("    3. Ví điện tử Momo/ZaloPay (E-WALLET)");

        int payChoice = readInt("  >> Lựa chọn phương thức (1-3): ", 1, 3);
        String method = (payChoice == 2) ? "CARD" : (payChoice == 3) ? "EWALLET" : "CASH";

        try {
            // 2. Xử lý thanh toán qua PaymentProcessor
            paymentProcessor.processPayment(order, method, scanner);

            // 3. Đưa vào danh sách đơn hàng toàn cục
            orders.add(order);
            saveData();

            // 4. In hóa đơn mua sắm hoàn chỉnh
            order.printReceipt();
            System.out.println("  [✓] Đơn hàng của bạn đã được xác nhận thành công!");

        } catch (PaymentFailedException e) {
            // Hoàn lại số lượng tồn kho nếu thanh toán thất bại
            for (CartItem item : order.getItems()) {
                item.getProduct().addStock(item.getQuantity());
            }
            throw e;
        }
    }

    private void viewOrderHistory(Customer customer) {
        printSectionHeader("LỊCH SỬ ĐƠN ĐẶT HÀNG CỦA BẠN");
        List<Order> history = customer.getOrderHistory();
        if (history.isEmpty()) {
            System.out.println("  [!] Bạn chưa có đơn đặt hàng nào trong lịch sử.");
            return;
        }
        for (Order o : history) {
            o.printReceipt();
        }
    }

    private void viewProfile(Customer customer) {
        printSectionHeader("THÔNG TIN TÀI KHOẢN KHÁCH HÀNG");
        System.out.println("  Mã người dùng   : " + customer.getUserId());
        System.out.println("  Tên đăng nhập   : " + customer.getUsername());
        System.out.println("  Hạng thành viên : " + customer.getCustomerType());
        System.out.println("  Số điện thoại   : " + customer.getPhoneNumber());
        System.out.println("  Địa chỉ nhận    : " + customer.getAddress());

        if (customer instanceof RegularCustomer) {
            RegularCustomer rc = (RegularCustomer) customer;
            System.out.println("  Điểm tích lũy   : " + rc.getLoyaltyPoints() + " điểm");
            System.out.printf("  Mức chiết khấu  : %.0f%%%n", rc.calculateDiscount(100.0));
        } else if (customer instanceof VIPCustomer) {
            VIPCustomer vc = (VIPCustomer) customer;
            System.out.println("  Cấp bậc VIP     : Level " + vc.getVipLevel());
            System.out.println("  Ngày tham gia   : " + vc.getMemberSince());
            System.out.printf("  Mức chiết khấu  : %.0f%%%n", vc.calculateDiscount(100.0));
        }
    }

    // ========================================================================
    // PHẦN 10: NẠP VÀ LƯU DỮ LIỆU BỀN VỮNG (Data Persistence Helpers)
    // ========================================================================

    private void loadData() {
        users    = dataManager.loadUsers();
        products = dataManager.loadProducts();
        orders   = dataManager.loadOrders(users, products);

        if (users.isEmpty())    seedDefaultUsers();
        if (products.isEmpty()) seedDefaultProducts();
    }

    private void saveData() {
        dataManager.saveUsers(users);
        dataManager.saveProducts(products);
        dataManager.saveOrders(orders);
    }

    /**
     * Khởi tạo tài khoản mẫu chuẩn OOP.
     */
    private void seedDefaultUsers() {
        users.add(new Admin("A001", "admin", "admin123", 1));
        users.add(new RegularCustomer("C001", "alice", "pass123",
            "123 Lê Lợi, Quận 1, TP.HCM", "0901234567"));
        users.add(new VIPCustomer("C002", "bob", "pass123",
            "456 Nguyễn Huệ, Quận 1, TP.HCM", "0912345678", 2));
        System.out.println("  [i] Đã nạp tài khoản mẫu: admin/admin123, alice/pass123, bob/pass123.");
    }

    /**
     * Khởi tạo sản phẩm sinh thái mẫu đại diện cho 2 nhánh kế thừa:
     *   1. ReusableProduct (Bình giữ nhiệt, Túi canvas, Ống hút tre, Hộp bã mía)
     *   2. OrganicProduct (Trà hữu cơ, Hạt dinh dưỡng, Xà bông sinh học, Tinh dầu)
     */
    private void seedDefaultProducts() {
        // Nhánh 1: ReusableProduct (Sản phẩm Tái sử dụng)
        products.add(new ReusableProduct("P001", "Bình giữ nhiệt Inox 304 Eco",
            "Bình giữ nhiệt chân không 500ml giữ nóng 12h, lạnh 24h", 18.50, 40,
            "Gia dụng xanh", "Inox 304 & Nắp tre", 2500, "BPA-Free & FDA"));

        products.add(new ReusableProduct("P002", "Túi vải Canvas hữu cơ EcoBag",
            "Túi tote vải bố 100% sợi bông organic thay thế túi nilon", 6.50, 60,
            "Thời trang xanh", "Vải Canvas hữu cơ", 1000, "GOTS Certified"));

        products.add(new ReusableProduct("P003", "Hộp cơm thủy tinh chịu nhiệt",
            "Hộp bảo quản thực phẩm 1000ml thủy tinh cao cấp nắp khóa", 12.00, 30,
            "Gia dụng xanh", "Thủy tinh Borosilicate", 1500, "LFGB Germany"));

        products.add(new ReusableProduct("P004", "Bộ ống hút tre tự nhiên (10 cái)",
            "Ống hút làm từ thân tre mộc tự nhiên có kèm cọ rửa", 4.50, 80,
            "Đồ dùng tái chế", "Tre tự nhiên 100%", 500, "FSC 100%"));

        // Nhánh 2: OrganicProduct (Sản phẩm Hữu cơ)
        products.add(new OrganicProduct("P005", "Trà hoa cúc sấy lạnh hữu cơ",
            "Trà hoa cúc hữu cơ thanh mát không hóa chất đóng hộp 100g", 9.90, 50,
            "Thực phẩm sinh thái", "2027-10-20", "USDA Organic", "Khô mát, tránh ánh nắng"));

        products.add(new OrganicProduct("P006", "Hạt Macca sấy nứt vỏ hữu cơ",
            "Hạt macca giàu dinh dưỡng từ nông trại sinh thái Tây Nguyên 500g", 14.50, 35,
            "Thực phẩm sinh thái", "2027-08-15", "VietGAP Organic", "Ngăn mát tủ lạnh"));

        products.add(new OrganicProduct("P007", "Xà bông sinh học tinh dầu tràm",
            "Bánh xà phòng tắm thảo mộc tự nhiên tự phân hủy sinh học 120g", 3.80, 70,
            "Chăm sóc cá nhân", "2028-01-01", "ECOCERT Organic", "Nơi khô ráo"));

        products.add(new OrganicProduct("P008", "Dầu gội bồ kết thảo dược mộc",
            "Dầu gội truyền thống nấu từ bồ kết cô đặc và vỏ bưởi 300ml", 8.20, 45,
            "Chăm sóc cá nhân", "2027-05-30", "Cruelty-Free & Bio", "Nhiệt độ phòng"));

        System.out.println("  [i] Đã nạp danh mục 8 sản phẩm sinh thái mẫu chuẩn OOP.");
    }

    private User findUserByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    private Product findProductById(String productId) {
        for (Product p : products) {
            if (p.getProductId().equalsIgnoreCase(productId)) return p;
        }
        return null;
    }

    // ========================================================================
    // PHẦN 11: TRỢ THỦ NHẬP LIỆU BẢO VỆ CHỐNG CRASH (Try-Catch Input Helpers)
    // ========================================================================

    /**
     * Nhập số nguyên từ bàn phím an toàn. Tự động bắt lỗi NumberFormatException
     * và yêu cầu nhập lại nếu sai định dạng.
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("  [!] Vui lòng nhập số trong khoảng từ %d đến %d.%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Dữ liệu không hợp lệ. Vui lòng nhập một số nguyên hợp lệ.");
            }
        }
    }

    /**
     * Nhập số thực từ bàn phím an toàn. Bắt lỗi không làm dừng chương trình.
     */
    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                double value = Double.parseDouble(line);
                if (value < min || value > max) {
                    System.out.printf("  [!] Giá trị phải nằm trong khoảng từ %.2f đến %.2f.%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Số thực không hợp lệ. Vui lòng nhập lại.");
            }
        }
    }

    // ========================================================================
    // PHẦN 12: TRỢ THỦ GIAO DIỆN (UI Decoration Helpers)
    // ========================================================================

    private void printSectionHeader(String title) {
        System.out.println("\n  ==========================================================");
        System.out.println("    " + title);
        System.out.println("  ==========================================================");
    }

    private void printBanner() {
        System.out.println();
        System.out.println("  ╔════════════════════════════════════════════════════════╗");
        System.out.println("  ║        🛒  ECOSHOP — HỆ THỐNG MUA SẮM TRỰC TUYẾN       ║");
        System.out.println("  ║      Mô phỏng Lập trình Hướng Đối tượng (OOP Java)     ║");
        System.out.println("  ╚════════════════════════════════════════════════════════╝");
        System.out.println("    Tính năng: Kế thừa · Đa hình · Đóng gói · Trừu tượng");
        System.out.println("               Lưu trữ JSON · Bắt lỗi Try-Catch an toàn");
    }
}
