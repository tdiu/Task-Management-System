package taskmanagement.Exception;

public class DuplicateEmailException extends RuntimeException {
    private String message;

    public DuplicateEmailException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
