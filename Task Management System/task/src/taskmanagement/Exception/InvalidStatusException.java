package taskmanagement.Exception;

public class InvalidStatusException extends RuntimeException {
    private final String message;

    public InvalidStatusException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
