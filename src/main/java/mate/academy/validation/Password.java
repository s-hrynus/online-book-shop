package mate.academy.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import mate.academy.validation.impl.PasswordValidation;

@Constraint(validatedBy = PasswordValidation.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid format password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
