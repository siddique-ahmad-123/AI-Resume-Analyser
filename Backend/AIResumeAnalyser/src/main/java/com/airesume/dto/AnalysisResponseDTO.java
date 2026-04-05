package com.airesume.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AnalysisResponseDTO {

    private String id;
    private String resumeId;
    private String originalFilename;
    private Integer atsScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> suggestions;
    private List<String> keywords;
    private String summary;
    private String jobDescription;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
