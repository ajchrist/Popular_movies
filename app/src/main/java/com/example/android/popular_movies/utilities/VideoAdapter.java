package com.example.android.popular_movies.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.data.MovieVideo;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {

    private List<MovieVideo> mMovieVideos = new ArrayList<>();
    final private VideoAdapterOnClickHandler mClickHandler;
    private Context context;


    public VideoAdapter(Context context, VideoAdapterOnClickHandler clickHandler){
        this.context = context;
        mClickHandler = clickHandler;
    }

    public interface VideoAdapterOnClickHandler {
        void onClick(MovieVideo movieVideo);
    }

    @Override
    public VideoAdapter.VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout = R.layout.movie_video;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout, parent, false);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.VideoAdapterViewHolder holder, int position) {
        String imageURL = mMovieVideos.get(position).getImageURL();
        Picasso.with(context).load(imageURL).resize(200,200).into(holder.mImageView);
        holder.mTextView.setText(mMovieVideos.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mMovieVideos == null){
            return 0;
        }
        return mMovieVideos.size();
    }

    public void setmMovieVideos(List<MovieVideo> movieVideos){
        mMovieVideos = movieVideos;
        notifyDataSetChanged();
    }

    class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView = (ImageView) itemView.findViewById(R.id.iv_video_poster);
        TextView mTextView = (TextView) itemView.findViewById(R.id.tv_video_name);

        VideoAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieVideo video = mMovieVideos.get(adapterPosition);
            mClickHandler.onClick(video);
        }
    }
}
