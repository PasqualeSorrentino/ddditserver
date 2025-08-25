package it.unisa.ddditserver.db.unit.cosmos.versioning;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import it.unisa.ddditserver.db.cosmos.versioning.CosmosVersionRepositoryImpl;
import it.unisa.ddditserver.subsystems.versioning.dto.version.CosmosVersionDTO;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ATTENTION: at the moment due to time restrictions only tests for happy paths are available
public class CosmosVersionRepositoryImplTest {
    @Mock
    CosmosContainer container;

    @InjectMocks
    private CosmosVersionRepositoryImpl repository;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field containerField = CosmosVersionRepositoryImpl.class.getDeclaredField("container");
        containerField.setAccessible(true);
        containerField.set(repository, container);
    }

    @Test
    // Happy path: saveVersion returns a valid URL when version is saved successfully
    void saveVersionReturnsDocumentUrl() {
        VersionDTO versionDTO = new VersionDTO(
                "repo1", "res1",
                "br1", "v1",
                "user", LocalDateTime.now(),
                "comment", List.of("tag1"),
                null, null
        );

        String blobUrl = "http://mock/blob/url";

        String resultUrl = repository.saveVersion(versionDTO, blobUrl);

        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains(versionDTO.getResourceName()));
    }

    @Test
   // Happy path: findVersionByUrl returns a VersionDTO with correct metadata
    void findVersionByUrlReturnsVersionDTO() {
        String versionId = "version-123";
        String partitionKey = "res1";
        String cosmosUrl = "https://dummy.documents.azure.com/dbs/metadata/colls/versions/docs/" + versionId + "?partitionKey=" + partitionKey;

        CosmosVersionDTO cosmosVersion = new CosmosVersionDTO(
                versionId, "res1", "res1", "v1",
                "user", LocalDateTime.now(), "comment", List.of("tag1"), "http://blob"
        );
        CosmosItemResponse<CosmosVersionDTO> mockResponse = mock(CosmosItemResponse.class);
        when(mockResponse.getItem()).thenReturn(cosmosVersion);

        when(container.readItem(eq(versionId), any(PartitionKey.class), eq(CosmosVersionDTO.class)))
                .thenReturn(mockResponse);

        VersionDTO result = repository.findVersionByUrl(cosmosUrl);

        assertNotNull(result);
        assertEquals(cosmosVersion.getVersionName(), result.getVersionName());
        assertEquals(cosmosVersion.getUsername(), result.getUsername());
    }

    @Test
    // Happy path: getBlobUrlByUrl returns the blob URL for a given Cosmos document URL
    void getBlobUrlByUrlReturnsBlobUrl() {
        String versionId = "version-123";
        String partitionKey = "res1";
        String cosmosUrl = "https://dummy.documents.azure.com/dbs/metadata/colls/versions/docs/" + versionId + "?partitionKey=" + partitionKey;

        CosmosVersionDTO cosmosVersion = new CosmosVersionDTO(
                versionId, "res1", "res1", "v1",
                "user", LocalDateTime.now(), "comment", List.of("tag1"), "http://blob"
        );
        CosmosItemResponse<CosmosVersionDTO> mockResponse = mock(CosmosItemResponse.class);
        when(mockResponse.getItem()).thenReturn(cosmosVersion);

        when(container.readItem(eq(versionId), any(PartitionKey.class), eq(CosmosVersionDTO.class)))
                .thenReturn(mockResponse);

        String blobUrl = repository.getBlobUrlByUrl(cosmosUrl);

        assertEquals(cosmosVersion.getBlobUrl(), blobUrl);
    }

    @Test
    // Happy path: deleteVersionByUrl successfully calls container.deleteItem without exceptions
    void deleteVersionByUrlSuccess() {
        String versionId = "version-123";
        String partitionKey = "res1";
        String cosmosUrl = "https://dummy.documents.azure.com/dbs/metadata/colls/versions/docs/" + versionId + "?partitionKey=" + partitionKey;

        assertDoesNotThrow(() -> repository.deleteVersionByUrl(cosmosUrl));
        verify(container, times(1))
                .deleteItem(eq(versionId), any(PartitionKey.class), any(CosmosItemRequestOptions.class));
    }
}
