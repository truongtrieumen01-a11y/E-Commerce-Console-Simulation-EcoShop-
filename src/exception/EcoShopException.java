package exception;

/**
 * Base exception class for EcoShop application.
 * Demonstrates custom Exception hierarchy (Error Handling requirement).
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
