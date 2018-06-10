package com.example.android.popular_movies.data;

// POJO that holds a popular movie
public class PopularMovie {

    private String title;
    private String description;
    private String vote_average;
    private String releaseDate;
    private String imageURL;
    private String id;

    public PopularMovie(String id, String title, String description, String vote_average, String releaseDate, String imageURL){
        this.id = id;
        this.title = title;
        this.description = description;
        this.vote_average = vote_average;
        this.releaseDate = reformatDate(releaseDate);
        this.imageURL = imageURL;
    }

    private String reformatDate(String date){
        if (date.contains("-")) {
            String[] oldDate = date.split("-");
            return String.format("%s/%s/%s", oldDate[1], oldDate[2], oldDate[0]);
        }
        return date;
    }

    public String getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getVote_average(){
        return vote_average;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public String getImageURL(){
        return imageURL;
    }

}
