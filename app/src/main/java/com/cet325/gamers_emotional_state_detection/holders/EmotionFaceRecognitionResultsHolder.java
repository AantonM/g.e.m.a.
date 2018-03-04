package com.cet325.gamers_emotional_state_detection.holders;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EmotionFaceRecognitionResultsHolder {

    private static EmotionFaceRecognitionResultsHolder single_instance = null;


    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> EmotionFaceRecognitionResults;

    public void addNewEmotionResult(int imageId, ArrayList<EmotionValuesDataset> emotionsResult) {
        EmotionFaceRecognitionResults.put(imageId, emotionsResult);
    }

    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getAllEmotionRecognitionResults() {
        return EmotionFaceRecognitionResults;
    }

    private EmotionFaceRecognitionResultsHolder()
    {
        EmotionFaceRecognitionResults = new LinkedHashMap<>();
    }

    public static EmotionFaceRecognitionResultsHolder getInstance()
    {
        if (single_instance == null)
            single_instance = new EmotionFaceRecognitionResultsHolder();

        return single_instance;
    }

    public static void clean()
    {
        single_instance = null;
    }


}
