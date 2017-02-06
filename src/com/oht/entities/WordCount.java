package com.oht.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Represent the Word counter for resource(s),
 * specified by {@link com.oht.OHTAPI#getWordCount(String[]) getWordCount API call}
 */
public class WordCount {

    private int totalWordCount;
    private Map<String, Integer> resources;

    public WordCount() {
    }

    public WordCount(JsonObject json) {
        JsonObject total = json.get("total").getAsJsonObject();
        this.totalWordCount = total.get("wordcount").getAsInt();

        if (json.has("resources")) {
            JsonArray array = json.get("resources").getAsJsonArray();
            this.resources = new HashMap<String, Integer>();
            for (JsonElement e : array) {
                JsonObject resource = e.getAsJsonObject();
                String key = resource.get("resource").getAsString();
                int value = resource.get("wordcount").getAsInt();
                this.resources.put(key, value);
            }
        }
    }

    /**
     * Total words count
     */
    public int getTotalWordCount() {
        return totalWordCount;
    }

    /**
     * Map of results per resource.
     * Key is Resource UUID, value is word count
     */
    public Map<String, Integer> getResources() {
        return resources;
    }
}
