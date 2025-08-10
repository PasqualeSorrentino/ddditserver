package it.unisa.ddditserver.validators.implementations;
import lombok.Value;

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
