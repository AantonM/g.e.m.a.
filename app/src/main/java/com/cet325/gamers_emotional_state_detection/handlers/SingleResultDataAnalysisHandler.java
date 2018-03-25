package com.cet325.gamers_emotional_state_detection.handlers;

import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedFacialEmotionRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.FacialEmotionRecognitionResultsHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SingleResultDataAnalysisHandler is responsible to handle all the work regarding the
 * data analysis and saving the analysed data into a holder
 */
public class SingleResultDataAnalysisHandler {

    private FacialEmotionRecognitionResultsHolder facialEmotionRecognitionResultsHolder;
    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionsResultList;
    private AnalysedFacialEmotionRecognitionResultsHolder analysedFacialEmotionRecognitionResultsHolder;
    private HashMap<String, Double> emotionValuesHM;

    /**
     * Constructor.
     * Initialise the holders
     */
    public SingleResultDataAnalysisHandler() {
        facialEmotionRecognitionResultsHolder = FacialEmotionRecognitionResultsHolder.getInstance();
        emotionsResultList = facialEmotionRecognitionResultsHolder.getAllEmotionRecognitionResults();
        analysedFacialEmotionRecognitionResultsHolder = AnalysedFacialEmotionRecognitionResultsHolder.getInstance();
        emotionValuesHM = new HashMap<>();
    }

    /**
     * Method that calls the different stages of the analysis
     */
    public void executeMultyLayerDataFusion() {

        removeEmptyValues();
        fuzeData();
    }

    /**
     * Method that removes all empty values from the result data. Null value clearing
     */
    private void removeEmptyValues() {
        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> singleFrame = emotionsResultList.entrySet().iterator(); singleFrame.hasNext(); ) {
            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> currentFrame = singleFrame.next();
            if (currentFrame.getValue() == null) {
                singleFrame.remove();
            }
        }

        Log.d("DevDebug:", "SingleResultDataAnalysisHandler: Empty values are removed from the result list.");
    }

    /**
     * Method that performas the three levels data analysis
     */
    private void fuzeData() {

        //iterate throught the data
        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> singleFrame = emotionsResultList.entrySet().iterator(); singleFrame.hasNext(); ) {

            //get single frame
            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> currentFrame = singleFrame.next();

            EmotionValuesDataset firstEmotionalValue;
            EmotionValuesDataset secondEmotionalValue;
            EmotionValuesDataset thirdEmotionalValue;

            // temp list to store 1st, 2dn and 3rd emotion
            ArrayList<EmotionValuesDataset> fuzedEmotions = new ArrayList<>();

            //Sort the EmotionValuesDataset by emotional strength
            Collections.sort(currentFrame.getValue(), new Comparator<EmotionValuesDataset>() {
                public int compare(EmotionValuesDataset o1, EmotionValuesDataset o2) {
                    return Double.compare(o2.getEmotionValue(), o1.getEmotionValue());
                }
            });

            //Layer 1 - get highest emotional value
            firstEmotionalValue = currentFrame.getValue().get(0);
            fuzedEmotions.add(firstEmotionalValue);
            calculateResults(firstEmotionalValue);

            //Layer2 - get second highest emotional value
            if (firstEmotionalValue.getEmotionValue() < 0.5 || currentFrame.getValue().get(1).getEmotionValue() > 0.25) {
                secondEmotionalValue = currentFrame.getValue().get(1);
                fuzedEmotions.add(secondEmotionalValue);
                calculateResults(secondEmotionalValue);

                //Layer3 - get third highest emotional value
                if ((firstEmotionalValue.getEmotionValue() < 0.5 && secondEmotionalValue.getEmotionValue() < 0.25) || currentFrame.getValue().get(2).getEmotionValue() > 0.2) {
                    thirdEmotionalValue = currentFrame.getValue().get(2);
                    fuzedEmotions.add(thirdEmotionalValue);
                    calculateResults(thirdEmotionalValue);
                }
            }

            //get the timestamp of the curent frame
            String currentFrameTimestamp = facialEmotionRecognitionResultsHolder.getTimestampForGivenImageID(currentFrame.getKey());

            //save the analysed data into the holder
            analysedFacialEmotionRecognitionResultsHolder.addNewEmotionResult(currentFrame.getKey(), fuzedEmotions, currentFrameTimestamp);
        }

        //save the overal emotional value into a holder
        analysedFacialEmotionRecognitionResultsHolder.setSummedEmotionValues(emotionValuesHM);

        Log.d("DevDebug:", "SingleResultDataAnalysisHandler: Data has been analysed.");
    }

    /**
     * Metod that calculates the overal emotional value
     *
     * @param newEmotionValuesDataset - EmotionValuesDataset: current emotion
     */
    private void calculateResults(EmotionValuesDataset newEmotionValuesDataset) {

        //if there are no emotions save so far
        if (emotionValuesHM.size() == 0) {
            emotionValuesHM.put(newEmotionValuesDataset.getEmotionName(), newEmotionValuesDataset.getEmotionValue());
        } else {
            boolean emotionExists = false;
            for (Map.Entry<String, Double> entry : emotionValuesHM.entrySet()) {
                //does the emotion exist if y add the value to the existing one
                if (entry.getKey().equals(newEmotionValuesDataset.getEmotionName())) {
                    double newValue = entry.getValue() + newEmotionValuesDataset.getEmotionValue();
                    entry.setValue(newValue);
                    emotionExists = true;
                    break;
                }
            }
            //add the emotion to the list if it doesn't exist
            if (!emotionExists) {
                emotionValuesHM.put(newEmotionValuesDataset.getEmotionName(), newEmotionValuesDataset.getEmotionValue());
            }
        }
    }

}
