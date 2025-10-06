package com.exadel.frs.commonservice.projection;

import java.util.UUID;

public class EnhancedEmbeddingProjection {
    private final UUID embeddingId;
    private final String subjectName;
    private final String calculator;

    public EnhancedEmbeddingProjection(UUID embeddingId, String subjectName, String calculator) {
        this.embeddingId = embeddingId;
        this.subjectName = subjectName;
        this.calculator = calculator;
    }

    public UUID embeddingId() {
        return embeddingId;
    }

    public String subjectName() {
        return subjectName;
    }

    public String calculator() {
        return calculator;
    }
}
