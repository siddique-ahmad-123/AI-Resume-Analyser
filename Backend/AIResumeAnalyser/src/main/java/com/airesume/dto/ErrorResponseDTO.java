package com.airesume.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Standardised error response envelope returned by GlobalExceptionHandler.
 */
@Data
@Builder
public class ErrorResponseDTO {

    private int status;
    private String error;
    private String message;
    private String path;
    private String timestamp;
    private Map<String, String> fieldErrors; // populated on validation failures
}
