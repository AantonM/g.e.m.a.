package com.cet325.gamers_emotional_state_detection.holders;


import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AnalysedEmotionFaceRecognitionResultsHolder {
    private static AnalysedEmotionFaceRecognitionResultsHolder single_instance = null;

    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> EmotionFaceRecognitionResults;

    public void addNewEmotionResult(int imageId, ArrayList<EmotionValuesDataset> emotionsResult) {
        EmotionFaceRecognitionResults.put(imageId, emotionsResult);
    }

    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getAllAnalysedEmotionRecognitionResults() {
        return EmotionFaceRecognitionResults;
    }

    private AnalysedEmotionFaceRecognitionResultsHolder()
    {
        EmotionFaceRecognitionResults = new LinkedHashMap<>();
    }

    public static AnalysedEmotionFaceRecognitionResultsHolder getInstance()
    {
        if (single_instance == null)
            single_instance = new AnalysedEmotionFaceRecognitionResultsHolder();

        return single_instance;
    }
}
