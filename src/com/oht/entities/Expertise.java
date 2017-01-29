package com.oht.entities;

import com.google.gson.JsonObject;

/**
 * Represents OHT expertise
 * Use {@link com.oht.OHTAPI#getSupportedExpertises(String, String) getSupportedExpertises} method
 * to get all expertises supported by OHT service
 * */
public class Expertise {

    private int expertiseId;
    private String name;
    private String code;

    public Expertise() {
    }

    public Expertise(JsonObject json) {
        this();

        this.name = json.get("name").getAsString();
        this.code = json.get("code").getAsString();
        this.expertiseId = json.get("expertise_id").getAsInt();
    }

    /**
     * Expertise Id
     */
    public int getId() {
        return expertiseId;
    }

    /**
     * Expertise name
     */
    public String getName() {
        return name;
    }

    /**
     * Expertise code
     */
    public String getCode() {
        return code;
    }
}
