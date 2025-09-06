package taskmanagement.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import taskmanagement.DAO.AppUserRepository;
import taskmanagement.DAO.CommentRepository;
import taskmanagement.DAO.TaskRepository;
import taskmanagement.DTO.*;
import taskmanagement.Exception.*;
import taskmanagement.Model.AppUser;
import taskmanagement.Model.Comment;
import taskmanagement.Model.Status;
import taskmanagement.Model.Task;
import taskmanagement.Security.AppUserService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final AppUserRepository appUserRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Autowired
    public UserServiceImpl(AppUserRepository appUserRepository, TaskRepository taskRepository, CommentRepository commentRepository,
                           PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, AppUserService appUserService) {
        this.appUserRepository = appUserRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.appUserService = appUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public UserDTO registerUser(RegistrationRequest request) {
        if (appUserRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateEmailException("Email already exists");
        }
        AppUser user = new AppUser(request);
        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);
        appUserRepository.save(user);

        return new UserDTO(user);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        AppUser user = appUserRepository.findAppUserByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserDTO(user);
    }

    @Override
    public String generateToken(User user) {
        AppUser appUser = appUserRepository.findAppUserByUsernameIgnoreCase(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + user.getUsername()));

        Set<String> authorityStrings = appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtClaimsSet claimSet = JwtClaimsSet.builder()
                .subject(appUser.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(60, ChronoUnit.SECONDS))
                .claim("scope", authorityStrings)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimSet)).getTokenValue();
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

    @Override
    public User getAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user;

        if (auth.getPrincipal() instanceof User) {
            user = (User) auth.getPrincipal();
        } else {
            String username = (String) auth.getName();
            user = (User) appUserService.loadUserByUsername(username);
        }
        return user;
    }

    @Override
    public void validateBindingRes(BindingResult result) throws InvalidInformationException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            throw new InvalidInformationException("Validation error", errors);
        }
    }

}
