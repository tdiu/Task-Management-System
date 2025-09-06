package taskmanagement.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import taskmanagement.DTO.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(RegistrationRequest request);
    UserDTO getUserByUsername(String username);
    public String generateToken(User user);
    TaskDTO addTask(TaskRequest request, User user);
    public List<CommentTaskDTO> getTasks(Optional<String> user, Optional<String> assignee);
    public TaskDTO assignTask(User user, int taskId, AssigneeRequest assignee);
    public CommentDTO addComment(CommentRequest comment, int taskId);
    public List<CommentDTO> getComments(int taskId);
    public User getAuthUser();
    public void validateBindingRes(BindingResult result);
}
