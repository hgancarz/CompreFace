package com.exadel.frs.commonservice.projection;

import java.util.UUID;

public class EmbeddingProjection {
    private final UUID embeddingId;
    private final String subjectName;

    public EmbeddingProjection(UUID embeddingId, String subjectName) {
        this.embeddingId = embeddingId;
        this.subjectName = subjectName;
    }

    public UUID embeddingId() {
        return embeddingId;
    }

    public String subjectName() {
        return subjectName;
    }
}
