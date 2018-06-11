package com.example.android.popular_movies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popular_movies.db.FavoriteMoviesContract.FavoriteMovieEntry;

public class FavoriteMovieDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoriteMovies.db";
    private static final int DATABASE_VERSION = 2;

    public FavoriteMovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_MOVIE_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                "UNIQUE (" + FavoriteMovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
