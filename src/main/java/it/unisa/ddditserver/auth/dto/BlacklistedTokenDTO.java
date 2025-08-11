package it.unisa.ddditserver.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedTokenDTO {
    private String id;
    private String tokenId;
}