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

        Switch vibrationSwitch = (Switch) findViewById(R.id.switchVibration);

        if(prefs.getBoolean("vibration", true)){
            vibrationSwitch.setChecked(true);
        }else{
            vibrationSwitch.setChecked(false);
        }

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(prefs.getBoolean("vibration", true)){
                    prefs.edit().putBoolean("vibration", false).apply();
                    Log.d("DevDebug", "Settings: Vibration is turned off");
                }else{
                    prefs.edit().putBoolean("vibration", true).apply();
                    Log.d("DevDebug", "Settings: Realtime displaying of results is turned on");
                }
            }
        });


        Switch timestampSwitch = (Switch) findViewById(R.id.switchTimestamp);

        if(prefs.getBoolean("timestamp", true)){
            timestampSwitch.setChecked(true);
        }else{
            timestampSwitch.setChecked(false);
        }

        timestampSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(prefs.getBoolean("timestamp", true)){
                    prefs.edit().putBoolean("timestamp", false).apply();
                    Log.d("DevDebug", "Settings: Timestamp displaying in results is turned off");
                }else{
                    prefs.edit().putBoolean("timestamp", true).apply();
                    Log.d("DevDebug", "Settings: Timestamp displaying in results is turned on");
                }
            }
        });
    }
}
