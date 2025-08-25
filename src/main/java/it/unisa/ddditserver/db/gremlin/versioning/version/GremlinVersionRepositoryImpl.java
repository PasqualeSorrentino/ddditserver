package it.unisa.ddditserver.db.gremlin.versioning.version;

import it.unisa.ddditserver.subsystems.versioning.dto.BranchDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import it.unisa.ddditserver.db.blobstorage.versioning.BlobStorageVersionRepository;
import it.unisa.ddditserver.db.cosmos.versioning.CosmosVersionRepository;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.subsystems.versioning.exceptions.version.VersionException;
import it.unisa.ddditserver.subsystems.versioning.service.version.NonClosingInputStreamResource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class GremlinVersionRepositoryImpl implements GremlinVersionRepository {
    private final GremlinConfig config;
    private final CosmosVersionRepository cosmosService;
    private final BlobStorageVersionRepository blobStorageService;
    private Client client;

    @Autowired
    public GremlinVersionRepositoryImpl(GremlinConfig config,
                                        CosmosVersionRepository cosmosService,
                                        BlobStorageVersionRepository blobStorageService) {
        this.config = config;
        this.cosmosService = cosmosService;
        this.blobStorageService = blobStorageService;
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
    public void saveVersion(VersionDTO versionDTO, boolean resourceType) {
        String repositoryName = versionDTO.getRepositoryName();
        String resourceName = versionDTO.getResourceName();
        String branchName = versionDTO.getBranchName();
        String versionName = versionDTO.getVersionName();

        String url = "";
        String cosmosDocumentUrl = "";

        try {
            if (resourceType) {
                url = blobStorageService.saveMesh(versionDTO);
            } else {
                url = blobStorageService.saveMaterial(versionDTO);
            }

            cosmosDocumentUrl = cosmosService.saveVersion(versionDTO, url);

            String query = "g.V().hasLabel('branch')" +
                    ".has('branchName', branchName)" +
                    ".as('branch')" +
                    ".in('HAS_BRANCH')" +
                    ".has('resourceName', resourceName)" +
                    ".in('CONTAINS')" +
                    ".has('repositoryName', repositoryName)" +
                    ".project('branchId', 'branchProps', 'repositoryId', 'repositoryProps')" +
                        ".by(select('branch').id())" +
                        ".by(select('branch').valueMap())" +
                        ".by(id())" +
                        ".by(valueMap())";

            List<Result> branchResults = client.submit(query, Map.of(
                    "branchName", branchName,
                    "resourceName", resourceName,
                    "repositoryName", repositoryName)).all().get();

            if (branchResults.isEmpty()) {
                // Roll back operations
                if (resourceType) {
                    blobStorageService.deleteMeshByUrl(url);
                } else {
                    blobStorageService.deleteMaterialByUrl(url);
                }

                cosmosService.deleteVersionByUrl(cosmosDocumentUrl);

                throw new VersionException("No branch found for " + resourceName + " resource in " + repositoryName +" repository");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> branchNodeMap = (Map<String, Object>) branchResults.get(0).getObject();
            Object branchId = branchNodeMap.get("branchId");

            query = "g.V(branchId)" +
                    ".out('HAS_VERSION')" +
                    ".union(" +
                    "identity()," +
                    "repeat(out('HAS_NEXT_VERSION')).emit())";

            List<Result> versionResults = client.submit(query, Map.of("branchId", branchId)).all().get();

            query = "g.addV('version')" +
                    ".property('repoId', repositoryName)" +
                    ".property('versionName', versionName)" +
                    ".property('cosmosDocumentUrl', cosmosDocumentUrl)";

            if (resourceType) {
                query += ".property('resourceType', 'mesh')";
            } else {
                query += ".property('resourceType', 'material')";
            }

            Result versionResult = client.submit(query,
                    Map.of(
                    "repositoryName", repositoryName,
                    "versionName", versionName,
                    "cosmosDocumentUrl", cosmosDocumentUrl)
            ).one();

            @SuppressWarnings("unchecked")
            Map<String, Object> versionNodeMap = (Map<String, Object>) versionResult.getObject();
            Object versionId = versionNodeMap.get("id");

            if (versionResults.isEmpty()) {
                query = "g.V(branchId)" +
                        ".addE('HAS_VERSION')" +
                        ".to(g.V(versionId))";

                client.submit(query, Map.of("branchId", branchId, "versionId", versionId)).all().get();
            } else {
                Result lastVersionResult = versionResults.get(versionResults.size() - 1);
                @SuppressWarnings("unchecked")
                Map<String, Object> lastVersionMap = (Map<String, Object>) lastVersionResult.getObject();
                Object lastVersionId = lastVersionMap.get("id");

                query= "g.V(lastVersionId)" +
                        ".addE('HAS_NEXT_VERSION')" +
                        ".to(g.V(versionId))";

                client.submit(query, Map.of("lastVersionId", lastVersionId, "versionId", versionId)).all().get();
            }

        } catch (Exception e) {
            // Roll back operations
            if (resourceType) {
                blobStorageService.deleteMeshByUrl(url);
            } else {
                blobStorageService.deleteMaterialByUrl(url);
            }

            cosmosService.deleteVersionByUrl(cosmosDocumentUrl);

            throw new VersionException("Error saving new version in Gremlin DB");
        }
    }

    @Override
    public boolean existsByVersion(VersionDTO versionDTO) {
        String repositoryName = versionDTO.getRepositoryName();
        String resourceName = versionDTO.getResourceName();
        String branchName = versionDTO.getBranchName();
        String versionName = versionDTO.getVersionName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)" +
                    ".out('HAS_BRANCH')" +
                    ".has('branchName', branchName)" +
                    ".out('HAS_VERSION')" +
                    ".union(identity(), repeat(out('HAS_NEXT_VERSION')).emit())" +
                    ".has('versionName', versionName)" +
                    ".valueMap()";

            List<Result> results = client.submit(query, Map.of(
                    "repositoryName", repositoryName,
                    "resourceName", resourceName,
                    "branchName", branchName,
                    "versionName", versionName)).all().get();

            if (results.isEmpty()) {
                return false;
            }

            if (results.size() > 1) {
                throw new VersionException("More than one version found in Gremlin DB");
            }

            return true;
        } catch (VersionException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error checking version existence in Gremlin DB");
        }
    }

    @Override
    public VersionDTO findVersionByBranch(VersionDTO versionDTO) {
        String repositoryName = versionDTO.getRepositoryName();
        String resourceName = versionDTO.getResourceName();
        String branchName = versionDTO.getBranchName();
        String versionName = versionDTO.getVersionName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)" +
                    ".out('HAS_BRANCH')" +
                    ".has('branchName', branchName)" +
                    ".out('HAS_VERSION')" +
                    ".union(identity(), repeat(out('HAS_NEXT_VERSION')).emit())" +
                    ".has('versionName', versionName)" +
                    ".valueMap()";

            List<Result> results = client.submit(query, Map.of(
                    "repositoryName", repositoryName,
                    "resourceName", resourceName,
                    "branchName", branchName,
                    "versionName", versionName)).all().get();

            if (results.isEmpty()) {
                throw new VersionException("Version node not found in Gremlin DB");
            }

            if (results.size() > 1) {
                throw new VersionException("More than one version found in Gremlin DB");
            }

            @SuppressWarnings("unchecked")
            Map<String, List<Object>> props = (Map<String, List<Object>>) results.get(0).getObject();
            String cosmosDocumentUrl = props.get("cosmosDocumentUrl").get(0).toString();

            if (cosmosDocumentUrl == null || cosmosDocumentUrl.isEmpty()) {
                throw new VersionException("Cosmos DB document not found in the version node of Gremlin DB");
            }

            return cosmosService.findVersionByUrl(cosmosDocumentUrl);
        } catch (VersionException e) {
            throw e;
        } catch (Exception e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error retrieving version from Gremlin graph");
        }
    }

    @Override
    public List<VersionDTO> findVersionsByBranch(BranchDTO branchDTO) {
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
                    ".has('branchName', branchName)" +
                    ".out('HAS_VERSION')" +
                    ".union(identity(), repeat(out('HAS_NEXT_VERSION')).emit())" +
                    ".valueMap()";

            List<Result> results = client.submit(query, Map.of(
                            "repositoryName", repositoryName,
                            "resourceName", resourceName,
                            "branchName", branchName)).all().get();

            List<VersionDTO> versions = new ArrayList<>();

            for (Result result : results) {
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> props = (Map<String, List<Object>>) result.getObject();
                String versionName = props.get("versionName").get(0).toString();
                versions.add(new VersionDTO(
                        null, null,
                        null, versionName,
                        null, null,
                        null, null,
                        null, null));
            }

            return versions;
        } catch (VersionException e) {
            throw e;
        } catch (Exception e) {
            throw new VersionException(e.getMessage());
        }
    }

    @Override
    public List<Pair<NonClosingInputStreamResource, String>> getFile(VersionDTO versionDTO) {
        String repositoryName = versionDTO.getRepositoryName();
        String resourceName = versionDTO.getResourceName();
        String branchName = versionDTO.getBranchName();
        String versionName = versionDTO.getVersionName();

        try {
            String query = "g.V()" +
                    ".hasLabel('repository')" +
                    ".has('repositoryName', repositoryName)" +
                    ".out('CONTAINS')" +
                    ".has('resourceName', resourceName)" +
                    ".out('HAS_BRANCH')" +
                    ".has('branchName', branchName)" +
                    ".out('HAS_VERSION')" +
                    ".union(identity(), repeat(out('HAS_NEXT_VERSION')).emit())" +
                    ".has('versionName', versionName)" +
                    ".valueMap()";

            List<Result> results = client.submit(query, Map.of(
                    "repositoryName", repositoryName,
                    "resourceName", resourceName,
                    "branchName", branchName,
                    "versionName", versionName)).all().get();

            if (results.isEmpty()) {
                throw new VersionException("Version node not found in Gremlin DB");
            }
            if (results.size() > 1) {
                throw new VersionException("More than one version found in Gremlin DB");
            }

            @SuppressWarnings("unchecked")
            Map<String, List<Object>> props = (Map<String, List<Object>>) results.get(0).getObject();
            String cosmosDocumentUrl = props.get("cosmosDocumentUrl").get(0).toString();
            String resourceType = props.get("resourceType").get(0).toString();

            if (cosmosDocumentUrl == null || cosmosDocumentUrl.isEmpty()) {
                throw new VersionException("Cosmos DB document not found in the version node of Gremlin DB");
            }

            String blobUrl = cosmosService.getBlobUrlByUrl(cosmosDocumentUrl);

            if (blobUrl == null || blobUrl.isEmpty()) {
                throw new VersionException("BLOB URL not found in the Cosmos DB document");
            }

            List<Pair<NonClosingInputStreamResource, String>> stream;

            if (resourceType.equalsIgnoreCase("mesh")) {
                Triple<InputStream, String, String> meshData = blobStorageService.findMeshByUrl(blobUrl);
                stream = List.of(Pair.of(
                        new NonClosingInputStreamResource(meshData.getLeft(), meshData.getRight(), meshData.getMiddle()),
                        meshData.getMiddle()
                ));
            } else {
                List<Triple<InputStream, String, String>> textures = blobStorageService.findMaterialByUrl(blobUrl);
                stream = textures.stream()
                        .map(p -> Pair.of(
                                new NonClosingInputStreamResource(p.getLeft(), p.getRight(), p.getMiddle()),
                                p.getMiddle()
                        ))
                        .toList();
            }

            return stream;
        } catch (VersionException e) {
            throw e;
        } catch (Exception e) {
            throw new VersionException("Error retrieving mesh file");
        }
    }
}