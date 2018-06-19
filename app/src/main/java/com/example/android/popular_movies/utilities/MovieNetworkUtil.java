package com.example.android.popular_movies.utilities;

// adapted from NetworkUtil.java from T05b.03-exercise-PolishAsyncTask
// from the toybox in stage 1

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.db.FavoriteMoviesContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MovieNetworkUtil {

    private final static String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular";
    private final static String TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private final static String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String API_KEY = "?api_key=<KEY>";


    public static JSONArray query(String setting, Context context) throws IOException, JSONException {
        URL url = null;
        switch (setting) {
            case "popular":
                url = new URL(Uri.parse(POPULAR_URL + API_KEY).buildUpon().build().toString());
                break;
            case "top_rated":
                url = new URL(Uri.parse(TOP_RATED_URL + API_KEY).buildUpon().build().toString());
                break;
        }
        if (url != null) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");
                if (scanner.hasNext()) {
                    return new JSONObject(scanner.next()).getJSONArray(context.getString(R.string.results));
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static JSONArray extendedQuery(String id, String extension, Context context) throws IOException, JSONException {

        URL url = new URL(Uri.parse(BASE_URL + id + extension + API_KEY).buildUpon().build().toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return new JSONObject(scanner.next()).getJSONArray(context.getString(R.string.results));
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // new method to abstract the use of the content provider query method
    public static Cursor providerQuery(Context context, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return context.getContentResolver().query(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder,
                null);
    }

    // new method to abstract the use of the content provider insert method
    public static void providerInsert(Context context, ContentValues contentValues){
        context.getContentResolver().insert(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
    }

    // new method to abstract the use of the content provider delete method
    public static void providerDelete(Context context, String selection, String[] selectionArgs){
        context.getContentResolver().delete(FavoriteMoviesContract.FavoriteMovieEntry.CONTENT_URI, selection, selectionArgs);
    }

}
