package com.cet325.gamers_emotional_state_detection.holders;


import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public HashMap<String, Double> getPercentageOfMainEmotionValues()
    {
        HashMap<String, Double> resultInPercentage = new HashMap<>();

        Double summedEmotionValue = 0.0;

        for (Map.Entry<String, Double> firstEntry : summedEmotionValues.entrySet())
        {
            summedEmotionValue += firstEntry.getValue();
        }

        for (Map.Entry<String, Double> secondEntry : summedEmotionValues.entrySet())
        {
           Double percentage = (secondEntry.getValue() * 100) / summedEmotionValue;
            resultInPercentage.put(secondEntry.getKey(), percentage);
        }

        return resultInPercentage;
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
