package com.algonquin.image_pipeline_backend.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * This service contains all Storage logic:
 * - Ensure containers exist
 * - Create SAS URLs (upload + download)
 * - Check if processed image exists
 *
 * Our naming rule (simple):
 * - Raw upload:      raw/<imageId>.jpg   (container name: raw)
 * - Processed image: processed/<imageId>.jpg (container name: processed)
 */
@Service
public class BlobStorageService {

    private final BlobServiceClient blobServiceClient;

    @Value("${app.raw-container:raw}")
    private String rawContainerName;

    @Value("${app.processed-container:processed}")
    private String processedContainerName;

    @Value("${app.sas-ttl-minutes:10}")
    private int sasTtlMinutes;

    public BlobStorageService(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
    }

    /**
     * Creates containers if they do not exist.
     * Note: In a real production environment, infra is often created separately.
     * For a midterm, this makes local testing easier.
     */
    public void ensureContainersExist() {
        getContainer(rawContainerName).createIfNotExists();
        getContainer(processedContainerName).createIfNotExists();
    }

    /**
     * Builds the blob name for raw images.
     */
    public String rawBlobName(String imageId) {
        return imageId + ".jpg";
    }

    public String processedBlobName(String imageId) {
        return imageId + ".jpg";
    }

    /**
     * Generates a SAS URL that allows uploading (write) ONE blob into the raw container.
     * Frontend will do PUT to this URL.
     */
    public String generateUploadSasUrl(String rawBlobName) {
        BlobClient blobClient = getContainer(rawContainerName).getBlobClient(rawBlobName);

        // Permission: create + write (upload)
        BlobSasPermission perm = new BlobSasPermission()
                .setCreatePermission(true)
                .setWritePermission(true);

        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(sasTtlMinutes);

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiry, perm);

        // SAS token is generated using account key (because we used connection string)
        String sasToken = blobClient.generateSas(values);

        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    /**
     * Checks if the processed image exists in the processed container.
     * This is the simplest way to show "PROCESSING" vs "READY" without a database.
     */
    public boolean processedExists(String processedBlobName) {
        BlobClient blobClient = getContainer(processedContainerName).getBlobClient(processedBlobName);
        return blobClient.exists();
    }

    /**
     * Generates a SAS URL to download (read) ONE processed blob.
     */
    public String generateDownloadSasUrl(String processedBlobName) {
        BlobClient blobClient = getContainer(processedContainerName).getBlobClient(processedBlobName);

        // Permission: read
        BlobSasPermission perm = new BlobSasPermission().setReadPermission(true);

        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(sasTtlMinutes);

        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiry, perm);

        String sasToken = blobClient.generateSas(values);

        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    public int getSasTtlMinutes() {
        return sasTtlMinutes;
    }

    private BlobContainerClient getContainer(String containerName) {
        return blobServiceClient.getBlobContainerClient(containerName);
    }
}
