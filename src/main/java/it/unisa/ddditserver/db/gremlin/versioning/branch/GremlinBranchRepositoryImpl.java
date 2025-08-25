package it.unisa.ddditserver.db.gremlin.versioning.branch;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.branch.BranchException;
import it.unisa.ddditserver.subsystems.versioning.exceptions.version.VersionException;
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
public class GremlinBranchRepositoryImpl implements GremlinBranchRepository {
    private final GremlinConfig config;
    private Client client;

    @Autowired
    public GremlinBranchRepositoryImpl(GremlinConfig config) {
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
    public void saveBranch(BranchDTO branchDTO) {
        String repositoryName = branchDTO.getRepositoryName();
        String resourceName = branchDTO.getResourceName();
        String branchName = branchDTO.getBranchName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)" +
                    ".as('r')" +
                    ".addV('branch')" +
                    ".property('repoId', repositoryName)" +
                    ".property('branchName', branchName)" +
                    ".as('b')" +
                    ".addE('HAS_BRANCH')" +
                    ".from('r')" +
                    ".to('b')";

            client.submit(query, Map.of(
                    "repositoryName", repositoryName,
                    "resourceName", resourceName,
                    "branchName", branchName));
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error creating mesh version in Gremlin DB");
        }
    }

    @Override
    public boolean existsByResource(BranchDTO branchDTO) {
        String repositoryName = branchDTO.getRepositoryName();
        String resourceName = branchDTO.getResourceName();
        String branchName = branchDTO.getBranchName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)" +
                    ".out('HAS_BRANCH')" +
                    ".has('branchName', branchName)";

            List<Result> results = client.submit(query, Map.of(
                    "repositoryName", repositoryName,
                    "resourceName", resourceName,
                    "branchName", branchName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new VersionException("More than one branch in the same resource in the same repository found in Gremlin DB");
            }

            return true;
        } catch (BranchException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error checking branch existence in Gremlin DB");
        }
    }

    @Override
    public List<BranchDTO> findBranchesByResource(ResourceDTO resourceDTO) {
        String repositoryName = resourceDTO.getRepositoryName();
        String resourceName = resourceDTO.getResourceName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)" +
                    ".out('HAS_BRANCH')" +
                    ".valueMap()";

            List<Result> results = client.submit(query, Map.of("repositoryName", repositoryName, "resourceName", resourceName)).all().get();

            List<BranchDTO> branches = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> props = (Map<String, List<Object>>) result.getObject();
                String branchName = props.get("branchName").get(0).toString();
                branches.add(new BranchDTO(repositoryName, resourceName, branchName));
            }

            return branches;
        } catch (BranchException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error finding branches by resource in Gremlin DB");
        }
    }
}
