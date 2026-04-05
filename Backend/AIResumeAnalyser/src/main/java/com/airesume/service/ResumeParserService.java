package com.airesume.service;

import com.airesume.exception.FileParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parses uploaded resume files (PDF / DOCX) and extracts plain text.
 */
@Slf4j
@Service
public class ResumeParserService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024L; // 10 MB
    private static final int MIN_EXTRACTED_LENGTH = 50; // guard against blank/image-only PDFs

    public String extractText(MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase()
                : "";

        try (InputStream inputStream = file.getInputStream()) {
            if (isPdf(contentType, filename)) {
                return extractFromPdf(inputStream, filename);
            } else if (isDocx(contentType, filename)) {
                return extractFromDocx(inputStream, filename);
            } else {
                throw new FileParseException("Unsupported file type. Only PDF and DOCX are accepted.");
            }
        } catch (FileParseException e) {
            throw e;
        } catch (IOException e) {
            log.error("IO error reading file {}: {}", filename, e.getMessage(), e);
            throw new FileParseException("Failed to read the uploaded file: " + e.getMessage(), e);
        }
    }

    // ── private helpers ────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileParseException("Uploaded file is empty or missing.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new FileParseException("File exceeds maximum allowed size of 10 MB.");
        }
    }

    private String extractFromPdf(InputStream inputStream, String filename) {
        try (PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {
            if (document.isEncrypted()) {
                throw new FileParseException("The uploaded PDF is encrypted/password-protected.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document).trim();
            if (text.length() < MIN_EXTRACTED_LENGTH) {
                throw new FileParseException(
                        "Could not extract meaningful text from the PDF. " +
                                "The file may be image-based or scanned.");
            }
            log.info("Extracted {} characters from PDF: {}", text.length(), filename);
            return text;
        } catch (FileParseException e) {
            throw e;
        } catch (Exception e) {
            throw new FileParseException("Failed to parse PDF: " + e.getMessage(), e);
        }
    }

    private String extractFromDocx(InputStream inputStream, String filename) {
        try (XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText().trim();
            if (text.length() < MIN_EXTRACTED_LENGTH) {
                throw new FileParseException("Could not extract meaningful text from the DOCX file.");
            }
            log.info("Extracted {} characters from DOCX: {}", text.length(), filename);
            return text;
        } catch (FileParseException e) {
            throw e;
        } catch (Exception e) {
            throw new FileParseException("Failed to parse DOCX: " + e.getMessage(), e);
        }
    }

    private boolean isPdf(String contentType, String filename) {
        return "application/pdf".equalsIgnoreCase(contentType)
                || filename.endsWith(".pdf");
    }

    private boolean isDocx(String contentType, String filename) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equalsIgnoreCase(contentType)
                || filename.endsWith(".docx");
    }
}
