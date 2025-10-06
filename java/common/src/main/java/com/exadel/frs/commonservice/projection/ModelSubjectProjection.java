package com.exadel.frs.commonservice.projection;

public class ModelSubjectProjection {
    private final String modelApiKey;
    private final String subjectName;

    public ModelSubjectProjection(String modelApiKey, String subjectName) {
        this.modelApiKey = modelApiKey;
        this.subjectName = subjectName;
    }

    public String modelApiKey() {
        return modelApiKey;
    }

    public String subjectName() {
        return subjectName;
    }
}
