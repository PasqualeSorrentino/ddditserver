package it.unisa.ddditserver.subsystems.versioning.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a branch in a versioning system.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository containing the branch.</li>
 *     <li>{@code resourceName} - the name of the resource the branch is associated with.</li>
 *     <li>{@code branchName} - the name of the branch.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {
    private String repositoryName;
    private String resourceName;
    private String branchName;
}
