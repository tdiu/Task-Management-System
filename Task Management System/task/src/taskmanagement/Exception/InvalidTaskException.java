package taskmanagement.Exception;

public class InvalidTaskException extends RuntimeException{
    private final String message;
    public InvalidTaskException(String message){
        super(message);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
