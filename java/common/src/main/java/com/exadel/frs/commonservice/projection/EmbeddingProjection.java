package com.exadel.frs.commonservice.projection;

import com.exadel.frs.commonservice.entity.Embedding;
import java.util.UUID;

public class EmbeddingProjection {

    private final UUID embeddingId;
    private final String subjectName;

    public EmbeddingProjection(UUID embeddingId, String subjectName) {
        this.embeddingId = embeddingId;
        this.subjectName = subjectName;
    }

    public static EmbeddingProjection from(Embedding embedding) {
        return new EmbeddingProjection(
                embedding.getId(),
                embedding.getSubject().getSubjectName()
        );
    }

    public static EmbeddingProjection from(EnhancedEmbeddingProjection projection) {
        return new EmbeddingProjection(
                projection.embeddingId(),
                projection.subjectName()
        );
    }

    public EmbeddingProjection withNewSubjectName(String newSubjectName) {
        return new EmbeddingProjection(
                this.embeddingId,
                newSubjectName
        );
    }

    public UUID embeddingId() { return embeddingId; }
    public String subjectName() { return subjectName; }
}
