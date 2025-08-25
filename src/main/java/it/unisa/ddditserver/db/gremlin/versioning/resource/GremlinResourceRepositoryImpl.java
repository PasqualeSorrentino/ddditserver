package it.unisa.ddditserver.db.gremlin.versioning.resource;

import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.subsystems.versioning.dto.RepositoryDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.ResourceDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.resource.ResourceException;
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
public class GremlinResourceRepositoryImpl implements GremlinResourceRepository {
    private final GremlinConfig config;
    private Client client;

    @Autowired
    public GremlinResourceRepositoryImpl(GremlinConfig config) {
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
    public void saveResource(ResourceDTO resourceDTO) {
        String repositoryName = resourceDTO.getRepositoryName();
        String resourceName = resourceDTO.getResourceName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".as('repo')" +
                    ".addV('resource')" +
                    ".property('repoId', repositoryName)" +
                    ".property('resourceName', resourceName)" +
                    ".as('res')" +
                    ".addE('CONTAINS')" +
                    ".from('repo')" +
                    ".to('res')";

            client.submit(query, Map.of("repositoryName", repositoryName, "resourceName", resourceName));
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new ResourceException("Error creating resource in Gremlin DB");
        }
    }

    @Override
    public boolean existsByRepository(ResourceDTO resourceDTO) {
        String repositoryName = resourceDTO.getRepositoryName();
        String resourceName = resourceDTO.getResourceName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)";

            List<Result> results = client.submit(query, Map.of("repositoryName", repositoryName, "resourceName", resourceName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new ResourceException("More than one resource with the same name in the same repository found in Gremlin DB");
            }

            return true;
        } catch (ResourceException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new ResourceException("Error checking resource existence in Gremlin DB");
        }
    }

    @Override
    public List<ResourceDTO> findResourcesByRepository(RepositoryDTO repositoryDTO) {
        String repositoryName = repositoryDTO.getRepositoryName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".hasLabel('resource')" +
                    ".valueMap()";

            List<Result> results = client.submit(query, Map.of("repositoryName", repositoryName)).all().get();
            List<ResourceDTO> resources = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> props = (Map<String, List<Object>>) result.getObject();
                String resourceName = props.get("resourceName").get(0).toString();
                resources.add(new ResourceDTO(repositoryName, resourceName));
            }

            return resources;
        } catch (ResourceException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new ResourceException("Error finding resources by repository in Gremlin DB");
        }
    }
}
