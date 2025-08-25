package it.unisa.ddditserver.validators.versioning.version;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import lombok.Value;

/**
 * Data Transfer Object (DTO) for version validation data.
 *
 * <ul>
 *     <li>{@code repositoryName} - the name of the repository containing the resource.</li>
 *     <li>{@code resourceName} - the name of the resource associated with the version.</li>
 *     <li>{@code branchName} - the name of the branch where the version resides.</li>
 *     <li>{@code versionName} - the name of the version being validated.</li>
 *     <li>{@code comment} - an optional comment describing the version.</li>
 *     <li>{@code mesh} - the uploaded mesh file for the version.</li>
 *     <li>{@code material} - a list of uploaded material files for the version.</li>
 * </ul>
 */
@Value
public class VersionValidationDTO {
    String repositoryName;
    String resourceName;
    String branchName;
    String versionName;
    String comment;
    MultipartFile mesh;
    List<MultipartFile> material;
}
