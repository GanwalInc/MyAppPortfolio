package com.ganwal.nanodegree.popularMovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;


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

/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider)menuItem.getActionProvider();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Extra Text");

        mShareActionProvider.setShareIntent(shareIntent);
        return true;
    }
*/


}
