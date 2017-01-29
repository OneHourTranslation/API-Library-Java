package com.oht.entities;

import com.google.gson.JsonObject;

/**
 * Represents OHT language.
 * Use the {@link com.oht.OHTAPI#getSupportedLanguages() getSupportedLanguages}
 * to get all languages, supported by OHT service
 */
public class Language {

    private String name;
    private String code;

    public Language() {
    }

    public Language(JsonObject json) {
        this();

        this.name = json.get("name").getAsString();
        this.code = json.get("code").getAsString();
    }

    /**
     * Language name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Language code
     * @return
     */
    public String getCode() {
        return code;
    }
}
