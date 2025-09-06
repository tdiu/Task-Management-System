package taskmanagement.Controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.DTO.RegistrationRequest;
import taskmanagement.DTO.TokenDTO;
import taskmanagement.DTO.UserDTO;
import taskmanagement.Service.UserServiceImpl;
import taskmanagement.Util.ResponseHelper;

@RestController
public class UserController {
    private UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/api/accounts")
    public ResponseEntity<Object> register(@Valid @RequestBody RegistrationRequest request, BindingResult result) {
        userService.validateBindingRes(result);
        UserDTO user = userService.registerUser(request);
        return ResponseHelper.success(user);
    }

    @PostMapping("/api/auth/token")
    public ResponseEntity<Object> token(@AuthenticationPrincipal User user) {
        TokenDTO token = new TokenDTO(userService.generateToken(user));
        return  ResponseHelper.success(token);
    }

}
