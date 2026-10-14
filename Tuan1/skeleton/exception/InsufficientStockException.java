package skeleton.exception;

/**
 * Ngoại lệ ném ra khi số lượng đặt mua vượt quá tồn kho hiện có.
 */
public class InsufficientStockException extends EcoShopException {
    public InsufficientStockException(String productName, int requested, int available) {
        super("Không đủ hàng tồn kho cho sản phẩm '" + productName + "'. Yêu cầu: " + requested
                + ", Hiện có: " + available, "HET_HANG");
    }
}
