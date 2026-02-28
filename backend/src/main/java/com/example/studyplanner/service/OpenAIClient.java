package com.example.studyplanner.service;

import com.example.studyplanner.model.Subtopic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIClient.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-4o}")
    private String model;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Subtopic> generateSubtopics(String topic, String subject) {
        String prompt = String.format(
                "Break down the topic \"%s\" in \"%s\" into study subtopics suitable for learning. " +
                        "Estimate realistic study hours for each subtopic. " +
                        "Return ONLY a valid JSON array in this exact format: [{\"name\": \"Subtopic name\", \"estimatedHours\": 2.0}, ...]. "
                        +
                        "Do not include markdown formatting.",
                topic, subject);

        String jsonResponse = callOpenAI(prompt);
        logger.info("AI Subtopics Response: {}", jsonResponse);

        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<Subtopic>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse subtopics JSON", e);
            throw new RuntimeException("Failed to parse AI response: " + jsonResponse, e);
        }
    }

    public Subtopic generateSubtopicContent(Subtopic subtopic, String topic, String subject) {
        String prompt = String.format(
                "Provide detailed study content for the subtopic \"%s\" in \"%s\" (Main Topic: \"%s\"). " +
                        "Return a valid JSON object with three fields: " +
                        "1. \"content\": A comprehensive explanation (at least 500 words). " +
                        "2. \"youtubeLinks\": An array of 2-4 direct YouTube video URLs from reputable educational channels. Prioritize RECENTLY UPLOADED videos (2025-2026 if available). "
                        +
                        "3. \"videoSearchQueries\": An array of 2-4 specific search terms. Include the current year (e.g., \"2025\" or \"2026\") in some queries to ensure recent results. "
                        +
                        "Format: {\"content\": \"...\", \"youtubeLinks\": [\"...\"], \"videoSearchQueries\": [\"...\"]}. "
                        +
                        "Do not include markdown formatting.",
                subtopic.getName(), subject, topic);

        String jsonResponse = callOpenAI(prompt);
        logger.info("AI Content Response: {}", jsonResponse);

        try {
            Map<String, Object> responseMap = objectMapper.readValue(jsonResponse,
                    new TypeReference<Map<String, Object>>() {
                    });
            subtopic.setContent((String) responseMap.get("content"));
            @SuppressWarnings("unchecked")
            List<String> youtubeLinks = (List<String>) responseMap.get("youtubeLinks");
            subtopic.setYoutubeLinks(youtubeLinks);
            @SuppressWarnings("unchecked")
            List<String> searchQueries = (List<String>) responseMap.get("videoSearchQueries");
            if (searchQueries != null) {
                subtopic.setVideoSearchQueries(searchQueries);
            }
            return subtopic;
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse content JSON", e);
            throw new RuntimeException("Failed to parse AI content response", e);
        }
    }

    private String callOpenAI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant that outputs only valid JSON."));
        messages.add(Map.of("role", "user", "content", prompt));

        body.put("messages", messages);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                Object choicesObj = responseBody.get("choices");
                if (choicesObj instanceof List<?>) {
                    List<?> choicesList = (List<?>) choicesObj;
                    if (!choicesList.isEmpty()) {
                        Object firstChoice = choicesList.get(0);
                        if (firstChoice instanceof Map<?, ?>) {
                            Map<?, ?> choiceMap = (Map<?, ?>) firstChoice;
                            Object messageObj = choiceMap.get("message");
                            if (messageObj instanceof Map<?, ?>) {
                                Map<?, ?> messageMap = (Map<?, ?>) messageObj;
                                String content = (String) messageMap.get("content");
                                // Cleanup markdown code blocks if present
                                if (content != null) {
                                    return content.replaceAll("```json", "").replaceAll("```", "").trim();
                                }
                            }
                        }
                    }
                }
            }
            throw new RuntimeException("Empty response from OpenAI");
        } catch (Exception e) {
            logger.error("OpenAI API call failed", e);
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage(), e);
        }
    }
}
