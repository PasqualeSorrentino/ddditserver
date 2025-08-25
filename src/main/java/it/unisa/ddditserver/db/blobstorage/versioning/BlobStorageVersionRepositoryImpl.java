package it.unisa.ddditserver.db.blobstorage.versioning;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import it.unisa.ddditserver.db.blobstorage.BlobStorageConfig;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.version.VersionException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Repository
public class BlobStorageVersionRepositoryImpl implements BlobStorageVersionRepository {
    private final BlobStorageConfig config;
    private BlobContainerClient meshesContainerClient;
    private BlobContainerClient materialsContainerClient;

    @Autowired
    public BlobStorageVersionRepositoryImpl(BlobStorageConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        // Build client connection to BLOB storage server
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(config.getConnectionString())
                .buildClient();

        this.meshesContainerClient = blobServiceClient.getBlobContainerClient(config.getMeshesContainer());
        this.materialsContainerClient = blobServiceClient.getBlobContainerClient(config.getMaterialsContainer());
    }

    @Override
    public String saveMesh(VersionDTO versionDTO) {
        String repoFolder = versionDTO.getRepositoryName();
        String branchFolder = versionDTO.getBranchName();
        String resourceFolder = versionDTO.getResourceName();
        String versionName = versionDTO.getVersionName();
        MultipartFile mesh = versionDTO.getMesh();

        // BLOB path: repoName/resourceName/branchName/versionName/fileName
        String blobPath = repoFolder + "/" + resourceFolder + "/" + branchFolder + "/" + versionName + "/" + mesh.getOriginalFilename();

        try {
            BlobClient blobClient = meshesContainerClient.getBlobClient(blobPath);

            try (InputStream dataStream = mesh.getInputStream()) {
                blobClient.upload(dataStream, mesh.getSize(), true);
            }

            BlobHttpHeaders headers = new BlobHttpHeaders()
                    .setContentType(mesh.getContentType() != null ? mesh.getContentType() : "application/octet-stream");
            blobClient.setHttpHeaders(headers);

            return blobClient.getBlobUrl();
        } catch (BlobStorageException | IOException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error during mesh saving in BLOB storage for " + mesh.getOriginalFilename() + " file");
        }
    }

    @Override
    public String saveMaterial(VersionDTO versionDTO) {
        String repoFolder = versionDTO.getRepositoryName();
        String branchFolder = versionDTO.getBranchName();
        String resourceFolder = versionDTO.getResourceName();
        String versionName = versionDTO.getVersionName();
        List<MultipartFile> material = versionDTO.getMaterial();

        for (MultipartFile texture : material) {
            // BLOB path: repoName/resourceName/branchName/versionName/textureFileName
            String blobPath = repoFolder + "/" + resourceFolder + "/" + branchFolder + "/" + versionName + "/" + texture.getOriginalFilename();

            BlobClient blobClient = materialsContainerClient.getBlobClient(blobPath);

            try {
                InputStream dataStream = texture.getInputStream();
                blobClient.upload(dataStream, texture.getSize(), false);

                BlobHttpHeaders headers = new BlobHttpHeaders()
                        .setContentType(texture.getContentType() != null ? texture.getContentType() : "application/octet-stream");
                blobClient.setHttpHeaders(headers);
            } catch (BlobStorageException | IOException e) {
                // If it is necessary use a RuntimeException for more detailed debug
                throw new VersionException("Error during material saving in BLOB storage for " + texture.getOriginalFilename() + " file");
            }
        }

        BlobClient folderClient;

        try {
            String folderBlobPath = repoFolder + "/"  + resourceFolder + "/" + branchFolder + "/" + versionName;
            folderClient = materialsContainerClient.getBlobClient(folderBlobPath);
        } catch (BlobStorageException e) {
            throw new VersionException("Error during material saving in BLOB storage");
        }

        return folderClient.getBlobUrl();
    }

    @Override
    public boolean existsMeshByUrl(String meshUrl) {
        if (meshUrl == null || meshUrl.isEmpty()) {
            throw new VersionException("Mesh URL can't be null or empty");
        }

        BlobClient blobClient;

        try {
            meshUrl = URLDecoder.decode(meshUrl, StandardCharsets.UTF_8);

            String containerUrl = meshesContainerClient.getBlobContainerUrl() + "/";
            String blobPath = meshUrl.startsWith(containerUrl) ? meshUrl.substring(containerUrl.length()) : meshUrl;

            blobClient = meshesContainerClient.getBlobClient(blobPath);
        } catch (BlobStorageException e) {
            throw new VersionException("Error during checking existence of mesh URL in BLOB storage");
        }

        return blobClient.exists();
    }

