package com.arden.photogallery.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.Parent;
import software.amazon.awssdk.services.rekognition.model.S3Object;

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
                .minConfidence(85F)
                .build();

        DetectLabelsResponse response = rekognitionClient.detectLabels(request);

        List<Label> labels = response.labels();

        // Collect parent labels
        Set<String> parentNames = labels.stream()
                .flatMap(label -> label.parents().stream())
                .map(Parent::name)
                .collect(Collectors.toSet());

        // Keep strong labels, remove generic parents, sort by confidence
        List<String> refined = labels.stream()
                .filter(label -> label.confidence() > 85)
                .filter(label -> !parentNames.contains(label.name()))
                .sorted((a, b) -> Float.compare(b.confidence(), a.confidence()))
                .limit(3)
                .map(Label::name)
                .toList();

        return String.join(", ", refined);
    }
}
