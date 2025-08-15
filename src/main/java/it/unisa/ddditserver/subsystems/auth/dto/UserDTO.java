package it.unisa.ddditserver.subsystems.auth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing user credentials.
 *
 * <ul>
 *     <li>{@code username} - the unique identifier for the user'.</li>
 *     <li>{@code password} - the user's password. This field should be handled securely and never logged in plain text.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    String username;
    String password;
}
