package it.unisa.ddditserver.validators.versioning.branch;

import lombok.Value;

/**
 * Data Transfer Object (DTO) for branch validation data.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository containing the branch.</li>
 *     <li>{@code resourceName} - the name of the resource the branch is associated with.</li>
 *     <li>{@code branchName} - the name of the branch being validated.</li>
 * </ul>
 */
@Value
public class BranchValidationDTO {
    String repositoryName;
    String resourceName;
    String branchName;
}
