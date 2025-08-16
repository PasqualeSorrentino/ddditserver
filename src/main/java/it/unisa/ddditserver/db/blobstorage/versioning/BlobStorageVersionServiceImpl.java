package it.unisa.ddditserver.db.blobstorage.versioning;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import it.unisa.ddditserver.db.blobstorage.BlobStorageConfig;
import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import it.unisa.ddditserver.subsystems.versioning.exceptions.version.VersionException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlobStorageVersionServiceImpl implements BlobStorageVersionService {
    private final BlobStorageConfig config;
    private BlobContainerClient meshesContainerClient;
    private BlobContainerClient materialsContainerClient;

    @Autowired
    public BlobStorageVersionServiceImpl(BlobStorageConfig config) {
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
        String versionName = versionDTO.getVersionName();
        MultipartFile mesh =  versionDTO.getMesh();
        // BLOB path: repoName/branchName/generatedVersionName
        String blobPath = repoFolder + "/" + branchFolder + "/" + versionName;

        try {
            BlobClient blobClient = meshesContainerClient.getBlobClient(blobPath);

            try (InputStream dataStream = mesh.getInputStream()) {
                blobClient.upload(dataStream, mesh.getSize(), false);
            }

            return blobClient.getBlobUrl();
        } catch (IOException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("IO error during mesh saving in BLOB storage for file: " + mesh.getOriginalFilename());
        }
    }

    @Override
    public String saveMaterial(VersionDTO versionDTO) {
        String repoFolder = versionDTO.getRepositoryName();
        String branchFolder = versionDTO.getBranchName().replaceAll("[^a-zA-Z0-9]", "_");
        String versionName = versionDTO.getVersionName();
        List<MultipartFile> material = versionDTO.getMaterial();

        for (MultipartFile texture : material) {
            // BLOB path: repoName/branchName/generatedVersionName/textureFileName
            String blobPath = repoFolder + "/" + branchFolder + "/" + versionName + "/" + texture.getOriginalFilename();

            BlobClient blobClient = materialsContainerClient.getBlobClient(blobPath);

            try (InputStream dataStream = texture.getInputStream()) {
                blobClient.upload(dataStream, texture.getSize(), false);
            } catch (IOException e) {
                // If it is necessary use a RuntimeException for more detailed debug
                throw new VersionException("IO error during material saving in BLOB storage for file: " + texture.getOriginalFilename());
            }
        }

        String folderBlobPath = repoFolder + "/" + branchFolder + "/" + versionName;
        BlobClient folderClient = materialsContainerClient.getBlobClient(folderBlobPath);

        return folderClient.getBlobUrl();
    }

    @Override
    public boolean existsMeshByUrl(String meshUrl) {
        if (meshUrl == null || meshUrl.isEmpty()) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Mesh URL can't be null or empty");
        }

        String blobName = meshUrl.substring(meshUrl.lastIndexOf('/') + 1);

        BlobClient blobClient = meshesContainerClient.getBlobClient(blobName);

        return blobClient.exists();
    }

    @Override
    public boolean existsMaterialByUrl(String materialFolderUrl) {
        if (materialFolderUrl == null || materialFolderUrl.isEmpty()) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Mesh URL can't be null or empty");
        }

        String folderPath = materialFolderUrl.substring(materialFolderUrl.indexOf(materialsContainerClient.getBlobContainerName())
                + materialsContainerClient.getBlobContainerName().length() + 1);

        PagedIterable<BlobItem> blobs = materialsContainerClient.listBlobsByHierarchy(folderPath + "/");

        return blobs.iterator().hasNext();
    }

    @Override
    public InputStream findMeshByUrl(String meshUrl) {
        if (meshUrl == null || meshUrl.isEmpty()) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Mesh URL can't be null or empty");
        }

        String blobName = meshUrl.substring(meshUrl.lastIndexOf('/') + 1);
        BlobClient blobClient = meshesContainerClient.getBlobClient(blobName);

        try {
            return blobClient.openInputStream();
        } catch (BlobStorageException e) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Error retrieving mesh from URL: " + meshUrl + " in BLOB storage");
        }
    }

    @Override
    public List<InputStream> findMaterialByUrl(String materialFolderUrl) {
        if (materialFolderUrl == null || materialFolderUrl.isEmpty()) {
            // If it is necessary use a RuntimeException for more detailed debug
            throw new VersionException("Material URL can't be null or empty");
        }

        String folderPath = materialFolderUrl.substring(
                materialFolderUrl.indexOf(materialsContainerClient.getBlobContainerName()) +
                        materialsContainerClient.getBlobContainerName().length() + 1
        );

        List<InputStream> streams = new ArrayList<>();

        for (BlobItem blobItem : materialsContainerClient.listBlobsByHierarchy(folderPath + "/")) {
            BlobClient blobClient = materialsContainerClient.getBlobClient(blobItem.getName());
            try {
                streams.add(blobClient.openInputStream());
            } catch (BlobStorageException e) {
                // If it is necessary use a RuntimeException for more detailed debug
                throw new VersionException("Error retrieving material file: " + blobItem.getName() + " in BLOB storage");
            }
        }

        return streams;
    }
}
