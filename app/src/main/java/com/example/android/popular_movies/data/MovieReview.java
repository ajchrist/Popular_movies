package com.example.android.popular_movies.data;

public class MovieReview {
    private String author;
    private String content;

    public MovieReview(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getContent() {
        return this.content;
    }
}
