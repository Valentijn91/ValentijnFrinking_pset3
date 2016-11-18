package com.appstudio.Valentijn.watchlist;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Valentijn on 16-11-2016.
 */

// https://developer.android.com/guide/topics/data/data-storage.html
public class MovieStorage {

    public static final String PREFS_NAME = "movieIDList";

    private Context context;
    private SharedPreferences sharedPreferences;


    MovieStorage(Context context) {

        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void add(String id) {

        List<String> list = this.get();
        list.add(id);

        // save the task list to preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putStringSet("moviesId", new HashSet<String>(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public List<String> get() {

        List<String> list = new ArrayList<String>();

        Set<String> data = sharedPreferences.getStringSet("moviesId", null);
        if (data != null){
            list.addAll(data);
        }

        if (null == list) {
            return new ArrayList<String>();
        }
        return list;
    }

    public void remove(String id) {

        List<String> list = this.get();

        list.remove(id);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("moviesId", new HashSet<String>(list));
        editor.commit();
    }

    public boolean exists(String id) {
        List<String> list = this.get();
        Iterator<String> iterator = list.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().equals(id)) {
                return true;
            }
        }
        return false;
    }
}

