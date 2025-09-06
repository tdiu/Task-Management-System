package taskmanagement.DTO;

import taskmanagement.Validation.EmailOrNone;

public record AssigneeRequest(
        @EmailOrNone(message="Assignee must be valid email or none")
        String assignee
        )
{}
