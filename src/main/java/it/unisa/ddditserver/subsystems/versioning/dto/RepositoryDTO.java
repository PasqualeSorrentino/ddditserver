package it.unisa.ddditserver.subsystems.versioning.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a repository.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDTO {
    private String repositoryName;
}
