package skeleton.exception;
 
/**
 * Lớp ngoại lệ cơ sở (Base Exception) cho toàn bộ hệ sinh thái EcoShop.
 *
 * Đại diện cho nguyên lý phân cấp ngoại lệ có cấu trúc trong OOP.
 */
public class EcoShopException extends Exception {

    private final String errorCode;

    public EcoShopException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
