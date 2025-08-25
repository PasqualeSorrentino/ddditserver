package it.unisa.ddditserver.db.gremlin.invitation;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.subsystems.auth.dto.UserDTO;
import it.unisa.ddditserver.subsystems.invitation.dto.InvitationDTO;
import it.unisa.ddditserver.subsystems.invitation.exceptions.InvitationException;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.repo.RepositoryException;
import jakarta.annotation.PostConstruct;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class GremlinInvitationRepositoryImpl implements GremlinInvitationRepository {
    private final GremlinConfig config;
    private Client client;

    @Autowired
    public GremlinInvitationRepositoryImpl(GremlinConfig config) {
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
    public void saveInvitation(UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO) {
        String fromUsername = fromUserDTO.getUsername();
        String toUsername = toUserDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".has('user', 'username', fromUsername)" +
                ".addE('HAS_INVITED')" +
                ".property('repositoryName', repositoryName)" +
                ".property('status', 'pending')" +
                ".to(g.V().has('user', 'username', toUsername))";

        try {
            client.submit(query, Map.of(
                    "fromUsername", fromUsername,
                    "toUsername", toUsername,
                    "repositoryName", repositoryName));
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new InvitationException("Error saving invitation to Gremlin DB");
        }
    }

    @Override
    public boolean existsByUserAndRepository(UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO) {
        String fromUsername = fromUserDTO.getUsername();
        String toUsername = toUserDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        String query = "g.V()" +
                ".has('user', 'username', fromUsername)" +
                ".outE('HAS_INVITED')" +
                ".has('repositoryName', repositoryName)" +
                ".where(__.inV().has('user', 'username', toUsername))";

        try {
            List<Result> results = client.submit(query, Map.of(
                    "fromUsername", fromUsername,
                    "toUsername", toUsername,
                    "repositoryName", repositoryName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new InvitationException("More than one invitation with the same information found in Gremlin DB");
            }

            return true;
        } catch (InvitationException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new InvitationException("Error checking for existing invitation in Gremlin DB");
        }
    }

    @Override
    public void acceptInvitation(UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO) {
        String fromUsername = fromUserDTO.getUsername();
        String toUsername = toUserDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        try {
            String query = "g.V()" +
                    ".hasLabel('user')" +
                    ".has('username', fromUsername)" +
                    ".outE('HAS_INVITED')" +
                    ".as('e')" +
                    ".inV().has('username', toUsername)" +
                    ".select('e')" +
                    ".has('repositoryName', repositoryName)" +
                    ".property('status', 'accepted')";

            client.submit(query, Map.of(
                    "fromUsername", fromUsername,
                    "toUsername", toUsername,
                    "repositoryName", repositoryName));
        } catch (InvitationException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new InvitationException("Error updating invitation status in Gremlin DB");
        }
    }

    @Override
    public boolean isAcceptedInvitation(UserDTO fromUserDTO, UserDTO toUserDTO, RepositoryDTO repositoryDTO) {
        String fromUsername = fromUserDTO.getUsername();
        String toUsername = toUserDTO.getUsername();
        String repositoryName = repositoryDTO.getRepositoryName();

        try {
            String query = "g.V().hasLabel('user').has('username', fromUsername)" +
                    ".outE('HAS_INVITED').has('repositoryName', repositoryName).has('status', 'accepted')" +
                    ".inV().has('username', toUsername)";


            List<Result> results = client.submit(query, Map.of("fromUsername", fromUsername,
                            "toUsername", toUsername,
                            "repositoryName", repositoryName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new InvitationException("More than one invitation found in Gremlin DB");
            }

            return true;
        } catch (InvitationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvitationException("Error checking invitation status in Gremlin DB");
        }
    }

    @Override
    public List<InvitationDTO> findInvitationsByUser(UserDTO userDTO) {
        String toUsername = userDTO.getUsername();

        String query = "g.V()" +
                ".hasLabel('user')" +
                ".has('username', toUsername)" +
                ".inE('HAS_INVITED')" +
                ".has('status','pending')" +
                ".as('e')" +
                ".outV()" +
                ".as('from')" +
                ".project('fromUsername','repositoryName')" +
                "  .by(select('from').values('username'))" +
                "  .by(select('e').values('repositoryName'))";

        try {
            List<Result> results = client.submit(query, Map.of("toUsername", toUsername)).all().get();
            List<InvitationDTO> invitations = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result.getObject();

                String fromUsername = map.get("fromUsername").toString();
                String repositoryName = map.get("repositoryName").toString();

                invitations.add(new InvitationDTO(fromUsername, repositoryName));
            }

            return invitations;
        } catch (InvitationException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException("Error finding pending invitations in Gremlin DB");
        }
    }
}
