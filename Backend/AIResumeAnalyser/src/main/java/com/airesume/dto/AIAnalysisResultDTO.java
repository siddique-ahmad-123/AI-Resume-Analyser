package com.airesume.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Internal DTO representing the structured JSON output from OpenAI analysis.
 * Mapped from the raw AI response string.
 */
@Data
@Builder
public class AIAnalysisResultDTO {

    private Integer atsScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
    private List<String> keywords;
    private String summary;
}
