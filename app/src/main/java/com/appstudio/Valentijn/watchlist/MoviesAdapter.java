package com.appstudio.Valentijn.watchlist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Valentijn on 16-11-2016.
 */

// https://developer.android.com/training/material/lists-cards.html
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movieList = new ArrayList<Movie>();

    public MoviesAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public int getItemCount() {
        try {
            return movieList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(MovieViewHolder movieViewHolder, int i) {
        Movie ci = movieList.get(i);
        movieViewHolder.vId = ci.getId();
        movieViewHolder.vTitle.setText(ci.getTitle());
        movieViewHolder.vYear.setText(ci.getYear());
        movieViewHolder.vGenre.setText(ci.getGenre());
        movieViewHolder.vPoster.setBackgroundColor(Color.rgb(230,230,230));
        movieViewHolder.setImage(ci.getImageUrl());
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardview, viewGroup, false);

        return new MovieViewHolder(itemView);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView vTitle;
        protected TextView vYear;
        protected TextView vGenre;
        protected ImageView vPoster;
        protected String vId;

        public MovieViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);

            vTitle =  (TextView) v.findViewById(R.id.title);
            vYear = (TextView)  v.findViewById(R.id.year);
            vGenre = (TextView)  v.findViewById(R.id.genre);
            vPoster = (ImageView)  v.findViewById(R.id.poster);
        }

        @Override public void onClick(View clickedView) {

            Intent intent = new Intent(clickedView.getContext(), DetailActivity.class);
            intent.putExtra("Movie", MovieViewHolder.this.vId );

            clickedView.getContext().startActivity(intent);

        }

        void setImage(String url) {
            new ImageFromUrl().execute(url);
        }

        class ImageFromUrl extends AsyncTask<String, Void, Drawable> {

            protected Drawable doInBackground(String... url) {

                try {
                    InputStream is = (InputStream) new URL(url[0]).getContent();
                    Drawable d = Drawable.createFromStream(is, "src name");
                    return d;
                } catch (Exception e) {
                    return null;
                }

            }

            protected void onPostExecute(Drawable image) {
                MovieViewHolder.this.vPoster.setImageDrawable(image);
            }
        }
    }
}
