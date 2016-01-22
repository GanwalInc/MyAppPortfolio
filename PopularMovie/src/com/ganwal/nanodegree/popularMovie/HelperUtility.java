package com.ganwal.nanodegree.popularMovie;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * As name says, helper class for most commonly used functionality that doesn't belong in  activities or fragments
 */

public class HelperUtility {

    private static final String LOG_TAG = HelperUtility.class.getSimpleName();
    public static final String MOVIE_DB_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=:1&api_key=:2";
    public static final String MOVIES_POPULARITY_SORT_ORDER = "popularity.desc";
    public static final String MOVIES_RATINGS_SORT_ORDER = "vote_average.desc";
    public static final String MOVIES_POPULARITY_SORT_ORDER_VALUE = "1";
    public static final String MOVIES_RATINGS_SORT_ORDER_VALUE = "2";
    public static final String MOVIE_API_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String DEFAULT_IMAGE_SIZE = "w185";

    public static String imageSize = DEFAULT_IMAGE_SIZE;

    public static String getImageSize() {
        return imageSize;
    }

    public static void setImageSize(String imageSize) {
        HelperUtility.imageSize = imageSize;
    }



    public static Date convertLongToDate(Long longDate){
        return (longDate != null
                 && longDate.compareTo(Long.MIN_VALUE) != 0 ) ? new Date(longDate) : null;
    }

    public static Long convertDateToLong(Date date){
        return date != null ? date.getTime() : Long.MIN_VALUE;
    }

    public static Date convertStringToDate(String dateStr){
        //assuming the date is in the format returned from movieDBapi is 2015-08-14
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            if(isStringNullOREmpty(dateStr)) {
                Log.w(LOG_TAG, "WARNING - Received empty/null Date String");
            } else {
                newDate = format.parse(dateStr);
            }

        } catch (ParseException e) {
            Log.e(LOG_TAG, "ERROR -  Can't parse date string" + e.getMessage());
        }
        return newDate;
    }

    public static String convertDatetoString(Date date){

        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        String dateStr = new String();
        if(date != null) {
            dateStr = format.format(date);
        }
        return dateStr;

    }

    public static String buildPosterImageURL(String imagePath) {
        StringBuffer posterPath = new StringBuffer();
        posterPath.append(HelperUtility.MOVIE_API_IMAGE_BASE_URL)
                .append(getImageSize())
                .append(imagePath);
        return posterPath.toString();
    }

    public static boolean isStringNullOREmpty(String str) {
        return (str == null | str.trim().length() < 1 ) ? true : false;
    }


    public static String findImageSize(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        String default_size = DEFAULT_IMAGE_SIZE;
        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                Log.d(LOG_TAG, "SCREEN SIZE SMALL");
                return default_size;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                Log.d(LOG_TAG, "SCREEN SIZE NORMAL");
                return default_size;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                Log.d(LOG_TAG, "SCREEN SIZE LARGE");
                return default_size;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                Log.d(LOG_TAG, "SCREEN SIZE XLARGE");
                return "w342";
            default:
                Log.d(LOG_TAG, "Can't find SCREEN SIZE");
                return default_size;
        }
    }

}
