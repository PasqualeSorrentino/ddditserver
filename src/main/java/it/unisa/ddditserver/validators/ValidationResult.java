package it.unisa.ddditserver.validators;

import lombok.Value;

/**
 * Represents the result of a validation check.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
@Value
public class ValidationResult {
    boolean valid;
    String message;

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(String message) {
        return new ValidationResult(false, message);
    }
}
