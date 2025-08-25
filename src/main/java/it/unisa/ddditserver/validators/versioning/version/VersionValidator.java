package it.unisa.ddditserver.validators.versioning.version;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating version's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface VersionValidator {
    /**
     * Validates the credentials of the given version.
     *
     * @param versionValidationDTO the version data transfer object containing version's information to validate
     * @param resourceType indicates a mesh version validation if true or a material version validation if false
     * @return a ValidationResult indicating if the version information is valid or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateVersion(VersionValidationDTO versionValidationDTO, boolean resourceType);

    /**
     * Validates whether the version exists.
     *
     * @param versionValidationDTO the version data transfer object containing version's information to validate
     * @param exists defines the kind of validation to perform; if true, the validation passes only if the version exists, false otherwise
     * @return a ValidationResult indicating if the version exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateExistence(VersionValidationDTO versionValidationDTO, boolean exists);

    /**
     * Validates whether the version data is well-formed and exists.
     *
     * @param versionValidationDTO the version data transfer object containing version's information to validate
     * @param resourceType indicates a mesh version validation if true or a material version validation if false
     * @return a ValidationResult indicating if the version data is well-formed and exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validate(VersionValidationDTO versionValidationDTO, boolean resourceType);
}
