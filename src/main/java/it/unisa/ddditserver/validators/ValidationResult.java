package it.unisa.ddditserver.validators;

import lombok.Value;

/**
 * Represents the result of a validation check.
 *
 * It includes an optional exception if the validation fails.
 * The exception includes details about the validation failure.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-14
 */
@Value
public class ValidationResult {
    boolean valid;
    Exception exception;

    public ValidationResult(boolean valid) {
        this.valid = valid;
        this.exception = null;
    }

    public ValidationResult(boolean valid, Exception exception) {
        this.valid = valid;
        this.exception = exception;
    }

    public static ValidationResult valid() {
        return new ValidationResult(true);
    }

    public static ValidationResult invalid(Exception exception) {
        return new ValidationResult(false, exception);
    }
}
