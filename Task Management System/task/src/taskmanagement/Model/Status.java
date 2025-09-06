package taskmanagement.Model;

public enum Status {
    CREATED,
    IN_PROGRESS,
    COMPLETED;

    public static Status fromString(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status " + status);
        }
    }
}
