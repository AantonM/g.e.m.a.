package com.cet325.gamers_emotional_state_detection.activities;

import android.os.Bundle;
import android.app.Activity;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.managers.SingleResultDataAnalysisManager;

public class AfterActionResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_action_result);

        startDataAnalysis();
    }

    private void startDataAnalysis() {
        SingleResultDataAnalysisManager srdaManager = new SingleResultDataAnalysisManager();
        srdaManager.startAnalysis();
    }

}
