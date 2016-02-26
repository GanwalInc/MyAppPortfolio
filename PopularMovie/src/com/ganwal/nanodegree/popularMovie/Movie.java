package com.ganwal.nanodegree.popularMovie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class Movie implements Parcelable {

    int id; //sql lite database id
    String movieDBId;
    String title;
    String thumbnailName;
    String synopsis;
    String userRating;
    Date releaseDate;


    //this field will mostly be empty, only used for storing movies in local db for using in offline mode
    byte[] thumbnailImage;
    List<MovieVideo> trailers;
    List<MovieReview> reviews;

    public Movie() {
    }

    public Movie(int id, String movieDBId, String title, String thumbnailName, String synopsis, String userRating,
                 Date releaseDate, byte[] thumbnailImage, List<MovieVideo> trailers, List<MovieReview> reviews) {
        this.id = id;
        this.movieDBId = movieDBId;
        this.title = title;
        this.thumbnailName = thumbnailName;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.thumbnailImage = thumbnailImage;
        this.trailers = trailers;
        this.reviews = reviews;
    }

    public Movie(Parcel in) {
        this.id = in.readInt();
        this.movieDBId = in.readString();
        this.title = in.readString();
        this.thumbnailName = in.readString();
        this.synopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = HelperUtility.convertLongToDate(in.readLong());
    }

    //getters and setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
    public String getMovieDBId() {
        return movieDBId;
    }

    public void setMovieDBId(String movieDBId) {
        this.movieDBId = movieDBId;
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

    public byte[] getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(byte[] thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }


    public List<MovieVideo> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<MovieVideo> trailers) {
        this.trailers = trailers;
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", movieDBId='" + movieDBId + '\'' +
                ", title='" + title + '\'' +
                ", thumbnailName='" + thumbnailName + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", userRating='" + userRating + '\'' +
                ", releaseDate=" + releaseDate + '\'' +
                ", thumbnailImage=" + thumbnailImage + '\'' +
                ", trailers=" + trailers + '\'' +
                ", reviews=" + reviews+ '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(movieDBId);
        dest.writeString(title);
        dest.writeString(thumbnailName);
        dest.writeString(synopsis);
        dest.writeString(userRating);
        dest.writeLong(HelperUtility.convertDateToLong(releaseDate));
        //should thumbnail image be parceled?
        dest.writeList(trailers);
        dest.writeList(reviews);
    }

    @Override
    public int describeContents() {
        return this.movieDBId.hashCode();
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

}


