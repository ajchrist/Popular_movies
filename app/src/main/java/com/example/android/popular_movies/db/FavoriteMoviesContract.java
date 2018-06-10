package com.example.android.popular_movies.db;

import android.net.Uri;
import android.provider.BaseColumns;

// using https://github.com/ajchrist/ud851-Exercises/tree/student/Lesson07-Waitlist
// as a guide to how to best implement a local sql database in android

public class FavoriteMoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popular_movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_RATING = "movieRating";
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";
        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
    }
}
