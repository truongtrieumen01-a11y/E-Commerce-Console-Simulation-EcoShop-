package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Order — represents a completed purchase transaction.
 *
 * OOP Pillars:
 *  - Encapsulation : all fields private, accessors provided
 *  - Abstraction   : implements JsonSerializable
 *
 * Key relationship: Order uses Customer.calculateDiscount() (polymorphism in action).
 */
public class Order implements JsonSerializable {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String          orderId;
    private Customer        customer;
    private List<CartItem>  items;
    private double          totalAmount;
    private double          discountAmount;
    private double          finalAmount;
    private LocalDateTime   orderDate;
    private OrderStatus     status;
    private String          paymentMethod;

    // ------------------------------------------------------------------ constructors
    public Order(String orderId, Customer customer, List<CartItem> items) {
        this.orderId       = orderId;
        this.customer      = customer;
        this.items         = new ArrayList<>(items);
        this.orderDate     = LocalDateTime.now();
        this.status        = OrderStatus.PENDING;
        this.paymentMethod = "CASH";
    }

    /**
     * Constructor phục hồi đơn hàng đầy đủ từ tầng lưu trữ JSON (JsonDataManager).
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
        this.paymentMethod  = (paymentMethod != null && !paymentMethod.isEmpty()) ? paymentMethod : "CASH";
    }

    // ------------------------------------------------------------------ business methods
    /**
     * Calculates final amount applying the customer's polymorphic discount.
     * This is where runtime polymorphism (calculateDiscount) is exercised.
     */
    public void calculateFinal() {
        totalAmount    = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        // POLYMORPHISM: same call, different result for Regular vs VIP
        discountAmount = customer.calculateDiscount(totalAmount);
        finalAmount    = totalAmount - discountAmount;
    }

    // ------------------------------------------------------------------ getters / setters
    public String       getOrderId()       { return orderId;       }
    public Customer     getCustomer()      { return customer;      }
    public List<CartItem> getItems()       { return new ArrayList<>(items); }
    public double       getTotalAmount()   { return totalAmount;   }
    public double       getDiscountAmount(){ return discountAmount; }
    public double       getFinalAmount()   { return finalAmount;   }
    public LocalDateTime getOrderDate()    { return orderDate;     }
    public OrderStatus  getStatus()        { return status;        }
    public String       getPaymentMethod() { return paymentMethod; }

    public void setStatus(OrderStatus status)           { this.status        = status;        }
    public void setPaymentMethod(String paymentMethod)  { this.paymentMethod = paymentMethod; }

    // ------------------------------------------------------------------ display
    public void printReceipt() {
        String payName = paymentMethod.equals("CASH") ? "Tiền mặt (CASH)" :
                         paymentMethod.equals("CARD") ? "Thẻ ngân hàng (CARD)" :
                         paymentMethod.equals("EWALLET") ? "Ví điện tử (E-WALLET)" : paymentMethod;
        System.out.println("\n  ╔═══════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                              ECOSHOP — HÓA ĐƠN MUA HÀNG                               ║");
        System.out.println("  ╚═══════════════════════════════════════════════════════════════════════════════════════╝");
        System.out.printf("    Mã đơn hàng : %s%n", orderId);
        System.out.printf("    Khách hàng  : %s (%s)%n", customer.getUsername(), customer.getCustomerType());
        System.out.printf("    Ngày đặt    : %s%n", orderDate.format(FMT));
        System.out.printf("    Thanh toán  : %s%n", payName);
        System.out.println("  ┌────────────────────────────────┬────────┬────────────────────┬────────────────────────┐");
        for (CartItem item : items) {
            System.out.println(item);
        }
        System.out.println("  └────────────────────────────────┴────────┴────────────────────┴────────────────────────┘");
        System.out.printf("    Tạm tính          : $%.2f%n", totalAmount);
        System.out.printf("    Giảm giá ưu đãi   : -$%.2f (%.0f%%)%n",
            discountAmount, totalAmount > 0 ? (discountAmount / totalAmount * 100) : 0);
        System.out.println("  -----------------------------------------------------------------------------------------");
        System.out.printf("    TỔNG THANH TOÁN   : $%.2f%n", finalAmount);
        System.out.println("  =========================================================================================");
    }

    // ------------------------------------------------------------------ JsonSerializable
    @Override
    public String toJson() {
        StringBuilder itemsJson = new StringBuilder("[");
        List<CartItem> list = items;
        for (int i = 0; i < list.size(); i++) {
            CartItem ci = list.get(i);
            itemsJson.append(String.format(
                "{\"productId\":\"%s\",\"productName\":\"%s\",\"quantity\":%d,\"subtotal\":%.2f}",
                ci.getProduct().getProductId(),
                ci.getProduct().getName().replace("\"", "\\\""),
                ci.getQuantity(), ci.getSubtotal()
            ));
            if (i < list.size() - 1) itemsJson.append(",");
        }
        itemsJson.append("]");

        return String.format(
            "{\"orderId\":\"%s\",\"customerId\":\"%s\",\"customerName\":\"%s\"," +
            "\"items\":%s,\"totalAmount\":%.2f,\"discountAmount\":%.2f," +
            "\"finalAmount\":%.2f,\"orderDate\":\"%s\",\"status\":\"%s\",\"paymentMethod\":\"%s\"}",
            orderId, customer.getUserId(), customer.getUsername(),
            itemsJson, totalAmount, discountAmount, finalAmount,
            orderDate.format(FMT), status.name(), paymentMethod
        );
    }

    @Override
    public String toString() {
        return String.format("[%-18s] %-22s | Khách: %-12s | $%8.2f -> $%8.2f | %s",
            status, orderId, customer.getUsername(), totalAmount, finalAmount, orderDate.format(FMT));
    }
}
