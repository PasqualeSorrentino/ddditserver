package it.unisa.ddditserver.db.gremlin.auth;

import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.auth.exceptions.AuthException;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class GremlinAuthRepositoryImpl implements GremlinAuthRepository {
    private final GremlinConfig config;
    private Client client;

    public GremlinAuthRepositoryImpl(GremlinConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        String endpoint = config.getEndpoint();
        // Remove protocol prefix (wss://)
        if (endpoint.startsWith("wss://")) {
            endpoint = endpoint.substring(6);
        }
        // Remove port and path after colon
        int colonIndex = endpoint.indexOf(':');
        if (colonIndex != -1) {
            endpoint = endpoint.substring(0, colonIndex);
        }
        // Remove trailing slash if present
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }

        // Build cluster connection to Gremlin server
        Cluster cluster = Cluster.build()
                .addContactPoint(endpoint)
                .port(443)
                .credentials(config.getUsername(), config.getKey())
                .enableSsl(true)
                .serializer(Serializers.GRAPHSON_V2D0)
                .create();

        this.client = cluster.connect();
    }

    @Override
    public void saveUser(UserDTO user) {
        String username = user.getUsername();
        String password = user.getPassword();

        String query = "g.addV('user')" +
                ".property('repoId', repoId)" +
                ".property('username', username)" +
                ".property('password', password)";

        // The partition key configured in the Azure portal is repoId,
        // so for users who have not yet created a repository the chosen value is unassignedRepoId
        try {
            client.submit(query, Map.of(
                            "repoId", "unassignedRepoId",
                            "username", username,
                            "password", password));
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error saving user in Gremlin DB");
        }
    }

    @Override
    public UserDTO findByUser(UserDTO userDTO) {
        String username = userDTO.getUsername();

        String query = "g.V()." +
                "hasLabel('user')." +
                "has('username', username)." +
                "valueMap()";
        try {
            List<Result> results = client.submit(query, java.util.Map.of("username", username)).all().get();

            if (results.isEmpty()) {
                throw new AuthException("Error finding user by username in Gremlin DB");
            }

            if (results.size() > 1) {
                throw new AuthException("More than one user with the same username found in Gremlin DB");
            }

            @SuppressWarnings("unchecked")
            Map<String, List<Object>> props = (Map<String, List<Object>>) results.get(0).getObject();

            String password = props.get("password").get(0).toString();

            return new UserDTO(username, password);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error finding user by username in Gremlin DB");
        }
    }

    @Override
    public boolean existsByUser(UserDTO userDTO) {
        String username = userDTO.getUsername();

        String query = "g.V()." +
                "hasLabel('user')." +
                "has('username', username)";

        try {
            List<Result> results = client.submit(query, java.util.Map.of("username", username)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new AuthException("More than one user with the same username found in Gremlin DB");
            }

            return true;
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new AuthException("Error checking user existence in Gremlin DB");
        }
    }
}

