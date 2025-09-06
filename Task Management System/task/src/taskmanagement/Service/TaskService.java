package taskmanagement.Service;

import org.springframework.security.core.userdetails.User;
import taskmanagement.DTO.*;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    TaskDTO addTask(TaskRequest request, User user);
    public List<TaskWithCommentsDTO> getTasks(Optional<String> user, Optional<String> assignee);
    public TaskDTO assignTask(User user, int taskId, AssigneeRequest assignee);
    public CommentDTO addComment(CommentRequest comment, int taskId);
    public List<CommentDTO> getComments(int taskId);
    public TaskDTO setStatus(User user, int taskId, StatusRequest status);
}
