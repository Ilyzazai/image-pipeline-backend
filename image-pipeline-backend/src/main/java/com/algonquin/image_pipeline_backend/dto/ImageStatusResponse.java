package com.algonquin.image_pipeline_backend.dto;

/**
 * Status response:
 * - PROCESSING: function hasn't saved output yet
 * - READY: processed image exists and downloadUrl is returned
 */
public class ImageStatusResponse {

    private String imageId;
    private String status; // PROCESSING or READY
    private String processedBlobName;
    private String downloadUrl; // only when READY

    public ImageStatusResponse(String imageId, String status, String processedBlobName, String downloadUrl) {
        this.imageId = imageId;
        this.status = status;
        this.processedBlobName = processedBlobName;
        this.downloadUrl = downloadUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public String getStatus() {
        return status;
    }

    public String getProcessedBlobName() {
        return processedBlobName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
