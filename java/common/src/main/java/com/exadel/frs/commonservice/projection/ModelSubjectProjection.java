package com.exadel.frs.commonservice.projection;

import java.util.Objects;

public class ModelSubjectProjection {
    private final String guid;
    private final Long subjectCount;

    public ModelSubjectProjection(String guid, Long subjectCount) {
        this.guid = guid;
        this.subjectCount = subjectCount;
    }

    public String guid() {
        return guid;
    }

    public Long subjectCount() {
        return subjectCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelSubjectProjection that = (ModelSubjectProjection) o;
        return Objects.equals(guid, that.guid) && Objects.equals(subjectCount, that.subjectCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, subjectCount);
    }
}
