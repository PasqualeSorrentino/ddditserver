package it.unisa.ddditserver.db.gremlin;

import it.unisa.ddditserver.auth.dto.UserDTO;

public interface GremlinAuthInterface {
    void saveUser(UserDTO user);

    UserDTO findByUsername(String username);

    boolean existsByUsername(String username);
}
