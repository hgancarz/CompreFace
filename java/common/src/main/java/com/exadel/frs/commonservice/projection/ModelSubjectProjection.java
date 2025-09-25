package com.exadel.frs.commonservice.projection;

public class ModelSubjectProjection {
    private final String modelGuid;
    private final long subjectCount;

    public ModelSubjectProjection(String modelGuid, long subjectCount) {
        this.modelGuid = modelGuid;
        this.subjectCount = subjectCount;
    }

    public String modelGuid() {
        return modelGuid;
    }

    public long subjectCount() {
        return subjectCount;
    }
}
