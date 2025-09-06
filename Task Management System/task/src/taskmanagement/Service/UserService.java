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
    public User getAuthUser();
    public void validateBindingRes(BindingResult result);
}
