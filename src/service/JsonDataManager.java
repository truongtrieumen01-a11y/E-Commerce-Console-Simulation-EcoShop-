package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  LỚP DỊCH VỤ: JsonDataManager (Bộ Lưu Trữ Dữ Liệu Bền Vững Bằng Tệp JSON)
 * ============================================================================
 *
 * 🎯 VAI TRÒ TRONG ĐỀ TÀI ECOSHOP:
 *  - Phụ trách tầng Truy Xuất Dữ Liệu (Data Access Layer - Persistence Manager).
 *  - Đọc và ghi các tệp dữ liệu vào thư mục `data/`:
 *      + `data/users.json`    : Người dùng hệ thống (Admin, Khách thường, VIP)
 *      + `data/products.json` : Danh mục kho hàng đa hình (Tái sử dụng, Hữu cơ)
 *      + `data/orders.json`   : Lịch sử đơn đặt hàng lồng ghép (Nested Orders)
 *  - TUÂN THỦ YÊU CẦU ĐỀ BÀI: Sử dụng hoàn toàn thư viện chuẩn Java SE (java.io, java.nio),
 *    tuyệt đối KHÔNG phụ thuộc vào bất kỳ thư viện ngoài nào (Gson, Jackson...).
 *
 * 🛡️ 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 *  │ Đóng gói           │ Đường dẫn tệp và các hàm phân tích cú pháp (Parser) đều để   │
 *  │ (Encapsulation)    │ phạm vi private. Lớp ngoài chỉ tương tác qua các hàm nghiệp  │
 *  │                    │ vụ công khai: saveUsers, loadUsers, saveProducts, v.v.       │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Trừu tượng         │ Ẩn toàn bộ độ phức tạp của File I/O, UTF-8 charset, parse    │
 *  │ (Abstraction)      │ chuỗi JSON đằng sau các phương thức gọn gàng.                │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Đa hình            │ 1. Khi lưu: Gọi obj.toJson() qua giao diện JsonSerializable. │
 *  │ (Polymorphism)     │ 2. Khi đọc: Sử dụng kỹ thuật Type Discriminator (trường      │
 *  │                    │    "type") để tái tạo đúng lớp con thực tế (Admin,          │
 *  │                    │    RegularCustomer, VIPCustomer, ReusableProduct,           │
 *  │                    │    OrganicProduct) mà không bị phụ thuộc cứng.               │
 *  └────────────────────┴──────────────────────────────────────────────────────────────┘
 */
public class JsonDataManager {

    private static final DateTimeFormatter ORDER_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH ĐÓNG GÓI (Encapsulated File Paths)
    // ========================================================================

