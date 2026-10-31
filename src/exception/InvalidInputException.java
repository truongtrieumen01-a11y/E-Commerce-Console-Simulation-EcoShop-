package exception;

/**
 * Thrown when user provides invalid input (e.g., text where number is required).
 * Satisfies: Error Handling - program must not crash from invalid user inputs.
 */
public class InvalidInputException extends EcoShopException {
    public InvalidInputException(String message) {
        super(message, "DU_LIEU_KHONG_HOP_LE");
    }
}
