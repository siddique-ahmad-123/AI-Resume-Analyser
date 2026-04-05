package com.airesume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnalysisRequestDTO {

    @NotBlank(message = "Resume ID must not be blank")
    private String resumeId;

    @Size(max = 10000, message = "Job description must not exceed 10,000 characters")
    private String jobDescription;
}
