package taskmanagement.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import taskmanagement.DTO.*;
import taskmanagement.Security.AppUserService;
import taskmanagement.Service.UserServiceImpl;
import taskmanagement.Util.ResponseHelper;

import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    private UserServiceImpl userService;
    private AppUserService appUserService;

    public TaskController(UserServiceImpl userService, AppUserService appUserService) {
        this.userService = userService;
        this.appUserService = appUserService;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<Object> getTasks(@RequestParam("author") Optional<String> user,
                                           @RequestParam("assignee") Optional<String> assignee) {
        List<TaskWithCommentsDTO> tasks = userService.getTasks(user, assignee);
        return ResponseHelper.success(tasks);
    }

    @GetMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<Object> getComments(@PathVariable("taskId") int taskId) {
        List<CommentDTO> comments = userService.getComments(taskId);
        return ResponseHelper.success(comments);
    }

    @PostMapping("/api/tasks")
    public ResponseEntity<Object> addTask(@Valid @RequestBody TaskRequest request, BindingResult result) {
        userService.validateBindingRes(result);
        User user = userService.getAuthUser();

        TaskDTO taskDTO = userService.addTask(request, user);
        return ResponseHelper.success(taskDTO);
    }


    @PostMapping("/api/tasks/{taskId}/comments")
    public ResponseEntity<Object> addComment(@PathVariable("taskId") int taskId, @Valid @RequestBody CommentRequest comment, BindingResult result ) {
        userService.validateBindingRes(result);

        CommentDTO commentDTO = userService.addComment(comment, taskId);
        return ResponseHelper.success(commentDTO);
    }

    @PutMapping("/api/tasks/{taskId}/assign")
    public ResponseEntity<Object> assignTask(@PathVariable("taskId") int taskId, @Valid @RequestBody AssigneeRequest request) {
        User user = userService.getAuthUser();

        TaskDTO taskDTO = userService.assignTask(user, taskId, request);
        return ResponseHelper.success(taskDTO);
    }

    @PutMapping("/api/tasks/{taskId}/status")
    public ResponseEntity<Object> updateTaskStatus(@PathVariable("taskId") int taskId, @RequestBody StatusRequest status) {
        User user = userService.getAuthUser();

        TaskDTO taskDTO = userService.setStatus(user, taskId, status);
        return ResponseHelper.success(taskDTO);
    }
}
