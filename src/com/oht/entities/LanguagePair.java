package com.oht.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents OHT language pair
 * Use the {@link com.oht.OHTAPI#getSupportedLanguagePairs() getSupportedLanguagePairs}
 * to get all language pairs, supported by OHT service
 */
public class LanguagePair {

    private Language source;
    private Map<Language, String> targets;

    public LanguagePair() {
    }

    public LanguagePair(JsonObject json) {
        this();

        this.source = new Language(json.get("source").getAsJsonObject());
        this.targets = new HashMap<Language, String>();

        JsonArray array = json.get("targets").getAsJsonArray();
        for (JsonElement e : array) {
            Language key = new Language(e.getAsJsonObject());
            String value = e.getAsJsonObject().get("availability").getAsString();

            this.targets.put(key, value);
        }
    }

    /**
     * Source Language
     */
    public Language getSource() {
        return source;
    }

    /**
     * Map. Key is Target language, value is availability
     * Availabilities:
     * * <b>high</b> = work is expected to start
     * * <b>medium</b> = work is expected to start within one day
     * * <b>low</b> = work is expected to start within a week
     */
    public Map<Language, String> getTargets() {
        return targets;
    }
}
