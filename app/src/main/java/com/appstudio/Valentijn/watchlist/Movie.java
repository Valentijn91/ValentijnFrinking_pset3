package com.appstudio.Valentijn.watchlist;

import org.json.JSONObject;

/**
 * Created by Valentijn on 16-11-2016.
 */

public class Movie {
    private String id;
    private String title, year, genre, actors, plot, imageUrl;

    // https://developer.android.com/reference/org/json/JSONObject.html
    // http://www.tutorialspoint.com/android/android_json_parser.htm
    Movie(JSONObject o) throws Exception {
        if (o.has("imdbID")) {
            this.id = o.getString("imdbID");
        } else {
            throw new Exception("IMDB ID Not found");
        }

        this.title = o.has("Title") ? o.getString("Title") : "Unknown Title";
        this.year = o.has("Year") ? o.getString("Year") : "";
        this.genre = o.has("Genre") ? o.getString("Genre") : "";
        this.imageUrl = o.has("Poster") ? o.getString("Poster") : "";
        this.actors = o.has("Actors") ? o.getString("Actors") : "";
        this.plot = o.has("Plot") ? o.getString("Plot") : "";

    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getActors() {
        return actors;
    }

    public String getGenre() {
        return genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId(){
        return id;
    }

    public String getPlot() {
        return plot;
    }
}
