package com.example.android.popular_movies.data;

public class MovieVideo {
    private String key;
    private String name;
    private static final String BASE_IMG_URL = "https://img.youtube.com/vi/";
    private static final String BASE_VIDEO_URL = "https://www.youtube.com/watch?v=";
    private static final String BASE_VID = "vnd.youtube:";

    public MovieVideo(String key, String name){
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getImageURL(){
        return BASE_IMG_URL+this.getKey()+"/0.jpg";
    }

    public String getVid(){
        return BASE_VID+this.getKey();
    }

    public String getVidURL(){
        return BASE_VIDEO_URL+this.getKey();
    }
}