    private final String usersFile;
    private final String productsFile;
    private final String ordersFile;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Khởi tạo thư mục và đường dẫn tệp)
    // ========================================================================

    /**
     * Khởi tạo bộ quản lý dữ liệu JSON.
     * Tự động kiểm tra và tạo thư mục chứa dữ liệu nếu chưa tồn tại.
     *
     * @param dataDirectory Tên thư mục lưu trữ (ví dụ: "data")
     */
    public JsonDataManager(String dataDirectory) {
        try {
            // Tự động tạo thư mục data/ nếu chưa có
            Files.createDirectories(Paths.get(dataDirectory));
        } catch (IOException e) {
            System.err.println("[CẢNH BÁO] Không thể tạo thư mục dữ liệu: " + e.getMessage());
        }

        // Tự động khớp dấu phân cách thư mục ("/" trên Linux/Mac, "\" trên Windows)
        this.usersFile    = dataDirectory + File.separator + "users.json";
        this.productsFile = dataDirectory + File.separator + "products.json";
        this.ordersFile   = dataDirectory + File.separator + "orders.json";
    }

    // ========================================================================
    // PHẦN 3: ĐỌC VÀ GHI NGƯỜI DÙNG (User Persistence - Đa hình User)
    // ========================================================================

    /**
     * Ghi danh sách người dùng vào file users.json.
     * ĐA HÌNH: Gọi `user.toJson()` của giao diện `JsonSerializable`.
     *
     * @param users Danh sách người dùng cần lưu
     */
    public void saveUsers(List<User> users) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < users.size(); i++) {
            sb.append("  ").append(users.get(i).toJson());
            if (i < users.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(usersFile, sb.toString());
    }

    /**
     * Tải danh sách người dùng từ file users.json.
     * Áp dụng Type Discriminator: dựa vào trường "type" để tạo đúng thực thể
     * (Admin, RegularCustomer, VIPCustomer).
     *
     * @return Danh sách người dùng đã được khôi phục; danh sách rỗng nếu tệp trống
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        String content = readFile(usersFile);
        if (content == null || content.isBlank()) return users;

        List<String> objects = splitJsonArray(content);
        for (String obj : objects) {
            try {
                String type     = extractString(obj, "type");
                String userId   = extractString(obj, "userId");
                String username = extractString(obj, "username");
                String password = extractString(obj, "password");

                if (type.equalsIgnoreCase("Admin")) {
                    int adminLevel = extractInt(obj, "adminLevel", 1);
                    users.add(new Admin(userId, username, password, adminLevel));
                } else if (type.equalsIgnoreCase("RegularCustomer")) {
                    String address = extractString(obj, "address");
                    String phone   = extractString(obj, "phoneNumber");
                    int    points  = extractInt(obj, "loyaltyPoints", 0);
                    RegularCustomer rc = new RegularCustomer(userId, username, password, address, phone);
                    rc.addLoyaltyPoints(points);
                    users.add(rc);
                } else if (type.equalsIgnoreCase("VIPCustomer")) {
                    String address  = extractString(obj, "address");
                    String phone    = extractString(obj, "phoneNumber");
                    int    vipLevel = extractInt(obj, "vipLevel", 1);
                    users.add(new VIPCustomer(userId, username, password, address, phone, vipLevel));
                } else {
                    // Dự phòng nếu không có type, thử đoán theo thuộc tính
                    if (obj.contains("\"adminLevel\"")) {
                        users.add(new Admin(userId, username, password, extractInt(obj, "adminLevel", 1)));
                    } else if (obj.contains("\"vipLevel\"")) {
                        users.add(new VIPCustomer(userId, username, password,
                            extractString(obj, "address"), extractString(obj, "phoneNumber"), extractInt(obj, "vipLevel", 1)));
                    } else {
                        RegularCustomer rc = new RegularCustomer(userId, username, password,
                            extractString(obj, "address"), extractString(obj, "phoneNumber"));
                        rc.addLoyaltyPoints(extractInt(obj, "loyaltyPoints", 0));
                        users.add(rc);
                    }
                }
            } catch (Exception e) {
                System.err.println("[CẢNH BÁO] Lỗi khi phân tích dữ liệu người dùng: " + e.getMessage());
            }
        }
        return users;
    }

    // ========================================================================
    // PHẦN 4: ĐỌC VÀ GHI SẢN PHẨM (Product Persistence - Đa hình Product)
    // ========================================================================

    /**
     * Ghi danh sách sản phẩm sinh thái vào file products.json.
     * ĐA HÌNH: Mỗi đối tượng ReusableProduct hoặc OrganicProduct tự sinh chuỗi JSON
     * chứa trường "type" định danh riêng biệt thông qua phương thức `toJson()`.
     *
     * @param products Danh sách sản phẩm cần lưu
     */
    public void saveProducts(List<Product> products) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < products.size(); i++) {
            sb.append("  ").append(products.get(i).toJson());
            if (i < products.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(productsFile, sb.toString());
    }

    /**
     * Tải danh sách sản phẩm từ file products.json.
     * ĐA HÌNH: Đọc trường "type" để tạo đúng lớp con:
     *   - "ReusableProduct" $\rightarrow$ Khởi tạo ReusableProduct (có material, reuseCount, ecoCertificate)
     *   - "OrganicProduct"  $\rightarrow$ Khởi tạo OrganicProduct (có expiryDate, organicCert, storage)
     *   - Tương thích ngược: Nếu tệp cũ không có "type", tự động nhận diện hoặc mặc định ReusableProduct.
     *
     * @return Danh sách các sản phẩm sinh thái
     */
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        String content = readFile(productsFile);
        if (content == null || content.isBlank()) return products;

        List<String> objects = splitJsonArray(content);
        for (String obj : objects) {
            try {
                String type      = extractString(obj, "type");
                String productId = extractString(obj, "productId");
                String name      = extractString(obj, "name");
                String desc      = extractString(obj, "description");
                double price     = extractDouble(obj, "price", 0.0);
                int    stock     = extractInt(obj, "stockQuantity", 0);
                String category  = extractString(obj, "category");

                if (type.equalsIgnoreCase("OrganicProduct") || obj.contains("\"expiryDate\"")) {
                    // Nhánh sản phẩm Hữu cơ
                    String expiryDate    = extractString(obj, "expiryDate");
                    String organicCert   = extractString(obj, "organicCertification");
                    String storage       = extractString(obj, "storageInstruction");
                    products.add(new OrganicProduct(productId, name, desc, price, stock, category,
                        expiryDate, organicCert, storage));
                } else {
                    // Nhánh sản phẩm Tái sử dụng (mặc định cho sản phẩm EcoShop)
                    String material      = extractString(obj, "material");
                    if (material.isEmpty()) material = "Vật liệu tái chế / Sinh thái";
                    int    reuseCount    = extractInt(obj, "estimatedReuseCount", 500);
                    String certificate   = extractString(obj, "ecoCertificate");
                    if (certificate.isEmpty()) certificate = "Tiêu chuẩn Xanh EcoShop";

                    products.add(new ReusableProduct(productId, name, desc, price, stock, category,
                        material, reuseCount, certificate));
                }
            } catch (Exception e) {
                System.err.println("[CẢNH BÁO] Lỗi khi phân tích dữ liệu sản phẩm: " + e.getMessage());
            }
        }
        return products;
    }

    // ========================================================================
    // PHẦN 5: ĐỌC VÀ GHI ĐƠN HÀNG (Order Persistence - Nested JSON Object)
    // ========================================================================

    /**
     * Ghi danh sách đơn đặt hàng vào file orders.json.
     *
     * @param orders Danh sách đơn hàng cần lưu
     */
    public void saveOrders(List<Order> orders) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < orders.size(); i++) {
            sb.append("  ").append(orders.get(i).toJson());
            if (i < orders.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        writeFile(ordersFile, sb.toString());
    }

    /**
     * Tải toàn bộ danh sách đơn đặt hàng từ file orders.json và liên kết với đối tượng
     * Customer và Product tương ứng trong bộ nhớ.
     *
     * @param users    Danh sách người dùng hiện có (dùng để tra cứu Customer theo customerId)
     * @param products Danh sách sản phẩm hiện có (dùng để tra cứu Product theo productId trong CartItem)
     * @return Danh sách đối tượng Order hoàn chỉnh
     */
    public List<Order> loadOrders(List<User> users, List<Product> products) {
        List<Order> orders = new ArrayList<>();
        String content = readFile(ordersFile);
        if (content == null || content.isBlank()) return orders;

        List<String> orderObjects = splitJsonArray(content);
        for (String ordJson : orderObjects) {
            try {
                String orderId       = extractString(ordJson, "orderId");
                String customerId   = extractString(ordJson, "customerId");
                double totalAmount   = extractDouble(ordJson, "totalAmount", 0.0);
                double discountAmount= extractDouble(ordJson, "discountAmount", 0.0);
                double finalAmount   = extractDouble(ordJson, "finalAmount", 0.0);
                String orderDateStr  = extractString(ordJson, "orderDate");
                String statusStr     = extractString(ordJson, "status");
                String paymentMethod = extractString(ordJson, "paymentMethod");
                if (paymentMethod.isEmpty()) paymentMethod = "CASH";

                // Phân tích trạng thái đơn hàng an toàn
                OrderStatus status = OrderStatus.CONFIRMED;
                if (!statusStr.isEmpty()) {
                    for (OrderStatus os : OrderStatus.values()) {
                        if (os.name().equalsIgnoreCase(statusStr)) {
                            status = os;
                            break;
                        }
                    }
                }

                // Phân tích ngày giờ đặt hàng
                LocalDateTime orderDate = LocalDateTime.now();
                if (!orderDateStr.isEmpty()) {
                    try {
                        orderDate = LocalDateTime.parse(orderDateStr, ORDER_DATE_FMT);
                    } catch (Exception ignored) {}
                }

                // Tìm khách hàng tương ứng trong danh sách users
                Customer customer = null;
                for (User u : users) {
                    if (u.getUserId().equalsIgnoreCase(customerId) && u instanceof Customer) {
                        customer = (Customer) u;
                        break;
                    }
                }

                // Phân tích danh sách các mặt hàng trong đơn (Nested CartItems)
                List<CartItem> items = new ArrayList<>();
                int itemsStart = ordJson.indexOf("\"items\":[");
                if (itemsStart >= 0) {
                    int bracketStart = itemsStart + 8; // Vị trí dấu [
                    int bracketEnd   = findMatchingBracket(ordJson, bracketStart);
                    if (bracketEnd > bracketStart) {
                        String itemsSubJson = ordJson.substring(bracketStart, bracketEnd + 1);
                        List<String> itemObjects = splitJsonArray(itemsSubJson);
                        for (String itJson : itemObjects) {
                            String pId  = extractString(itJson, "productId");
                            int    qty  = extractInt(itJson, "quantity", 1);
                            double prc  = extractDouble(itJson, "price", 0.0);
                            String pName= extractString(itJson, "productName");

                            // Tìm sản phẩm trong kho
                            Product matchedProd = null;
                            for (Product p : products) {
                                if (p.getProductId().equalsIgnoreCase(pId)) {
                                    matchedProd = p;
                                    break;
                                }
                            }

                            // Nếu sản phẩm đã bị xóa khỏi kho, tạo sản phẩm giữ chỗ để không lỗi hiển thị hóa đơn
                            if (matchedProd == null) {
                                matchedProd = new ReusableProduct(pId, pName.isEmpty() ? "Sản phẩm cũ" : pName,
                                    "Đã lưu trong hóa đơn", prc, 0, "Lịch sử", "Không xác định", 1, "N/A");
                            }

                            items.add(new CartItem(matchedProd, qty));
                        }
                    }
                }

                // Tạo đối tượng Order hoàn chỉnh
                Order order = new Order(orderId, customer, items, totalAmount, discountAmount,
                                        finalAmount, orderDate, status, paymentMethod);
                orders.add(order);

                // Gắn đơn hàng vào lịch sử cá nhân của khách hàng
                if (customer != null) {
                    customer.addOrderToHistory(order);
                }

            } catch (Exception e) {
                System.err.println("[CẢNH BÁO] Lỗi khi khôi phục đơn hàng từ JSON: " + e.getMessage());
            }
        }
        return orders;
    }

    // ========================================================================
    // PHẦN 6: TRỢ THỦ ĐỌC VÀ GHI TỆP (File I/O Helpers)
    // ========================================================================

    private void writeFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("[LỖI I/O] Không thể ghi tệp " + filePath + ": " + e.getMessage());
        }
    }

    private String readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) return null;
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("[LỖI I/O] Không thể đọc tệp " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    // ========================================================================
    // PHẦN 7: BỘ PHÂN TÍCH CÚ PHÁP JSON THUẦN JAVA (State-Machine JSON Parser)
    // ========================================================================

    /**
     * Tách mảng JSON "[{...}, {...}]" thành danh sách các chuỗi JSON Object riêng biệt.
     * Sử dụng máy trạng thái đếm độ sâu ngoặc nhọn `{` và `}` để xử lý chính xác
     * các đối tượng lồng nhau (Nested JSON).
     */
    private List<String> splitJsonArray(String json) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        int start = -1;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    result.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    /**
     * Tìm vị trí dấu ngoặc vuông đóng `]` tương ứng với dấu mở `[`.
     */
    private int findMatchingBracket(String text, int openPos) {
        int depth = 0;
        for (int i = openPos; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Trích xuất giá trị String của khóa trong chuỗi JSON.
     */
    private String extractString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";

        int start = idx + search.length();
        int end   = start;

        while (end < json.length()) {
            if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
            end++;
        }

        return json.substring(start, end).replace("\\\"", "\"");
    }

    /**
     * Trích xuất giá trị int của khóa trong chuỗi JSON.
     */
    private int extractInt(String json, String key, int defaultVal) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx < 0) return defaultVal;

        int start = idx + search.length();
        int end   = start;

        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }

        try {
            return Integer.parseInt(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * Trích xuất giá trị double của khóa trong chuỗi JSON.
     */
    private double extractDouble(String json, String key, double defaultVal) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx < 0) return defaultVal;

        int start = idx + search.length();
        int end   = start;

        while (end < json.length() &&
               (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) {
            end++;
        }

        try {
            return Double.parseDouble(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    // ========================================================================
    // PHẦN 7: ĐỒNG BỘ TOÀN BỘ TẦNG LƯU TRỮ (Full Persistence Sync)
    // ========================================================================

    /**
     * Đồng bộ hóa và ghi toàn bộ dữ liệu 3 phân hệ (User, Product, Order) xuống các tệp JSON tương ứng.
     * [YÊU CẦU TUẦN 3]: Đảm bảo việc nạp và lưu dữ liệu được thực hiện nhất quán, giữ nguyên tầng lưu trữ JSON
     * như GVHD đã lưu ý: "Khi merge 2 nhánh em nhớ giữ nguyên tầng lưu trữ JSON, đó là chỗ dễ vỡ nhất lúc hợp nhất."
     *
     * @param users    Danh sách người dùng hiện tại
     * @param products Danh mục sản phẩm kho
     * @param orders   Danh sách lịch sử đơn đặt hàng
     */
    public void saveAllData(List<User> users, List<Product> products, List<Order> orders) {
        saveUsers(users);
        saveProducts(products);
        saveOrders(orders);
    }
}
