package com.oht.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by oht on 29/01/17.
 */
public class Tags {

    private Map<Integer, String> tags;

    public Tags(){
    }

    public Tags(JsonObject json){
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, String>>(){}.getType();
        this.tags = gson.fromJson(json, type);
    }

    public Map<Integer, String> getTags(){
        return tags;
    }

}
