package com.ganwal.nanodegree.popularMovie.data;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Content Provider for Popular Movie app sqlite database
 */
public class MovieContentProvider extends ContentProvider{

    private static final String TAG = MovieContentProvider.class.getSimpleName();

    private MovieDBHelper movieDBHelper;

    /* ********************* UriMatcher *************************** */

    private static final UriMatcher sUriMatcher =  new UriMatcher(UriMatcher.NO_MATCH);
    //uri Matcher codes
    private static final int ALL_MOVIES = 1;
    private static final int MOVIE_ID = 2;
    private static final int ALL_MOVIE_VIDEOS = 3;
    private static final int VIDEO_MOVIE_ID = 4;
    private static final int ALL_MOVIE_REVIEWS = 5;
    private static final int REVIEW_MOVIE_ID = 6;


    static {
        //add uris to match
        sUriMatcher.addURI(ProviderContract.CONTENT_AUTHORITY,
                ProviderContract.MovieEntry.TABLE_MOVIE,
                ALL_MOVIES);

        sUriMatcher.addURI(ProviderContract.CONTENT_AUTHORITY,
                ProviderContract.MovieEntry.TABLE_MOVIE + "/#",
                MOVIE_ID);

        sUriMatcher.addURI(ProviderContract.CONTENT_AUTHORITY,
                        ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO,
                ALL_MOVIE_VIDEOS);

        sUriMatcher.addURI(ProviderContract.CONTENT_AUTHORITY,
                        ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO + "/#",
                VIDEO_MOVIE_ID);

        sUriMatcher.addURI(ProviderContract.CONTENT_AUTHORITY,
                ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW,
                ALL_MOVIE_REVIEWS);

        sUriMatcher.addURI(ProviderContract.CONTENT_AUTHORITY,
                ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW + "/#",
                REVIEW_MOVIE_ID);
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_MOVIE_VIDEOS:
                return  ProviderContract.MovieVideoEntry.CONTENT_DIR_TYPE;
            case VIDEO_MOVIE_ID:
                return  ProviderContract.MovieVideoEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        int uriType = sUriMatcher.match(uri);
        long new_id;
        switch (uriType) {
            case ALL_MOVIES:
                new_id = database.insert(ProviderContract.MovieEntry.TABLE_MOVIE, null, values);
                Log.d(TAG, "Inserted movie, new_id:"+new_id);
                if(new_id > 0) {
                    return ProviderContract.MovieEntry.getMovieUri(new_id);
                }
                break;
            case ALL_MOVIE_VIDEOS:
                new_id = database.insert(ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO, null, values);
                Log.d(TAG, "Inserted movie video, new_id:"+new_id);
                if(new_id > 0) {
                    return ProviderContract.MovieEntry.getMovieUri(new_id); //we are returning this back but don't use it anywhere
                }
                break;
            case ALL_MOVIE_REVIEWS:
                new_id = database.insert(ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW, null, values);
                Log.d(TAG, "Inserted movie review, new_id:"+new_id);
                if(new_id > 0) {
                    return ProviderContract.MovieEntry.getMovieUri(new_id); //we are returning this back but don't use it anywhere
                }
                break;
            default:
                throw new UnsupportedOperationException("Can't determine the operation. Uri:"+uri);
        }
        //notify the registered observer for the change
        getContext().getContentResolver().notifyChange(uri, null);
        return null;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        int uriType = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        //Deleting the movie. Movie's child records; reviews and videos are using foreign key reference,
        // they will be deleted when movie is deleted
        switch (uriType) {
            case ALL_MOVIES:
                rowsDeleted = database.delete(ProviderContract.MovieEntry.TABLE_MOVIE, selection,
                        selectionArgs);
                break;
            case MOVIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = database.delete(ProviderContract.MovieEntry.TABLE_MOVIE,
                            ProviderContract.MovieEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = database.delete(ProviderContract.MovieEntry.TABLE_MOVIE,
                            ProviderContract.MovieEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new UnsupportedOperationException("Can't determine the operation. Uri:"+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        SQLiteDatabase database = movieDBHelper.getWritableDatabase();
        int uriType = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (uriType) {
            case ALL_MOVIES:
                rowsUpdated = database.update(ProviderContract.MovieEntry.TABLE_MOVIE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = database.update(ProviderContract.MovieEntry.TABLE_MOVIE,
                            values,
                            ProviderContract.MovieEntry._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = database.update(ProviderContract.MovieEntry.TABLE_MOVIE,
                            values,
                            ProviderContract.MovieEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new UnsupportedOperationException("Can't determine the operation. Uri:"+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase database = movieDBHelper.getReadableDatabase();
        int uriType = sUriMatcher.match(uri);
        Log.d(TAG, "Querying DB. uri:" + uri + "\n uriType:"+uriType);
        switch(uriType){
            case ALL_MOVIES:{
                retCursor = database.query(
                        ProviderContract.MovieEntry.TABLE_MOVIE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case MOVIE_ID:{
                retCursor = database.query(
                        ProviderContract.MovieEntry.TABLE_MOVIE,
                        projection,
                        ProviderContract.MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case ALL_MOVIE_VIDEOS:{
                retCursor = database.query(
                        ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case VIDEO_MOVIE_ID:{
                retCursor = database.query(
                        ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO,
                        projection,
                        ProviderContract.MovieVideoEntry.COL_MOVIE_ID+ " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case ALL_MOVIE_REVIEWS:{
                retCursor = database.query(
                        ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case REVIEW_MOVIE_ID:{
                retCursor = database.query(
                        ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW,
                        projection,
                        ProviderContract.MovieVideoEntry.COL_MOVIE_ID+ " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                throw new UnsupportedOperationException("Can't determine the operation. Uri:"+uri);
            }
        }
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }


}
