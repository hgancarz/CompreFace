package com.exadel.frs.commonservice.projection;

public class ModelSubjectProjection {

    private final String guid;
    private final Long subjectCount;

    public ModelSubjectProjection(String guid, Long subjectCount) {
        this.guid = guid;
        this.subjectCount = subjectCount;
    }

    public String guid() { return guid; }
    public Long subjectCount() { return subjectCount; }
}
