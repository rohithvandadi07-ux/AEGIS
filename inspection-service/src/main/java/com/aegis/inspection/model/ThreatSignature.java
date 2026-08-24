package com.aegis.inspection.model;

import com.pgvector.PGvector;
import jakarta.persistence.*;

@Entity
@Table(name = "threat_signatures")
public class ThreatSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String signatureName;

    private String category;

    @Column(columnDefinition = "vector(1536)")
    private PGvector embedding;

    public ThreatSignature() {}

    public ThreatSignature(String signatureName, String category, PGvector embedding) {
        this.signatureName = signatureName;
        this.category = category;
        this.embedding = embedding;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSignatureName() { return signatureName; }
    public void setSignatureName(String signatureName) { this.signatureName = signatureName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public PGvector getEmbedding() { return embedding; }
    public void setEmbedding(PGvector embedding) { this.embedding = embedding; }
}
