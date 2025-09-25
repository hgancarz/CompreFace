package com.exadel.frs.commonservice.projection;

import java.util.UUID;

public class EnhancedEmbeddingProjection {
    private final UUID id;
    private final String subjectName;
    private final String calculator;
    private final byte[] rawContent;

    public EnhancedEmbeddingProjection(UUID id, String subjectName, String calculator, byte[] rawContent) {
        this.id = id;
        this.subjectName = subjectName;
        this.calculator = calculator;
        this.rawContent = rawContent;
    }

    public UUID id() {
        return id;
    }

    public String subjectName() {
        return subjectName;
    }

    public String calculator() {
        return calculator;
    }

    public byte[] rawContent() {
        return rawContent;
    }
}
