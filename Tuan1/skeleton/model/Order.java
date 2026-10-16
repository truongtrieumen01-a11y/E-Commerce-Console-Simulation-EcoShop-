package skeleton.model;
 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp Order — Đại diện cho một giao dịch đơn hàng hoàn chỉnh.
 *
 * Điểm nối cho tính Đa hình (Polymorphism):
 * Phương thức calculateFinal() gọi customer.calculateDiscount() để tự động
 * áp dụng chiết khấu đúng theo lớp con của Customer tại thời điểm chạy.
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class Order implements JsonSerializable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String orderId;
    private Customer customer;
    private List<CartItem> items;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private String paymentMethod;

    public Order(String orderId, Customer customer, List<CartItem> items) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.paymentMethod = "CASH";
    }

    /**
     * Tính toán tổng tiền, chiết khấu đa hình và số tiền thanh toán cuối cùng.
     */
    public void calculateFinal() {
        // TODO: Tính tổng tiền các món, gọi customer.calculateDiscount() đa hình trong Tuần 1
    }

    public void printReceipt() {
        // TODO: In hóa đơn thanh toán dạng bảng trực quan
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
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
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toJson() {
        // TODO: Chuyển đổi đối tượng Order thành chuỗi JSON
        return "";
    }

    @Override
    public String toString() {
        return String.format("[%-18s] %-22s | Khách: %-12s | $%8.2f -> $%8.2f | %s",
            status, orderId, customer != null ? customer.getUsername() : "",
            totalAmount, finalAmount, orderDate != null ? orderDate.format(FMT) : "");
    }
}
