package com.cet325.gamers_emotional_state_detection.handlers;

import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedEmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SingleResultDataAnalysisHandler {

    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionsResultList;
    private AnalysedEmotionFaceRecognitionResultsHolder analysedEmotionFaceRecognitionResultsHolder;
    HashMap<String, Double> emotionValuesHM;

    public SingleResultDataAnalysisHandler() {
        emotionsResultList = EmotionFaceRecognitionResultsHolder.getInstance().getAllEmotionRecognitionResults();
        analysedEmotionFaceRecognitionResultsHolder = AnalysedEmotionFaceRecognitionResultsHolder.getInstance();
        emotionValuesHM = new HashMap<>();
    }

    public void executeMultyLayerDataFusion() {

        removeEmptyValues();
        fuzeData();
    }

    private void removeEmptyValues() {
        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> singleFrame = emotionsResultList.entrySet().iterator(); singleFrame.hasNext(); ) {
            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> currentFrame = singleFrame.next();
            if (currentFrame.getValue() == null) {
                singleFrame.remove();
            }
        }

        Log.d("DevDebug:", "SingleResultDataAnalysisHandler: Empty values are removed from the result list.");
    }


    private void fuzeData() {
        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> singleFrame = emotionsResultList.entrySet().iterator(); singleFrame.hasNext(); ) {

            //get single frame
            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> currentFrame = singleFrame.next();

            EmotionValuesDataset firstEmotionalValue;
            EmotionValuesDataset secondEmotionalValue;
            EmotionValuesDataset thirdEmotionalValue;

            // temp list to store 1st, 2dn and 3rd emotion
            ArrayList<EmotionValuesDataset> fuzedEmotions = new ArrayList<>();

            //Sort the EmotionValuesDataset by emotional strength
            Collections.sort(currentFrame.getValue(), new Comparator<EmotionValuesDataset>(){
                public int compare(EmotionValuesDataset o1, EmotionValuesDataset o2){
                    return Double.compare(o2.getEmotionValue(), o1.getEmotionValue());
                }
            });

            //Layer 1
            firstEmotionalValue = currentFrame.getValue().get(0);
            fuzedEmotions.add(firstEmotionalValue);
            calculateResults(firstEmotionalValue);

            //Layer2
            if(firstEmotionalValue.getEmotionValue() < 0.5 || currentFrame.getValue().get(1).getEmotionValue() > 0.25){
                secondEmotionalValue = currentFrame.getValue().get(1);
                fuzedEmotions.add(secondEmotionalValue);
                calculateResults(secondEmotionalValue);

                //Layer3
                if((firstEmotionalValue.getEmotionValue() < 0.5 && secondEmotionalValue.getEmotionValue() < 0.25) || currentFrame.getValue().get(2).getEmotionValue() > 0.2){
                    thirdEmotionalValue = currentFrame.getValue().get(2);
                    fuzedEmotions.add(thirdEmotionalValue);
                    calculateResults(thirdEmotionalValue);
                }
            }

            analysedEmotionFaceRecognitionResultsHolder.addNewEmotionResult(currentFrame.getKey(), fuzedEmotions);
        }

        analysedEmotionFaceRecognitionResultsHolder.setSummedEmotionValues(emotionValuesHM);

        Log.d("DevDebug:", "SingleResultDataAnalysisHandler: Data has been analysed.");
    }

    private void calculateResults(EmotionValuesDataset newEmotionValuesDataset) {

        //if there are no emotions save so far
        if(emotionValuesHM.size() == 0){
            emotionValuesHM.put(newEmotionValuesDataset.getEmotionName(), newEmotionValuesDataset.getEmotionValue());
        }else {
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
            if(!emotionExists){
                emotionValuesHM.put(newEmotionValuesDataset.getEmotionName(), newEmotionValuesDataset.getEmotionValue());
            }
        }
    }

}
