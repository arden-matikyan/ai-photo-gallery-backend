package com.arden.photogallery.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;


@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    // inject from yaml 
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(String originalFilename, byte[] fileBytes) {

        String key = UUID.randomUUID() + "_" + originalFilename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    public String generatePresignedUrl(String key) {

        S3Presigner presigner = S3Presigner.create();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest
                = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return presigner.presignGetObject(presignRequest)
                .url()
                .toString();
    }
}
