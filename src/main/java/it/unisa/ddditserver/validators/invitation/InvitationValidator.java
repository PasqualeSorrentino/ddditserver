package it.unisa.ddditserver.validators.invitation;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating invitation data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-15
 */
public interface InvitationValidator {
    /**
     * Validates the provided invitation.
     *
     * @param invitationValidationDTO the invitation data transfer object containing invitation information to validate
     * @return a ValidationResult indicating if the invitation is valid or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateInvitation(InvitationValidationDTO invitationValidationDTO);

    /**
     * Validates whether an invitation has already been sent to a specific user for a specific repository.
     *
     * @param invitationValidationDTO the invitation data transfer object containing invitation information to validate
     * @param exists defines the kind of validation to perform; if true, the validation passes only if the invitation exists, false otherwise
     * @return a ValidationResult indicating if a pending invitation exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validatePendingInvitation(InvitationValidationDTO invitationValidationDTO, boolean exists);
}
