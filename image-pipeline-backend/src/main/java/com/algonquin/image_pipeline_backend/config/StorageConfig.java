package com.algonquin.image_pipeline_backend.config;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Creates the Azure Blob client (connection to Storage Account).
 * 12-Factor: connection string is taken from ENV var AZURE_STORAGE_CONNECTION_STRING.
 */
@Configuration
public class StorageConfig {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Bean
    public BlobServiceClient blobServiceClient() {
        // If connection string isn't set, the backend can't talk to Storage.
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException("AZURE_STORAGE_CONNECTION_STRING is required (set it as an environment variable).");
        }

        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
