package com.arden.photogallery.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();


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
You are a photography analysis assistant for a hobbyists portfolio.

Return ONLY valid JSON in this format:

{
  "caption": "short natural description",
  "mood": "one or two word mood",
  "style": "photography style",
  "lighting": "lighting condition",
  "primary_subject": "main subject"
}
""";

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "input", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "input_text",
                                                "text", prompt
                                        ),
                                        Map.of(
                                                "type", "input_image",
                                                "image_url", imageUrl
                                        )
                                )
                        )
                ),
                "max_output_tokens", 300
        );

        Map response = webClient.post()
                .uri("/responses")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Extract output text
        List output = (List) response.get("output");
        Map firstOutput = (Map) output.get(0);
        List content = (List) firstOutput.get("content");
        Map textPart = (Map) content.get(0);

        String jsonText = textPart.get("text").toString();

        return new ObjectMapper().readValue(jsonText, Map.class);
    }

}
