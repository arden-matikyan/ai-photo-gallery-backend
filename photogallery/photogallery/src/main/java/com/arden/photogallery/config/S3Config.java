package com.arden.photogallery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

// This Spring @Configuration defines a singleton S3Client bean built with the AWS SDK v2. The s3Client() method returns an S3Client from its builder and sets a Region; because the method is annotated with @Bean, Spring will manage that client instance and inject it where needed.


@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1) // CHANGE THIS
                .build();
    }
}
