package com.ganwal.nanodegree.popularMovie;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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
        if(movieArray == null || movieArray.length() < 1) {
            return null;
        }
        // Extract the Movie details from the results
        List<Movie>resultList = new ArrayList<Movie>(movieArray.length());
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObj = movieArray.getJSONObject(i);
            Movie movie = new Movie();
            movie.setMovieId(movieObj.getString("id"));
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



}
