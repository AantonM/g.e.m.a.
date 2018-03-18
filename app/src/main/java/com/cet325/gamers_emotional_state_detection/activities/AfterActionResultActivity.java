package com.cet325.gamers_emotional_state_detection.activities;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedEmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.managers.ExportResultsManager;
import com.cet325.gamers_emotional_state_detection.managers.SingleResultDataAnalysisManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AfterActionResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_action_result);

        exportRawData();
        startDataAnalysis();
        exportAnalysedData();
        displayAnalysedData();
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

    private void displayAnalysedData() {
        AnalysedEmotionFaceRecognitionResultsHolder results = AnalysedEmotionFaceRecognitionResultsHolder.getInstance();
        PieChart mChart = (PieChart) findViewById(R.id.piechart);

        List<PieEntry> pieChartEntries = new ArrayList<>();

        for (Map.Entry<String, Double> emotionResultSet : results.getSummedEmotionValues().entrySet()) {
            pieChartEntries.add(new PieEntry( emotionResultSet.getValue().floatValue(), emotionResultSet.getKey()));
        }

        PieDataSet set = new PieDataSet(pieChartEntries, "Emotion Results");
        PieData data = new PieData(set);
        mChart.setData(data);
        set.setColors(
                getResources().getColor(R.color.pieColour1),
                getResources().getColor(R.color.pieColour2),
                getResources().getColor(R.color.pieColour3),
                getResources().getColor(R.color.pieColour4),
                getResources().getColor(R.color.pieColour5),
                getResources().getColor(R.color.pieColour6),
                getResources().getColor(R.color.pieColour7),
                getResources().getColor(R.color.pieColour8));
        mChart.invalidate();
    }


}
