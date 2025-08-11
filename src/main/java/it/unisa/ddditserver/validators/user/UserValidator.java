package it.unisa.ddditserver.validators.user;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating user's data.
 * <p>
 * This interface provides methods to validate user's credentials and
 * to check for the existence of a user.
 * </p>
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public interface UserValidator {
    /**
     * Validates the credentials of the given user.
     *
     * @param userDTO the user data transfer object containing user's information to validate
     * @return a ValidationResult indicating if the credentials are valid or not, including an optional message
     */
    ValidationResult validateCredentials(UserValidationDTO userDTO);

    /**
     * Validates whether the user exists.
     *
     * @param userDTO the user data transfer object containing user's information to validate
     * @return a ValidationResult indicating if the user exists or not, including an optional message
     */
    ValidationResult validateExistence(UserValidationDTO userDTO);
}