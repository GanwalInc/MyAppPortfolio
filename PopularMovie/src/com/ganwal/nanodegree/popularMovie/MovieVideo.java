package com.ganwal.nanodegree.popularMovie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie Video
 */

public class MovieVideo implements Parcelable {

    int movieId;
    String movieDBId; //this id is coming from themoviedb
    String key;
    String name;
    String site;
    String type;

    public MovieVideo() {
    }

    public MovieVideo(int movieId, String movieDBId, String key, String name, String site, String type) {
        this.movieId = movieId;
        this.movieDBId = movieDBId;
        this.key = key;
        this.name = name;
        this.site = site;
        this.type = type;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieDBId() {
        return movieDBId;
    }

    public void setMovieDBId(String movieDBId) {
        this.movieDBId = movieDBId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MovieVideo{" +
                "movieId='" + movieId + '\'' +
                ", movieDBId='" + movieDBId + '\'' +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return this.movieDBId.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(movieDBId);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeString(type);
    }
}
