package com.oht.entities;

import com.google.gson.JsonObject;

/**
 * Order summary for specific resource
 */
public class ResourceQuote {

    private String uuid;
    private int wordCount;
    private float credits;
    private float price;

    public ResourceQuote() {
    }

    public ResourceQuote(JsonObject json) {
        this();

        this.uuid = json.get("resource").getAsString();
        this.wordCount = json.get("wordcount").getAsInt();
        this.credits = json.get("credits").getAsFloat();
        this.price = json.get("price").getAsFloat();
    }

    /**
     * UUID of the resource
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Word count of the resource
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Credits worth of the resource
     */
    public float getCredits() {
        return credits;
    }

    /**
     * Price of the resource
     */
    public float getPrice() {
        return price;
    }
}
