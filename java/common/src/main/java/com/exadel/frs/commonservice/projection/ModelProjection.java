package com.exadel.frs.commonservice.projection;

import com.exadel.frs.commonservice.enums.ModelType;
import java.time.LocalDateTime;

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

    public String guid() { return guid; }
    public String name() { return name; }
    public String apiKey() { return apiKey; }
    public ModelType type() { return type; }
    public LocalDateTime createdDate() { return createdDate; }

    // JavaBean getters for frameworks (e.g., MapStruct) compatibility on Java 11
    public String getGuid() { return guid; }
    public String getName() { return name; }
    public String getApiKey() { return apiKey; }
    public ModelType getType() { return type; }
    public LocalDateTime getCreatedDate() { return createdDate; }
}
