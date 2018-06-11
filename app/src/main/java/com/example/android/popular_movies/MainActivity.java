package com.example.android.popular_movies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popular_movies.data.PopularMovie;
import com.example.android.popular_movies.databinding.ActivityMoviesBinding;
import com.example.android.popular_movies.db.FavoriteMovieDatabaseHelper;
import com.example.android.popular_movies.db.FavoriteMoviesContract;
import com.example.android.popular_movies.utilities.MovieAdapter;
import com.example.android.popular_movies.utilities.MovieNetworkUtil;
import com.example.android.popular_movies.utilities.MovieParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<PopularMovie>>,
        MovieAdapter.MovieAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private MovieAdapter movieAdapter;
    private SharedPreferences mSharedPrefs;
    private ActivityMoviesBinding moviesBinding;
    private SQLiteDatabase mDB;
    private static final int MOVIE_LOADER_ID = 0;

    public SharedPreferences getSharedPrefs() {
        return mSharedPrefs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FavoriteMovieDatabaseHelper dbHelper = new FavoriteMovieDatabaseHelper(this);
        mDB = dbHelper.getReadableDatabase();
        movieAdapter = new MovieAdapter(this, this);
        setContentView(R.layout.activity_movies);
        moviesBinding = DataBindingUtil.setContentView(this, R.layout.activity_movies);
        moviesBinding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });
        this.reorient();
        moviesBinding.movieRecyclerview.setAdapter(movieAdapter);
        setupSharedPreferences();
    }

    @Override
    protected void onPostResume() {
        this.reorient();
        this.load();
        super.onPostResume();
    }

    private void reorient() {
        GridLayoutManager mLayoutManager;
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            mLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
            moviesBinding.movieRecyclerview.setLayoutManager(mLayoutManager);
        } else if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            moviesBinding.movieRecyclerview.setLayoutManager(mLayoutManager);
        }
    }

    private void setupSharedPreferences() {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    public void load() {
        if (this.isNetworkAvailable()) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
        } else {
            moviesBinding.swipe.setRefreshing(false);
            Toast.makeText(this, getString(R.string.not_connected_error), Toast.LENGTH_LONG).show();
            Toast.makeText(this, getString(R.string.reconnect), Toast.LENGTH_LONG).show();
        }
    }

    //test if connected to internet
    //https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    //start activity that will show a single movie
    @Override
    public void onClick(PopularMovie movie) {
        Intent startSingleMovieActivity = new Intent(this, SingleMovieActivity.class);
        startSingleMovieActivity.putExtra(getString(R.string.id), movie.getId());
        startSingleMovieActivity.putExtra(getString(R.string.title), movie.getTitle());
        startSingleMovieActivity.putExtra(getString(R.string.poster_path), movie.getImageURL());
        startSingleMovieActivity.putExtra(getString(R.string.overview), movie.getDescription());
        startSingleMovieActivity.putExtra(getString(R.string.release_date), movie.getReleaseDate());
        startSingleMovieActivity.putExtra(getString(R.string.vote_average), movie.getVote_average());
        startActivity(startSingleMovieActivity);
    }

    @Override
    public Loader<List<PopularMovie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<PopularMovie>>(this) {

            Context context = MainActivity.this;
            List<PopularMovie> popularMovies;

            @Override
            protected void onStartLoading() {
                if (popularMovies != null) {
                    deliverResult(popularMovies);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(List<PopularMovie> popularMovies) {
                this.popularMovies = popularMovies;
                super.deliverResult(popularMovies);
            }

            @Override
            public List<PopularMovie> loadInBackground() {
                popularMovies = new ArrayList<>();
                SharedPreferences sharedPreferences = getSharedPrefs();
                String setting = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular_value));
                if (setting.equals(getString(R.string.pref_sort_favorites))) {
                    Cursor mCursor = mDB.query(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
                    MovieParser parser = new MovieParser(mCursor);
                    if (mCursor.getCount() > 0) {
                        for (int i = 0; i < mCursor.getCount(); i++) {
                            popularMovies.add(parser.parseCursor(i));
                        }
                    }
                    mCursor.close();
                    return popularMovies;
                }
                try {
                    JSONArray array = MovieNetworkUtil.query(setting, context);
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            popularMovies.add(new MovieParser((JSONObject) array.get(i)).parseJSON(context));
                        }
                    }
                    return popularMovies;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<PopularMovie>> loader, List<PopularMovie> data) {
        movieAdapter.setmPopularMovies(data);
        moviesBinding.swipe.setRefreshing(false);
        if (data != null) {
            if (data.isEmpty() && getSharedPrefs().getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_popular_value)).equals(getString(R.string.pref_sort_favorites))) {
                Toast.makeText(this, getString(R.string.no_favorites), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<PopularMovie>> loader) {
    }

    @Override
    public void onRefresh() {
    }
}
