package com.ganwal.nanodegree.popularMovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Main screen for Popular movie app, it shows one pane and two pane UI's
 */

public class MainActivity extends Activity implements MainFragment.OnMovieSelectedListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ID = "movieID";

    public static final String MAIN_FRAGMENT_TAG = "MFTAG";
    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            DetailFragment df = (DetailFragment)fm.findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if(df == null) {
                df = new DetailFragment();
                fm.beginTransaction().replace(R.id.movie_detail_container, df, DETAIL_FRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        Fragment mainFragment = fm.findFragmentByTag(MAIN_FRAGMENT_TAG);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            fm.beginTransaction().replace(R.id.container, mainFragment, MAIN_FRAGMENT_TAG).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Intent intent = new Intent(this, MovieSettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie selectedMovie) {
        if(mTwoPane) {
            //two pane
            DetailFragment df = new DetailFragment();
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.EXTRA_MOVIE_ID, selectedMovie);
            df.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.movie_detail_container, df,
                    MainActivity.DETAIL_FRAGMENT_TAG).commit();

        } else {
            //one pane
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(MainActivity.EXTRA_MOVIE_ID, selectedMovie);
            startActivity(intent);
        }
    }
}
