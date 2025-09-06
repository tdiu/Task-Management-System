package taskmanagement.DTO;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank
        String text
) {
}
