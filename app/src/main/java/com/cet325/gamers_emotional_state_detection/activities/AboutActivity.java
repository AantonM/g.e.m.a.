package com.cet325.gamers_emotional_state_detection.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cet325.gamers_emotional_state_detection.R;

/**
 * An activity responsible for the about page.
 * <p>
 * layout - activity_about
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     * Initial method called during the creation of the activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

}
