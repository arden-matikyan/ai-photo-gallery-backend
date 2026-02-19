package com.arden.photogallery.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder().baseUrl("https://api.openai.com/v1").build();


    /* 
        public String generateCaption(String tags) {

        String prompt = "Write a natural, short, aesthetic caption for a photo with these elements: " + tags;

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", new Object[]{
                    Map.of("role", "user", "content", prompt)
                }
        );

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (java.util.List<?>) response.get("choices");
                    var firstChoice = (Map<?, ?>) choices.get(0);
                    var message = (Map<?, ?>) firstChoice.get("message");
                    return message.get("content").toString();
                })
                .block();
    }
     */
    public Map<String, Object> analyzeImage(String imageUrl) throws Exception {

        String prompt = """
    You are a photography analysis assistant for a hobbyist's portfolio.

    Analyze the image and return ONLY valid JSON in this format:

    {
      "caption": "short natural description",
      "mood": "one or two word mood",
      "style": "photography style",
      "lighting": "lighting condition",
      "primary_subject": "main subject"
    }
    """;

        // enforce structure of the API output 
        Map<String, Object> responseFormat = Map.of(
                "type", "json_schema",
                "name", "photo_metadata",
                "schema", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "caption", Map.of("type", "string"),
                                "mood", Map.of("type", "string"),
                                "style", Map.of("type", "string"),
                                "lighting", Map.of("type", "string"),
                                "primary_subject", Map.of("type", "string")
                        ),
                        "required", List.of(
                                "caption",
                                "mood",
                                "style",
                                "lighting",
                                "primary_subject"
                        ), 
                        "additionalProperties", false
                )
        );

        // instruction block to send to gpt 
        Map<String, Object> textPart = Map.of(
                "type", "input_text",
                "text", prompt
        );

        // image to send 
        Map<String, Object> imagePart = Map.of(
                "type", "input_image",
                "image_url", imageUrl
        );

        // packaged message 
        Map<String, Object> message = Map.of(
                "role", "user",
                "content", List.of(textPart, imagePart)
        );

        // JSON requet of message full 
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "input", List.of(message),
                "text", Map.of(
                        "format", responseFormat
                ),
                "max_output_tokens", 300
        );

        // API call 
        Map response = webClient.post()
                .uri("/responses")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus( // log any errors 
                        status -> status.isError(),
                        clientResponse
                        -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.out.println("OpenAI ERROR BODY: " + errorBody);
                                    return Mono.error(new RuntimeException(errorBody));
                                })
                )
                .bodyToMono(Map.class)
                .block();

        // Extract structured JSON from response
        List<?> output = (List<?>) response.get("output");
        Map<?, ?> firstOutput = (Map<?, ?>) output.get(0);
        List<?> content = (List<?>) firstOutput.get("content");

        Map<?, ?> textItem = content.stream()
                .map(item -> (Map<?, ?>) item)
                .filter(item -> "output_text".equals(item.get("type")))
                .findFirst()
                .orElseThrow();

        String jsonText = textItem.get("text").toString();

        try {
            return new ObjectMapper().readValue(jsonText, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GPT JSON: " + jsonText, e);
        }
    }

}
