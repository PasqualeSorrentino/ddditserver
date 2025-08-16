package it.unisa.ddditserver.db.blobstorage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BlobStorageConfig {
    @Value("${BLOB_STORAGE_CONNECTION_STRING}")
    private String connectionString;

    @Value("${BLOB_STORAGE_CONTAINER_MESHES}")
    private String meshesContainer;

    @Value("${BLOB_STORAGE_CONTAINER_MATERIALS}")
    private String materialsContainer;
}
