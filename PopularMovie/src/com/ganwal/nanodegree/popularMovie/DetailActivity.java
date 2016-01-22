package com.ganwal.nanodegree.popularMovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


public class DetailActivity extends Activity {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        FragmentManager fm = getFragmentManager();
        android.app.Fragment detailFragment = fm.findFragmentById(R.id.movie_detail_container);
        if (detailFragment == null) {
            detailFragment = new DetailFragment();
            fm.beginTransaction().add(R.id.movie_detail_container, detailFragment).commit();
        }
    }



}
