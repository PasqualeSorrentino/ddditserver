package it.unisa.ddditserver.validators.user;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating user's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.2
 * @since 2025-08-11
 */
public interface UserValidator {
    /**
     * Validates the credentials of the given user.
     *
     * @param userValidationDTO the user data transfer object containing user's information to validate
     * @return a ValidationResult indicating if the credentials are valid or not, including an optional message
     */
    ValidationResult validateCredentials(UserValidationDTO userValidationDTO);

    /**
     * Validates whether the user exists.
     *
     * @param userValidationDTO the user data transfer object containing user's information to validate
     * @param exists define the kind of validation to perform, if it is true then the method consider the validation passed only if the user exists, false otherwise
     * @return a ValidationResult indicating if the user exists or not, including an optional message
     */
    ValidationResult validateExistence(UserValidationDTO userValidationDTO, boolean exists);

    /**
     * Validates if the provided password and the one stored in the database match.
     *
     * @param userValidationDTO the user data transfer object containing user's information to validate
     * @return a ValidationResult indicating if the passwords match or not, including an optional message
     */
    ValidationResult validateMatchingPasswords(UserValidationDTO userValidationDTO);

    /**
     * Validates if the user is already logged.
     *
     * @param token the user token to validate
     * @return a ValidationResult indicating if the user is already logged, including an optional message
     */
    ValidationResult validateLoggedStatus(String token);
}