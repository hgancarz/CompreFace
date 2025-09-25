package com.exadel.frs.commonservice.projection;

public class ModelProjection {
    private final String name;
    private final String type;

    public ModelProjection(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }
}
