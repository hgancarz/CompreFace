package com.exadel.frs.commonservice.projection;

import java.util.Arrays;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnhancedEmbeddingProjection that = (EnhancedEmbeddingProjection) o;
        return Objects.equals(embeddingId, that.embeddingId) && 
               Arrays.equals(embeddingData, that.embeddingData) && 
               Objects.equals(subjectName, that.subjectName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(embeddingId, subjectName);
        result = 31 * result + Arrays.hashCode(embeddingData);
        return result;
    }
}
