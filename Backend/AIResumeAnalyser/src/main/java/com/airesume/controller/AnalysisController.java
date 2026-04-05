package com.airesume.controller;

import com.airesume.dto.AnalysisRequestDTO;
import com.airesume.dto.AnalysisResponseDTO;
import com.airesume.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * POST /api/v1/analyses
     * Runs AI analysis on an uploaded resume. Optionally accepts a job description
     * for comparison.
     */
    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> analyse(
            @Valid @RequestBody AnalysisRequestDTO request) {

        log.info("Analysis request for resumeId: {}", request.getResumeId());
        AnalysisResponseDTO response = analysisService.analyseResume(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analyses/{id}
     * Returns a single analysis by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(analysisService.getAnalysisById(id));
    }

    /**
     * GET /api/v1/analyses/history?page=0&size=10
     * Returns paginated history of all analyses.
     */
    @GetMapping("/history")
    public ResponseEntity<Page<AnalysisResponseDTO>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(analysisService.getHistory(pageable));
    }

    /**
     * GET /api/v1/analyses/resume/{resumeId}
     * Returns all analyses for a specific resume.
     */
    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<AnalysisResponseDTO>> getByResume(@PathVariable String resumeId) {
        return ResponseEntity.ok(analysisService.getAnalysesByResume(resumeId));
    }
}
