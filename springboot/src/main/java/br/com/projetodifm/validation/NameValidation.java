package br.com.projetodifm.validation;

import br.com.projetodifm.validation.constraints.Name;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidation implements ConstraintValidator<Name, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var name = value.split(" ");
        return !(name.length > 1 || !name[0].matches("^[A-Z]+(.)*"));
    }
}
