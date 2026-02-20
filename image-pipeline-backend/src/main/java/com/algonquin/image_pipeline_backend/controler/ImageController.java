package com.algonquin.image_pipeline_backend.controler;

import com.algonquin.image_pipeline_backend.dto.*;
import com.algonquin.image_pipeline_backend.service.BlobStorageService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * REST endpoints for frontend:
 *
 * 1) POST /api/uploads/init
 *    -> backend returns SAS upload URL (for raw container)
 *
 * 2) GET /api/images/{imageId}
 *    -> backend checks processed container
 *    -> returns PROCESSING or READY (with SAS download URL)
 */
@RestController
@RequestMapping("/api")
public class ImageController {

    private final BlobStorageService blobStorageService;

    public ImageController(BlobStorageService blobStorageService) {
        this.blobStorageService = blobStorageService;
    }

    @PostConstruct
    void startup() {
        // Ensure containers exist (useful for testing)
        blobStorageService.ensureContainersExist();
    }

    /**
     * Step 1 (Backend role):
     * Frontend asks for permission to upload.
     * Backend generates imageId + SAS upload URL to raw container.
     */
    @PostMapping("/uploads/init")
    public InitUploadResponse initUpload(@Valid @RequestBody InitUploadRequest req) {

        // We create a unique id for this image.
        // Everything in our pipeline is referenced by imageId.
        String imageId = UUID.randomUUID().toString();

        // We decided to standardize on .jpg for this midterm.
        // So output naming is very simple.
        String rawBlobName = blobStorageService.rawBlobName(imageId);

        // SAS URL that frontend can use for PUT upload
        String uploadUrl = blobStorageService.generateUploadSasUrl(rawBlobName);

        // Tell frontend when SAS expires
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(blobStorageService.getSasTtlMinutes());

        return new InitUploadResponse(
                imageId,
                rawBlobName,
                uploadUrl,
                expiresAt.toString()
        );
    }

    /**
     * Step 2 (Backend role):
     * Frontend polls for result.
     * Backend checks if processed blob exists.
     */
    @GetMapping("/images/{imageId}")
    public ImageStatusResponse getStatus(@PathVariable String imageId) {

        String processedBlobName = blobStorageService.processedBlobName(imageId);

        boolean ready = blobStorageService.processedExists(processedBlobName);

        if (!ready) {
            return new ImageStatusResponse(
                    imageId,
                    "PROCESSING",
                    processedBlobName,
                    null
            );
        }

        // If ready, generate a SAS read URL for processed output
        String downloadUrl = blobStorageService.generateDownloadSasUrl(processedBlobName);

        return new ImageStatusResponse(
                imageId,
                "READY",
                processedBlobName,
                downloadUrl
        );
    }
}
