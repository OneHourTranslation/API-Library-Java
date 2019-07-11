package com.oht.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents detailed specification of the project
 */
public class ProjectDetails {

    // <editor-fold desc="private members">
    private int projectId;
    private String type;
    private String projectStatus;
    private String statusCode;
    private String sourceLanguage;
    private String targetLanguage;
    private Collection<String> sources;
    private Collection<String> translations;
    private Collection<String> proofs;
    private Collection<String> transcriptions;
    private Collection<String> results;
    private Collection<String> reference;
    private int wordCount;
    private int length;
    private JsonObject custom;
    private String linguistUuid;
    private Collection<String> tags;
    private JsonObject resourceBinding;

    //</editor-fold>

    public ProjectDetails() {
    }

    public ProjectDetails(JsonObject json) {
        this();

        this.projectId = json.get("project_id").getAsInt();
        this.type = json.get("project_type").getAsString();
        this.projectStatus = json.get("project_status").getAsString();
        this.statusCode = json.get("project_status_code").getAsString();
        this.sourceLanguage = json.get("source_language").getAsString();
        this.targetLanguage = json.get("target_language").getAsString();

        if (json.has("resources")) {
            JsonObject resources = json.get("resources").getAsJsonObject();

            // sources
            if (resources.has("sources") && resources.get("sources").isJsonArray()) {
                this.sources = new ArrayList<String>();
                JsonArray array = resources.get("sources").getAsJsonArray();
                for (JsonElement e : array)
                    this.sources.add(e.getAsString());
            }

            // translations
            if (resources.has("translations") && resources.get("translations").isJsonArray()) {
                this.translations = new ArrayList<String>();
                JsonArray array = resources.get("translations").getAsJsonArray();
                for (JsonElement e : array)
                    this.translations.add(e.getAsString());
            }

            // proofs
            if (resources.has("proofs") && resources.get("proofs").isJsonArray()) {
                this.proofs = new ArrayList<String>();
                JsonArray array = resources.get("proofs").getAsJsonArray();
                for (JsonElement e : array)
                    this.proofs.add(e.getAsString());
            }

            // transcriptions
            if (resources.has("transcriptions") && resources.get("transcriptions").isJsonArray()) {
                this.transcriptions = new ArrayList<String>();
                JsonArray array = resources.get("transcriptions").getAsJsonArray();
                for (JsonElement e : array)
                    this.transcriptions.add(e.getAsString());
            }

            // results
            if (resources.has("results") && resources.get("results").isJsonArray()) {
                this.results = new ArrayList<String>();
                JsonArray array = resources.get("results").getAsJsonArray();
                for (JsonElement e : array)
                    this.results.add(e.getAsString());
            }

            // reference
            if (resources.has("reference") && resources.get("reference").isJsonArray()) {
                this.reference = new ArrayList<String>();
                JsonArray array = resources.get("reference").getAsJsonArray();
                for (JsonElement e : array)
                    this.reference.add(e.getAsString());
            }

            // tags
            if (json.has("tags") && json.get("tags").isJsonArray()) {
                this.tags = new ArrayList<String>();
                JsonArray array = json.get("tags").getAsJsonArray();
                for (JsonElement e : array)
                    this.tags.add(e.getAsString());
            }
        }

        if (json.has("wordcount"))
            this.wordCount = json.get("wordcount").getAsInt();

        if (json.has("length"))
            this.length = json.get("length").getAsInt();

        if (json.has("custom")) {
            this.custom = json.get("custom").getAsJsonObject();
        }

        if (json.has("resource_binding")) {
            this.resourceBinding = json.get("resource_binding").getAsJsonObject();
        }

        if (json.has("linguist_uuid") && !json.get("linguist_uuid").isJsonNull()) {
            this.linguistUuid = json.get("linguist_uuid").getAsString();
        }
    }

    /**
     * The unique id of the requested project
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Project type (Translation | Expert Translation | Proofreading | Transcription | Translation + Proofreading)
     */
    public String getType() {
        return type;
    }

    /**
     * Project status
     */
    public String getProjectStatus() {
        return projectStatus;
    }

    /**
     * Status code:
     *  * <b>pending</b> - project submitted to OHT, but professional worker (translator/proofreader) did not start working yet
     *  * <b>in_progress</b> - worker started working on this project
     *  * <b>submitted</b> - the worker uploaded the first target resource to the project.
     *  This does not mean that the project is completed.
     *  * <b>signed</b> - the worker declared (with his signature) that he finished working on this project and all
     *  resources have been uploaded.
     *  * <b>completed</b> - final state of the project, after which we cannot guarantee fixes or corrections.
     *  This state is automatically enforced after 4 days of inactivity on the project.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Source Language
     */
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    /**
     * Target language
     */
    public String getTargetLanguage() {
        return targetLanguage;
    }

    /**
     * List of source resource UUIDs related to the requested project
     */
    public Collection<String> getSources() {
        return sources;
    }

    /**
     * List of translation resource UUIDs related to the requested project
     */
    public Collection<String> getTranslations() {
        return translations;
    }

    /**
     * List of proofreading resource UUIDs related to the requested project
     */
    public Collection<String> getProofs() {
        return proofs;
    }

    /**
     * List of transcription resource UUIDs related to the requested project
     */
    public Collection<String> getTranscriptions() {
        return transcriptions;
    }

    /**
     * List of result resource UUIDs related to the requested project
     */
    public Collection<String> getResults() {
        return results;
    }

    /**
     * List of project tags
     */
    public Collection<String> getTags() {
        return tags;
    }

    /**
     * List of reference resource UUIDs related to the requested project
     */
    public Collection<String> getReference() {
        return reference;
    }

    /**
     * Words count
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Linguist uuid
     */
    public String getLinguistUuid() {
        return linguistUuid;
    }

    /**
     * Length in seconds (transcription projects only)
     */
    public int getLength() {
        return length;
    }

    /**
     * @return JsonObject of custom fields.
     */
    public JsonObject getCustomFields(){
        return custom;
    }

    /**
     * @return JsonObject of resource binding.
     */
    public JsonObject getResourceBinding(){
        return resourceBinding;
    }
}
