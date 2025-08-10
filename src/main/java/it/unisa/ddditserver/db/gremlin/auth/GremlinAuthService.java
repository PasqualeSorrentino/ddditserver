package it.unisa.ddditserver.db.gremlin.auth;

import it.unisa.ddditserver.auth.dto.UserDTO;

public interface GremlinAuthService {
    void saveUser(UserDTO user);

    UserDTO findByUsername(String username);

    boolean existsByUsername(String username);
}
