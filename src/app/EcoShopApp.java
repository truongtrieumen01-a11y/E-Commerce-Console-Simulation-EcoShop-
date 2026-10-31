package app;

import exception.*;
import model.*;
import service.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * EcoShopApp — Chương trình Console chính (Entry point).
 *
 * Thể hiện đầy đủ 4 trụ cột OOP:
 *  - Đóng gói (Encapsulation) : các thuộc tính private (users, products, orders, currentUser)
 *  - Trừu tượng (Abstraction) : gọi các phương thức từ interface (calculateDiscount, toJson)
 *  - Kế thừa (Inheritance)    : quản lý các lớp con của User
 *  - Đa hình (Polymorphism)   : calculateDiscount() tự động gọi đúng phương thức của lớp con lúc chạy
 *
 * Xử lý ngoại lệ (Error Handling): mọi thao tác nhập liệu đều được bọc trong try-catch để không bị crash.
 */
public class EcoShopApp {

    // ------------------------------------------------------------------ fields (Encapsulation)
    private final JsonDataManager  dataManager;
    private final PaymentProcessor paymentProcessor;
    private final Scanner          scanner;

    private List<User>    users;
    private List<Product> products;
    private List<Order>   orders;
    private User          currentUser;

    // ------------------------------------------------------------------ constructor
    public EcoShopApp() {
        this.dataManager      = new JsonDataManager("data");
        this.paymentProcessor = new PaymentProcessor();
        this.scanner          = new Scanner(System.in, StandardCharsets.UTF_8);
        this.orders           = new ArrayList<>();
        initData();
    }

