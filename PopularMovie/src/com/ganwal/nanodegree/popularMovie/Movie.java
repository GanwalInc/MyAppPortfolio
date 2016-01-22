package com.ganwal.nanodegree.popularMovie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Movie implements Parcelable {

    String movieId;
    String title;
    String thumbnailName;
    String synopsis;
    String userRating;
    Date releaseDate;

    public Movie() { }

    public Movie(String movieId, String title, String thumbnailName, String synopsis, String userRating, Date releaseDate) {
        this.movieId = movieId;
        this.title = title;
        this.thumbnailName = thumbnailName;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public Movie(Parcel in) {
        this.movieId = in.readString();
        this.title = in.readString();
        this.thumbnailName = in.readString();
        this.synopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = HelperUtility.convertLongToDate(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(title);
        dest.writeString(thumbnailName);
        dest.writeString(synopsis);
        dest.writeString(userRating);
        dest.writeLong(HelperUtility.convertDateToLong(releaseDate));
    }

    @Override
    public int describeContents() {
        return this.movieId.hashCode();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };




    //getters and setters
    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId='" + movieId + '\'' +
                ", title='" + title + '\'' +
                ", thumbnailName='" + thumbnailName + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", userRating='" + userRating + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
