package com.exadel.frs.commonservice.projection;

import java.util.Date;
import java.util.Objects;

public class ModelStatisticProjection {
    private final long requestCount;
    private final Date createdDate;

    public ModelStatisticProjection(long requestCount, Date createdDate) {
        this.requestCount = requestCount;
        this.createdDate = createdDate;
    }

    public long requestCount() {
        return requestCount;
    }

    public Date createdDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelStatisticProjection that = (ModelStatisticProjection) o;
        return requestCount == that.requestCount && Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestCount, createdDate);
    }
}
