package com.airesume.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    private String id;

    @Indexed
    private String resumeId;

    private String jobDescription;

    private Integer atsScore;

    private List<String> strengths;

    private List<String> weaknesses;

    private List<String> suggestions;

    private List<String> keywords;

    private String summary;

    private String rawAiResponse;

    @CreatedDate
    private LocalDateTime createdAt;
}
