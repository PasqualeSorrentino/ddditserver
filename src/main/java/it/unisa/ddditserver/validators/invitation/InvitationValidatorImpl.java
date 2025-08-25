package it.unisa.ddditserver.validators.invitation;

import it.unisa.ddditserver.db.gremlin.invitation.GremlinInvitationRepository;
import it.unisa.ddditserver.db.gremlin.versioning.repo.GremlinRepositoryRepository;
import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.invitation.exceptions.AlreadyInvitedException;
import it.unisa.ddditserver.subsystems.invitation.exceptions.InvitationException;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.validators.ValidationResult;
import it.unisa.ddditserver.validators.versioning.repo.RepositoryValidationDTO;
import it.unisa.ddditserver.validators.versioning.repo.RepositoryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component responsible for validating invitation data and checking for pending invitations.
 *
 * <p>This validator ensures that:
 * <ul>
 *     <li>The target repository exists and meets predefined validation rules.</li>
 *     <li>No pending invitation already exists for the given user-repository pair.</li>
 * </ul>
 *
 * It uses:
 * <ul>
 *     <li>{@link RepositoryValidator} to validate repository data</li>
 *     <li>{@link GremlinInvitationRepository} to check existence in the graph database</li>
 * </ul>
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-15
 */
@Component
public class InvitationValidatorImpl implements InvitationValidator {
    private final RepositoryValidator repositoryValidator;
    private final GremlinInvitationRepository gremlinService;
    private final GremlinRepositoryRepository gremlinRepositoryRepository;

    @Autowired
    public InvitationValidatorImpl(RepositoryValidator repositoryValidator,
                                   GremlinInvitationRepository gremlinService,
                                   GremlinRepositoryRepository gremlinRepositoryRepository) {
        this.repositoryValidator = repositoryValidator;
        this.gremlinService = gremlinService;
        this.gremlinRepositoryRepository = gremlinRepositoryRepository;
    }

    @Override
    public ValidationResult validateInvitation(InvitationValidationDTO invitationValidationDTO) {
        String fromUsername = invitationValidationDTO.getFromUsername();
        String toUsername = invitationValidationDTO.getToUsername();
        String repositoryName = invitationValidationDTO.getRepositoryName();

        if (fromUsername.equals(toUsername)) {
            throw new InvitationException("Can't invite yourself to any repository or accept an invitation from yourself");
        }

        if (gremlinRepositoryRepository.isContributor(new RepositoryDTO(repositoryName), new UserDTO(toUsername, null)) ||
                gremlinRepositoryRepository.isOwner(new RepositoryDTO(repositoryName), new UserDTO(toUsername, null))) {
            throw new InvitationException("Can't invite an owner or a contributor to their repository");
        }

        // Check if the repository is valid
        RepositoryValidationDTO repositoryValidationDTO = new RepositoryValidationDTO(repositoryName);
        ValidationResult repositoryValidationResult = repositoryValidator.validate(repositoryValidationDTO);
        if (!repositoryValidationResult.isValid()) {
            return repositoryValidationResult;
        }

        return ValidationResult.valid();
    }

    @Override
    public ValidationResult validatePendingInvitation(InvitationValidationDTO invitationValidationDTO, boolean exists) {
        String fromUsername = invitationValidationDTO.getFromUsername();
        String toUsername = invitationValidationDTO.getToUsername();
        String repositoryName = invitationValidationDTO.getRepositoryName();

        UserDTO fromUserDTO = new UserDTO(fromUsername, null);
        UserDTO toUserDTO = new UserDTO(toUsername, null);
        RepositoryDTO repositoryDTO = new RepositoryDTO(repositoryName);

        if (gremlinService.isAcceptedInvitation(fromUserDTO, toUserDTO, repositoryDTO)) {
            throw new AlreadyInvitedException("Invitation for " + repositoryName + " repository from " + toUsername + " has already been accepted");
        }

        if (exists) {
            if (!gremlinService.existsByUserAndRepository(fromUserDTO, toUserDTO, repositoryDTO)) {
                throw new AlreadyInvitedException("Invitation for " + repositoryName + " repository not found from " + fromUsername);
            }
        } else {
            if (gremlinService.existsByUserAndRepository(fromUserDTO, toUserDTO, repositoryDTO)) {
                throw new AlreadyInvitedException("Already invited " + toUsername + " to " + repositoryName + " repository");
            }
        }
        return ValidationResult.valid();
    }
}
