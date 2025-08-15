package it.unisa.ddditserver.subsystems.auth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a blacklisted authentication token.
 *
 * <ul>
 *     <li>{@code id} - the unique identifier of the blacklisted token entry in CosmosDB.</li>
 *     <li>{@code tokenId} - the identifier of the token that has been blacklisted.</li>
 *     <li>{@code ttl} - the time-to-live of the blacklisted token in seconds.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedTokenDTO {
    private String id;
    private String tokenId;
    private Integer ttl;
}
