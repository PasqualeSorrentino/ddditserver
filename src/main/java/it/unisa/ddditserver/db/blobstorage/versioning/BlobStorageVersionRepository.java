package it.unisa.ddditserver.db.blobstorage.versioning;

import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import org.apache.commons.lang3.tuple.Triple;
import java.io.InputStream;
import java.util.List;

/**
 * Repository interface for managing version-related operations
 * in a BLOB storage database.
 *
 * @author Angelo Antonio Prisco
 * @version 1.1
 * @since 2025-08-25
 */
public interface BlobStorageVersionRepository {
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
     * Checks if a material exists for the specified URL.
     *
     * @param materialFolderUrl the BLOB storage URL that identifies the folder of the material
     * @return true if branch exists, false otherwise
     */
    boolean existsMaterialByUrl(String materialFolderUrl);

    /**
     * Retrieves the mesh data as an input stream for the specified URL.
     *
     * @param meshUrl the BLOB storage URL that identifies the mesh
     * @return a Triple containing an InputStream containing the mesh data, the content-type of the file and filename
     */
    Triple<InputStream, String, String> findMeshByUrl(String meshUrl);

    /**
     * Retrieves the list of material data streams for the specified folder URL.
     *
     * @param materialFolderUrl the BLOB storage URL that identifies the folder of the material
     * @return a list of a Triple containing an InputStream containing the texture data, the content-type of the file and filename
     */
    List<Triple<InputStream, String, String>> findMaterialByUrl(String materialFolderUrl);

    /**
     * Delete the mesh BLOB for the specified URL.
     *
     * @param meshUrl the BLOB storage URL that identifies the mesh
     */
    void deleteMeshByUrl(String meshUrl);

    /**
     * Delete the material folder for the specified URL.
     *
     * @param materialFolderUrl the BLOB storage URL that identifies the folder of the material
     */
    void deleteMaterialByUrl(String materialFolderUrl);
}
