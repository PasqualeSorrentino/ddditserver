package it.unisa.ddditserver.validators.repo;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating repository's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
public interface RepositoryValidator {
    /**
     * Validates the name of the given repository.
     *
     * @param repositoryValidationDTO the repository data transfer object containing repository's information to validate
     * @return a ValidationResult indicating if the name of the repository is valid or not, including an optional message
     */
    ValidationResult validateName(RepositoryValidationDTO repositoryValidationDTO);

    /**
     * Validates whether the repository exists.
     *
     * @param repositoryValidationDTO the repository data transfer object containing repository's information to validate
     * @param exists define the kind of validation to perform, if it is true then the method consider the validation passed only if the repository exists, false otherwise
     * @return a ValidationResult indicating if the repository exists or not, including an optional message
     */
    ValidationResult validateExistence(RepositoryValidationDTO repositoryValidationDTO, boolean exists);
}
