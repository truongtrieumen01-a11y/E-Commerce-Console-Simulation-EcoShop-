package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  LỚP: Order (Đơn đặt hàng — Đại diện cho một giao dịch mua bán thành công)
 * ============================================================================
 *
 * 🎯 VAI TRÒ TRONG ĐỀ TÀI ECOSHOP:
 *  - Chứa thông tin đơn hàng đã chốt từ giỏ hàng (ShoppingCart).
 *  - Lưu trữ ảnh chụp danh sách các mục hàng (snapshot của CartItem).
 *  - Là nơi ĐA HÌNH ĐƯỢC KÍCH HOẠT MẠNH MẼ NHẤT thông qua phương thức:
 *      customer.calculateDiscount(totalAmount)
 *
 * 🛡️ 4 TRỤ CỘT OOP ĐƯỢC THỂ HIỆN:
 *  ┌────────────────────┬──────────────────────────────────────────────────────────────┐
 *  │ Đóng gói           │ Mọi thuộc tính (orderId, customer, items, totalAmount, v.v.)  │
 *  │ (Encapsulation)    │ đều khai báo private, truy xuất thông qua hệ thống Getter    │
 *  │                    │ và Setter an toàn.                                           │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Trừu tượng         │ implements JsonSerializable: Đóng gói toàn bộ logic định     │
 *  │ (Abstraction)      │ dạng đơn hàng thành chuỗi JSON lồng nhau (Nested JSON).      │
 *  ├────────────────────┼──────────────────────────────────────────────────────────────┤
 *  │ Kế thừa &          │ Order giữ tham chiếu tới Customer (lớp cha). Khi chạy,       │
 *  │ Đa hình            │ dù Customer thực tế là RegularCustomer hay VIPCustomer,      │
 *  │ (Polymorphism)     │ Java runtime đều tự động gọi đúng công thức chiết khấu       │
 *  │                    │ của từng hạng thẻ mà không cần dùng câu lệnh if-else!         │
 *  └────────────────────┴──────────────────────────────────────────────────────────────┘
 */
public class Order implements JsonSerializable {

    // Bộ định dạng ngày giờ chuẩn ISO / Hiển thị
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========================================================================
    // PHẦN 1: THUỘC TÍNH (Private — Đóng gói Encapsulation)
    // ========================================================================

    private String          orderId;
    private Customer        customer;
    private List<CartItem>  items;
    private double          totalAmount;
    private double          discountAmount;
    private double          finalAmount;
    private LocalDateTime   orderDate;
    private OrderStatus     status;
    private String          paymentMethod;

    // ========================================================================
    // PHẦN 2: CONSTRUCTOR (Hàm khởi tạo)
    // ========================================================================

    /**
     * Constructor 1: Dùng khi tạo đơn hàng mới từ giỏ hàng lúc khách mua hàng.
     *
     * @param orderId  Mã đơn hàng mới (duy nhất)
     * @param customer Đối tượng khách hàng đặt mua
     * @param items    Danh sách mục hàng đã snapshot từ giỏ
     */
    public Order(String orderId, Customer customer, List<CartItem> items) {
        this.orderId        = orderId;
        this.customer       = customer;
        this.items          = new ArrayList<>(items);
        this.orderDate      = LocalDateTime.now();
        this.status         = OrderStatus.PENDING;
        this.paymentMethod  = "CASH";
    }

    /**
     * Constructor 2: Dùng khi JsonDataManager nạp đơn hàng đã lưu từ file orders.json.
     * Cho phép khôi phục nguyên vẹn ngày đặt hàng, trạng thái và số tiền đã tính.
     */
    public Order(String orderId, Customer customer, List<CartItem> items,
                 double totalAmount, double discountAmount, double finalAmount,
                 LocalDateTime orderDate, OrderStatus status, String paymentMethod) {
        this.orderId        = orderId;
        this.customer       = customer;
        this.items          = new ArrayList<>(items);
        this.totalAmount    = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount    = finalAmount;
        this.orderDate      = (orderDate != null) ? orderDate : LocalDateTime.now();
        this.status         = (status != null) ? status : OrderStatus.CONFIRMED;
        this.paymentMethod  = (paymentMethod != null) ? paymentMethod : "CASH";
    }

    // ========================================================================
    // PHẦN 3: NGHIỆP VỤ TÍNH TIỀN ĐA HÌNH (Polymorphic Calculation)
    // ========================================================================

    /**
     * TÍNH TOÁN TỔNG TIỀN ĐƠN HÀNG VỚI CHIẾT KHẤU ĐA HÌNH:
     *
     * Bước 1: Tính tổng giá trị các mặt hàng:
     *         totalAmount = Tổng (đơn giá * số lượng từng món)
     *
     * Bước 2: Gọi ĐA HÌNH (Polymorphism):
     *         discountAmount = customer.calculateDiscount(totalAmount);
     *         - Nếu customer là RegularCustomer: tính chiết khấu theo loyaltyPoints (0% - 5%)
     *         - Nếu customer là VIPCustomer: tính chiết khấu theo vipLevel (10% - 20%)
     *
     * Bước 3: Tính số tiền thực thanh toán:
     *         finalAmount = totalAmount - discountAmount;
     */
    public void calculateFinal() {
        totalAmount = 0.0;
        for (CartItem item : items) {
            totalAmount += item.getSubtotal();
        }

        // TÂM ĐIỂM ĐA HÌNH (Polymorphic Call)
        if (customer != null) {
            discountAmount = customer.calculateDiscount(totalAmount);
        } else {
            discountAmount = 0.0;
        }

        finalAmount = Math.max(0.0, totalAmount - discountAmount);
    }

