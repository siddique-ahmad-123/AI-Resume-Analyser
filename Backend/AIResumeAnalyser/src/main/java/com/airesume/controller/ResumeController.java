package com.airesume.controller;

import com.airesume.dto.AnalysisRequestDTO;
import com.airesume.dto.AnalysisResponseDTO;
import com.airesume.dto.ResumeUploadResponseDTO;
import com.airesume.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final AnalysisService analysisService;

    /**
     * POST /api/v1/resumes/upload
     * Accepts a multipart PDF or DOCX resume. Returns resume metadata.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeUploadResponseDTO> uploadResume(
            @RequestPart("file") MultipartFile file) {

        log.info("Upload request: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        ResumeUploadResponseDTO response = analysisService.uploadResume(file);
        URI location = URI.create("/api/v1/resumes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
