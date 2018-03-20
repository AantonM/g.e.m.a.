package com.cet325.gamers_emotional_state_detection.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedEmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.managers.ExportResultsManager;
import com.cet325.gamers_emotional_state_detection.managers.HolderCleanerManager;
import com.cet325.gamers_emotional_state_detection.managers.SingleResultDataAnalysisManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AfterActionResultActivity extends AppCompatActivity {

    private final Float TEXT_SIZE = 15f;

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
        displayPieChart();
    }

    private void displayPieChart() {
        AnalysedEmotionFaceRecognitionResultsHolder results  = AnalysedEmotionFaceRecognitionResultsHolder.getInstance();

        PieChart mChart = (PieChart) findViewById(R.id.piechart);

        List<PieEntry> pieChartEntries = new ArrayList<>();

        for (Map.Entry<String, Double> emotionResultSet : results.getSummedEmotionValues().entrySet()) {
            pieChartEntries.add(new PieEntry( emotionResultSet.getValue().floatValue(), emotionResultSet.getKey()));
        }

        PieDataSet set = new PieDataSet(pieChartEntries, " ");
        PieData data = new PieData(set);
        data.setValueTextSize(15f);
        data.setValueTextColor(getResources().getColor(R.color.fontColor));
        mChart.setData(data);
        set.setColors(
                getResources().getColor(R.color.pieColour1),
                getResources().getColor(R.color.pieColour2),
                getResources().getColor(R.color.pieColour3),
                getResources().getColor(R.color.pieColour4),
                getResources().getColor(R.color.pieColour5),
                getResources().getColor(R.color.pieColour6),
                getResources().getColor(R.color.pieColour7),
                getResources().getColor(R.color.pieColour8)
        );

        mChart.getDescription().setEnabled(false);
        mChart.invalidate();

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(TEXT_SIZE);
    }

    @Override
    public void onBackPressed() {
        HolderCleanerManager holderCleanerManager = new HolderCleanerManager();
        holderCleanerManager.cleanHolders();

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
    }
}

