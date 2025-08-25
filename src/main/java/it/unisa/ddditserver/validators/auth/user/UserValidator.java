package it.unisa.ddditserver.validators.auth.user;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating user's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.3
 * @since 2025-08-13
 */
public interface UserValidator {
    /**
     * Validates the credentials of the given user.
     *
     * @param userValidationDTO the user data transfer object containing user's information to validate
     * @return a ValidationResult indicating if the credentials are valid or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateUser(UserValidationDTO userValidationDTO);

    /**
     * Validates whether the user exists.
     *
     * @param userValidationDTO the user data transfer object containing user's information to validate
     * @param exists defines the kind of validation to perform; if true, the validation passes only if the user exists, false otherwise
     * @return a ValidationResult indicating if the user exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateExistence(UserValidationDTO userValidationDTO, boolean exists);

    /**
     * Validates if the provided password and the one stored in the database match.
     *
     * @param userValidationDTO the user data transfer object containing user's information to validate
     * @return a ValidationResult indicating if the passwords match or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateMatchingPasswords(UserValidationDTO userValidationDTO);
}
