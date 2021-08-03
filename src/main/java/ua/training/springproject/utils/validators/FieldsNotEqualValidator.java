package ua.training.springproject.utils.validators;

import org.springframework.beans.BeanWrapperImpl;
import ua.training.springproject.utils.annotations.FieldsNotEqual;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsNotEqualValidator implements ConstraintValidator<FieldsNotEqual, Object> {

    private String firstField;
    private String secondField;


    @Override
    public void initialize(FieldsNotEqual constraint) {
        firstField = constraint.firstField();
        secondField = constraint.secondField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Object fieldValue = new BeanWrapperImpl(object).getPropertyValue(firstField);
        Object fieldMatchValue = new BeanWrapperImpl(object).getPropertyValue(secondField);
        if (fieldValue != null) {
            return !fieldValue.equals(fieldMatchValue);
        } else {
            return !(fieldMatchValue == null);
        }
    }
}

