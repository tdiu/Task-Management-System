package taskmanagement.DTO;

import taskmanagement.Model.Status;
import taskmanagement.Model.Task;

public class TaskDTO {
    private String id;
    private String title;
    private String description;
    private Status status;
    private String author;
    private String assignee;
    private long total_comments;

    public TaskDTO(Task task) {
        this.id = task.getId().toString();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.author = task.getAuthor().getEmail();
        this.assignee = (task.getAssignee() == null) ? "none" : task.getAssignee().getEmail();
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
}
