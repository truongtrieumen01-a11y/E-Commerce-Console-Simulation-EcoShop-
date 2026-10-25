package exception;

/**
 * Thrown when a product with the given ID cannot be found.
 */
public class ProductNotFoundException extends EcoShopException {
    public ProductNotFoundException(String productId) {
        super("Không tìm thấy sản phẩm với mã: " + productId, "KHONG_TIM_THAY_SP");
    }
}
