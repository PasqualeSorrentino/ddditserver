package it.unisa.ddditserver.validators.auth.user;

import lombok.Value;

/**
 * Data Transfer Object (DTO) for user validation data.
 *
 * <ul>
 *     <li>{@code username} - the user's unique identifier.</li>
 *     <li>{@code password} - the user's password.</li>
 * </ul>
 */
@Value
public class UserValidationDTO {
    String username;
    String password;
}
