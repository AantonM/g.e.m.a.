package com.cet325.gamers_emotional_state_detection.activities;

import android.os.Bundle;
import android.app.Activity;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.managers.ExportResultsManager;
import com.cet325.gamers_emotional_state_detection.managers.SingleResultDataAnalysisManager;

public class AfterActionResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_action_result);

        exportRawData();
        startDataAnalysis();
        exportAnalysedData();
    }

    private void exportRawData() {
        ExportResultsManager exportResultsManager = new ExportResultsManager();
        exportResultsManager.exportRawData();
    }

    private void startDataAnalysis() {
        SingleResultDataAnalysisManager srdaManager = new SingleResultDataAnalysisManager();
        srdaManager.startAnalysis();
    }

    private void exportAnalysedData() {
        ExportResultsManager exportResultsManager = new ExportResultsManager();
        exportResultsManager.exportAnalysedData();
    }

}
