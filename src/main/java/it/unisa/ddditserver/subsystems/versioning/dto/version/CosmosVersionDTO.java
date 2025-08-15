package it.unisa.ddditserver.subsystems.versioning.dto.version;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a version metadata to store in CosmosDB.
 *
 * <ul>
 *     <li>{@code id} - the unique document ID in CosmosDB, same as {@code versionName}.</li>
 *     <li>{@code resourceName} - the name of the resource, used as the Partition Key in CosmosDB.</li>
 *     <li>{@code versionName} - the name of the version.</li>
 *     <li>{@code username} - the username of the user who pushed the version.</li>
 *     <li>{@code pushedAt} - the date and time when the version was pushed.</li>
 *     <li>{@code comment} - an optional comment describing the version.</li>
 *     <li>{@code tags} - a list of tags associated with the version.</li>
 *     <li>{@code blobUrl} - the URL pointing to the version stored in BLOB Storage.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CosmosVersionDTO {
    private String id;
    private String resourceName;
    private String versionName;
    private String username;
    private LocalDateTime pushedAt;
    private String comment;
    private List<String> tags;
    private String blobUrl;
}
