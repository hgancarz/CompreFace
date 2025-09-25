package com.exadel.frs.commonservice.projection;

import java.util.UUID;

/**
 * @param embeddingData embedding column of embedding table
 */
public class EnhancedEmbeddingProjection {
    private final UUID embeddingId;
    private final double[] embeddingData;
    private final String subjectName;

    public EnhancedEmbeddingProjection(UUID embeddingId, double[] embeddingData, String subjectName) {
        this.embeddingId = embeddingId;
        this.embeddingData = embeddingData;
        this.subjectName = subjectName;
    }

    public UUID embeddingId() {
        return embeddingId;
    }

    public double[] embeddingData() {
        return embeddingData;
    }

    public String subjectName() {
        return subjectName;
    }
}
