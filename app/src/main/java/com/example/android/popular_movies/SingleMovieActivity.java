package com.example.android.popular_movies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.example.android.popular_movies.data.MovieVideo;
import com.example.android.popular_movies.databinding.ActivitySingleMovieBinding;
import com.example.android.popular_movies.db.FavoriteMovieDatabaseHelper;
import com.example.android.popular_movies.db.FavoriteMoviesContract;
import com.example.android.popular_movies.utilities.MovieNetworkUtil;
import com.example.android.popular_movies.utilities.MovieParser;
import com.example.android.popular_movies.utilities.VideoAdapter;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// based off the detailactivty.java from sunshine
// where a single day's weather is received and shown

public class SingleMovieActivity extends AppCompatActivity implements
        VideoAdapter.VideoAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<MovieVideo>> {

    private ActivitySingleMovieBinding mSingleMovie;
    private String mId, mTitle, mPosterPath, mOverview, mReleaseDate, mVoteAverage;
    private VideoAdapter videoAdapter;
    private static final int MOVIE_VIDEO_ID = 2;
    //https://github.com/ajchrist/ud851-Exercises/blob/student/Lesson07-Waitlist/T07.05-Solution-AddGuests/app/src/main/java/com/example/android/waitlist/MainActivity.java
    //is my guide to implementing adding to db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoAdapter = new VideoAdapter(this, this);
        mSingleMovie = DataBindingUtil.setContentView(this, R.layout.activity_single_movie);
        FavoriteMovieDatabaseHelper dbHelper = new FavoriteMovieDatabaseHelper(this);
        Intent mStartingIntent = getIntent();
        mId = mStartingIntent.getStringExtra(getString(R.string.id));
        mTitle = mStartingIntent.getStringExtra(getString(R.string.title));
        mPosterPath = mStartingIntent.getStringExtra(getString(R.string.poster_path));
        mOverview = mStartingIntent.getStringExtra(getString(R.string.overview));
        mReleaseDate = mStartingIntent.getStringExtra(getString(R.string.release_date));
        mVoteAverage = mStartingIntent.getStringExtra(getString(R.string.vote_average));
        mSingleMovie.Favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                editDB(checked, mId, mTitle, mPosterPath, mOverview, mReleaseDate, mVoteAverage);

            }
        });
    }

    private void editDB(boolean checked, String id, String title, String posterPath, String overview, String releaseDate, String voteAverage) {
        if (checked) {
            ContentValues values = new ContentValues();
            values.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, id);
            values.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
            values.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_TITLE, title);
            values.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            values.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            values.put(FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_RATING, voteAverage);
            // insert with content provider
            MovieNetworkUtil.providerInsert(this, values);
        } else {
            // delete with content provider
            MovieNetworkUtil.providerDelete(this,
                    FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " LIKE " + id,
                    null);
        }
    }

    private void setAdapterLayout() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSingleMovie.vRecyclerview.setLayoutManager(manager);
    }

    public void load() {
        if (this.isNetworkAvailable()) {
            getSupportLoaderManager().restartLoader(MOVIE_VIDEO_ID, null, SingleMovieActivity.this);
        } else {
            Toast.makeText(this, getString(R.string.not_connected_error), Toast.LENGTH_LONG).show();
            Toast.makeText(this, getString(R.string.reconnect), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPostResume() {
        this.setLayout();
        this.setAdapterLayout();
        mSingleMovie.vRecyclerview.setAdapter(videoAdapter);
        this.load();
        super.onPostResume();
    }

    void setLayout() {
        Picasso.with(this).load(mPosterPath).into(mSingleMovie.ivSingleMoviePoster);
        mSingleMovie.tvSingleMovieTitle.setText(mTitle);
        mSingleMovie.tvSingleMovieDescription.setText(mOverview);
        mSingleMovie.tvSingleMovieRating.setText(mVoteAverage);
        mSingleMovie.tvSingleMovieReleaseDate.setText(mReleaseDate);
        this.checkInDB();
    }

    private void checkInDB() {
        Cursor inDB;
        // query with content provider
        inDB = MovieNetworkUtil.providerQuery(this,
                new String[]{FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID},
                FavoriteMoviesContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " LIKE " + mId,
                null,
                null);
        int i = inDB.getCount();
        if (i > 0) {
            mSingleMovie.Favorite.setChecked(true);
        }
        inDB.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reviews) {
            Intent startSettingsActivity = new Intent(this, ReviewActivity.class);
            startSettingsActivity.putExtra(getString(R.string.id), mId);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reviews_menu, menu);
        return true;
    }

    @Override
    public void onClick(MovieVideo movieVideo) {
        // https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        // this is how to launch a youtube video in the cleanest way
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieVideo.getVid()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieVideo.getVidURL()));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }


    public Loader<List<MovieVideo>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<MovieVideo>>(this) {

            Context context = SingleMovieActivity.this;
            List<MovieVideo> movieVideos;

            @Override
            protected void onStartLoading() {
                if (movieVideos != null) {
                    deliverResult(movieVideos);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(List<MovieVideo> movieVideos) {
                this.movieVideos = movieVideos;
                super.deliverResult(movieVideos);
            }

            @Override
            public List<MovieVideo> loadInBackground() {
                movieVideos = new ArrayList<>();
                String extension = getString(R.string.videos);
                try {
                    JSONArray array = MovieNetworkUtil.extendedQuery(mId, extension, context);
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            movieVideos.add(new MovieParser((JSONObject) array.get(i)).parseVideo(context));
                        }
                    }
                    return movieVideos;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<List<MovieVideo>> loader, List<MovieVideo> data) {
        videoAdapter.setmMovieVideos(data);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieVideo>> loader) {

    }
}
