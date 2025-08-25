package it.unisa.ddditserver.validators.versioning.branch;

import it.unisa.ddditserver.validators.ValidationResult;

/**
 * Interface for validating branch's data.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-13
 */
public interface BranchValidator {
    /**
     * Validates the information of the given branch.
     *
     * @param branchValidationDTO the branch data transfer object containing branch's information to validate
     * @return a ValidationResult indicating if the branch information is valid or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateBranch(BranchValidationDTO branchValidationDTO);

    /**
     * Validates whether the branch exists.
     *
     * @param branchValidationDTO the branch data transfer object containing branch's information to validate
     * @param exists defines the kind of validation to perform; if true, the validation passes only if the branch exists, false otherwise
     * @return a ValidationResult indicating if the branch exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validateExistence(BranchValidationDTO branchValidationDTO, boolean exists);

    /**
     * Validates whether the branch data is well-formed and exists.
     *
     * @param branchValidationDTO the branch data transfer object containing branch's information to validate
     * @return a ValidationResult indicating if the branch data is well-formed and exists or not.
     *         If not, the method throws a custom exception with the reason of the failure.
     */
    ValidationResult validate(BranchValidationDTO branchValidationDTO);
}
