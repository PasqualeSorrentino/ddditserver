package it.unisa.ddditserver.validators.versioning.repo;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating repository's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface RepositoryValidator {
    /**
     * Validates the information of the given repository.
     *
     * @param repositoryValidationDTO the repository data transfer object containing repository's information to validate
     * @return a ValidationResult indicating if the repository information is valid or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateRepository(RepositoryValidationDTO repositoryValidationDTO);

    /**
     * Validates whether the repository exists.
     *
     * @param repositoryValidationDTO the repository data transfer object containing repository's information to validate
     * @param exists defines the kind of validation to perform; if true, the validation passes only if the repository exists, false otherwise
     * @return a ValidationResult indicating if the repository exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateExistence(RepositoryValidationDTO repositoryValidationDTO, boolean exists);

    /**
     * Validates whether the repository data is well-formed and exists.
     *
     * @param repositoryValidationDTO the repository data transfer object containing repository's information to validate
     * @return a ValidationResult indicating if the repository data is well-formed and exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validate(RepositoryValidationDTO repositoryValidationDTO);
}
