package service;

import exception.InsufficientStockException;
import exception.PaymentFailedException;
import model.CartItem;
import model.Customer;
import model.Order;
import model.OrderStatus;
import model.RegularCustomer;

import java.util.Scanner;

/**
 * PaymentProcessor — handles payment logic.
 *
 * Demonstrates:
 *  - Abstraction   : hides complex payment logic behind processPayment()
 *  - Encapsulation : paymentMethod is private
 *  - Error Handling: throws PaymentFailedException on invalid amounts
 */
public class PaymentProcessor {

    private String paymentMethod;

    // ------------------------------------------------------------------ constructor
    public PaymentProcessor() {
        this.paymentMethod = "CASH";
    }

    // ------------------------------------------------------------------ operations
    /**
     * Processes payment for an order. Validates amount and sets order status.
     * @param order    the order to pay for
     * @param method   "CASH", "CARD", or "EWALLET"
     * @param scanner  console input (used to simulate card number entry, etc.)
     * @throws PaymentFailedException if validation fails
     */
    public void processPayment(Order order, String method, Scanner scanner)
            throws PaymentFailedException {

        // [Sửa lỗi 2]: Chặn đơn hàng null, rỗng hoặc đã thanh toán / đã hủy
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            throw new PaymentFailedException("Đơn hàng không hợp lệ hoặc đang trống.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.CONFIRMED) {
            throw new PaymentFailedException("Đơn hàng đã bị hủy hoặc đã thanh toán trước đó.");
        }

        validateAmount(order.getFinalAmount());

        this.paymentMethod = method.toUpperCase().trim();
        order.setPaymentMethod(this.paymentMethod);

        switch (this.paymentMethod) {
            case "CASH":
                processCash(order, scanner);
                break;
            case "CARD":
                processCard(order, scanner);
                break;
            case "EWALLET":
                processEwallet(order, scanner);
                break;
            default:
                throw new PaymentFailedException("Phương thức thanh toán không hỗ trợ: " + method);
        }

        // [Sửa lỗi 1]: Vòng lặp trừ tồn kho sản phẩm sau khi thanh toán thành công
        try {
            for (CartItem item : order.getItems()) {
                item.getProduct().reduceStock(item.getQuantity());
            }
        } catch (IllegalArgumentException e) {
            throw new PaymentFailedException(e.getMessage());
        }

        order.setStatus(OrderStatus.CONFIRMED);

        // Award loyalty points for Regular customers after successful payment
        if (order.getCustomer() instanceof RegularCustomer) {
            RegularCustomer rc = (RegularCustomer) order.getCustomer();
            rc.earnPointsFromPurchase(order.getFinalAmount());
        }
    }

    private void validateAmount(double amount) throws PaymentFailedException {
        if (amount <= 0) {
            throw new PaymentFailedException("Số tiền thanh toán phải lớn hơn 0.");
        }
    }

    private void processCash(Order order, Scanner scanner) {
        System.out.println("\n  ┌──────────────────────────────────────────────┐");
        System.out.println("  │        THANH TOÁN BẰNG TIỀN MẶT (CASH)        │");
        System.out.println("  └──────────────────────────────────────────────┘");
        System.out.printf("  Số tiền cần trả: $%.2f%n", order.getFinalAmount());
        double tendered = 0;
        while (tendered < order.getFinalAmount()) {
            System.out.print("  Nhập số tiền khách đưa: $");
            try {
                tendered = Double.parseDouble(scanner.nextLine().trim());
                if (tendered < order.getFinalAmount()) {
                    System.out.printf("  [!] Chưa đủ tiền. Cần ít nhất $%.2f%n", order.getFinalAmount());
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Số tiền không hợp lệ. Vui lòng nhập số.");
                tendered = 0;
            }
        }
        System.out.printf("  [✓] Tiền thừa trả lại khách: $%.2f%n", tendered - order.getFinalAmount());
    }

    private void processCard(Order order, Scanner scanner) throws PaymentFailedException {
        System.out.println("\n  ┌──────────────────────────────────────────────┐");
        System.out.println("  │      THANH TOÁN QUA THẺ NGÂN HÀNG (CARD)      │");
        System.out.println("  └──────────────────────────────────────────────┘");
        System.out.print("  Nhập số thẻ (16 chữ số): ");
        String cardNum = scanner.nextLine().trim().replaceAll("\\s+", "");
        if (cardNum.length() != 16 || !cardNum.matches("\\d+")) {
            throw new PaymentFailedException("Số thẻ không hợp lệ. Phải gồm đúng 16 chữ số.");
        }
        System.out.print("  Nhập mã bảo mật CVV (3 chữ số): ");
        String cvv = scanner.nextLine().trim();
        if (cvv.length() != 3 || !cvv.matches("\\d+")) {
            throw new PaymentFailedException("Mã CVV không hợp lệ. Phải gồm đúng 3 chữ số.");
        }
        System.out.printf("  Đang trừ $%.2f vào thẻ đuôi %s...%n",
            order.getFinalAmount(), cardNum.substring(12));
        System.out.println("  [✓] Thanh toán thẻ đã được phê duyệt thành công.");
    }

    private void processEwallet(Order order, Scanner scanner) throws PaymentFailedException {
        System.out.println("\n  ┌──────────────────────────────────────────────┐");
        System.out.println("  │      THANH TOÁN VÍ ĐIỆN TỬ (E-WALLET)         │");
        System.out.println("  └──────────────────────────────────────────────┘");
        System.out.print("  Nhập số điện thoại liên kết ví: ");
        String phone = scanner.nextLine().trim();
        if (phone.length() < 9 || !phone.matches("\\d+")) {
            throw new PaymentFailedException("Số điện thoại liên kết ví không hợp lệ.");
        }
        System.out.printf("  Mã OTP đã gửi đến %s. Nhập OTP (6 số): ", phone);
        String otp = scanner.nextLine().trim();
        if (otp.length() != 6 || !otp.matches("\\d+")) {
            throw new PaymentFailedException("Định dạng mã OTP không hợp lệ (phải gồm 6 chữ số).");
        }
        System.out.printf("  [✓] Thanh toán ví điện tử số tiền $%.2f thành công.%n", order.getFinalAmount());
    }

    /** Generates a formatted receipt string. */
    public String generateReceipt(Order order) {
        return String.format("Hóa đơn — Mã: %s | Phương thức: %s | Số tiền: $%.2f",
            order.getOrderId(), order.getPaymentMethod(), order.getFinalAmount());
    }

    public String getPaymentMethod() { return paymentMethod; }
}
