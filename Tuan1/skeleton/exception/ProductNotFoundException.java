package skeleton.exception;

/**
 * Ngoại lệ ném ra khi không tìm thấy sản phẩm theo mã yêu cầu.
 */
public class ProductNotFoundException extends EcoShopException {
    public ProductNotFoundException(String productId) {
        super("Không tìm thấy sản phẩm với mã: " + productId, "KHONG_TIM_THAY_SP");
    }
}
