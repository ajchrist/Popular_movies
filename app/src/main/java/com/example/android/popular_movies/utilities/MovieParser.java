package com.example.android.popular_movies.utilities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.data.MovieReview;
import com.example.android.popular_movies.data.MovieVideo;
import com.example.android.popular_movies.data.PopularMovie;
import com.example.android.popular_movies.db.FavoriteMoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieParser {
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w500";
    private static JSONObject mMovieJSON;
    private static Cursor mMovieCursor;

    public MovieParser(JSONObject movieJSON){
        mMovieJSON = movieJSON;
    }

    public MovieParser(Cursor movieCursor){
        mMovieCursor = movieCursor;
    }

    public PopularMovie parseJSON(Context context) throws JSONException {
        String id = String.valueOf(mMovieJSON.getInt(context.getString(R.string.id)));
        String title = mMovieJSON.getString(context.getString(R.string.title));
        String imagePath = mMovieJSON.getString(context.getString(R.string.poster_path)).replace("\\", "");
        String description = mMovieJSON.getString(context.getString(R.string.overview));
        String releaseDate = mMovieJSON.getString(context.getString(R.string.release_date));
        String vote_average = String.valueOf(mMovieJSON.getDouble(context.getString(R.string.vote_average)));
        String imageURL = BASE_IMAGE_URL + imagePath;
        return new PopularMovie(id, title, description, vote_average, releaseDate, imageURL);
    }

    public PopularMovie parseCursor(int i){
        mMovieCursor.moveToPosition(i);
        String id = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID));
        String title = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE));
        String imageURL = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH));
        String description = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW));
        String releaseDate = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE));
        String vote_average = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_RATING));
        return new PopularMovie(id, title, description, vote_average, releaseDate, imageURL);
    }

    public MovieReview parseReview(Context context) throws JSONException {
        String author = mMovieJSON.getString(context.getString(R.string.json_author));
        String content = mMovieJSON.getString(context.getString(R.string.json_content));
        return new MovieReview(author, content);
    }

    public MovieVideo parseVideo(Context context) throws JSONException{
        String key = mMovieJSON.getString(context.getString(R.string.video_key));
        String name = mMovieJSON.getString(context.getString(R.string.video_name));
        return new MovieVideo(key, name);
    }

}
