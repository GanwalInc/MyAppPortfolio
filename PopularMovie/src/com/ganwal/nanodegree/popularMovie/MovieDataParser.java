package com.ganwal.nanodegree.popularMovie;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * parses JSON data returned from themoviedb APIs
 */
public class MovieDataParser {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    public List<Movie> getMoviesFromJSONString(String jsonString) throws JSONException {
        // Create a JSON object hierarchy from the results
        JSONObject moviesJSON = new JSONObject(jsonString);
        JSONArray movieArray = moviesJSON.getJSONArray("results");
        if (movieArray == null || movieArray.length() < 1) {
            return null;
        }
        // Extract the Movie details from the results
        List<Movie> resultList = new ArrayList<Movie>(movieArray.length());
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObj = movieArray.getJSONObject(i);
            Movie movie = new Movie();
            movie.setMovieDBId(movieObj.getString("id"));
            movie.setTitle(movieObj.getString("original_title"));
            movie.setSynopsis(movieObj.getString("overview"));
            movie.setThumbnailName(movieObj.getString("poster_path"));
            movie.setUserRating(movieObj.getString("vote_average"));
            movie.setReleaseDate(HelperUtility.convertStringToDate(movieObj.getString("release_date")));
            resultList.add(movie);
            Log.d(LOG_TAG, " *****************************************************");
            Log.d(LOG_TAG, " Movie:" + movie.toString());
        }
        return resultList;
    }


    public List<MovieVideo> getVideosFromJSONString(String jsonString) throws JSONException {
        // Create a JSON object hierarchy from the results
        JSONObject videosJSON = new JSONObject(jsonString);
        JSONArray videoArray = videosJSON.getJSONArray("results");
        if(videoArray == null || videoArray.length() < 1) {
            return null;
        }
        // Extract the Video details from the results
        List<MovieVideo>resultList = new ArrayList<MovieVideo>(videoArray.length());
        for (int i = 0; i < videoArray.length(); i++) {
            JSONObject videoObj = videoArray.getJSONObject(i);
            MovieVideo video = new MovieVideo();
            //currently we are only interested in video type trailers
            String videoType = videoObj.getString("type");
            if(videoType != null && videoType.equalsIgnoreCase("Trailer")) {
                video.setMovieDBId(videoObj.getString("id"));
                video.setKey(videoObj.getString("key"));
                video.setName(videoObj.getString("name"));
                video.setSite(videoObj.getString("site"));
                video.setType(videoType);
                resultList.add(video);
            }

            Log.d(LOG_TAG, " *****************************************************");
            Log.d(LOG_TAG, " Video:" + video.toString());
        }
        return resultList;
    }

    public List<MovieReview> getReviewsFromJSONString(String jsonString) throws JSONException {
        // Create a JSON object hierarchy from the results
        JSONObject reviewsJSON = new JSONObject(jsonString);
        JSONArray reviewArray = reviewsJSON.getJSONArray("results");
        if (reviewArray == null || reviewArray.length() < 1) {
            return null;
        }
        // Extract the review details from the results
        List<MovieReview> resultList = new ArrayList<MovieReview>(reviewArray.length());
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewObj = reviewArray.getJSONObject(i);
            MovieReview review = new MovieReview();
            review.setMovieDBId(reviewObj.getString("id"));
            review.setAuthor(reviewObj.getString("author"));
            review.setContent(reviewObj.getString("content"));
            review.setUrl(reviewObj.getString("url"));
            resultList.add(review);
            Log.d(LOG_TAG, " *****************************************************");
            Log.d(LOG_TAG, " Review:" + review.toString());
        }
        return resultList;
    }


}
