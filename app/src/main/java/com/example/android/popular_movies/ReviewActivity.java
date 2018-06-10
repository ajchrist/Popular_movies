package com.example.android.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popular_movies.data.MovieReview;
import com.example.android.popular_movies.databinding.ActivityReviewBinding;
import com.example.android.popular_movies.utilities.MovieNetworkUtil;
import com.example.android.popular_movies.utilities.MovieParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<List<MovieReview>> {

    private ActivityReviewBinding mSingleMovieReview;
    private static final int MOVIE_REVIEW_LOADER_ID = 1;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        mSingleMovieReview = DataBindingUtil.setContentView(this, R.layout.activity_review);
        Intent mStartingIntent = getIntent();
        mId = mStartingIntent.getStringExtra(getString(R.string.id));
    }

    //test if connected to internet
    //https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void load() {
        if (this.isNetworkAvailable()) {
            getSupportLoaderManager().restartLoader(MOVIE_REVIEW_LOADER_ID, null, ReviewActivity.this);
        } else {
            Toast.makeText(this, getString(R.string.not_connected_error), Toast.LENGTH_LONG).show();
            Toast.makeText(this, getString(R.string.reconnect), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        this.load();
        super.onPostResume();
    }

    @Override
    public Loader<List<MovieReview>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<MovieReview>>(this) {

            Context context = ReviewActivity.this;
            List<MovieReview> movieReviews;

            @Override
            protected void onStartLoading() {
                if (movieReviews != null) {
                    deliverResult(movieReviews);
                }else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(List<MovieReview> movieReviews) {
                this.movieReviews = movieReviews;
                super.deliverResult(movieReviews);
            }

            @Override
            public List<MovieReview> loadInBackground() {
                movieReviews = new ArrayList<>();
                String extension = getString(R.string.reviews);
                try {
                    JSONArray array = MovieNetworkUtil.extendedQuery(mId, extension, context);
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            movieReviews.add(new MovieParser((JSONObject) array.get(i)).parseReview(context));
                        }
                    }
                    return movieReviews;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
        if (!data.isEmpty()){
            mSingleMovieReview.tvSingleMovieReview.setText(builder(data));
        }
    }

    private String builder(List<MovieReview> list){
        StringBuilder builder = new StringBuilder();
        for (MovieReview review : list){
            builder.append(review.getAuthor()).append("\n\n").append(review.getContent()).append("\n\n");
        }
        return builder.toString();
    }

    @Override
    public void onLoaderReset(Loader<List<MovieReview>> loader) {

    }


    // from https://stackoverflow.com/questions/44193058/parent-activity-keeps-reseting-when-navigating-back-from-child
    // I was having issues with SingleMovie not resuming appropriately
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
