package it.unisa.ddditserver.validators.versioning.resource;

import lombok.Value;

/**
 * Data Transfer Object (DTO) for resource validation data.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository containing the resource.</li>
 *     <li>{@code resourceName} - the name of the resource being validated.</li>
 * </ul>
 */
@Value
public class ResourceValidationDTO {
    String repositoryName;
    String resourceName;
}
