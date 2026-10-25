package exception;

/**
 * Thrown when requested quantity exceeds available stock.
 */
public class InsufficientStockException extends EcoShopException {
    public InsufficientStockException(String productName, int requested, int available) {
        super("Không đủ hàng tồn kho cho sản phẩm '" + productName + "'. Yêu cầu: " + requested
                + ", Hiện có: " + available, "HET_HANG");
    }
}
