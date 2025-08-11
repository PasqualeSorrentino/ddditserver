package it.unisa.ddditserver.auth.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String username;
    private String password;
}
