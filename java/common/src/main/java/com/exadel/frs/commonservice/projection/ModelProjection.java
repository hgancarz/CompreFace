package com.exadel.frs.commonservice.projection;

public class ModelProjection {
    private final String name;
    private final String apiKey;

    public ModelProjection(String name, String apiKey) {
        this.name = name;
        this.apiKey = apiKey;
    }

    public String name() {
        return name;
    }

    public String apiKey() {
        return apiKey;
    }
}
