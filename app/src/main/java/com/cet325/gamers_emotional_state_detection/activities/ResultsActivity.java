package com.cet325.gamers_emotional_state_detection.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedFacialEmotionRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.managers.ExportResultsManager;
import com.cet325.gamers_emotional_state_detection.managers.HolderCleanerManager;
import com.cet325.gamers_emotional_state_detection.managers.SingleResultDataAnalysisManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * <p>
 * An activity responsible for the results page.
 * <p>
 * layout - activity_results
 */
public class ResultsActivity extends AppCompatActivity {

    private final Float TEXT_SIZE = 15f;
    private boolean showTimestamps; //from preferences

    /***
     * Initial method called during the creation of the activity.
     * Checks the preferences and calls the method to analyse, export and display the data.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //Check if displaying timestamps is enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        showTimestamps = prefs.getBoolean("timestamp", true);

        exportRawData();
        startDataAnalysis();
        exportAnalysedData();
        displayAnalysedData();
    }

    /**
     * Export unanalysied raw data into Json file, by using ExportResultsManager.
     */
    private void exportRawData() {
        ExportResultsManager exportResultsManager = new ExportResultsManager();
        exportResultsManager.exportRawData();
    }

    /**
     * Call SingleResultDataAnalysisManager to analyse the result data.
     */
    private void startDataAnalysis() {
        SingleResultDataAnalysisManager srdaManager = new SingleResultDataAnalysisManager();
        srdaManager.startAnalysis();
    }

    /**
     * Export analysied data into Json file, by using ExportResultsManager.
     */
    private void exportAnalysedData() {
        ExportResultsManager exportResultsManager = new ExportResultsManager();
        exportResultsManager.exportAnalysedData();
    }

    /**
     * Display the analysied data on the screen.
     */
    private void displayAnalysedData() {
        try {
            displayPieChart();
            displayLineChart();
        } catch (Exception e) {
            Log.d("ResultActivity", "Displaying charts exception: " + e);
        }
    }

    /**
     * Display the analysed data from the holder into a piechart (id-piechart)
     */
    private void displayPieChart() {
        //Get the holder that holder of the analysed results
        AnalysedFacialEmotionRecognitionResultsHolder results = AnalysedFacialEmotionRecognitionResultsHolder.getInstance();

        //check if there is data to be displayed
        if (results.getPercentageOfMainEmotionValues().size() == 0) {
            return;
        }

        PieChart mChart = (PieChart) findViewById(R.id.piechart);

        List<PieEntry> pieChartEntries = new ArrayList<>();

        //for each percentage record withing the holder object create a new entry for the pie chart
        for (Map.Entry<String, Double> emotionResultSet : results.getPercentageOfMainEmotionValues().entrySet()) {
            pieChartEntries.add(new PieEntry(emotionResultSet.getValue().floatValue(), emotionResultSet.getKey()));
        }

        //format the pic chart
        PieDataSet set = new PieDataSet(pieChartEntries, " ");
        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
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

        //format the legend below the pie chart
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(TEXT_SIZE);
    }

    /**
     * Display the analysed data from the holder into a linechart (id-linechart)
     */
    private void displayLineChart() {
        //Get the holder that holder of the analysed results
        AnalysedFacialEmotionRecognitionResultsHolder results = AnalysedFacialEmotionRecognitionResultsHolder.getInstance();

        //check if there is data to be displayed
        if (results.getAllAnalysedEmotionRecognitionResults().size() == 0) {
            return;
        }


        //format the cahrt
        LineChart lineChart = (LineChart) findViewById(R.id.linechart);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(10);

        Legend l = lineChart.getLegend();
        l.setTextSize(TEXT_SIZE);

        //counter of the records, each record represents a frame
        //after data clean up the number of frames might be non sequentially
        int frameNumber = 0;

        //Create a new list of entryis for each emotion (new line on the chart)
        ArrayList<Entry> angerValues = new ArrayList<>();
        ArrayList<Entry> contemptValues = new ArrayList<>();
        ArrayList<Entry> disgustValues = new ArrayList<>();
        ArrayList<Entry> fearValues = new ArrayList<>();
        ArrayList<Entry> happinessValues = new ArrayList<>();
        ArrayList<Entry> neutralValues = new ArrayList<>();
        ArrayList<Entry> sadnessValues = new ArrayList<>();
        ArrayList<Entry> surpriseValues = new ArrayList<>();

        //For each frame get a list of the emotions and their values
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

            //iterate the emotions and add the list of entries.
            for (EmotionValuesDataset singleEmotion : singleFrame.getValue()) {

                //default value of the emotions
                angerEmotionValue = 0;
                contemptEmotionValue = 0;
                disgustEmotionValue = 0;
                fearEmotionValue = 0;
                happinessEmotionValue = 0;
                neutralEmotionValue = 0;
                sadnessEmotionValue = 0;
                surpriseEmotionValue = 0;

                switch (singleEmotion.getEmotionName()) {
                    case "anger":
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
                angerValues.add(new Entry(frameNumber, angerEmotionValue));
                contemptValues.add(new Entry(frameNumber, contemptEmotionValue));
                disgustValues.add(new Entry(frameNumber, disgustEmotionValue));
                fearValues.add(new Entry(frameNumber, fearEmotionValue));
                happinessValues.add(new Entry(frameNumber, happinessEmotionValue));
                neutralValues.add(new Entry(frameNumber, neutralEmotionValue));
                sadnessValues.add(new Entry(frameNumber, sadnessEmotionValue));
                surpriseValues.add(new Entry(frameNumber, surpriseEmotionValue));

                //display the timestamp of each frame
                if (showTimestamps) {
                    displayTimeStamp(frameNumber, results.getTimestampForGivenImageID(singleFrame.getKey()));
                }
            }

        }

        //append the list of entried to the dataset
        LineDataSet angerSet, contemptSet, disgustSet, fearSet, happinessSet, neutralSet, sadnessSet, surpriseSet;
        angerSet = new LineDataSet(angerValues, "angry");
        contemptSet = new LineDataSet(contemptValues, "contempt");
        disgustSet = new LineDataSet(disgustValues, "disgust");
        fearSet = new LineDataSet(fearValues, "fear");
        happinessSet = new LineDataSet(happinessValues, "happiness");
        neutralSet = new LineDataSet(neutralValues, "neutral");
        sadnessSet = new LineDataSet(sadnessValues, "sadness");
        surpriseSet = new LineDataSet(surpriseValues, "surprise");

        //format the chart
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

    /**
     * Display the timestamp of each frame in a textview (id - txtFramesTimestamp).
     *
     * @param frameId   - the id of the frame
     * @param timestamp - the value of the timestamp
     */
    private void displayTimeStamp(int frameId, String timestamp) {
        TextView timestamps = (TextView) findViewById(R.id.txtFramesTimestamp);

        timestamps.append("Frame " + String.valueOf(frameId) + ":   " + timestamp + "\n");

    }


    /**
     * When the back button is pressed clean all the data from the holders and return to the HomeActivity
     */
    @Override
    public void onBackPressed() {
        HolderCleanerManager holderCleanerManager = new HolderCleanerManager();
        holderCleanerManager.cleanHolders();

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

