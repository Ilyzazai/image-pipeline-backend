package com.algonquin.image_pipeline_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request from frontend to start an upload.
 * We keep it minimal for the midterm.
 */
public class InitUploadRequest {

    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType; // example: image/jpeg

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
