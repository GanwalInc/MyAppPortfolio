package com.ganwal.nanodegree.myappportfolio;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app_launcher);
    }

    /** Called when the user touches the button */
    public void startSpotifyApp(View view) {
        showLaunchAppMsg(getApplicationContext(), "This button will launch Spotify app!");
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


}