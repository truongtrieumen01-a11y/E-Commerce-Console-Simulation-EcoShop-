package skeleton.service;

import skeleton.exception.PaymentFailedException;
import skeleton.model.Order;
import java.util.Scanner;

/**
 * Lớp PaymentProcessor — Xử lý nghiệp vụ thanh toán đa phương thức (CASH, CARD, EWALLET).
 *
 * [SKELETON TUẦN 1] — Khung thiết kế, chưa hiện thực thân hàm logic.
 */
public class PaymentProcessor {

    private String paymentMethod;

    public PaymentProcessor() {
        this.paymentMethod = "CASH";
    }

    /**
     * Xử lý giao dịch thanh toán cho đơn hàng.
     *
     * @param order đơn hàng cần thanh toán
     * @param method phương thức: "CASH", "CARD", hoặc "EWALLET"
     * @param scanner nguồn nhập liệu từ bàn phím
     * @throws PaymentFailedException nếu thanh toán thất bại
     */
    public void processPayment(Order order, String method, Scanner scanner)
            throws PaymentFailedException {
        // TODO: Xác thực số tiền > 0, gọi hàm con xử lý tương ứng và cập nhật OrderStatus.CONFIRMED
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
