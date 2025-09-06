package taskmanagement.DTO;

import taskmanagement.Model.Comment;

public class CommentDTO {
    private String id;
    private String task_id;
    private String text;
    private String author;

    public CommentDTO() {
    }

    public CommentDTO(String commentID, String taskID, String text, String author) {
        this.id = commentID;
        this.task_id = taskID;
        this.text = text;
        this.author = author;
    }

    public CommentDTO(Comment comment) {
        this.id = String.valueOf(comment.getId());
        this.task_id = String.valueOf(comment.getTask().getId());
        this.text = comment.getText();
        this.author = comment.getAuthor();
    }

    public String getId() {
        return id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
