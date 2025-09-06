package taskmanagement.Exception;

import java.util.Map;

public class InvalidInformationException extends RuntimeException {
    private final Map<String, String> errors;

    public InvalidInformationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String,String> getErrors() {
        return errors;
    }
}
