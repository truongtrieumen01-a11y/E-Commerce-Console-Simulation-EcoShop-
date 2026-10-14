package skeleton.exception;

/**
 * Ngoại lệ ném ra khi người dùng nhập dữ liệu không hợp lệ.
 */
public class InvalidInputException extends EcoShopException {
    public InvalidInputException(String message) {
        super(message, "DU_LIEU_KHONG_HOP_LE");
    }
}
