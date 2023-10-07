package mate.academy.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import mate.academy.validation.Password;

public class PasswordValidation implements ConstraintValidator<Password, String> {
    private static final String
            PATTERN_OF_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_=+-]).{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && Pattern.compile(PATTERN_OF_PASSWORD)
                .matcher(password).matches();
    }
}
