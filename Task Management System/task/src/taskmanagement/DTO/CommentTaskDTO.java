package taskmanagement.DTO;

import taskmanagement.Model.Status;
import taskmanagement.Model.Task;

public class CommentTaskDTO {
    private String id;
    private String title;
    private String description;
    private Status status;
    private String author;
    private String assignee;
    private long total_comments;

    public CommentTaskDTO(Task task, long totalComments) {
        this.id = task.getId().toString();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.author = task.getAuthor().getEmail();
        this.assignee = (task.getAssignee() == null) ? "none" : task.getAssignee().getEmail();
        this.total_comments = totalComments;
    }

    public CommentTaskDTO(String id, String title, String description, Status status, String author, String assignee, long totalComments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.author = author;
        this.assignee = assignee;
        this.total_comments = totalComments;
    }

    public String getid() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public long getTotal_comments() {
        return total_comments;
    }

    public void setTotal_comments(long total_comments) {
        this.total_comments = total_comments;
    }
}
