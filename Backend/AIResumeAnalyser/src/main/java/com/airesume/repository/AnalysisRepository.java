package com.airesume.repository;

import com.airesume.model.Analysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisRepository extends MongoRepository<Analysis, String> {

    Page<Analysis> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Analysis> findByResumeIdOrderByCreatedAtDesc(String resumeId);
}
