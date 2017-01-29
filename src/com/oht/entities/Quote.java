package com.oht.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents order summary
 */
public class Quote {

    private String currency;
    private Collection<ResourceQuote> resources;
    private int totalWordCount;
    private float totalCredits;
    private float netPrice;
    private float transactionFee;
    private float totalPrice;

    public Quote() {
    }

    public Quote(JsonObject json) {
        this();

        this.currency = json.get("currency").getAsString();

        JsonObject total = json.get("total").getAsJsonObject();
        this.totalWordCount = total.get("wordcount").getAsInt();
        this.totalCredits = total.get("credits").getAsFloat();
        this.netPrice = total.get("net_price").getAsFloat();
        this.transactionFee = total.get("transaction_fee").getAsFloat();
        this.totalPrice = total.get("price").getAsFloat();

        if (json.has("resources")) {
            List<ResourceQuote> resources = new ArrayList<ResourceQuote>();
            JsonArray array = json.get("resources").getAsJsonArray();
            for (JsonElement e : array) {
                resources.add(new ResourceQuote(e.getAsJsonObject()));
            }
            this.resources = resources;
        }
    }

    /**
     * Currency selected by user (or default)
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Collection of results per resource. See {@link ResourceQuote ResourceQuote}
     */
    public Collection<ResourceQuote> getResources() {
        return resources;
    }

    /**
     * Total words count
     */
    public int getTotalWordCount() {
        return totalWordCount;
    }

    /**
     * Sum of credits to charge
     */
    public float getTotalCredits() {
        return totalCredits;
    }

    /**
     * Price in selected currency, based on credits and discounts
     */
    public float getNetPrice() {
        return netPrice;
    }

    /**
     * Price in selected currency, based on fee from payment vendors
     */
    public float getTransactionFee() {
        return transactionFee;
    }

    /**
     * Total price in selected currency, based on net price and transaction fee.
     */
    public float getTotalPrice() {
        return totalPrice;
    }

}
