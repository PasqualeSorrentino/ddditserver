package it.unisa.ddditserver.validators.versioning.resource;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating resource's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface ResourceValidator {
    /**
     * Validates the information of the given resource.
     *
     * @param resourceValidationDTO the resource data transfer object containing resource's information to validate
     * @return a ValidationResult indicating if the resource is valid or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateResource(ResourceValidationDTO resourceValidationDTO);

    /**
     * Validates whether the resource exists.
     *
     * @param resourceValidationDTO the resource data transfer object containing resource's information to validate
     * @param exists defines the kind of validation to perform; if true, the validation passes only if the resource exists, false otherwise
     * @return a ValidationResult indicating if the resource exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateExistence(ResourceValidationDTO resourceValidationDTO, boolean exists);

    /**
     * Validates whether the resource data is well-formed and exists.
     *
     * @param resourceValidationDTO the resource data transfer object containing resource's information to validate
     * @return a ValidationResult indicating if the resource data is well-formed and exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validate(ResourceValidationDTO resourceValidationDTO);
}
