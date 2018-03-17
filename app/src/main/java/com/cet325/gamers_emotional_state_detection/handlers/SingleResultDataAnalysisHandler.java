package com.cet325.gamers_emotional_state_detection.handlers;

import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SingleResultDataAnalysisHandler {

    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionsResultList;
    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> fusedEmotionsResultList;

    public SingleResultDataAnalysisHandler() {
        emotionsResultList = EmotionFaceRecognitionResultsHolder.getInstance().getAllEmotionRecognitionResults();
    }

    public void executeMultyLayerDataFusion() {

        fusedEmotionsResultList = new LinkedHashMap<>();
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

            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> currentFrame = singleFrame.next();

            EmotionValuesDataset firstEmotionalValue;
            EmotionValuesDataset secondEmotionalValue;
            EmotionValuesDataset thirdEmotionalValue;

            ArrayList<EmotionValuesDataset> fuzedEmotions = new ArrayList<>();

            //Sort the EmotionValuesDataset by emotional strenght
            Collections.sort(currentFrame.getValue(), new Comparator<EmotionValuesDataset>(){
                public int compare(EmotionValuesDataset o1, EmotionValuesDataset o2){
                    return Double.compare(o2.getEmotionValue(), o1.getEmotionValue());
                }
            });

            //Layer 1
            firstEmotionalValue = currentFrame.getValue().get(0);
            fuzedEmotions.add(firstEmotionalValue);

            //Layer2
            if(firstEmotionalValue.getEmotionValue() < 0.5 || currentFrame.getValue().get(1).getEmotionValue() > 0.25){
                secondEmotionalValue = currentFrame.getValue().get(1);
                fuzedEmotions.add(secondEmotionalValue);

                //Layer3
                if((firstEmotionalValue.getEmotionValue() < 0.5 && secondEmotionalValue.getEmotionValue() < 0.25) || currentFrame.getValue().get(2).getEmotionValue() > 0.2){
                    thirdEmotionalValue = currentFrame.getValue().get(2);
                    fuzedEmotions.add(thirdEmotionalValue);
                }
            }

            fusedEmotionsResultList.put(currentFrame.getKey(),fuzedEmotions);
        }
        Log.d("DevDebug:", "SingleResultDataAnalysisHandler: Data has been analysed.");

    }

}
