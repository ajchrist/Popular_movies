package com.example.android.popular_movies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popular_movies.db.FavoriteMovieDatabaseHelper;
import com.example.android.popular_movies.db.FavoriteMoviesContract;

import java.util.Objects;


//https://github.com/ajchrist/ud851-Sunshine/blob/student/S09.03-Solution-ContentProviderDelete/app/src/main/java/com/example/android/sunshine/data/WeatherProvider.java
//is what i used to help me create my ContentProvider
public class MovieProvider extends ContentProvider {

    public static final int CODE_FAVORITES = 100;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITES, CODE_FAVORITES);

        return matcher;
    }

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavoriteMovieDatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoriteMovieDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITES:
                db.beginTransaction();
                long successfulInsert = 0;
                try {
                    successfulInsert = db.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (successfulInsert > 0){
                    Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
                }
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;


        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_FAVORITES:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
