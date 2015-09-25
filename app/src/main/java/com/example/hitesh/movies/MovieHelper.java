package com.example.hitesh.movies;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hitesh on 9/24/15.
 */
public class MovieHelper {
    private HashMap<String, ArrayList<Movie>> moviesListLookup;

    private String sortKey;

    public MovieHelper(String defaultData, String ratingsData, String votesData) {
        this.moviesListLookup = new HashMap<String, ArrayList<Movie>>();
        try {
            this.moviesListLookup.put("default", this.setMovies(defaultData));
            this.moviesListLookup.put("ratings", this.setMovies(ratingsData));
            this.moviesListLookup.put("votes", this.setMovies(votesData));
        }catch (JSONException e){
        }
    }
    public MovieHelper(String data, String sortBy) {
        this.moviesListLookup = new HashMap<String, ArrayList<Movie>>();
        try {
            this.sortKey = sortBy;
            this.moviesListLookup.put(sortBy, this.setMovies(data));
        }catch (JSONException e){
        }
    }

    public ArrayList<Movie> getMoviesByKey(String key){
        if( this.moviesListLookup.containsKey( key )){
            return this.moviesListLookup.get(key);
        }
        return null;
    }
    public ArrayList<Movie> setMovies(String responseData)
            throws JSONException {
        ArrayList<Movie> moviesList = new ArrayList<Movie>();
        JSONObject respJson = new JSONObject(responseData);
        JSONArray movies = respJson.getJSONArray("results");
        String[] imgurls = new String[movies.length()];
        for(int i=0; i < movies.length(); ++i){
            Movie movie = new Movie(movies.getJSONObject(i).getString("adult"),
                    movies.getJSONObject(i).getString("id"),
                    movies.getJSONObject(i).getString("backdrop_path"),
                    movies.getJSONObject(i).getString("original_title"),
                    movies.getJSONObject(i).getString("overview"),
                    Double.parseDouble(movies.getJSONObject(i).getString("popularity")),
                    movies.getJSONObject(i).getString("poster_path"),
                    movies.getJSONObject(i).getString("release_date"),
                    movies.getJSONObject(i).getString("title"),
                    Double.parseDouble(movies.getJSONObject(i).getString("vote_average")),
                    Integer.parseInt(movies.getJSONObject(i).getString("vote_count"))
            );
            moviesList.add( movie );
            imgurls[i] = "http://image.tmdb.org/t/p/w185//"+movies.getJSONObject(i).getString("backdrop_path");

        }
        return moviesList;

    }


    public HashMap<String, ArrayList<Movie>> getMoviesListLookup() {
        return moviesListLookup;
    }

}
