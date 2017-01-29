package com.oht.entities;

import com.google.gson.JsonObject;

/**
 * Represents OHT resource
 * To get resource information
 * use {@link com.oht.OHTAPI#getResource(String, Integer, String)} getResource} method
 * */
public class Resource {

    private String type;
    private int length;
    private String fileName;
    private String fileMime;
    private String downloadUrl;
    private String content;

    public Resource() {
    }

    public Resource(JsonObject json) {
        this();

        this.type = json.get("type").getAsString();
        this.length = json.get("length").getAsInt();
        if(json.has("file_name")) {
            this.fileName = json.get("file_name").getAsString();
        }
        if(json.has("file_mime")){
            this.fileMime = json.get("file_mime").getAsString();
        }

        this.downloadUrl = json.get("download_url").getAsString();

        if (null != json.get("content"))
            this.content = json.get("content").getAsString();
    }

    /**
     * Type of resource
     * (text or file)
     */
    public String getType() {
        return type;
    }

    /**
     * File size in bytes
     */
    public int getLength() {
        return length;
    }

    /**
     * File name (only for files)
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * File MIME (only for files)
     */
    public String getFileMime() {
        return fileMime;
    }

    /**
     * URL to download as file
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getContent() {
        return content;
    }
}
