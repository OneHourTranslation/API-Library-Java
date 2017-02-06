package com.oht.entities;

import com.google.gson.JsonObject;

/**
 * Short representation of the project
 */
public class Project {

    private int projectId;
    private int wordCount;
    private float credits;

    public Project() {
    }

    public Project(JsonObject json) {
        this();

        this.projectId = json.get("project_id").getAsInt();
        this.wordCount = json.get("wordcount").getAsInt();
        this.credits = json.get("credits").getAsFloat();
    }

    /**
     * Unique id of the newly created project
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Total word count of the newly project
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Total credit worth of the newly project
     */
    public float getCredits() {
        return credits;
    }
}
