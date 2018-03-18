package com.cet325.gamers_emotional_state_detection.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.cet325.gamers_emotional_state_detection.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Switch realTimeResultsSwitch = (Switch) findViewById(R.id.switchRealTimeResults);

        if(prefs.getBoolean("displayResultsRealtime", true)){
            realTimeResultsSwitch.setChecked(true);
        }else{
            realTimeResultsSwitch.setChecked(false);
        }

        realTimeResultsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(prefs.getBoolean("displayResultsRealtime", true)){
                    prefs.edit().putBoolean("displayResultsRealtime", false).apply();
                    Log.d("DevDebug", "Settings: Realtime displaying of results is turned off");
                }else{
                    prefs.edit().putBoolean("displayResultsRealtime", true).apply();
                    Log.d("DevDebug", "Settings: Realtime displaying of results is turned on");
                }
            }
        });
    }
}
