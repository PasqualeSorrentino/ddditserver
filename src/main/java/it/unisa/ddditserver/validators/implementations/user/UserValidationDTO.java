package it.unisa.ddditserver.validators.implementations.user;
import lombok.Value;

/**
 * Data Transfer Object for user validation data.
 *
 * Contains the user's credentials to be validated: username and password.
 * Primarily used in the user credential validation processes.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
@Value
public class UserValidationDTO {
    String username;
    String password;
}
