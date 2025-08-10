package it.unisa.ddditserver.validators.implementations.user;
import lombok.Value;

@Value
public class UserValidationDTO {
    String username;
    String password;
}
