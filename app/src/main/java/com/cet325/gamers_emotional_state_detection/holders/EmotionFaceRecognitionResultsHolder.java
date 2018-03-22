package com.cet325.gamers_emotional_state_detection.holders;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EmotionFaceRecognitionResultsHolder {

    private static EmotionFaceRecognitionResultsHolder single_instance = null;


    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionFaceRecognitionResults;
    private LinkedHashMap<Integer, String> timestampList;


    private EmotionFaceRecognitionResultsHolder()
    {
        emotionFaceRecognitionResults = new LinkedHashMap<>();
        timestampList = new LinkedHashMap<>();
    }

    public void addNewEmotionResult(int imageId, ArrayList<EmotionValuesDataset> emotionsResult,String timestamp) {
        emotionFaceRecognitionResults.put(imageId, emotionsResult);
        timestampList.put(imageId, timestamp);
    }

    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getAllEmotionRecognitionResults() {
        return emotionFaceRecognitionResults;
    }

    public String getTimestampForGivenImageID(int imageId) {
        return timestampList.get(imageId);
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
