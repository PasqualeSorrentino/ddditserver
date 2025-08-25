package it.unisa.ddditserver.validators.invitation;

import lombok.Value;

/**
 * Data Transfer Object (DTO) for invitation validation data.
 *
 * <ul>
 *     <li>{@code fromUsername} - the user who send the invitation.</li>
 *     <li>{@code toUsername} - the user who will receive the invitation.</li>
 *     <li>{@code repositoryName} - the name of the repository to which the user is invited.</li>
 * </ul>
 */
@Value
public class InvitationValidationDTO {
    String fromUsername;
    String toUsername;
    String repositoryName;

}
