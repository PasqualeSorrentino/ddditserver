package it.unisa.ddditserver.subsystems.invitation.service;

import it.unisa.ddditserver.subsystems.invitation.dto.InvitationDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for invitation-related operations.
 * Provides methods to send, accept, and list invitations for authenticated users.
 *
 * Author: Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-16
 */
public interface InvitationService {

    /**
     * Sends a new invitation to a user on behalf of the authenticated user.
     *
     * @param invitationDTO the data transfer object containing invitation details
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with relevant response data
     */
    ResponseEntity<Map<String, String>> sendInvitation(InvitationDTO invitationDTO, String token);

    /**
     * Accepts an invitation on behalf of the authenticated user.
     *
     * @param invitationDTO the data transfer object containing invitation details to accept
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with relevant response data
     */
    ResponseEntity<Map<String, String>> acceptInvitation(InvitationDTO invitationDTO, String token);

    /**
     * Retrieves a list of pending invitations for the authenticated user.
     *
     * @param token the JWT token representing the authenticated user
     * @return a ResponseEntity containing a map with the list of pending invitations
     */
    ResponseEntity<Map<String, Object>> listPendingInvitations(String token);
}
