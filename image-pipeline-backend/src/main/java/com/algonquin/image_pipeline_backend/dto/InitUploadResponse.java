package com.algonquin.image_pipeline_backend.dto;

/**
 * Response sent back to frontend:
 * - imageId: used to query status later
 * - uploadUrl: SAS link to upload to raw container
 * - expiresAtIso: tells user when the SAS expires
 */
public class InitUploadResponse {

    private String imageId;
    private String rawBlobName;
    private String uploadUrl;
    private String expiresAtIso;

    public InitUploadResponse(String imageId, String rawBlobName, String uploadUrl, String expiresAtIso) {
        this.imageId = imageId;
        this.rawBlobName = rawBlobName;
        this.uploadUrl = uploadUrl;
        this.expiresAtIso = expiresAtIso;
    }

    public String getImageId() {
        return imageId;
    }

    public String getRawBlobName() {
        return rawBlobName;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getExpiresAtIso() {
        return expiresAtIso;
    }
}
