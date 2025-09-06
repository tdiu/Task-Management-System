package taskmanagement.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;


public class EmailOrNoneValidator implements ConstraintValidator<EmailOrNone, String> {
    private static final String NONE_VAL = "none";
    private final EmailValidator emailValidator = new EmailValidator();

    @Override
    public void initialize(EmailOrNone constraintAnnotation) {
        emailValidator.initialize(null);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }
        return NONE_VAL.equals(email) || emailValidator.isValid(email, context);
    }
}