    @Override
    public boolean existsMaterialByUrl(String materialFolderUrl) {
        if (materialFolderUrl == null || materialFolderUrl.isEmpty()) {
            throw new VersionException("Material URL can't be null or empty");
        }

        PagedIterable<BlobItem> blobs;

        try {
            materialFolderUrl = URLDecoder.decode(materialFolderUrl, StandardCharsets.UTF_8);

            String containerUrl = materialsContainerClient.getBlobContainerUrl() + "/";
            String relativePath = materialFolderUrl.startsWith(containerUrl)
                    ? materialFolderUrl.substring(containerUrl.length())
                    : materialFolderUrl;

            if (!relativePath.endsWith("/")) {
                relativePath += "/";
            }

            ListBlobsOptions options = new ListBlobsOptions().setPrefix(relativePath);
            blobs = materialsContainerClient.listBlobsByHierarchy("/", options, Duration.ofSeconds(30));
        } catch (BlobStorageException e) {
            throw new VersionException("Error during checking existence of material URL in BLOB storage");
        }

        return blobs.iterator().hasNext();
    }

    @Override
    public Triple<InputStream, String, String> findMeshByUrl(String meshUrl) {
        if (meshUrl == null || meshUrl.isEmpty()) {
            throw new VersionException("Mesh URL can't be null or empty");
        } else {
            existsMeshByUrl(meshUrl);
        }

        InputStream inputStream;
        String contentType;
        String meshName;

        try {
            meshUrl = URLDecoder.decode(meshUrl, StandardCharsets.UTF_8);

            String containerUrl = meshesContainerClient.getBlobContainerUrl() + "/";
            String relativePath = meshUrl.startsWith(containerUrl)
                    ? meshUrl.substring(containerUrl.length())
                    : meshUrl;

            BlobClient blobClient = meshesContainerClient.getBlobClient(relativePath);

            inputStream = blobClient.openInputStream();

            String path = blobClient.getBlobName();
            meshName = path.substring(path.lastIndexOf("/") + 1);

            contentType = blobClient.getProperties().getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
        } catch (BlobStorageException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error during mesh retrieving in BLOB storage");
        }

        return Triple.of(inputStream, contentType, meshName);
    }

    @Override
    public List<Triple<InputStream, String, String>> findMaterialByUrl(String materialFolderUrl) {
        if (materialFolderUrl == null || materialFolderUrl.isEmpty()) {
            throw new VersionException("Material URL can't be null or empty");
        } else {
            existsMaterialByUrl(materialFolderUrl);
        }

        try {
            materialFolderUrl = URLDecoder.decode(materialFolderUrl, StandardCharsets.UTF_8);

            String containerUrl = materialsContainerClient.getBlobContainerUrl() + "/";
            String relativePath = materialFolderUrl.startsWith(containerUrl)
                    ? materialFolderUrl.substring(containerUrl.length())
                    : materialFolderUrl;

            if (!relativePath.endsWith("/")) {
                relativePath += "/";
            }

            return materialsContainerClient.listBlobsByHierarchy(relativePath).stream()
                    .map(blobItem -> {
                        BlobClient client = materialsContainerClient.getBlobClient(blobItem.getName());
                        String path = client.getBlobName();
                        String textureName = path.substring(path.lastIndexOf("/") + 1);
                        InputStream inputStream = client.openInputStream();
                        String contentType = client.getProperties().getContentType();
                        if (contentType == null || contentType.isEmpty()) {
                            contentType = "application/octet-stream";
                        }
                        return Triple.of(inputStream, contentType, textureName);
                    })
                    .toList();
        } catch (BlobStorageException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error during material retrieving in BLOB storage");
        }
    }

    @Override
    public void deleteMeshByUrl(String meshUrl) {
        if (meshUrl == null || meshUrl.isEmpty()) {
            throw new VersionException("Mesh URL can't be null or empty");
        }

        try {
            meshUrl = URLDecoder.decode(meshUrl, StandardCharsets.UTF_8);

            String containerUrl = meshesContainerClient.getBlobContainerUrl() + "/";
            String relativePath = meshUrl.startsWith(containerUrl)
                    ? meshUrl.substring(containerUrl.length())
                    : meshUrl;

            BlobClient blobClient = meshesContainerClient.getBlobClient(relativePath);

            if (blobClient.exists()) {
                blobClient.delete();
            } else {
                // If it is necessary use a RuntimeException for more detailed debug
                throw new VersionException("Mesh not found in BLOB storage during deletion");
            }
        } catch (BlobStorageException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error deleting mesh in BLOB storage");
        }
    }

    @Override
    public void deleteMaterialByUrl(String materialFolderUrl) {
        if (materialFolderUrl == null || materialFolderUrl.isEmpty()) {
            throw new VersionException("Material URL can't be null or empty");
        }

        try {
            materialFolderUrl = URLDecoder.decode(materialFolderUrl, StandardCharsets.UTF_8);

            String containerUrl = materialsContainerClient.getBlobContainerUrl() + "/";
            String relativePath = materialFolderUrl.startsWith(containerUrl)
                    ? materialFolderUrl.substring(containerUrl.length())
                    : materialFolderUrl;

            if (!relativePath.endsWith("/")) {
                relativePath += "/";
            }

            for (BlobItem blobItem : materialsContainerClient.listBlobsByHierarchy(relativePath)) {
                if (blobItem.isPrefix()) {
                    continue;
                }
                BlobClient blobClient = materialsContainerClient.getBlobClient(blobItem.getName());
                blobClient.delete();
            }
        } catch (BlobStorageException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error deleting material in BLOB storage");
        }
    }
}
