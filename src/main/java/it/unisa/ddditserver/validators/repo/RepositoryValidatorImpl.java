package it.unisa.ddditserver.validators.repo;

import it.unisa.ddditserver.db.gremlin.auth.GremlinAuthService;
import it.unisa.ddditserver.db.gremlin.versioning.repo.GremlinRepositoryService;
import it.unisa.ddditserver.validators.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

/**
 * Component responsible for validating repository data and checking its existence.
 *
 * This validator verifies that the repository name respect predefined pattern and length constraints.
 * It also checks if a repository already exists using the {@link GremlinAuthService}.
 * Validations include:
 * <ul>
 *     <li>Repository name must be 3-30 characters long, containing only letters, digits, underscores, and dots.</li>
 *     <li>Existence of the repository in the graph database.</li>
 * </ul>
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
@Component
public class RepositoryValidatorImpl implements RepositoryValidator {
    private final GremlinRepositoryService gremlinService;

    @Autowired
    public RepositoryValidatorImpl(GremlinRepositoryService gremlinService) {
        this.gremlinService = gremlinService;
    }

    private static final int REPOSITORY_NAME_MIN_LENGTH = 3;
    private static final int REPOSITORY_NAME_MAX_LENGTH = 30;

    private static final Pattern REPOSITORY_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    private boolean isValidName(String repositoryName) {
        if (repositoryName == null) return false;
        int length = repositoryName.length();
        return length >= REPOSITORY_NAME_MIN_LENGTH &&
                length <= REPOSITORY_NAME_MAX_LENGTH &&
                REPOSITORY_NAME_PATTERN.matcher(repositoryName).matches();
    }

    @Override
    public ValidationResult validateName(RepositoryValidationDTO repositoryValidationDTO) {
        if (!isValidName(repositoryValidationDTO.getRepositoryName())) {
            return ValidationResult.invalid("Repository name must be 3-30 chars long, letters, digits, _ and . only");
        }
        return ValidationResult.valid();
    }

    @Override
    public ValidationResult validateExistence(RepositoryValidationDTO repositoryValidationDTO, boolean exists) {
        if (exists) {
            if (!gremlinService.existsByRepositoryName(repositoryValidationDTO.getRepositoryName())) {
                return ValidationResult.invalid("Repository does not exist");
            }
        }
        else {
            if (gremlinService.existsByRepositoryName(repositoryValidationDTO.getRepositoryName())) {
                return ValidationResult.invalid("Repository already exists");
            }
        }
        return ValidationResult.valid();
    }
}
