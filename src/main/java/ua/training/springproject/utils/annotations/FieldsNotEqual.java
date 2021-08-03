package ua.training.springproject.utils.annotations;

import ua.training.springproject.utils.validators.FieldsNotEqualValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FieldsNotEqualValidator.class})
public @interface FieldsNotEqual {

    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String firstField();

    String secondField();

}