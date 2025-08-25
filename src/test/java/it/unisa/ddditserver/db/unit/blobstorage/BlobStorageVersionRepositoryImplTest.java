package it.unisa.ddditserver.db.unit.blobstorage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlobInputStream;
import it.unisa.ddditserver.db.blobstorage.BlobStorageConfig;
import it.unisa.ddditserver.db.blobstorage.versioning.BlobStorageVersionRepositoryImpl;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ATTENTION: at the moment due to time restrictions only tests for happy paths are available
@ExtendWith(MockitoExtension.class)
public class BlobStorageVersionRepositoryImplTest {
    @Mock
    private BlobStorageConfig config;

    @Mock
    private BlobContainerClient meshesContainerClient;

    @Mock
    private BlobContainerClient materialsContainerClient;

    @Mock
    private BlobClient blobClient;

    // Can't use @InjectMocks because findMeshByUrl()/findMaterialByUrl() test need to mock existsMeshByUrl()/existsMaterialByUrl()
    @Spy
    private BlobStorageVersionRepositoryImpl repository = new BlobStorageVersionRepositoryImpl(config);

    @BeforeEach
    public void setUp() throws Exception {
        Field meshesField = BlobStorageVersionRepositoryImpl.class.getDeclaredField("meshesContainerClient");
        meshesField.setAccessible(true);
        meshesField.set(repository, meshesContainerClient);

        Field materialsField = BlobStorageVersionRepositoryImpl.class.getDeclaredField("materialsContainerClient");
        materialsField.setAccessible(true);
        materialsField.set(repository, materialsContainerClient);
    }

    @Test
    // Happy path: Valid VersionDTO with a mesh file uploads successfully and returns the blob URL
    void saveMeshSuccess() throws Exception {
        VersionDTO version = new VersionDTO();
        version.setRepositoryName("repo");
        version.setBranchName("main");
        version.setResourceName("res");
        version.setVersionName("v1");

        MultipartFile mesh = mock(MultipartFile.class);
        when(mesh.getOriginalFilename()).thenReturn("mesh.fbx");
        when(mesh.getSize()).thenReturn(100L);
        when(mesh.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));
        when(mesh.getContentType()).thenReturn("application/octet-stream");

        version.setMesh(mesh);
        
        when(meshesContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("http://mock/mesh.fbx");

        String url = repository.saveMesh(version);

        assertEquals("http://mock/mesh.fbx", url);
        verify(blobClient, times(1)).upload(any(InputStream.class), eq(100L), eq(true));
        verify(blobClient, times(1)).setHttpHeaders(any());
    }

    @Test
    // Happy path: Valid VersionDTO with material files uploads successfully and returns the folder URL
    void saveMaterialSuccess() throws Exception {
        VersionDTO version = new VersionDTO();
        version.setRepositoryName("repo");
        version.setBranchName("main");
        version.setResourceName("res");
        version.setVersionName("v1");

        MultipartFile texture = mock(MultipartFile.class);
        when(texture.getOriginalFilename()).thenReturn("texture.png");
        when(texture.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));
        when(texture.getSize()).thenReturn(50L);
        when(texture.getContentType()).thenReturn("image/png");

        version.setMaterial(List.of(texture));

        when(materialsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("http://mock/material/");

        String folderUrl = repository.saveMaterial(version);

        assertEquals("http://mock/material/", folderUrl);
        verify(blobClient, times(1)).upload(any(InputStream.class), eq(50L), eq(false));
        verify(blobClient, times(1)).setHttpHeaders(any());
    }

    @Test
    // Happy path: Returns true when mesh exists at the given URL
    void existsMeshByUrlReturnsTrue() {
        String containerUrl = "http://mock/container";
        String meshPath = "path/to/mesh.fbx";
        String meshUrl = containerUrl + "/" + meshPath;

        when(meshesContainerClient.getBlobContainerUrl()).thenReturn(containerUrl);
        when(meshesContainerClient.getBlobClient(meshPath)).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        boolean exists = repository.existsMeshByUrl(meshUrl);

        assertTrue(exists);
        verify(meshesContainerClient).getBlobClient(meshPath);
        verify(blobClient).exists();
    }

    @Test
    // Happy path: Returns true when at least one material exists in the folder URL
    void existsMaterialByUrlReturnsTrue() {
        String containerUrl = "http://mock/container";
        String folderPath = "materials/";
        String materialFolderUrl = containerUrl + "/" + folderPath;

        BlobItem blobItem = mock(BlobItem.class);

        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(List.of(blobItem).iterator());

        when(materialsContainerClient.getBlobContainerUrl()).thenReturn(containerUrl);
        when(materialsContainerClient.listBlobsByHierarchy(anyString(), any(ListBlobsOptions.class), any(Duration.class)))
                .thenReturn(pagedIterable);

        boolean exists = repository.existsMaterialByUrl(materialFolderUrl);

        assertTrue(exists);
        verify(materialsContainerClient).listBlobsByHierarchy(anyString(), any(ListBlobsOptions.class), any(Duration.class));
    }

