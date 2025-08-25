package it.unisa.ddditserver.validators.versioning.repo;

import lombok.Value;

/**
 * Data Transfer Object (DTO) for repository validation data.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository being validated.</li>
 * </ul>
 */
@Value
public class RepositoryValidationDTO {
    String repositoryName;
}
