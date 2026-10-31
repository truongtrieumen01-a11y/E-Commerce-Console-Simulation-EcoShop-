package exception;

/**
 * Thrown when payment processing fails.
 */
public class PaymentFailedException extends EcoShopException {
    public PaymentFailedException(String reason) {
        super("Thanh toán thất bại: " + reason, "LOI_THANH_TOAN");
    }
}
