package com.cet325.gamers_emotional_state_detection.holders;


import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AnalysedEmotionFaceRecognitionResultsHolder {
    private static AnalysedEmotionFaceRecognitionResultsHolder single_instance = null;

    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionFaceRecognitionResults;
    private LinkedHashMap<Integer, String> timestampList;

    private HashMap<String, Double> summedEmotionValues;

    private AnalysedEmotionFaceRecognitionResultsHolder()
    {
        emotionFaceRecognitionResults = new LinkedHashMap<>();
        timestampList = new LinkedHashMap<>();
    }

    public void addNewEmotionResult(int imageId, ArrayList<EmotionValuesDataset> emotionsResult, String timestamp) {
        emotionFaceRecognitionResults.put(imageId, emotionsResult);
        timestampList.put(imageId, timestamp);
    }

    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getAllAnalysedEmotionRecognitionResults() {
        return emotionFaceRecognitionResults;
    }

    public String getTimestampForGivenImageID(int imageId) {
        return timestampList.get(imageId);
    }


    public void setSummedEmotionValues(HashMap<String, Double> summedEmotionValues){
        this.summedEmotionValues = summedEmotionValues;
    }

    public  HashMap<String, Double> getSummedEmotionValues() {
        return summedEmotionValues;
    }


    public static AnalysedEmotionFaceRecognitionResultsHolder getInstance()
    {
        if (single_instance == null)
            single_instance = new AnalysedEmotionFaceRecognitionResultsHolder();

        return single_instance;
    }

    public static void clean()
    {
        single_instance = null;
    }
}
