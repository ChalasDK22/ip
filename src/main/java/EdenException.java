/**
 * Represents an error caused by an invalid command entered by the user.
 */
public class EdenException extends Exception {
    public EdenException(String message) {
        super(message);
    }
}