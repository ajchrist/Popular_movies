package com.example.android.popular_movies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.data.PopularMovie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// based on ForecastAdapter.java from sunshine project
// written during phase 1 of udacity AND GWG scholarship

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private List<PopularMovie> mPopularMovies = new ArrayList<>();
    private Context context;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler){
        this.context = context;
        mClickHandler = clickHandler;

    }

    final private MovieAdapterOnClickHandler mClickHandler;



    public interface MovieAdapterOnClickHandler {
        void onClick(PopularMovie movie);
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout = R.layout.popular_movie;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String imageURL = mPopularMovies.get(position).getImageURL();
        Picasso.with(context).load(imageURL).into(holder.mImageView);
    }

    public void setmPopularMovies(List<PopularMovie> popularMovies){
        mPopularMovies = popularMovies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mPopularMovies == null){
            return 0;
        }
        return mPopularMovies.size();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

        MovieAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            PopularMovie movie = mPopularMovies.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }
}