    @Test
    // Happy path: Returns false when no mesh exists at the given URL
    void existsMeshByUrlReturnsFalse() {
        String containerUrl = "http://mock/container";
        String meshPath = "path/to/mesh.fbx";
        String meshUrl = containerUrl + "/" + meshPath;

        when(meshesContainerClient.getBlobContainerUrl()).thenReturn(containerUrl);
        when(meshesContainerClient.getBlobClient(meshPath)).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(false);

        boolean exists = repository.existsMeshByUrl(meshUrl);

        assertFalse(exists);
        verify(meshesContainerClient).getBlobClient(meshPath);
        verify(blobClient).exists();
    }

    @Test
    // Happy path: Returns false when no material exists in the folder URL
    void existsMaterialByUrlReturnsFalse() {
        String containerUrl = "http://mock/container";
        String folderPath = "materials/";
        String materialFolderUrl = containerUrl + "/" + folderPath;

        BlobItem blobItem = mock(BlobItem.class);

        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(List.<BlobItem>of().iterator());

        when(materialsContainerClient.getBlobContainerUrl()).thenReturn(containerUrl);
        when(materialsContainerClient.listBlobsByHierarchy(anyString(), any(ListBlobsOptions.class), any(Duration.class)))
                .thenReturn(pagedIterable);

        boolean exists = repository.existsMaterialByUrl(materialFolderUrl);

        assertFalse(exists);
        verify(materialsContainerClient).listBlobsByHierarchy(anyString(), any(ListBlobsOptions.class), any(Duration.class));
    }

    @Test
    // Happy path: Finds mesh by valid URL and returns InputStream, content type, and mesh name
    void findMeshByUrlSuccess() {
        String meshUrl = "http://mock/container/path/to/mesh.fbx";

        BlobInputStream blobStream = mock(BlobInputStream.class);

        when(meshesContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.openInputStream()).thenReturn(blobStream);
        when(blobClient.getBlobName()).thenReturn("path/to/mesh.fbx");

        BlobProperties properties = mock(BlobProperties.class);
        when(properties.getContentType()).thenReturn("application/octet-stream");
        when(blobClient.getProperties()).thenReturn(properties);

        doReturn(true).when(repository).existsMeshByUrl(anyString());

        Triple<InputStream, String, String> result = repository.findMeshByUrl(meshUrl);

        assertEquals("mesh.fbx", result.getRight());
        assertEquals("application/octet-stream", result.getMiddle());
        assertEquals(blobStream, result.getLeft());
    }

    @Test
    void findMaterialByUrlSuccess() {
        String materialFolderUrl = "http://mock/container/materials/";

        BlobItem blobItem = mock(BlobItem.class);
        PagedIterable<BlobItem> pagedIterableForFind = mock(PagedIterable.class);
        
        BlobInputStream blobInputStream = mock(BlobInputStream.class);
        BlobProperties properties = mock(BlobProperties.class);

        when(blobClient.getBlobName()).thenReturn("path/to/texture.png");
        when(blobItem.getName()).thenReturn("materials/texture.png");
        when(pagedIterableForFind.stream()).thenReturn(Stream.of(blobItem));
        when(materialsContainerClient.getBlobContainerUrl()).thenReturn("http://mock/container");
        when(materialsContainerClient.listBlobsByHierarchy(anyString())).thenReturn(pagedIterableForFind);
        when(materialsContainerClient.getBlobClient("materials/texture.png")).thenReturn(blobClient);
        when(blobClient.openInputStream()).thenReturn(blobInputStream);
        when(blobClient.getProperties()).thenReturn(properties);
        when(properties.getContentType()).thenReturn("image/png");

        doReturn(true).when(repository).existsMaterialByUrl(anyString());

        List<Triple<InputStream, String, String>> results = repository.findMaterialByUrl(materialFolderUrl);

        assertEquals(1, results.size());
        Triple<InputStream, String, String> triple = results.get(0);
        assertEquals("texture.png", triple.getRight());
        assertEquals("image/png", triple.getMiddle());
        assertEquals(blobInputStream, triple.getLeft());
    }

    @Test
    // Happy path: Deletes mesh by valid URL without exception
    void deleteMeshByUrlSuccess() {
        String meshUrl = "http://mock/container/path/to/mesh.fbx";
        
        when(meshesContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        repository.deleteMeshByUrl(meshUrl);

        verify(blobClient, times(1)).delete();
    }

    @Test
    // Happy path: Deletes all materials in a valid folder URL without exception
    void deleteMaterialByUrlSuccess() {
        String materialFolderUrl = "http://mock/container/materials/";
        BlobItem blobItem = mock(BlobItem.class);
        when(blobItem.getName()).thenReturn("materials/texture.png");
        when(blobItem.isPrefix()).thenReturn(false);
        
        when(materialsContainerClient.getBlobClient("materials/texture.png")).thenReturn(blobClient);

        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(List.of(blobItem).iterator());

        when(materialsContainerClient.listBlobsByHierarchy(anyString())).thenReturn(pagedIterable);

        repository.deleteMaterialByUrl(materialFolderUrl);

        verify(blobClient, times(1)).delete();
    }
}

