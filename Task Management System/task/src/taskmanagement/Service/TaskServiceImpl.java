package taskmanagement.Service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import taskmanagement.DAO.AppUserRepository;
import taskmanagement.DAO.CommentRepository;
import taskmanagement.DAO.TaskRepository;
import taskmanagement.DTO.*;
import taskmanagement.Exception.DuplicateEmailException;
import taskmanagement.Exception.InvalidStatusException;
import taskmanagement.Exception.InvalidTaskException;
import taskmanagement.Exception.UnauthorizedException;
import taskmanagement.Model.AppUser;
import taskmanagement.Model.Comment;
import taskmanagement.Model.Status;
import taskmanagement.Model.Task;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final AppUserRepository appUserRepository;

    public TaskServiceImpl(TaskRepository taskRepository, CommentRepository commentRepository, AppUserRepository appUserRepository) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public TaskDTO addTask(TaskRequest request, User user) {
        Task task = new Task(request);
        String userName = user.getUsername();
        task.setAuthor(appUserRepository.findAppUserByUsernameIgnoreCase(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userName)));
        taskRepository.save(task);

        return new TaskDTO(task);
    }

    @Override
    public List<TaskWithCommentsDTO> getTasks(Optional<String> user, Optional<String> assignee) {
        List<TaskWithCommentsDTO> tasks;

        if (user.isPresent() && assignee.isPresent()) {
            tasks = taskRepository.findByAuthorAndAssignee(user.get().trim(), assignee.get().trim());
        } else if (user.isEmpty() && assignee.isEmpty()) {
            tasks = taskRepository.findAllTaskCommentCounts();
        } else {
            tasks = user.isPresent() ? taskRepository.findByAuthorCaseInsensitive(user.get().trim())
                    : taskRepository.findByAssignee(assignee.get().trim());
        }
        return tasks;
    }

    @Override
    public TaskDTO assignTask(User user, int taskId, AssigneeRequest request) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new InvalidTaskException("Task not found with id: " + taskId));
        String newAssignee = request.assignee().trim();
        String taskCreator = task.getAuthor().getUsername();

        if (!user.getUsername().equalsIgnoreCase(taskCreator)) {
            throw new UnauthorizedException("You are not allowed to assign this task");
        }

        AppUser assignee = "none".equals(request.assignee()) ? null : appUserRepository.findAppUserByUsernameIgnoreCase(newAssignee)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + newAssignee));

        task.setAssignee(assignee);
        taskRepository.save(task);
        return new TaskDTO(task);
    }

    public TaskDTO setStatus(User user, int taskId, StatusRequest status) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new InvalidTaskException("Task not found with id: " + taskId));
        String taskCreator = task.getAuthor().getUsername();
        String assignee = task.getAssignee() == null ? null : task.getAssignee().getUsername();

        boolean isCreator = taskCreator.equalsIgnoreCase(user.getUsername());
        boolean isAssignee = (assignee != null && assignee.equalsIgnoreCase(user.getUsername()));
        if (isCreator || isAssignee) {
            try {
                task.setStatus(Status.fromString(status.status()));
            } catch (IllegalArgumentException e) {
                throw new InvalidStatusException("Invalid status");
            }

        } else {
            throw new UnauthorizedException("You are not allowed to modify this task");
        }

        taskRepository.save(task);
        return new TaskDTO(task);
    }

    @Override
    public CommentDTO addComment(CommentRequest commentRequest, int taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new InvalidTaskException("Task not found with id: " + taskId));

        String text = commentRequest.text();
        String author = SecurityContextHolder.getContext().getAuthentication().getName();

        Comment newComment = new Comment(task, text, author);

        // Check for dupes
        Optional<Comment> existingComment = commentRepository.findByTaskIdAndTextAndAuthor(taskId, text, author);
        if (existingComment.isPresent()) {
            throw new DuplicateEmailException("A similar comment already exists");
        }

        task.getComments().add(newComment);
        commentRepository.save(newComment);
        taskRepository.save(task);
        return new CommentDTO(String.valueOf(newComment.getId()), String.valueOf(taskId), text, author);
    }

    @Override
    public List<CommentDTO> getComments(int taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new InvalidTaskException("Task not found with id: " + taskId);
        }
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return comments.stream().map(CommentDTO::new).collect(Collectors.toList());
    }
}
