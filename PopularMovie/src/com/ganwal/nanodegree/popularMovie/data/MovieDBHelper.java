package com.ganwal.nanodegree.popularMovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by PY on 2/4/2016.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String TAG = MovieDBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "popularmovie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL(getMovieCreateSQL());
        db.execSQL(getMovieVideoCreateSQL());
        db.execSQL(getMovieReviewCreateSQL());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to upgrade now in sqllite db.

    }

    public static final String getMovieCreateSQL() {
        StringBuilder createSQL = new StringBuilder();
        createSQL.append("create table ").append(ProviderContract.MovieEntry.TABLE_MOVIE + "(")
                .append(ProviderContract.MovieEntry._ID + " integer primary key autoincrement, ")
                .append(ProviderContract.MovieEntry.COL_MOVIE_DB_ID + " integer unique not null,")
                .append(ProviderContract.MovieEntry.COL_TITLE + " text not null,")
                .append(ProviderContract.MovieEntry.COL_THUMBNAIL_NAME + " text,")
                .append(ProviderContract.MovieEntry.COL_SYNOPSIS + " text,")
                .append(ProviderContract.MovieEntry.COL_USER_RATING + " text,")
                .append(ProviderContract.MovieEntry.COL_RELEASE_DATE + " INTEGER,")
                .append(ProviderContract.MovieEntry.COL_THUMBNAIL_IMG + " BLOB)");
        Log.d(TAG, "Create Movie Table SQL:" + createSQL.toString());
        return createSQL.toString();
    }


    public static final String getMovieVideoCreateSQL() {
        StringBuilder createSQL = new StringBuilder();
        createSQL.append("create table ").append(ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO + "(")
                .append(ProviderContract.MovieVideoEntry.COL_MOVIE_ID + " integer not null,")
                .append(ProviderContract.MovieVideoEntry.COL_MOVIE_DB_ID + " text,")
                .append(ProviderContract.MovieVideoEntry.COL_KEY + " text,")
                .append(ProviderContract.MovieVideoEntry.COL_NAME + " text,")
                .append(ProviderContract.MovieVideoEntry.COL_SITE + " text,")
                .append(ProviderContract.MovieVideoEntry.COL_TYPE + " text,")
                .append("FOREIGN KEY(" + ProviderContract.MovieVideoEntry.COL_MOVIE_ID + ") ")
                .append("REFERENCES " + ProviderContract.MovieEntry.TABLE_MOVIE +"(")
                .append(ProviderContract.MovieEntry._ID+")")
                .append(" ON DELETE CASCADE ")
                .append(")");
        Log.d(TAG, "Create Movie Video Table SQL:" + createSQL.toString());
        return createSQL.toString();
    }


    public static final String getMovieReviewCreateSQL() {
        StringBuilder createSQL = new StringBuilder();
        createSQL.append("create table ").append(ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW + "(")
                .append(ProviderContract.MovieReviewEntry.COL_MOVIE_ID + " integer not null,")
                .append(ProviderContract.MovieReviewEntry.COL_MOVIE_DB_ID + " text,")
                .append(ProviderContract.MovieReviewEntry.COL_AUTHOR + " text,")
                .append(ProviderContract.MovieReviewEntry.COL_CONTENT + " text,")
                .append(ProviderContract.MovieReviewEntry.COL_URL + " text,")
                .append("FOREIGN KEY(" + ProviderContract.MovieReviewEntry.COL_MOVIE_ID + ") ")
                .append("REFERENCES " + ProviderContract.MovieEntry.TABLE_MOVIE +"(")
                .append(ProviderContract.MovieEntry._ID+")")
                .append(" ON DELETE CASCADE ")
                .append(")");
        Log.d(TAG, "Create Movie Review Table SQL:" + createSQL.toString());
        return createSQL.toString();
    }

}
