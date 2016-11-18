package com.appstudio.Valentijn.watchlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    MovieStorage storage;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView.Adapter movieAdapter;

    // Search
    private EditText searchBox;
    private Button searchButton;

    private ProgressDialog pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        storage = new MovieStorage(this);

        // Search
        searchBox = (EditText) findViewById(R.id.searchBox);
        searchButton = (Button) findViewById(R.id.searchButton);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recList.setLayoutManager(llm);

        movieAdapter = new MoviesAdapter(movieList);
        recList.setAdapter(movieAdapter);

        startFetchingDetails();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        reloadList();
    }

    @Override
    public void onBackPressed() {
        reloadList();
    }

    void reloadList(){
        startFetchingDetails();
        movieList.clear();
    }

    void searchButtonClicked(View v) {

        pd = new ProgressDialog(ListActivity.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        String search = searchBox.getText().toString();

        if (!search.equals("")) {
            Omdb api = new Omdb();
            api.isSearch = true;
            try {
                api.execute("http://www.omdbapi.com/?type=movie&s=" + URLEncoder.encode(search, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    void prepareSearchResults(String result) {
        String responceText = "No results";

        try {
            JSONObject object = new JSONObject(result);
            if (object.getBoolean("Response")) {
                JSONArray a = object.getJSONArray("Search");
                if (a.length() != 0) {
                    movieList.clear();
                    responceText = a.length() + " results";

                    for (int i = 0; i < a.length(); i++) {
                        try {
                            movieList.add(new Movie(a.getJSONObject(i)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    movieAdapter.notifyDataSetChanged();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            responceText = "Error has occured";
        }

        pd.dismiss();
        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, responceText, duration);
        toast.show();
    }

    void startFetchingDetails() {

        List<String> list = storage.get();

        if (list.size() == 0) {
            movieAdapter.notifyDataSetChanged();
            return;
        }

        for (String id: list) {
            updateList(id);
        }
    }

    void updateList(String id) {

        if (null == id) {
            return;
        }

        new Omdb().execute("http://www.omdbapi.com/?i=" + id);

    }

    void loadItemsInList(String result) {

        try {
            JSONObject object = new JSONObject(result);
            Movie movie = new Movie(object);
            movie.getId();
            movieList.add(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        movieAdapter.notifyDataSetChanged();
    }

    // https://developer.android.com/reference/android/os/AsyncTask.html
    public class Omdb extends AsyncTask<String, String, String> {

        Boolean isSearch = false;

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

            if (isSearch) {
                ListActivity.this.prepareSearchResults(result);
            } else {
                ListActivity.this.loadItemsInList(result);
            }
        }
    }
}
