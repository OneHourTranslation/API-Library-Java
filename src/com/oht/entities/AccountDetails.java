package com.oht.entities;

import com.google.gson.JsonObject;

/**
 * Basic account details and credits balance.
 * To get account details
 * use {@link com.oht.OHTAPI#getAccountDetails() getAccountDetails} method
 */
public class AccountDetails {

    private int accountId;
    private String accountUsername;
    private float credits;
    private String role;
    private String uuid;

    public AccountDetails() {
    }

    public AccountDetails(JsonObject object) {
        this();

        this.accountId = object.get("account_id").getAsInt();
        this.accountUsername = object.get("account_username").getAsString();
        this.credits = object.get("credits").getAsFloat();
        this.role = object.get("role").getAsString();
        this.uuid = object.get("uuid").getAsString();
    }

    /**
     * Unique account id in OHT
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * OHT username
     */
    public String getAccountUsername() {
        return accountUsername;
    }

    /**
     * Currently available credits balance
     */
    public float getAccountCredits() {
        return credits;
    }

    /**
     * OHT role
     */
    public String getAccountRole() {
        return role;
    }

    /**
     * OHT uuid
     */
    public String getAccountUuid() {
        return uuid;
    }
}
