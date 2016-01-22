package com.ganwal.nanodegree.myappportfolio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app_launcher);
    }

    /** Called when the user touches the button */
    public void startMovieApp(View view) {
        launchMovieApp();
    }

    public void startScoresApp(View view) {
        showLaunchAppMsg(getApplicationContext(), "This button will launch Scores app!");
    }

    public void startLibraryApp(View view) {
        showLaunchAppMsg(getApplicationContext(), "This button will launch Library app!");
    }

    public void startBiggerApp(View view) {
        showLaunchAppMsg(getApplicationContext(), "This button will launch Build It Bigger app!");
    }

    public void startReaderApp(View view) {
        showLaunchAppMsg(getApplicationContext(), "This button will launch XYZ Reader app!");
    }

    public void startCapstoneApp(View view) {
        showLaunchAppMsg(getApplicationContext(), "This button will launch Capstone app!");
    }


    private void showLaunchAppMsg(Context context, CharSequence text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void launchMovieApp() {
        PackageManager pm = getPackageManager();
        try {
            String packageName = "com.ganwal.nanodegree.popularMovie";
            Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
            startActivity(launchIntent);
        } catch (Exception e1) {
        }
    }


}