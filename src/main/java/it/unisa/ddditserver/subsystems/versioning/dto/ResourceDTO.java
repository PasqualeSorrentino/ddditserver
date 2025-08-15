package it.unisa.ddditserver.subsystems.versioning.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a resource within a repository.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository containing the resource.</li>
 *     <li>{@code resourceName} - the name of the resource.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO {
    private String repositoryName;
    private String resourceName;
}
