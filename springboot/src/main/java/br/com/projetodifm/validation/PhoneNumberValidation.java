package br.com.projetodifm.validation;

import br.com.projetodifm.validation.constraints.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidation implements ConstraintValidator<PhoneNumber, String>{

    // private static final String PHONE_NUMBER_REGEX = "\\[0-9]d{2}\\[9]d{1}\\[0-9]d{8}";

    public boolean isValid(String value, ConstraintValidatorContext context) {
        // return value.matches(PHONE_NUMBER_REGEX);
        return true;
    }
    
}
