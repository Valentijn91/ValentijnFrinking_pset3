package com.appstudio.Valentijn.watchlist;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    TextView title, year, genre, actors, plot;
    String id;
    CheckBox checkbox;
    ImageView poster;

    com.appstudio.Valentijn.watchlist.MovieStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        storage = new MovieStorage(this);

        title = (TextView) findViewById(R.id.vTitle);
        year = (TextView) findViewById(R.id.vYear);
        genre = (TextView) findViewById(R.id.vGenre);
        actors = (TextView) findViewById(R.id.vActors);
        plot = (TextView) findViewById(R.id.vPlot);
        checkbox = (CheckBox) findViewById(R.id.vCheckbox);
        poster = (ImageView) findViewById(R.id.vPoster);
        poster.setBackgroundColor(Color.rgb(230, 230, 230));

        String movieId;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                movieId = null;
            } else {
                movieId = extras.getString("Movie");
            }
        } else {
            movieId = (String) savedInstanceState.getSerializable("Movie");
        }

        checkbox.setChecked(storage.exists(movieId));
        new Omdb().execute("http://www.omdbapi.com/?i=" + movieId);
    }

    void onCheckboxClicked (View v) {
        if (checkbox.isChecked()) {
            storage.add(id);
        } else {
            storage.remove(id);
        }
    }
    void updateDetailPage (String result) {

        try {
            JSONObject object = new JSONObject(result);
            Movie movie = new Movie(object);

            new ImageFromUrl().execute(movie.getImageUrl());

            title.setText(movie.getTitle());
            year.setText(movie.getYear());
            genre.setText(movie.getGenre());
            actors.setText(movie.getActors());
            plot.setText(movie.getPlot());
            id = movie.getId();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // https://developer.android.com/reference/android/os/AsyncTask.html
    public class Omdb extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            DetailActivity.this.updateDetailPage(result);

        }
    }

    // https://developer.android.com/reference/android/os/AsyncTask.html
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
            DetailActivity.this.poster.setImageDrawable(image);
        }
    }
}
