package it.unisa.ddditserver.validators.user;

import it.unisa.ddditserver.auth.dto.UserDTO;
import it.unisa.ddditserver.db.gremlin.auth.GremlinAuthService;
import it.unisa.ddditserver.validators.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

/**
 * Component responsible for validating user credentials and checking username existence.
 *
 * This validator verifies that usernames and passwords respect predefined patterns and length constraints.
 * It also checks if a username already exists using the {@link GremlinAuthService}.
 * Validations include:
 * <ul>
 *     <li>Username must be 3-30 characters long, containing only letters, digits, underscores, and dots.</li>
 *     <li>Password must be 8-50 characters long and include at least one uppercase letter, one lowercase letter,
 *         one digit, and one special character.</li>
 *     <li>Existence of the user in the graph database.</li>
 *     <li>Matching passwords given and the one stored in the graph database.</li>
 *     <li>Current session status of the user.</li>
 * </ul>
 *
 * @author Angelo Antonio Prisco
 * @version 1.2
 * @since 2025-08-11
 */
@Component
public class UserValidatorImpl implements UserValidator {
    private final GremlinAuthService gremlinService;

    @Autowired
    public UserValidatorImpl(GremlinAuthService gremlinService) {
        this.gremlinService = gremlinService;
    }

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 50;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    private boolean isValidUsername(String username) {
        if (username == null) return false;
        int length = username.length();
        return length >= USERNAME_MIN_LENGTH &&
                length <= USERNAME_MAX_LENGTH &&
                USERNAME_PATTERN.matcher(username).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) return false;
        int length = password.length();
        return length >= PASSWORD_MIN_LENGTH &&
                length <= PASSWORD_MAX_LENGTH &&
                PASSWORD_PATTERN.matcher(password).matches();
    }

    public ValidationResult validateCredentials(UserValidationDTO userValidationDTO) {
        if (!isValidUsername(userValidationDTO.getUsername())) {
            return ValidationResult.invalid("Username must be 3-30 chars long, letters, digits, _ and . only");
        }
        if (!isValidPassword(userValidationDTO.getPassword())) {
            return ValidationResult.invalid("Password must be 8-50 chars, with uppercase, lowercase, digit and special char");
        }
        return ValidationResult.valid();
    }

    public ValidationResult validateExistence(UserValidationDTO userValidationDTO, boolean exists) {
        if (exists) {
            if (!gremlinService.existsByUsername(userValidationDTO.getUsername())) {
                return ValidationResult.invalid("Username does not exist");
            }
        }
        else {
            if (gremlinService.existsByUsername(userValidationDTO.getUsername())) {
                return ValidationResult.invalid("Username already exists");
            }
        }
        return ValidationResult.valid();
    }

    @Override
    public ValidationResult validateMatchingPasswords(UserValidationDTO userValidationDTO) {
        UserDTO retrievedUser = gremlinService.findByUsername(userValidationDTO.getUsername());

        String storedPassword = retrievedUser.getPassword();
        String providedPassword = userValidationDTO.getPassword();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(providedPassword, storedPassword)) {
            return ValidationResult.invalid("Given password does not match");
        }

        return ValidationResult.valid();
    }

    @Override
    public ValidationResult validateLoggedStatus(String token) {
        if (token != null && !token.isEmpty()) {
            return ValidationResult.invalid("User is already logged in");
        }
        return ValidationResult.valid();
    }
}