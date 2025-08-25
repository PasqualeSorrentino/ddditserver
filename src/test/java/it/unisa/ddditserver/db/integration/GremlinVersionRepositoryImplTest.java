package it.unisa.ddditserver.db.integration;

import it.unisa.ddditserver.db.blobstorage.BlobStorageConfig;
import it.unisa.ddditserver.db.blobstorage.versioning.BlobStorageVersionRepositoryImpl;
import it.unisa.ddditserver.db.cosmos.CosmosConfig;
import it.unisa.ddditserver.db.cosmos.versioning.CosmosVersionRepositoryImpl;
import it.unisa.ddditserver.db.gremlin.GremlinConfig;
import it.unisa.ddditserver.db.gremlin.versioning.version.GremlinVersionRepositoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GremlinVersionRepositoryImplTest {
    @Mock
    private GremlinConfig gremlinConfig;

    @Mock
    private CosmosConfig cosmosConfig;

    @Mock
    private BlobStorageConfig blobStorageConfig;

    @Autowired
    private CosmosVersionRepositoryImpl cosmosRepository;

    @Autowired
    private BlobStorageVersionRepositoryImpl blobRepository;

    @Autowired
    private GremlinVersionRepositoryImpl repository;

    // ATTENTION: currently, none of these environment variables exist on GitHub or on the VM,
    // mocking configs is necessary to ensure repository operations in the test environment are separated from production
    @BeforeAll
    void setUp() {
        when(gremlinConfig.getEndpoint()).thenReturn(System.getenv("TEST_GREMLIN_ENDPOINT"));
        when(gremlinConfig.getUsername()).thenReturn(System.getenv("TEST_GREMLIN_USERNAME"));
        when(gremlinConfig.getKey()).thenReturn(System.getenv("TEST_GREMLIN_KEY"));

        when(cosmosConfig.getEndpoint()).thenReturn(System.getenv("TEST_COSMOS_SQL_ENDPOINT"));
        when(cosmosConfig.getKey()).thenReturn(System.getenv("TEST_COSMOS_SQL_KEY"));
        when(cosmosConfig.getDatabaseName()).thenReturn(System.getenv("TEST_COSMOS_SQL_DATABASE"));
        when(cosmosConfig.getVersionsContainerName()).thenReturn(System.getenv("TEST_COSMOS_SQL_CONTAINER_VERSIONS"));
        when(cosmosConfig.getTokenBlacklistContainerName()).thenReturn(System.getenv("TEST_COSMOS_SQL_CONTAINER_TOKEN_BLACKLIST"));

        when(blobStorageConfig.getConnectionString()).thenReturn(System.getenv("TEST_BLOB_STORAGE_CONNECTION_STRING"));
        when(blobStorageConfig.getConnectionString()).thenReturn(System.getenv("TEST_BLOB_STORAGE_CONTAINER_MESHES"));
        when(blobStorageConfig.getConnectionString()).thenReturn(System.getenv("BLOB_STORAGE_CONTAINER_MATERIALS"));
    }
}
