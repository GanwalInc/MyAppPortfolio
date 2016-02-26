package com.ganwal.nanodegree.popularMovie.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import com.ganwal.nanodegree.popularMovie.HelperUtility;
import com.ganwal.nanodegree.popularMovie.Movie;
import com.ganwal.nanodegree.popularMovie.MovieReview;
import com.ganwal.nanodegree.popularMovie.MovieVideo;

/**
 * Helper class for Movie Content Provider
 */
public class ProviderContract {

    public static final String CONTENT_AUTHORITY = "com.ganwal.nanodegree.popularMovie.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /********************************** Movie *****************************************/
    public static class MovieEntry implements BaseColumns {
        //table name
        public static final String TABLE_MOVIE = "movie";
        //movie table columns
        public static final String _ID = "_id";
        public static final String COL_MOVIE_DB_ID = "movie_db_id";
        public static final String COL_TITLE = "title";
        public static final String COL_THUMBNAIL_NAME = "thumbnail_name";
        public static final String COL_SYNOPSIS = "synopsis";
        public static final String COL_USER_RATING = "user_rating";
        public static final String COL_RELEASE_DATE = "release_date";
        public static final String COL_THUMBNAIL_IMG = "thumbnail_img";

        public  static String[] projections = { _ID, COL_MOVIE_DB_ID, COL_TITLE,
                COL_THUMBNAIL_NAME, COL_SYNOPSIS,
                COL_USER_RATING, COL_RELEASE_DATE, COL_THUMBNAIL_IMG };


        //build the uris
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_MOVIE).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE;

        public static Uri getMovieUri (long id) {
            return ContentUris.withAppendedId(BASE_CONTENT_URI, id);
        }

        public static ContentValues loadContentValues(Movie movie) {
            ContentValues values = new ContentValues();
            values.put(ProviderContract.MovieEntry.COL_MOVIE_DB_ID, movie.getMovieDBId());
            values.put(ProviderContract.MovieEntry.COL_TITLE, movie.getTitle());
            values.put(ProviderContract.MovieEntry.COL_THUMBNAIL_NAME, movie.getThumbnailName());
            values.put(ProviderContract.MovieEntry.COL_SYNOPSIS, movie.getSynopsis());
            values.put(ProviderContract.MovieEntry.COL_USER_RATING, movie.getUserRating());
            values.put(ProviderContract.MovieEntry.COL_RELEASE_DATE, HelperUtility.convertDateToLong(movie.getReleaseDate()));
            if(movie.getThumbnailImage() != null) {
                values.put(ProviderContract.MovieEntry.COL_THUMBNAIL_IMG, movie.getThumbnailImage());
            }
            return values;
        }


        public static Movie cursorToMovie(Cursor cursor) {
            Movie movie = new Movie();
            int i = -1;
            movie.setId(cursor.getInt(++i));
            movie.setMovieDBId(cursor.getString(++i));
            movie.setTitle(cursor.getString(++i));
            movie.setThumbnailName(cursor.getString(++i));
            movie.setSynopsis(cursor.getString(++i));
            movie.setUserRating(cursor.getString(++i));
            movie.setReleaseDate(HelperUtility.convertLongToDate(cursor.getLong(++i)));
            byte[] thumbnailImage =cursor.getBlob(++i);
            if(thumbnailImage != null) {
                movie.setThumbnailImage(thumbnailImage);
            }
            return movie;
        }

    }

    /********************************** Movie  Video *****************************************/
    public static class MovieVideoEntry implements BaseColumns {
        //table name
        public static final String TABLE_MOVIE_VIDEO = "movie_video";
        //movie video table columns
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_MOVIE_DB_ID = "movie_db_id";
        public static final String COL_KEY = "key";
        public static final String COL_NAME = "name";
        public static final String COL_SITE = "site";
        public static final String COL_TYPE = "type";



        public  static String[] projections = {COL_MOVIE_ID,
                COL_MOVIE_DB_ID, COL_KEY,
                COL_NAME, COL_SITE,
                COL_TYPE};

        //build the uris
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_MOVIE_VIDEO).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_VIDEO;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_VIDEO;



        public static ContentValues loadContentValues(MovieVideo movieVideo) {
            ContentValues values = new ContentValues();
           // values.put(ProviderContract.MovieVideoEntry.COL_MOVIE_ID, movieVideo.getMovieId());
            values.put(ProviderContract.MovieVideoEntry.COL_MOVIE_DB_ID, movieVideo.getMovieDBId());
            values.put(ProviderContract.MovieVideoEntry.COL_KEY, movieVideo.getKey());
            values.put(ProviderContract.MovieVideoEntry.COL_NAME, movieVideo.getName());
            values.put(ProviderContract.MovieVideoEntry.COL_SITE, movieVideo.getSite());
            values.put(ProviderContract.MovieVideoEntry.COL_TYPE, movieVideo.getType());
            return values;
        }


        public static MovieVideo cursorToMovieVideo(Cursor cursor) {
            MovieVideo movieVideo = new MovieVideo();
            int i = -1;
            movieVideo.setMovieId(cursor.getInt(++i));
            movieVideo.setMovieDBId(cursor.getString(++i));
            movieVideo.setKey(cursor.getString(++i));
            movieVideo.setName(cursor.getString(++i));
            movieVideo.setSite(cursor.getString(++i));
            movieVideo.setType(cursor.getString(++i));
            return movieVideo;
        }
    }

    /********************************** Movie Review *****************************************/

    public static class MovieReviewEntry implements BaseColumns {
        //table name
        public static final String TABLE_MOVIE_REVIEW = "movie_review";
        //movie video table columns
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_MOVIE_DB_ID = "movie_db_id";
        public static final String COL_AUTHOR = "author";
        public static final String COL_CONTENT = "content";
        public static final String COL_URL = "url";



        public  static String[] projections = {COL_MOVIE_ID,
                COL_MOVIE_DB_ID, COL_AUTHOR,
                COL_CONTENT, COL_URL};


        //build the uris
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_MOVIE_REVIEW).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_REVIEW;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIE_REVIEW;


        public static ContentValues loadContentValues(MovieReview movieReview) {
            ContentValues values = new ContentValues();
            values.put(ProviderContract.MovieReviewEntry.COL_MOVIE_ID, movieReview.getMovieId());
            values.put(ProviderContract.MovieReviewEntry.COL_MOVIE_DB_ID, movieReview.getMovieDBId());
            values.put(ProviderContract.MovieReviewEntry.COL_AUTHOR, movieReview.getAuthor());
            values.put(ProviderContract.MovieReviewEntry.COL_CONTENT, movieReview.getContent());
            values.put(ProviderContract.MovieReviewEntry.COL_URL, movieReview.getUrl());
            return values;
        }


        public static MovieReview cursorToMovieReview(Cursor cursor) {
            MovieReview movieReview = new MovieReview();
            int i = -1;
            movieReview.setMovieId(cursor.getInt(++i));
            movieReview.setMovieDBId(cursor.getString(++i));
            movieReview.setAuthor(cursor.getString(++i));
            movieReview.setContent(cursor.getString(++i));
            movieReview.setUrl(cursor.getString(++i));
            return movieReview;
        }
    }

}
