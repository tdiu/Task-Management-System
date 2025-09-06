package taskmanagement.DTO;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank(message="Field cannot be blank")
        String title,

        @NotBlank(message="Field cannot be blank")
        String description
) {}
