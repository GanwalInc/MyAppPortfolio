package com.ganwal.nanodegree.popularMovie;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Movie Review
 */

public class MovieReview implements Parcelable {

    int movieId;
    String movieDBId; //this id is coming from themoviedb
    String author;
    String content;
    String url;

    public MovieReview() {
    }

    public MovieReview(int movieId, String movieDBId, String author, String content, String url) {
        this.movieId = movieId;
        this.movieDBId = movieDBId;
        this.author = author;
        this.content = content;
        this.url = url;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MovieReview{" +
                "movieId='" + movieId + '\'' +
                ", movieDBId='" + movieDBId + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
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
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }


}

