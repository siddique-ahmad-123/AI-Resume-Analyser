package com.airesume.service;

import com.airesume.dto.*;
import com.airesume.exception.ResourceNotFoundException;
import com.airesume.model.Analysis;
import com.airesume.model.Resume;
import com.airesume.repository.AnalysisRepository;
import com.airesume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ResumeRepository resumeRepository;
    private final AnalysisRepository analysisRepository;
    private final ResumeParserService parserService;
    private final AIAnalysisService aiAnalysisService;

    /**
     * Uploads a resume, extracts text, persists the record, and returns the
     * metadata.
     */
    public ResumeUploadResponseDTO uploadResume(MultipartFile file) {
        String extractedText = parserService.extractText(file);

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        String fileType = resolveFileType(filename);

        Resume resume = Resume.builder()
                .originalFilename(filename)
                .fileType(fileType)
                .extractedText(extractedText)
                .build();

        Resume saved = resumeRepository.save(resume);
        log.info("Saved resume {}: {}", saved.getId(), filename);

        return ResumeUploadResponseDTO.builder()
                .id(saved.getId())
                .originalFilename(saved.getOriginalFilename())
                .fileType(saved.getFileType())
                .extractedCharCount(extractedText.length())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /**
     * Runs AI analysis on a previously uploaded resume. Optionally accepts a job
     * description.
     */
    public AnalysisResponseDTO analyseResume(AnalysisRequestDTO request) {
        String resumeId = request.getResumeId();
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resume not found with ID: " + resumeId));

        log.info("Analysing resume {} with AI...", resumeId);
        AIAnalysisResultDTO aiResult = aiAnalysisService.analyseResume(resume.getExtractedText(),
                request.getJobDescription());

        Analysis analysis = Analysis.builder()
                .resumeId(resumeId)
                .jobDescription(request.getJobDescription())
                .atsScore(aiResult.getAtsScore())
                .strengths(aiResult.getStrengths())
                .weaknesses(aiResult.getWeaknesses())
                .suggestions(aiResult.getSuggestions())
                .keywords(aiResult.getKeywords())
                .summary(aiResult.getSummary())
                .build();

        Analysis saved = analysisRepository.save(analysis);
        log.info("Saved analysis {} for resume {}", saved.getId(), resumeId);

        return toAnalysisResponseDTO(saved, resume);
    }

    /**
     * Returns paginated history of all analyses across all resumes.
     */
    public Page<AnalysisResponseDTO> getHistory(Pageable pageable) {
        Page<Analysis> page = analysisRepository.findAllByOrderByCreatedAtDesc(pageable);

        // Collect unique resume IDs and fetch them in one query
        List<String> resumeIds = page.stream()
                .map(Analysis::getResumeId)
                .distinct()
                .toList();
        Map<String, Resume> resumeMap = resumeRepository.findAllById(resumeIds).stream()
                .collect(Collectors.toMap(Resume::getId, r -> r));

        return page.map(a -> toAnalysisResponseDTO(a,
                resumeMap.getOrDefault(a.getResumeId(), unknownResume(a.getResumeId()))));
    }

    /**
     * Returns all analyses for a specific resume.
     */
    public List<AnalysisResponseDTO> getAnalysesByResume(String resumeId) {
        if (!resumeRepository.existsById(resumeId)) {
            throw new ResourceNotFoundException("Resume not found with ID: " + resumeId);
        }
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();
        return analysisRepository.findByResumeIdOrderByCreatedAtDesc(resumeId).stream()
                .map(a -> toAnalysisResponseDTO(a, resume))
                .toList();
    }

    /**
     * Fetches a single analysis by ID.
     */
    public AnalysisResponseDTO getAnalysisById(String analysisId) {
        Analysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Analysis not found with ID: " + analysisId));
        Resume resume = resumeRepository.findById(analysis.getResumeId())
                .orElse(unknownResume(analysis.getResumeId()));
        return toAnalysisResponseDTO(analysis, resume);
    }

    // ── Mapper ─────────────────────────────────────────────────────────────────

    private AnalysisResponseDTO toAnalysisResponseDTO(Analysis analysis, Resume resume) {
        return AnalysisResponseDTO.builder()
                .id(analysis.getId())
                .resumeId(resume.getId())
                .originalFilename(resume.getOriginalFilename())
                .atsScore(analysis.getAtsScore())
                .strengths(analysis.getStrengths())
                .weaknesses(analysis.getWeaknesses())
                .suggestions(analysis.getSuggestions())
                .keywords(analysis.getKeywords())
                .summary(analysis.getSummary())
                .jobDescription(analysis.getJobDescription())
                .createdAt(analysis.getCreatedAt())
                .build();
    }

    private Resume unknownResume(String resumeId) {
        return Resume.builder().id(resumeId).originalFilename("unknown").fileType("UNKNOWN").build();
    }

    private String resolveFileType(String filename) {
        if (filename.toLowerCase().endsWith(".pdf"))
            return "PDF";
        if (filename.toLowerCase().endsWith(".docx"))
            return "DOCX";
        return "UNKNOWN";
    }
}
