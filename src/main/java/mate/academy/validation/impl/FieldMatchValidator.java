package mate.academy.validation.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import mate.academy.validation.FieldMatch;
import org.springframework.beans.PropertyAccessorFactory;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField;
    private String secondField;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstField = constraintAnnotation.first();
        secondField = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        Object firstObj = getValue(o, firstField);
        Object secondObj = getValue(o, secondField);
        return Objects.equals(firstObj, secondObj);
    }

    private Object getValue(Object o, String fieldName) {
        return PropertyAccessorFactory.forBeanPropertyAccess(o).getPropertyValue(fieldName);
    }
}