    // ================================================================== ENTRY POINT
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));
        } catch (Exception ignored) {}

        EcoShopApp app = new EcoShopApp();
        app.run();
    }

    // ================================================================== MAIN LOOP
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
                // EOF reached (stdin closed / piped input ended) — exit cleanly
                running = false;
            } catch (Exception e) {
                // Global safety net — application never crashes from bad input
                System.out.println("\n  [x] Đã xảy ra lỗi không mong muốn: " + e.getMessage());
                System.out.println("  >> Đang quay lại menu chính...");
                currentUser = null;
            }
        }
        saveAllData();
        System.out.println("\n  ==========================================================");
        System.out.println("     Cảm ơn bạn đã mua sắm tại EcoShop! Hẹn gặp lại! 👋");
        System.out.println("  ==========================================================");
        scanner.close();
    }

    // ================================================================== MENUS
    /**
     * Hiển thị menu chính trước khi đăng nhập.
     * @return false nếu người dùng chọn thoát
     */
    private boolean showMainMenu() {
        System.out.println("\n  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │                 🛒  TRANG CHỦ ECOSHOP                  │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Đăng nhập tài khoản                                │");
        System.out.println("  │  2. Đăng ký tài khoản mới (Khách hàng)                 │");
        System.out.println("  │  3. Xem danh sách sản phẩm (Khách vãng lai)            │");
        System.out.println("  │  0. Thoát chương trình                                 │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-3): ", 0, 3);
        switch (choice) {
            case 1: handleLogin();        break;
            case 2: handleRegister();     break;
            case 3: browseProducts(null); break;
            case 0: return false;
        }
        return true;
    }

    // ------------------------------------------------------------------ ADMIN MENU
    private void showAdminMenu() {
        Admin admin = (Admin) currentUser;
        System.out.println("\n  ┌────────────────────────────────────────────────────────┐");
        System.out.printf("  │  👤 MENU QUẢN TRỊ VIÊN — Xin chào: %-19s │%n", admin.getUsername());
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Xem toàn bộ sản phẩm                               │");
        System.out.println("  │  2. Thêm sản phẩm xanh đa hình                         │");
        System.out.println("  │  3. Chỉnh sửa thông tin sản phẩm & Nhập kho (addStock) │");
        System.out.println("  │  4. Xóa sản phẩm                                       │");
        System.out.println("  │  5. Xem danh sách đơn hàng                             │");
        System.out.println("  │  6. Xem danh sách người dùng                           │");
        System.out.println("  │  7. Cập nhật trạng thái đơn hàng                       │");
        System.out.println("  │  8. Xem thống kê tổng doanh thu bán lẻ                 │");
        System.out.println("  │  0. Đăng xuất                                          │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-8): ", 0, 8);
        switch (choice) {
            case 1: browseProducts(null);         break;
            case 2: adminAddProduct();            break;
            case 3: adminEditProduct();           break;
            case 4: adminRemoveProduct();         break;
            case 5: adminViewOrders();            break;
            case 6: adminViewUsers();             break;
            case 7: adminChangeOrderStatus();     break;
            case 8: adminViewRevenueStatistics(); break;
            case 0:
                System.out.println("  [✓] Đã đăng xuất thành công.");
                currentUser = null;
                break;
        }
    }

    // ------------------------------------------------------------------ CUSTOMER MENU
    private void showCustomerMenu() {
        Customer customer = (Customer) currentUser;
        String title = String.format("%s [%s]", customer.getUsername(), customer.getCustomerType());
        System.out.println("\n  ┌────────────────────────────────────────────────────────┐");
        System.out.printf("  │  🛍  MENU KHÁCH HÀNG — %-32s │%n", title);
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  1. Xem danh mục sản phẩm                              │");
        System.out.println("  │  2. Xem giỏ hàng của tôi                               │");
        System.out.println("  │  3. Thêm sản phẩm vào giỏ hàng                         │");
        System.out.println("  │  4. Xóa sản phẩm khỏi giỏ hàng                         │");
        System.out.println("  │  5. Thanh toán & Đặt hàng                              │");
        System.out.println("  │  6. Xem lịch sử đơn hàng                               │");
        System.out.println("  │  7. Xem thông tin tài khoản                            │");
        System.out.println("  │  0. Đăng xuất                                          │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-7): ", 0, 7);
        try {
            switch (choice) {
                case 1: browseProducts(customer);   break;
                case 2: viewCart(customer);         break;
                case 3: addToCart(customer);        break;
                case 4: removeFromCart(customer);   break;
                case 5: checkout(customer);         break;
                case 6: viewOrderHistory(customer); break;
                case 7: viewProfile(customer);      break;
                case 0:
                    System.out.println("  [✓] Đã đăng xuất thành công.");
                    currentUser = null;
                    break;
            }
        } catch (EcoShopException e) {
            System.out.println("\n  [!] LỖI HỆ THỐNG: " + e);
        }
    }

    // ================================================================== AUTH
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
            if (user.getUsername().equalsIgnoreCase(username) && user.authenticate(password)) {
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
                throw new InvalidInputException("Tên đăng nhập '" + username + "' đã tồn tại.");

            System.out.print("  Mật khẩu (tối thiểu 6 ký tự): ");
            String password = scanner.nextLine().trim();
            if (password.length() < 6)
                throw new InvalidInputException("Mật khẩu phải có ít nhất 6 ký tự.");

            System.out.print("  Số điện thoại: ");
            String phone = scanner.nextLine().trim();
            if (phone.isEmpty())
                throw new InvalidInputException("Số điện thoại không được để trống.");

            System.out.print("  Địa chỉ nhận hàng: ");
            String address = scanner.nextLine().trim();
            if (address.isEmpty())
                throw new InvalidInputException("Địa chỉ không được để trống.");

            System.out.println("  Loại tài khoản: [1] Khách hàng Thường   [2] Khách hàng VIP");
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
            System.out.printf("%n  [✓] Đăng ký tài khoản thành công! Chào mừng %s đến với EcoShop!%n", username);

        } catch (InvalidInputException e) {
            System.out.println("  [!] LỖI NHẬP LIỆU: " + e.getMessage());
        }
    }

    // ================================================================== PRODUCT BROWSING
    private void browseProducts(Customer viewer) {
        printSectionHeader("DANH MỤC SẢN PHẨM");
        if (products.isEmpty()) {
            System.out.println("  [!] Hiện chưa có sản phẩm nào trong hệ thống.");
            return;
        }

        // Lọc theo danh mục
        Set<String> categories = new LinkedHashSet<>();
        for (Product p : products) categories.add(p.getCategory());

        System.out.println("  Lọc theo danh mục sản phẩm (Nhấn Enter hoặc chọn 0 để xem tất cả):");
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
        } catch (NumberFormatException e) {
            // Hiển thị tất cả
        }

        System.out.println("\n  ┌────────┬────────────────────────────────┬────────────┬──────────┬──────────────────┬────────────────────────────────────────────────────────┐");
        System.out.printf("  │ %-6s │ %-30s │ %-10s │ %-8s │ %-16s │ %-54s │%n",
            "Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho", "Danh mục", "Đặc tính sinh thái / Nhãn xanh đa hình (Eco Badge)");
        System.out.println("  ├────────┼────────────────────────────────┼────────────┼──────────┼──────────────────┼────────────────────────────────────────────────────────┤");
        int count = 0;
        for (Product p : products) {
            if (filterCat != null && !p.getCategory().equals(filterCat)) continue;
            System.out.printf("  │ %-6s │ %-30s │ $%9.2f │ %8d │ %-16s │ %-54s │%n",
                p.getProductId(), p.getName(), p.getPrice(), p.getStockQuantity(), p.getCategory(), p.getEcoBadge());
            count++;
        }
        System.out.println("  └────────┴────────────────────────────────┴────────────┴──────────┴──────────────────┴────────────────────────────────────────────────────────┘");
        System.out.printf("  Đang hiển thị: %d sản phẩm%n", count);

        if (viewer != null) {
            System.out.printf("  [*] Ưu đãi hiện tại của bạn: Giảm $%.2f cho mỗi $100.00 mua hàng%n",
                viewer.calculateDiscount(100.0));
        }
    }

    // ================================================================== CART OPERATIONS
    private void viewCart(Customer customer) {
        printSectionHeader("GIỎ HÀNG CỦA BẠN");
        customer.getCart().printCart();
    }

    private void addToCart(Customer customer) throws EcoShopException {
        printSectionHeader("THÊM SẢN PHẨM VÀO GIỎ HÀNG");
        browseProducts(customer);

        System.out.print("\n  >> Nhập Mã SP cần mua (VD: P001): ");
        String productId = scanner.nextLine().trim().toUpperCase();

        Product product = findProductById(productId);
        if (product == null) throw new ProductNotFoundException(productId);
        if (!product.isAvailable()) {
            System.out.println("  [!] Sản phẩm này hiện đã hết hàng.");
            return;
        }

        int qty = readInt("  >> Nhập số lượng (Tồn kho hiện có: " + product.getStockQuantity() + "): ",
            1, product.getStockQuantity());

        try {
            customer.getCart().addItem(product, qty);
            System.out.printf("  [✓] Đã thêm %d x '%s' vào giỏ hàng.%n", qty, product.getName());
        } catch (InsufficientStockException e) {
            System.out.println("  [!] LỖI TỒN KHO: " + e.getMessage());
        }
    }

    private void removeFromCart(Customer customer) {
        printSectionHeader("XÓA SẢN PHẨM KHỎI GIỎ HÀNG");
        customer.getCart().printCart();
        if (customer.getCart().isEmpty()) return;

        System.out.print("  >> Nhập Mã SP muốn xóa (hoặc nhấn Enter để hủy): ");
        String productId = scanner.nextLine().trim().toUpperCase();
        if (productId.isEmpty()) return;

        boolean removed = customer.getCart().removeItem(productId);
        System.out.println(removed
            ? "  [✓] Đã xóa sản phẩm khỏi giỏ hàng."
            : "  [!] Không tìm thấy sản phẩm này trong giỏ hàng.");
    }

    // ================================================================== CHECKOUT
    private void checkout(Customer customer) throws EcoShopException {
        printSectionHeader("THANH TOÁN ĐƠN HÀNG");
        customer.getCart().printCart();

        if (customer.getCart().isEmpty()) {
            System.out.println("  [!] Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán!");
            return;
        }

        // Tính chiết khấu đa hình (Polymorphism)
        double subtotal = customer.getCart().getTotalAmount();
        double discount = customer.calculateDiscount(subtotal);   // POLYMORPHISM
        double finalAmt = subtotal - discount;

        System.out.println("\n  ┌──────────────────────────────────────────────┐");
        System.out.println("  │             TÓM TẮT ĐƠN HÀNG                 │");
        System.out.println("  ├──────────────────────────────────────────────┤");
        System.out.printf("  │  Hạng khách hàng : %-25s │%n", customer.getCustomerType());
        System.out.printf("  │  Tổng tạm tính   : $%-24.2f │%n", subtotal);
        System.out.printf("  │  Tiền giảm giá   : -$%-23.2f │%n", discount);
        System.out.printf("  │  CẦN THANH TOÁN  : $%-24.2f │%n", finalAmt);
        System.out.println("  └──────────────────────────────────────────────┘");

        System.out.print("\n  >> Xác nhận đặt hàng? (Y = Đồng ý / N = Hủy): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y") && !confirm.equalsIgnoreCase("C")) {
            System.out.println("  [!] Đã hủy thao tác đặt hàng.");
            return;
        }

        // Chọn phương thức thanh toán
        System.out.println("\n  Chọn phương thức thanh toán:");
        System.out.println("    1. Tiền mặt (Cash)");
        System.out.println("    2. Thẻ ngân hàng (Card)");
        System.out.println("    3. Ví điện tử (E-Wallet)");
        int payChoice = readInt("  >> Lựa chọn của bạn (1-3): ", 1, 3);
        String method = payChoice == 1 ? "CASH" : payChoice == 2 ? "CARD" : "EWALLET";

        // Tạo đơn hàng
        Order order = customer.placeOrder();

        try {
            paymentProcessor.processPayment(order, method, scanner);
            orders.add(order);
            saveData();
            System.out.println("\n  [✓] Đặt hàng và thanh toán thành công!");
            order.printReceipt();
        } catch (PaymentFailedException e) {
            // Hoàn lại tồn kho nếu thanh toán thất bại
            for (CartItem item : order.getItems()) {
                item.getProduct().addStock(item.getQuantity());
            }
            customer.getOrderHistory().clear();
            System.out.println("\n  [x] LỖI THANH TOÁN: " + e.getMessage());
            System.out.println("  >> Các sản phẩm đã được hoàn trả lại vào giỏ hàng.");
            for (CartItem item : order.getItems()) {
                try {
                    customer.getCart().addItem(item.getProduct(), item.getQuantity());
                } catch (InsufficientStockException ignored) {}
            }
        }
    }

    // ================================================================== HISTORY & PROFILE
    private void viewOrderHistory(Customer customer) {
        printSectionHeader("LỊCH SỬ ĐƠN HÀNG CỦA BẠN");
        List<Order> history = customer.getOrderHistory();
        if (history.isEmpty()) {
            System.out.println("  [!] Bạn chưa có đơn hàng nào.");
            return;
        }
        System.out.println("  -----------------------------------------------------------------------------------------");
        for (Order o : history) System.out.println("  " + o);
        System.out.println("  -----------------------------------------------------------------------------------------");
    }

    private void viewProfile(Customer customer) {
        printSectionHeader("THÔNG TIN TÀI KHOẢN CÁ NHÂN");
        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.printf("  │  Mã khách hàng   : %-35s │%n", customer.getUserId());
        System.out.printf("  │  Tên đăng nhập   : %-35s │%n", customer.getUsername());
        System.out.printf("  │  Số điện thoại   : %-35s │%n", customer.getPhoneNumber());
        System.out.printf("  │  Địa chỉ         : %-35s │%n", customer.getAddress());
        System.out.printf("  │  Hạng thành viên : %-35s │%n", customer.getCustomerType());
        if (customer instanceof RegularCustomer) {
            RegularCustomer rc = (RegularCustomer) customer;
            String loyaltyInfo = String.format("%d điểm (Mốc tiếp theo: %s)",
                rc.getLoyaltyPoints(), nextTier(rc.getLoyaltyPoints()));
            System.out.printf("  │  Điểm tích lũy   : %-35s │%n", loyaltyInfo);
        } else if (customer instanceof VIPCustomer) {
            VIPCustomer vc = (VIPCustomer) customer;
            String vipInfo = String.format("Cấp %d (Giảm %.0f%%)",
                vc.getVipLevel(), vc.getDiscountRate() * 100);
            System.out.printf("  │  Cấp độ VIP      : %-35s │%n", vipInfo);
        }
        String discountPreview = String.format("$%.2f trên mỗi $100 chi tiêu", customer.calculateDiscount(100.0));
        System.out.printf("  │  Mức chiết khấu  : %-35s │%n", discountPreview);
        System.out.println("  └────────────────────────────────────────────────────────┘");
    }

    private String nextTier(int pts) {
        if (pts < 100)  return "100 điểm";
        if (pts < 500)  return "500 điểm";
        if (pts < 1000) return "1000 điểm";
        return "Đã đạt tối đa";
    }

    // ================================================================== ADMIN OPERATIONS
    private void adminAddProduct() {
        printSectionHeader("THÊM SẢN PHẨM MỚI (SẢN PHẨM XANH ĐA HÌNH)");
        try {
            System.out.println("  Chọn loại sản phẩm sinh thái cần thêm:");
            System.out.println("    1. Sản phẩm Tái sử dụng (ReusableProduct)");
            System.out.println("    2. Sản phẩm Hữu cơ (OrganicProduct)");
            int typeChoice = readInt("  >> Lựa chọn của bạn (1-2): ", 1, 2);

            String productId = "P" + String.format("%03d", products.size() + 1);
            System.out.printf("  Mã sản phẩm tự động: %s%n", productId);
            System.out.print("  Tên sản phẩm: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) throw new InvalidInputException("Tên sản phẩm không được để trống.");

            System.out.print("  Mô tả sản phẩm: ");
            String desc = scanner.nextLine().trim();

            double price = readDouble("  Giá bán ($): ", 0.01, 99999.99);
            int    stock = readInt("  Số lượng tồn kho ban đầu: ", 0, 99999);

            System.out.print("  Danh mục sản phẩm: ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) category = "Chung";

            Product newProd;
            if (typeChoice == 1) {
                System.out.print("  Chất liệu thân thiện môi trường: ");
                String material = scanner.nextLine().trim();
                if (material.isEmpty()) material = "Vật liệu tái chế";
                int reuseCount = readInt("  Số lần tái sử dụng ước tính: ", 1, 100000);
                System.out.print("  Chứng chỉ sinh thái / Tiêu chuẩn xanh: ");
                String cert = scanner.nextLine().trim();
                if (cert.isEmpty()) cert = "Eco-Standard";
                newProd = new ReusableProduct(productId, name, desc, price, stock, category, material, reuseCount, cert);
            } else {
                System.out.print("  Hạn sử dụng (YYYY-MM-DD): ");
                String expiry = scanner.nextLine().trim();
                if (expiry.isEmpty()) expiry = "2027-12-31";
                System.out.print("  Chứng nhận hữu cơ (USDA/EU/VietGAP...): ");
                String orgCert = scanner.nextLine().trim();
                if (orgCert.isEmpty()) orgCert = "Chuẩn Hữu Cơ";
                System.out.print("  Hướng dẫn bảo quản: ");
                String storage = scanner.nextLine().trim();
                if (storage.isEmpty()) storage = "Nhiệt độ phòng";
                newProd = new OrganicProduct(productId, name, desc, price, stock, category, expiry, orgCert, storage);
            }

            products.add(newProd);
            saveAllData();
            System.out.printf("  [✓] Đã thêm thành công %s: '%s' (Mã: %s)%n",
                newProd.getProductType(), name, productId);

        } catch (InvalidInputException e) {
            System.out.println("  [!] LỖI NHẬP LIỆU: " + e.getMessage());
        }
    }

    private void adminEditProduct() {
        printSectionHeader("CHỈNH SỬA SẢN PHẨM & QUẢN LÝ KHO HÀNG");
        browseProducts(null);
        System.out.print("\n  >> Nhập Mã SP cần sửa: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        Product p = findProductById(productId);
        if (p == null) {
            System.out.println("  [!] Không tìm thấy sản phẩm có mã: " + productId);
            return;
        }

        System.out.println("  Đang chọn sửa: " + p.getName() + " (" + p.getProductId() + ") - Phân loại: " + p.getProductType());
        System.out.println("  Bạn muốn thay đổi thông tin nào?");
        System.out.println("    1. Cập nhật giá bán");
        System.out.println("    2. Nhập thêm hàng vào kho (addStock)");
        System.out.println("    3. Điều chỉnh lại số lượng tồn kho");
        System.out.println("    4. Cập nhật tên sản phẩm");
        System.out.println("    5. Cập nhật mô tả sản phẩm");
        System.out.println("    0. Hủy");
        int choice = readInt("  >> Lựa chọn (0-5): ", 0, 5);

        switch (choice) {
            case 1: {
                double newPrice = readDouble("  Nhập giá mới ($): ", 0.01, 99999.99);
                p.setPrice(newPrice);
                System.out.println("  [✓] Đã cập nhật giá bán thành công.");
                break;
            }
            case 2: {
                int addQty = readInt("  Nhập số lượng hàng cần nhập thêm vào kho: ", 1, 99999);
                p.addStock(addQty);
                System.out.printf("  [✓] Đã nhập thêm %d sản phẩm vào kho qua addStock(). Tồn kho hiện tại: %d%n",
                    addQty, p.getStockQuantity());
                break;
            }
            case 3: {
                int newStock = readInt("  Nhập số lượng tồn kho mới: ", 0, 99999);
                p.setStockQuantity(newStock);
                System.out.printf("  [✓] Đã điều chỉnh số lượng tồn kho thành: %d%n", newStock);
                break;
            }
            case 4: {
                System.out.print("  Nhập tên sản phẩm mới: ");
                String newName = scanner.nextLine().trim();
                if (!newName.isEmpty()) {
                    p.setName(newName);
                    System.out.println("  [✓] Đã cập nhật tên sản phẩm.");
                }
                break;
            }
            case 5: {
                System.out.print("  Nhập mô tả mới: ");
                String newDesc = scanner.nextLine().trim();
                p.setDescription(newDesc);
                System.out.println("  [✓] Đã cập nhật mô tả sản phẩm.");
                break;
            }
            case 0:
                return;
        }
        saveAllData();
    }

    private void adminRemoveProduct() {
        printSectionHeader("XÓA SẢN PHẨM");
        browseProducts(null);
        System.out.print("\n  >> Nhập Mã SP cần xóa: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        boolean removed = products.removeIf(p -> p.getProductId().equalsIgnoreCase(productId));
        if (removed) {
            saveAllData();
            System.out.println("  [✓] Đã xóa sản phẩm thành công.");
        } else {
            System.out.println("  [!] Không tìm thấy sản phẩm có mã: " + productId);
        }
    }

    private void adminViewOrders() {
        printSectionHeader("DANH SÁCH TẤT CẢ ĐƠN HÀNG");
        if (orders.isEmpty()) {
            System.out.println("  [!] Chưa có đơn hàng nào trong hệ thống.");
            return;
        }
        System.out.println("  -----------------------------------------------------------------------------------------");
        for (Order o : orders) System.out.println("  " + o);
        System.out.println("  -----------------------------------------------------------------------------------------");
        System.out.printf("  Tổng số đơn hàng: %d%n", orders.size());
    }

    private void adminViewUsers() {
        printSectionHeader("DANH SÁCH NGƯỜI DÙNG HỆ THỐNG");
        System.out.println("  -----------------------------------------------------------------------------------------");
        for (User u : users) System.out.println("  " + u);
        System.out.println("  -----------------------------------------------------------------------------------------");
        System.out.printf("  Tổng số người dùng: %d%n", users.size());
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
            System.out.println("  [!] Không tìm thấy đơn hàng.");
            return;
        }

        System.out.println("  Chọn trạng thái mới:");
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("    %d. %s%n", i + 1, statuses[i]);
        }
        int choice = readInt("  >> Lựa chọn: ", 1, statuses.length);
        target.setStatus(statuses[choice - 1]);
        System.out.printf("  [✓] Đã chuyển trạng thái đơn hàng %s thành: %s%n", orderId, statuses[choice - 1]);
        saveAllData();
    }

    /**
     * Báo cáo thống kê tổng doanh thu bán lẻ hệ thống (Yêu cầu phân hệ Quản trị viên Tuần 3).
     */
    private void adminViewRevenueStatistics() {
        printSectionHeader("THỐNG KÊ TỔNG DOANH THU BÁN LẺ");
        if (orders.isEmpty()) {
            System.out.println("  [!] Chưa có đơn hàng nào trong hệ thống để thống kê.");
            return;
        }

        int totalOrders = orders.size();
        int confirmedCount = 0;
        int deliveredCount = 0;
        int shippedCount = 0;
        int pendingCount = 0;
        int cancelledCount = 0;

        double totalGrossRevenue = 0.0;
        double totalDiscountGiven = 0.0;
        double totalNetRevenue = 0.0;

        Map<String, Double> revenueByPayment = new HashMap<>();

        for (Order o : orders) {
            totalGrossRevenue += o.getTotalAmount();
            totalDiscountGiven += o.getDiscountAmount();
            totalNetRevenue += o.getFinalAmount();

            OrderStatus s = o.getStatus();
            if (s == OrderStatus.DELIVERED) deliveredCount++;
            else if (s == OrderStatus.SHIPPED) shippedCount++;
            else if (s == OrderStatus.CONFIRMED) confirmedCount++;
            else if (s == OrderStatus.PENDING) pendingCount++;
            else if (s == OrderStatus.CANCELLED) cancelledCount++;

            String payMethod = o.getPaymentMethod();
            revenueByPayment.put(payMethod, revenueByPayment.getOrDefault(payMethod, 0.0) + o.getFinalAmount());
        }

        System.out.println("  ┌────────────────────────────────────────────────────────┐");
        System.out.println("  │               BÁO CÁO DOANH THU HỆ THỐNG               │");
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.printf("  │  Tổng số đơn hàng đã đặt     : %-23d │%n", totalOrders);
        System.out.printf("  │   • Đã giao thành công       : %-23d │%n", deliveredCount);
        System.out.printf("  │   • Đang giao hàng           : %-23d │%n", shippedCount);
        System.out.printf("  │   • Đã xác nhận thanh toán   : %-23d │%n", confirmedCount);
        System.out.printf("  │   • Đang chờ xử lý           : %-23d │%n", pendingCount);
        System.out.printf("  │   • Đã hủy                   : %-23d │%n", cancelledCount);
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.printf("  │  Tổng giá trị đơn hàng (Gross): $%-22.2f │%n", totalGrossRevenue);
        System.out.printf("  │  Tổng tiền chiết khấu ưu đãi : -$%-21.2f │%n", totalDiscountGiven);
        System.out.printf("  │  DOANH THU THỰC NHẬN (Net)   : $%-22.2f │%n", totalNetRevenue);
        System.out.println("  ├────────────────────────────────────────────────────────┤");
        System.out.println("  │  Doanh thu theo phương thức thanh toán:                │");
        for (Map.Entry<String, Double> entry : revenueByPayment.entrySet()) {
            String methodDesc = entry.getKey().equals("CASH") ? "Tiền mặt (CASH)" :
                                entry.getKey().equals("CARD") ? "Thẻ ngân hàng (CARD)" :
                                entry.getKey().equals("EWALLET") ? "Ví điện tử (E-WALLET)" : entry.getKey();
            System.out.printf("  │   • %-24s : $%-22.2f │%n", methodDesc, entry.getValue());
        }
        System.out.println("  └────────────────────────────────────────────────────────┘");
    }

    // ================================================================== DATA PERSISTENCE (Tuần 3)
    /**
     * Tự động nạp toàn bộ danh sách User, Product, Order từ các tệp JSON khi ứng dụng khởi chạy.
     * [YÊU CẦU TUẦN 3]: Tầng dữ liệu JSON bền vững, nạp và khôi phục liên kết đối tượng hoàn chỉnh.
     */
    public void initData() {
        users    = dataManager.loadUsers();
        products = dataManager.loadProducts();
        orders   = dataManager.loadOrders(users, products);

        if (users.isEmpty())    seedDefaultUsers();
        if (products.isEmpty()) seedDefaultProducts();
        System.out.printf("  [i] Khởi tạo dữ liệu JSON thành công (%d người dùng, %d sản phẩm, %d đơn hàng).%n",
            users.size(), products.size(), orders.size());
    }

    /**
     * Tự động lưu cập nhật xuống tệp JSON sau mỗi giao dịch mua hàng hoặc thay đổi kho.
     * [YÊU CẦU TUẦN 3]: Đồng bộ hóa dữ liệu bền vững.
     */
    public void saveAllData() {
        dataManager.saveAllData(users, products, orders);
    }

    /** Tương thích ngược nội bộ cho các lời gọi saveData() */
    private void saveData() {
        saveAllData();
    }

    /** Tạo tài khoản mặc định nếu chưa có dữ liệu */
    private void seedDefaultUsers() {
        users.add(new Admin("A001", "admin", "admin123", 1));
        users.add(new RegularCustomer("C001", "alice", "pass123",
            "123 Lê Lợi, Quận 1, TP.HCM", "0901234567"));
        users.add(new VIPCustomer("C002", "bob", "pass123",
            "456 Nguyễn Huệ, Quận 1, TP.HCM", "0912345678", 2));
        System.out.println("  [i] Đã khởi tạo tài khoản mẫu (admin/admin123, alice/pass123, bob/pass123)");
    }

    /** Tạo sản phẩm sinh thái mẫu nếu chưa có dữ liệu (Đa hình ReusableProduct & OrganicProduct) */
    private void seedDefaultProducts() {
        products.add(new ReusableProduct("P001", "Bình giữ nhiệt Inox 304 Eco",
            "Bình giữ nhiệt chân không 500ml giữ nóng 12h lạnh 24h nắp tre mộc", 18.50, 40, "Gia dụng xanh",
            "Inox 304 & Nắp tre", 2500, "BPA-Free & FDA"));
        products.add(new ReusableProduct("P002", "Túi vải Canvas hữu cơ EcoBag",
            "Túi tote vải bố 100% sợi bông organic thay thế túi nilon khi đi chợ", 6.50, 60, "Thời trang xanh",
            "Vải Canvas hữu cơ", 1000, "GOTS Certified"));
        products.add(new ReusableProduct("P003", "Hộp cơm thủy tinh chịu nhiệt",
            "Hộp bảo quản thực phẩm 1000ml thủy tinh cao cấp nắp khóa gioăng silicone", 12.00, 30, "Gia dụng xanh",
            "Thủy tinh Borosilicate", 1500, "LFGB Germany"));
        products.add(new ReusableProduct("P004", "Bộ ống hút tre tự nhiên (10 cái)",
            "Ống hút làm từ thân tre mộc tự nhiên có kèm cọ rửa sơ dừa", 4.50, 80, "Đồ dùng tái chế",
            "Tre tự nhiên 100%", 500, "FSC 100%"));
        products.add(new OrganicProduct("P005", "Trà hoa cúc sấy lạnh hữu cơ",
            "Trà hoa cúc hữu cơ thanh mát không hóa chất đóng hộp thiếc 100g", 9.90, 50, "Thực phẩm sinh thái",
            "2027-10-20", "USDA Organic", "Khô mát, tránh ánh nắng"));
        products.add(new OrganicProduct("P006", "Hạt Macca sấy nứt vỏ hữu cơ",
            "Hạt macca giàu dinh dưỡng từ nông trại sinh thái Tây Nguyên 500g", 14.50, 45, "Thực phẩm sinh thái",
            "2027-08-15", "VietGAP Organic", "Ngăn mát tủ lạnh"));
        products.add(new OrganicProduct("P007", "Xà bông sinh học tinh dầu tràm",
            "Bánh xà phòng tắm thảo mộc tự nhiên tự phân hủy sinh học 120g", 3.80, 25, "Chăm sóc cá nhân",
            "2028-01-01", "ECOCERT Organic", "Nơi khô ráo"));
        products.add(new OrganicProduct("P008", "Dầu gội bồ kết thảo dược mộc",
            "Dầu gội truyền thống nấu từ bồ kết cô đặc và vỏ bưởi mần trầu 300ml", 8.20, 45, "Chăm sóc cá nhân",
            "2027-05-30", "Cruelty-Free & Bio", "Nhiệt độ phòng"));
        System.out.println("  [i] Đã nạp danh sách sản phẩm sinh thái mẫu (Đa hình ReusableProduct & OrganicProduct).");
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

    // ================================================================== INPUT HELPERS (try-catch)
    /**
     * Đọc một số nguyên từ bàn phím trong khoảng [min, max].
     * Tự động hỏi lại nếu nhập sai — KHÔNG BAO GIỜ bị crash vì NumberFormatException.
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.printf("  [!] Vui lòng nhập một số từ %d đến %d.%n", min, max);
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Dữ liệu không hợp lệ. Vui lòng nhập một số nguyên.");
            }
        }
    }

    /**
     * Đọc một số thực từ bàn phím trong khoảng [min, max].
     * Tự động hỏi lại nếu nhập sai — KHÔNG BAO GIỜ bị crash.
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
                System.out.println("  [!] Số không hợp lệ. Vui lòng nhập lại.");
            }
        }
    }

    // ================================================================== UI HELPERS
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
