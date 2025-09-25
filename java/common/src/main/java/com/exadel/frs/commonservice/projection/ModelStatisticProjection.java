package com.exadel.frs.commonservice.projection;

import java.util.Date;

public class ModelStatisticProjection {

    private final long requestCount;
    private final Date createdDate;

    public ModelStatisticProjection(long requestCount, Date createdDate) {
        this.requestCount = requestCount;
        this.createdDate = createdDate;
    }

    public long requestCount() { return requestCount; }
    public Date createdDate() { return createdDate; }
}
