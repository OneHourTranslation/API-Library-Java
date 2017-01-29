package com.oht.entities;

import com.google.gson.JsonObject;
import com.oht.OHTException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents comment posted on the project page
 */
public class Comment implements Comparable<Comment> {

    private int id;
    private Date date;
    private String commenterName;
    private String commenterRole;
    private String content;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Comment() {
    }

    public Comment(JsonObject json) throws OHTException {
        this();

        this.id = json.get("id").getAsInt();
        this.commenterName = json.get("commenter_name").getAsString();
        this.commenterRole = json.get("commenter_role").getAsString();
        this.content = json.get("comment_content").getAsString();

        try {
            this.date = formatter.parse(json.get("date").getAsString());
        } catch (ParseException e) {
            throw new OHTException(e);
        }
    }

    /**
     * Unique id of the comment
     */
    public int getId() {
        return id;
    }

    /**
     * Date the comment was created
     */
    public Date getDate() {
        return date;
    }

    /**
     * Short representation of the user’s name
     */
    public String getCommenterName() {
        return commenterName;
    }

    /**
     * Role:
     * * <b>admin</b> - OHT support team
     * * <b>provider</b> - The translator / proofreader / transcriber that is assigned to the project
     * * <b>potential-provider</b> - A translator / proofreader / transcriber that was allowed to view the
     * project before it was assigned
     */
    public String getCommenterRole() {
        return commenterRole;
    }

    /**
     * Text content
     */
    public String getContent() {
        return content;
    }

    @Override
    public int compareTo(Comment o) {
        return this.getId() - o.getId();
    }
}