    // ========================================================================
    // PHẦN 4: GETTER & SETTER (Đóng gói — Encapsulation)
    // ========================================================================

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        // Trả về bản sao để bảo vệ danh sách nội bộ của Order
        return new ArrayList<>(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod != null) {
            this.paymentMethod = paymentMethod.toUpperCase();
        }
    }

    // ========================================================================
    // PHẦN 5: HIỂN THỊ HÓA ĐƠN CHI TIẾT
    // ========================================================================

    /**
     * In phiếu thu / hóa đơn mua sắm định dạng bảng Unicode bắt mắt trên Console.
     */
    public void printReceipt() {
        String payName = paymentMethod.equals("CASH") ? "Tiền mặt (CASH)" :
                         paymentMethod.equals("CARD") ? "Thẻ ngân hàng (CARD)" :
                         paymentMethod.equals("EWALLET") ? "Ví điện tử (E-WALLET)" : paymentMethod;

        System.out.println("\n  ╔═══════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                              ECOSHOP — HÓA ĐƠN MUA HÀNG                               ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════════════════════════════════╝");
        System.out.printf("    Mã đơn hàng : %s%n", orderId);
        System.out.printf("    Khách hàng  : %s (%s)%n",
            (customer != null) ? customer.getUsername() : "Khách",
            (customer != null) ? customer.getCustomerType() : "Khách Vãng Lai");
        System.out.printf("    Ngày đặt    : %s%n", orderDate.format(FMT));
        System.out.printf("    Thanh toán  : %s%n", payName);
        System.out.printf("    Trạng thái  : %s%n", status.getDisplayName());
        System.out.println("  ┌────────────────────────────────┬────────┬────────────────────┬────────────────────────┐");
        for (CartItem item : items) {
            System.out.println(item);
        }
        System.out.println("  └────────────────────────────────┴────────┴────────────────────┴────────────────────────┘");
        System.out.printf("    Tạm tính          : $%.2f%n", totalAmount);
        System.out.printf("    Giảm giá ưu đãi   : -$%.2f (%.0f%%)%n",
            discountAmount, (totalAmount > 0) ? (discountAmount / totalAmount * 100) : 0);
        System.out.println("  -----------------------------------------------------------------------------------------");
        System.out.printf("    TỔNG THANH TOÁN   : $%.2f%n", finalAmount);
        System.out.println("  =========================================================================================");
    }

    // ========================================================================
    // PHẦN 6: TUẦN TỰ HÓA DỮ LIỆU JSON (Nested JsonSerializable)
    // ========================================================================

    /**
     * Chuyển đơn hàng thành chuỗi JSON lồng nhau (Nested JSON) để lưu ra orders.json.
     */
    @Override
    public String toJson() {
        StringBuilder itemsJson = new StringBuilder("[");
        List<CartItem> list = items;
        for (int i = 0; i < list.size(); i++) {
            CartItem ci = list.get(i);
            itemsJson.append(String.format(
                "{\"productId\":\"%s\",\"productName\":\"%s\",\"quantity\":%d,\"price\":%.2f,\"subtotal\":%.2f}",
                ci.getProduct().getProductId(),
                ci.getProduct().getName().replace("\"", "\\\""),
                ci.getQuantity(),
                ci.getProduct().getPrice(),
                ci.getSubtotal()
            ));
            if (i < list.size() - 1) itemsJson.append(",");
        }
        itemsJson.append("]");

        String customerId   = (customer != null) ? customer.getUserId() : "UNKNOWN";
        String customerName = (customer != null) ? customer.getUsername() : "UNKNOWN";

        return String.format(
            "{\"orderId\":\"%s\",\"customerId\":\"%s\",\"customerName\":\"%s\"," +
            "\"items\":%s,\"totalAmount\":%.2f,\"discountAmount\":%.2f," +
            "\"finalAmount\":%.2f,\"orderDate\":\"%s\",\"status\":\"%s\",\"paymentMethod\":\"%s\"}",
            orderId, customerId, customerName,
            itemsJson, totalAmount, discountAmount, finalAmount,
            orderDate.format(FMT), status.name(), paymentMethod
        );
    }

    @Override
    public String toString() {
        String cName = (customer != null) ? customer.getUsername() : "N/A";
        return String.format("[%-16s] %-24s | Khách: %-10s | $%8.2f -> $%8.2f | %s",
            status.getDisplayName(), orderId, cName, totalAmount, finalAmount, orderDate.format(FMT));
    }
}
