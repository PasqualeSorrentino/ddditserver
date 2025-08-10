package it.unisa.ddditserver.validators.implementations.user;

import it.unisa.ddditserver.validators.implementations.ValidationResult;
import lombok.experimental.UtilityClass;
import java.util.regex.Pattern;

@UtilityClass
public class UserValidator {

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 50;

    // NOTE: The username can contain only letters, numbers, underscores and dots
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    // NOTE: Password must contain at least one lowercase letter, one uppercase letter,
    // one digit, and one special character
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    private boolean isValidUsername(String username) {
        if (username == null) return false;
        int length = username.length();
        if (length < USERNAME_MIN_LENGTH || length > USERNAME_MAX_LENGTH) return false;
        if (!USERNAME_PATTERN.matcher(username).matches()) return false;
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        int length = password.length();
        if (length < PASSWORD_MIN_LENGTH || length > PASSWORD_MAX_LENGTH) return false;
        if (!PASSWORD_PATTERN.matcher(password).matches()) return false;
        return true;
    }

    public ValidationResult validateCredentials(String username, String password) {
        if (!isValidUsername(username)) {
            return ValidationResult.invalid("Username must be 3-30 chars long, letters, digits, _ and . only");
        }
        if (!isValidPassword(password)) {
            return ValidationResult.invalid("Password must be 8-50 chars, with uppercase, lowercase, digit and special char");
        }
        return ValidationResult.valid();
    }
}

