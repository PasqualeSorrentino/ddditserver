package it.unisa.ddditserver.validators;

import it.unisa.ddditserver.validators.implementations.ValidationResult;

public interface Validator<T> {
    ValidationResult validate(T input);
}
