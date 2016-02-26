package com.ganwal.nanodegree.popularMovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


public class ReviewActivity extends Activity {

    public static final String LOG_TAG = ReviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        FragmentManager fm = getFragmentManager();
        Fragment reviewFragment = fm.findFragmentById(R.id.movie_review_container);
        if (reviewFragment == null) {
            reviewFragment = new ReviewFragment();
            fm.beginTransaction().add(R.id.movie_review_container, reviewFragment).commit();
        }
    }

}
