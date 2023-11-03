package br.com.projetodifm.validation.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.projetodifm.util.ErrorMessages;
import br.com.projetodifm.validation.PasswordValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordValidation.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default ErrorMessages.INVALID_CONTENT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
