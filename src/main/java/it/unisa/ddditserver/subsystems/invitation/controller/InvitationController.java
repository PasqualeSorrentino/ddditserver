package it.unisa.ddditserver.subsystems.invitation.controller;

import it.unisa.ddditserver.subsystems.invitation.dto.InvitationDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller interface for invitation endpoints.
 *
 * Provides operations to send, accept, and list invitations
 * within the repository management system.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-11
 */
public interface InvitationController {

    /**
     * Handles the request to send an invitation to a user for a repository.
     *
     * @param invitationDTO the invitation data transfer object containing invitation details
     * @param request the HTTP servlet request object
     * @return a ResponseEntity indicating the result of the invitation sending operation
     */
    ResponseEntity<?> sendInvitation(@RequestBody InvitationDTO invitationDTO, HttpServletRequest request);

    /**
     * Handles the request to accept an invitation to a repository.
     *
     * @param invitationDTO the invitation data transfer object containing invitation details
     * @param request the HTTP servlet request object
     * @return a ResponseEntity indicating the result of the invitation acceptance operation
     */
    ResponseEntity<?> acceptInvitation(@RequestBody InvitationDTO invitationDTO, HttpServletRequest request);

    /**
     * Handles the request to list all pending invitations.
     *
     * @param request the HTTP servlet request object
     * @return a ResponseEntity containing the list of pending invitations
     */
    ResponseEntity<?> listPendingInvitations(HttpServletRequest request);
}
