package com.exadel.frs.commonservice.projection;

import java.util.UUID;

public class EmbeddingProjection {
    private final UUID id;
    private final String subjectName;

    public EmbeddingProjection(UUID id, String subjectName) {
        this.id = id;
        this.subjectName = subjectName;
    }

    public UUID id() {
        return id;
    }

    public String subjectName() {
        return subjectName;
    }
}
