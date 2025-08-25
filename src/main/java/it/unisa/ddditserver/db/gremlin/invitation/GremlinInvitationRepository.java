package it.unisa.ddditserver.db.gremlin.invitation;

import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.invitation.dto.InvitationDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import java.util.List;

/**
 * Repository interface for managing invitation-related operations
 * in a Gremlin-compatible graph database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface GremlinInvitationRepository {
    /**
     * Saves a new invitation in the graph database creating an edge between the two users with the repository name used as property of the edge.
     *
     * @param fromUserDTO the user who want to send the invitation
     * @param toUserDTO the user who will receive the invitation
     * @param repositoryDTO the repository specified in the invitation
     */
    void saveInvitation (UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO);

    /**
     * Checks if the user already sent an invitation to another one for the same repository.
     *
     * @param fromUserDTO the user who sent the invitation
     * @param toUserDTO the user who received the invitation
     * @param repositoryDTO the repository specified in the invitation
     * @return true if the user already sent an invitation to another one for the same repository, false otherwise
     */
    boolean existsByUserAndRepository (UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO);

    /**
     * Let a user accept an invitation for a repository from another user.
     *
     * @param fromUserDTO the user who sent the invitation
     * @param toUserDTO the user who received the invitation
     * @param repositoryDTO the repository specified in the invitation
     */
    void acceptInvitation(UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO);

    /**
     * Checks if a user accepted an invitation for a repository from another user.
     *
     * @param fromUserDTO the user who sent the invitation
     * @param toUserDTO the user who received the invitation
     * @param repositoryDTO the repository specified in the invitation
     * @return true if the user already accepted an invitation from another one for a specific repository, false otherwise
     */
    boolean isAcceptedInvitation(UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO);

    /**
     * Finds all pending invitations of a user.
     *
     * @param userDTO the user to search for
     * @return a list of {@link InvitationDTO} representing invitations to the user
     */
    List<InvitationDTO> findInvitationsByUser(UserDTO userDTO);
}
