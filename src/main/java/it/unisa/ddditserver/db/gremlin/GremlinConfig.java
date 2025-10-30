package it.unisa.ddditserver.db.gremlin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GremlinConfig {

    @Value("${GREMLIN_ENDPOINT}")
    private String endpoint;

    @Value("${GREMLIN_USERNAME}")
    private String username;

    @Value("${GREMLIN_KEY}")
    private String key;
}

