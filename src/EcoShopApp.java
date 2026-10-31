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
        loadData();
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
        saveData();
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
        System.out.println("  │  2. Thêm sản phẩm mới                                  │");
        System.out.println("  │  3. Chỉnh sửa thông tin sản phẩm                       │");
        System.out.println("  │  4. Xóa sản phẩm                                       │");
        System.out.println("  │  5. Xem danh sách đơn hàng                             │");
        System.out.println("  │  6. Xem danh sách người dùng                           │");
        System.out.println("  │  7. Cập nhật trạng thái đơn hàng                       │");
        System.out.println("  │  0. Đăng xuất                                          │");
        System.out.println("  └────────────────────────────────────────────────────────┘");

        int choice = readInt("  >> Chọn chức năng (0-7): ", 0, 7);
        switch (choice) {
            case 1: browseProducts(null);     break;
            case 2: adminAddProduct();        break;
            case 3: adminEditProduct();       break;
            case 4: adminRemoveProduct();     break;
            case 5: adminViewOrders();        break;
            case 6: adminViewUsers();         break;
            case 7: adminChangeOrderStatus(); break;
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

        System.out.println("\n  ┌────────┬────────────────────────────────┬────────────┬──────────┬──────────────────┐");
        System.out.printf("  │ %-6s │ %-30s │ %-10s │ %-8s │ %-16s │%n",
            "Mã SP", "Tên sản phẩm", "Giá bán", "Tồn kho", "Danh mục");
        System.out.println("  ├────────┼────────────────────────────────┼────────────┼──────────┼──────────────────┤");
        int count = 0;
        for (Product p : products) {
            if (filterCat != null && !p.getCategory().equals(filterCat)) continue;
            System.out.println(p);
            count++;
        }
        System.out.println("  └────────┴────────────────────────────────┴────────────┴──────────┴──────────────────┘");
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
        printSectionHeader("THÊM SẢN PHẨM MỚI");
        try {
            String productId = "P" + String.format("%03d", products.size() + 1);
            System.out.print("  Tên sản phẩm: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) throw new InvalidInputException("Tên sản phẩm không được để trống.");

            System.out.print("  Mô tả sản phẩm: ");
            String desc = scanner.nextLine().trim();

            double price = readDouble("  Giá bán ($): ", 0.01, 99999.99);
            int    stock = readInt("  Số lượng tồn kho: ", 0, 99999);

            System.out.print("  Danh mục sản phẩm: ");
            String category = scanner.nextLine().trim();
            if (category.isEmpty()) category = "Chung";

            Product p = new Product(productId, name, desc, price, stock, category);
            products.add(p);
            saveData();
            System.out.printf("  [✓] Đã thêm sản phẩm '%s' thành công với mã: %s%n", name, productId);

        } catch (InvalidInputException e) {
            System.out.println("  [!] LỖI NHẬP LIỆU: " + e.getMessage());
        }
    }

    private void adminEditProduct() {
        printSectionHeader("CHỈNH SỬA SẢN PHẨM");
        browseProducts(null);
        System.out.print("\n  >> Nhập Mã SP cần sửa: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        Product p = findProductById(productId);
        if (p == null) {
            System.out.println("  [!] Không tìm thấy sản phẩm có mã: " + productId);
            return;
        }

        System.out.println("  Đang chọn sửa: " + p.getName() + " (" + p.getProductId() + ")");
        System.out.println("  Bạn muốn thay đổi thông tin nào?");
        System.out.println("    1. Giá bán   2. Tồn kho   3. Tên sản phẩm   4. Mô tả   0. Hủy");
        int choice = readInt("  >> Lựa chọn (0-4): ", 0, 4);

        switch (choice) {
            case 1: {
                double newPrice = readDouble("  Nhập giá mới ($): ", 0.01, 99999.99);
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
                System.out.println("  [✓] Đã cập nhật mô tả sản phẩm.");
                break;
            }
            case 0:
                return;
        }
        saveData();
    }

    private void adminRemoveProduct() {
        printSectionHeader("XÓA SẢN PHẨM");
        browseProducts(null);
        System.out.print("\n  >> Nhập Mã SP cần xóa: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        boolean removed = products.removeIf(p -> p.getProductId().equalsIgnoreCase(productId));
        if (removed) {
            saveData();
            System.out.println("  [✓] Đã xóa sản phẩm thành công.");
        } else {
            System.out.println("  [!] Không tìm thấy sản phẩm có mã: " + productId);
        }
    }

    private void adminViewOrders() {
        printSectionHeader("DANH SÁCH TẤT CẢ ĐƠN HÀNG");
        if (orders.isEmpty()) {
            System.out.println("  [!] Chưa có đơn hàng nào trong phiên làm việc này.");
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
        saveData();
    }

    // ================================================================== DATA HELPERS
    private void loadData() {
        users    = dataManager.loadUsers();
        products = dataManager.loadProducts();

        if (users.isEmpty())    seedDefaultUsers();
        if (products.isEmpty()) seedDefaultProducts();
    }

    private void saveData() {
        dataManager.saveUsers(users);
        dataManager.saveProducts(products);
        dataManager.saveOrders(orders);
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

    /** Tạo sản phẩm mẫu nếu chưa có dữ liệu */
    private void seedDefaultProducts() {
        products.add(new Product("P001", "Laptop Dell XPS 15",
            "Laptop hiệu năng cao màn hình 15.6 inch", 1299.99, 10, "Điện tử"));
        products.add(new Product("P002", "Điện thoại iPhone 16 Pro",
            "Smartphone Apple cao cấp chip A18", 999.00, 15, "Điện tử"));
        products.add(new Product("P003", "Tai nghe Sony WH-1000XM5",
            "Tai nghe chống ồn không dây cao cấp", 349.99, 20, "Điện tử"));
        products.add(new Product("P004", "Giày Nike Air Max 270",
            "Giày chạy bộ thể thao êm ái", 129.99, 30, "Giày dép"));
        products.add(new Product("P005", "Quần Jeans Levi's 501",
            "Quần jeans dáng đứng cổ điển", 79.99, 25, "Thời trang"));
        products.add(new Product("P006", "Sách Nhà Giả Kim",
            "Tiểu thuyết bán chạy của Paulo Coelho", 14.99, 50, "Sách"));
        products.add(new Product("P007", "Nồi áp suất Instant Pot",
            "Nồi áp suất điện đa năng 7 trong 1", 89.99, 12, "Gia dụng"));
        products.add(new Product("P008", "Thảm tập Yoga Cao Cấp",
            "Thảm yoga chống trượt dày 6mm", 39.99, 35, "Thể thao"));
        System.out.println("  [i] Đã nạp danh sách sản phẩm mẫu.");
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
