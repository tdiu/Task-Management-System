package taskmanagement.DTO;

import taskmanagement.Model.Status;
import taskmanagement.Model.Task;

public class TaskWithCommentsDTO extends TaskDTO{
    private long total_comments;

    public TaskWithCommentsDTO(String id, String title, String description, Status status, String author, String assignee, long totalComments) {
        super(id, title, description, status, author, assignee);
        this.total_comments = totalComments;
    }

    public long getTotal_comments() {
        return total_comments;
    }

    public void setTotal_comments(long total_comments) {
        this.total_comments = total_comments;
    }
}
