package com.airesume.service;

import com.airesume.dto.AIAnalysisResultDTO;
import com.airesume.exception.AIServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Calls the Groq API (OpenAI-compatible) to analyse a resume.
 * Returns a structured {@link AIAnalysisResultDTO}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIAnalysisService {

    private final WebClient openAIWebClient;
    private final ObjectMapper objectMapper;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${groq.max-tokens:2000}")
    private int maxTokens;

    public AIAnalysisResultDTO analyseResume(String resumeText, String jobDescription) {
        String prompt = buildPrompt(resumeText, jobDescription);
        String rawResponse = callGroq(prompt);
        return parseAIResponse(rawResponse);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private String buildPrompt(String resumeText, String jobDescription) {
        String jdSection = (jobDescription != null && !jobDescription.isBlank())
                ? "JOB DESCRIPTION:\n" + jobDescription.trim() + "\n\n"
                : "";

        return """
                You are an expert ATS (Applicant Tracking System) and career coach.
                Analyse the following resume%s and return a JSON object with EXACTLY this structure (no markdown, raw JSON only):

                {
                  "atsScore": <integer 0-100>,
                  "strengths": [<list of 3-5 concise strength strings>],
                  "weaknesses": [<list of 3-5 concise weakness strings>],
                  "suggestions": [<list of 3-5 actionable improvement suggestions>],
                  "keywords": [<list of important keywords present or missing from the resume>],
                  "summary": "<2-3 sentence overall assessment>"
                }

                Scoring guide:
                - 80-100: Excellent, well-optimised for ATS
                - 60-79: Good, minor improvements needed
                - 40-59: Average, significant gaps
                - 0-39: Poor, major restructure required

                %sRESUME TEXT:
                %s
                """
                .formatted(
                        jdSection.isEmpty() ? "" : " against the provided job description",
                        jdSection,
                        resumeText.substring(0, Math.min(resumeText.length(), 6000))
                );
    }

    private String callGroq(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "temperature", 0.3,
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are an expert resume analyser. Always respond with valid JSON only."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        try {
            log.debug("Calling Groq model: {}", model);
            String response = openAIWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0)
                    .path("message").path("content").asText();
            log.debug("Groq raw content length: {}", content.length());
            return content;

        } catch (WebClientResponseException e) {
            log.error("Groq API error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AIServiceException(
                    "Groq API returned an error: " + e.getStatusCode().value(), e);
        } catch (JsonProcessingException e) {
            throw new AIServiceException("Failed to parse Groq response", e);
        } catch (Exception e) {
            log.error("Unexpected error calling Groq: {}", e.getMessage(), e);
            throw new AIServiceException("Failed to reach AI service: " + e.getMessage(), e);
        }
    }

    private AIAnalysisResultDTO parseAIResponse(String rawContent) {
        String cleaned = rawContent.trim()
                .replaceAll("^```json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("\\s*```$", "");

        try {
            JsonNode root = objectMapper.readTree(cleaned);

            return AIAnalysisResultDTO.builder()
                    .atsScore(root.path("atsScore").asInt(0))
                    .strengths(jsonArrayToList(root.path("strengths")))
                    .weaknesses(jsonArrayToList(root.path("weaknesses")))
                    .suggestions(jsonArrayToList(root.path("suggestions")))
                    .keywords(jsonArrayToList(root.path("keywords")))
                    .summary(root.path("summary").asText(""))
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse AI JSON response: {}", cleaned, e);
            throw new AIServiceException("AI returned an unparseable response. Please retry.", e);
        }
    }

    private List<String> jsonArrayToList(JsonNode arrayNode) {
        List<String> list = new ArrayList<>();
        if (arrayNode.isArray()) {
            arrayNode.forEach(item -> list.add(item.asText()));
        }
        return list;
    }
}
