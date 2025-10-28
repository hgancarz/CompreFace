package com.exadel.frs.commonservice.projection;

import com.exadel.frs.commonservice.enums.ModelType;
import java.time.LocalDateTime;
import java.util.Objects;

public class ModelProjection {
    private final String guid;
    private final String name;
    private final String apiKey;
    private final ModelType type;
    private final LocalDateTime createdDate;

    public ModelProjection(String guid, String name, String apiKey, ModelType type, LocalDateTime createdDate) {
        this.guid = guid;
        this.name = name;
        this.apiKey = apiKey;
        this.type = type;
        this.createdDate = createdDate;
    }

    public String guid() {
        return guid;
    }

    public String name() {
        return name;
    }

    public String apiKey() {
        return apiKey;
    }

    public ModelType type() {
        return type;
    }

    public LocalDateTime createdDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelProjection that = (ModelProjection) o;
        return Objects.equals(guid, that.guid) && 
               Objects.equals(name, that.name) && 
               Objects.equals(apiKey, that.apiKey) && 
               type == that.type && 
               Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, name, apiKey, type, createdDate);
    }
}
