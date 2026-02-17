package com.arden.photogallery.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AIService {

    private final RekognitionClient rekognitionClient;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String generateTags(String key) {

        DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket(bucketName)
                                .name(key)
                                .build())
                        .build())
                .maxLabels(10)
                .build();

        DetectLabelsResponse response = rekognitionClient.detectLabels(request);

        return response.labels()
                .stream()
                .map(Label::name)
                .collect(Collectors.joining(", "));
    }
}
