package com.example.hitesh.movies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hitesh on 9/24/15.
 */
public class Movie implements Parcelable {
    private String adult;
    private String id;
    private String backdrop_path;
    private String original_title;
    private String overview;
    private Double popularity;
    private String poster_path;
    private String release_date;
    private String title;
    private Double vote_average;
    private int vote_count;
    private int localDBID;
    private byte[] movie_poster;
    private float app_user_ratings;


    public Movie(String adult, String id, String backdrop_path, String original_title, String overview,
                 Double popularity, String poster_path, String release_date, String title, Double vote_average,
                 int vote_count) {
        this.adult = adult;
        this.id = id;
        this.backdrop_path = backdrop_path;
        this.original_title = original_title;
        this.overview = overview;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.title = title;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    public Movie(){}
    private Movie(Parcel in) {
        this.adult = in.readString();
        this.id = in.readString();
        this.original_title = in.readString();
        this.backdrop_path = in.readString();
        this.original_title = in.readString();
        this.overview = in.readString();
        this.popularity = in.readDouble();
        this.poster_path = in.readString();
        this.release_date = in.readString();
        this.title = in.readString();
        this.vote_average = in.readDouble();
        this.vote_count = in.readInt();
    }

    public byte[] getMovie_poster(){ return movie_poster;}
    void setMovie_poster(byte[] movie_poster){
        this.movie_poster = movie_poster;
    }
    public String getImageUrl() {
        return "http://image.tmdb.org/t/p/w185//" + this.backdrop_path;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Boolean getAdult() {
        return Boolean.parseBoolean(this.adult);
    }

    public String getId() {
        return id;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getTitle() {
        return title;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public int getLocalDBID(){ return this.localDBID;}
    public void setLocalDBID(int localDBID){this.localDBID = localDBID;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.adult);
        parcel.writeString(this.id);
        parcel.writeString(this.original_title);
        parcel.writeString(this.backdrop_path);
        parcel.writeString(this.original_title);
        parcel.writeString(this.overview);
        parcel.writeDouble(this.popularity);
        parcel.writeString(this.poster_path);
        parcel.writeString(this.release_date);
        parcel.writeString(this.title);
        parcel.writeDouble(this.vote_average);
        parcel.writeInt(this.vote_count);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public float getApp_user_ratings() {
        return app_user_ratings;
    }

    public void setApp_user_ratings(float app_user_ratings) {
        this.app_user_ratings = app_user_ratings;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }
}

