package skeleton.exception;

/**
 * Ngoại lệ ném ra khi giao dịch thanh toán thất bại.
 */
public class PaymentFailedException extends EcoShopException {
    public PaymentFailedException(String reason) {
        super("Thanh toán thất bại: " + reason, "LOI_THANH_TOAN");
    }
}
