package it.unisa.ddditserver.subsystems.invitation.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an invitation to a repository.
 *
 * <ul>
 *     <li>{@code username} - the username of the user being invited.</li>
 *     <li>{@code repositoryName} - the name of the repository to which the user is invited.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {
    String fromUsername;
    String toUsername;
    String repositoryName;
}
