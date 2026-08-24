package com.aegis.inspection.repository;

import com.aegis.inspection.model.ThreatSignature;
import com.pgvector.PGvector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreatSignatureRepository extends JpaRepository<ThreatSignature, Long> {

    // Using Cosine Distance (<=> operator in pgvector). 
    // Similarity = 1 - Distance. So distance < 0.15 means similarity > 0.85
    @Query(value = "SELECT * FROM threat_signatures WHERE embedding <=> cast(:vector as vector) < :threshold ORDER BY embedding <=> cast(:vector as vector) LIMIT :maxResults", nativeQuery = true)
    List<ThreatSignature> findSimilarThreats(@Param("vector") String vector, @Param("threshold") double threshold, @Param("maxResults") int maxResults);
}
