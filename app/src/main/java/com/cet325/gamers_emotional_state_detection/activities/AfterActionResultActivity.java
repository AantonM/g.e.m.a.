package com.cet325.gamers_emotional_state_detection.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AfterActionResultActivity extends AppCompatActivity {

    private final Float TEXT_SIZE = 15f;
    private boolean showTimestamps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_action_result);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        showTimestamps = prefs.getBoolean("timestamp", true);

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
        try{
            displayPieChart();
            displayLineChart();
        }catch (Exception e){
            Log.d("AfterActionResultActivity","Displaying charts exception: " + e);
        }
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
        data.setValueTextSize(TEXT_SIZE);
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


    private void displayLineChart() {
        AnalysedEmotionFaceRecognitionResultsHolder results  = AnalysedEmotionFaceRecognitionResultsHolder.getInstance();


        if (results.getAllAnalysedEmotionRecognitionResults().size() == 0){
           return;
        }


        LineChart lineChart = (LineChart) findViewById(R.id.linechart);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(10);

        Legend l = lineChart.getLegend();
        l.setTextSize(TEXT_SIZE);

        int frameNumber = 0;

        ArrayList<Entry> angerValues = new ArrayList<>();
        ArrayList<Entry> contemptValues = new ArrayList<>();
        ArrayList<Entry> disgustValues = new ArrayList<>();
        ArrayList<Entry> fearValues = new ArrayList<>();
        ArrayList<Entry> happinessValues = new ArrayList<>();
        ArrayList<Entry> neutralValues = new ArrayList<>();
        ArrayList<Entry> sadnessValues = new ArrayList<>();
        ArrayList<Entry> surpriseValues = new ArrayList<>();

        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> allFramesList = results.getAllAnalysedEmotionRecognitionResults().entrySet().iterator(); allFramesList.hasNext(); ) {
            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> singleFrame = allFramesList.next();

            float angerEmotionValue;
            float contemptEmotionValue;
            float disgustEmotionValue;
            float fearEmotionValue;
            float happinessEmotionValue;
            float neutralEmotionValue;
            float sadnessEmotionValue;
            float surpriseEmotionValue;

            frameNumber++;

            for(EmotionValuesDataset singleEmotion : singleFrame.getValue()){
                angerEmotionValue = 0;
                contemptEmotionValue = 0;
                disgustEmotionValue = 0;
                fearEmotionValue = 0;
                happinessEmotionValue = 0;
                neutralEmotionValue = 0;
                sadnessEmotionValue = 0;
                surpriseEmotionValue = 0;

                switch (singleEmotion.getEmotionName()){
                    case "anger" :
                        angerEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "contempt":
                        contemptEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "disgust":
                        disgustEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "fear":
                        fearEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "happiness":
                        happinessEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "neutral":
                        neutralEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "sadness":
                        sadnessEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                    case "surprise":
                        surpriseEmotionValue = (float) singleEmotion.getEmotionValue().floatValue();
                        break;
                }
                angerValues.add(new Entry(frameNumber,angerEmotionValue));
                contemptValues.add(new Entry(frameNumber,contemptEmotionValue));
                disgustValues.add(new Entry(frameNumber,disgustEmotionValue));
                fearValues.add(new Entry(frameNumber,fearEmotionValue));
                happinessValues.add(new Entry(frameNumber,happinessEmotionValue));
                neutralValues.add(new Entry(frameNumber,neutralEmotionValue));
                sadnessValues.add(new Entry(frameNumber,sadnessEmotionValue));
                surpriseValues.add(new Entry(frameNumber,surpriseEmotionValue));

                if(showTimestamps){
                    displayTimeStamp(frameNumber,results.getTimestampForGivenImageID(singleFrame.getKey()));
                }
            }

        }

        LineDataSet angerSet, contemptSet, disgustSet, fearSet, happinessSet, neutralSet, sadnessSet, surpriseSet;
        angerSet = new LineDataSet(angerValues, "angry");
        contemptSet = new LineDataSet(contemptValues, "contempt");
        disgustSet = new LineDataSet(disgustValues, "disgust");
        fearSet = new LineDataSet(fearValues, "fear");
        happinessSet = new LineDataSet(happinessValues, "happiness");
        neutralSet = new LineDataSet(neutralValues, "neutral");
        sadnessSet = new LineDataSet(sadnessValues, "sadness");
        surpriseSet = new LineDataSet(surpriseValues, "surprise");

        angerSet.setColor(getResources().getColor(R.color.pieColour1));
        contemptSet.setColor(getResources().getColor(R.color.pieColour2));
        disgustSet.setColor(getResources().getColor(R.color.pieColour3));
        fearSet.setColor(getResources().getColor(R.color.pieColour4));
        happinessSet.setColor(getResources().getColor(R.color.pieColour5));
        neutralSet.setColor(getResources().getColor(R.color.pieColour6));
        sadnessSet.setColor(getResources().getColor(R.color.pieColour7));
        surpriseSet.setColor(getResources().getColor(R.color.pieColour8));

        angerSet.setLineWidth(5f);
        contemptSet.setLineWidth(5f);
        disgustSet.setLineWidth(5f);
        fearSet.setLineWidth(5f);
        happinessSet.setLineWidth(5f);
        neutralSet.setLineWidth(5f);
        sadnessSet.setLineWidth(5f);
        surpriseSet.setLineWidth(5f);

        LineData data = new LineData(angerSet, contemptSet, disgustSet, fearSet, happinessSet, neutralSet, sadnessSet, surpriseSet);
        data.setValueTextSize(TEXT_SIZE);
        lineChart.setData(data);
    }

    private void displayTimeStamp(int frameId, String timestamp) {
        TextView timestamps = (TextView) findViewById(R.id.txtFramesTimestamp);

        timestamps.append("Frame " + String.valueOf(frameId) + ":   " + timestamp + "\n");

    }

    @Override
    public void onBackPressed() {
        HolderCleanerManager holderCleanerManager = new HolderCleanerManager();
        holderCleanerManager.cleanHolders();

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

