package it.unisa.ddditserver.db.blobstorage.versioning;

import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import java.io.InputStream;
import java.util.List;

/**
 * Service interface for managing version-related operations
 * in a BLOB storage database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.0
 * @since 2025-08-13
 */
public interface BlobStorageVersionService {
    /**
     * Saves a new mesh BLOB in the BLOB storage.
     *
     * @param versionDTO the versionDTO containing version information
     * @return a string containing the mesh URL
     */
    String saveMesh(VersionDTO versionDTO);

    /**
     * Saves a new material folder with textures BLOB in the BLOB storage.
     *
     * @param versionDTO the versionDTO containing version information
     * @return a string containing the material folder URL
     */
    String saveMaterial(VersionDTO versionDTO);

    /**
     * Checks if a mesh exists for the specified URL.
     *
     * @param meshUrl the BLOB storage URL that identifies the mesh
     * @return true if branch exists, false otherwise
     */
    boolean existsMeshByUrl(String meshUrl);

    /**
     * Checks if a mesh exists for the specified URL.
     *
     * @param materialFolderUrl the BLOB storage URL that identifies the folder of the material
     * @return true if branch exists, false otherwise
     */
    boolean existsMaterialByUrl(String materialFolderUrl);

    /**
     * Retrieves the mesh data as an input stream for the specified URL.
     *
     * @param meshUrl the BLOB storage URL that identifies the mesh
     * @return an InputStream containing the mesh data
     */
    InputStream findMeshByUrl(String meshUrl);

    /**
     * Retrieves the list of material data streams for the specified folder URL.
     *
     * @param materialFolderUrl the BLOB storage URL that identifies the folder of the material
     * @return a list of InputStreams, each containing the data of a texture of the specified material;
     */
    List<InputStream> findMaterialByUrl(String materialFolderUrl);
}
