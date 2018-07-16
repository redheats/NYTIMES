package com.example.redheats.nytimes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Article {

    String web_url;
    String headline;
    String thumbnail;
    String snippet;

    public String getWeb_url() {
        return web_url;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSnippet() {
        return snippet;
    }

    public Article(){

    }

    public Article(JSONObject jsonObject){
        try{
            this.web_url = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            this.snippet = jsonObject.getString("snippet");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0){
                JSONObject multimediaObject = multimedia.getJSONObject(0);
                this.thumbnail = "https://www.nytimes.com/" + multimediaObject.getString("url");
            }
            else {
                this.thumbnail = "";
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray){
        ArrayList<Article> articles = new ArrayList<>();

        for (int i=0;i<jsonArray.length();i++){
            try {
                articles.add(new Article(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return articles;
    }
}
