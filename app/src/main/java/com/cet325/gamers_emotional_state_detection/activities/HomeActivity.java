package com.cet325.gamers_emotional_state_detection.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cet325.gamers_emotional_state_detection.R;

/**
 * An activity responsible for the Home page, including initial preferences set up and check
 * <p>
 * layout - activity_home
 */
public class HomeActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    /**
     * {@inheritDoc}
     * <p>
     * Called when the activity is starting.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("HomeActivity","VM heap size:" + String.valueOf(Runtime.getRuntime().maxMemory()));

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //When the start button is pressed call UserDetailsActivity
        Button btnStartGame = (Button) findViewById(R.id.btnPlayGame);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userDetailsIntent = new Intent(v.getContext(), UserDetailsActivity.class);
                userDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(userDetailsIntent);
            }
        });
    }

    /**
     * Set up shared preferences when the application is started for the first time.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).apply();

            //By default the realtime display of results is enabled.
            prefs.edit().putBoolean("displayResultsRealtime", true).apply();

            //Vibration settings
            prefs.edit().putBoolean("vibration", true).apply();

            //Display timestamps
            prefs.edit().putBoolean("timestamp", true).apply();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param menu - The options menu in which you place your items.
     * @return boolean - true: for the menu to be displayed; false: it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param item - The menu item that was selected.
     * @return boolean - false: to allow normal menu processing to
     * proceed, true: to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //check if the setting button is pressed
        if (id == R.id.btn_settings) {

            //call the SettingsActivity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(settingsIntent);
        }

        //check if the about button is pressed
        if (id == R.id.btn_about) {

            //call the AboutActivity
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            aboutIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(aboutIntent);
        }
        return true;
    }
}
