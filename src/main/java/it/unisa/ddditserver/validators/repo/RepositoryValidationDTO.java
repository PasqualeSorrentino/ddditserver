package it.unisa.ddditserver.validators.repo;

import lombok.Value;

/**
 * Data Transfer Object for repository validation data.
 *
 * Contains the repository's name to be validated.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-12
 */
@Value
public class RepositoryValidationDTO {
    String repositoryName;
}
