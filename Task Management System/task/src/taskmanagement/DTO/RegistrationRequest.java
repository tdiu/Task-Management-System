package taskmanagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @Email(message = "Invalid Email")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message="Field cannot be blank")
        @Size(min = 6, message = "Password must have a length of at least 6")
        @Pattern(regexp = "^\\S+$", message = "Input cannot contain spaces")
        String password
) {}
