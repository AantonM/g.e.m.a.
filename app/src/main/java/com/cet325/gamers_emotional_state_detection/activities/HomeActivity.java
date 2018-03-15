package com.cet325.gamers_emotional_state_detection.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cet325.gamers_emotional_state_detection.R;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).apply();

            //By default the realtime display of results is enabled.
            prefs.edit().putBoolean("displayResultsRealtime", true).apply();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.btn_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(settingsIntent);
        }

        if(id == R.id.btn_about) {
            Toast.makeText(HomeActivity.this, "To be implemented...", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
