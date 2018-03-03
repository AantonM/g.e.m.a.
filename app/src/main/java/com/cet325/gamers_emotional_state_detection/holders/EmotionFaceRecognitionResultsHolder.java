package com.cet325.gamers_emotional_state_detection.holders;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class EmotionFaceRecognitionResultsHolder {

    private static EmotionFaceRecognitionResultsHolder single_instance = null;


    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionResultForGivenImg;

    public void setEmotionResultForGivenImg(int imageId, ArrayList<EmotionValuesDataset> emotionsResult) {
        emotionResultForGivenImg.put(imageId, emotionsResult);
    }

    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getEmotionResultForGivenImg() {
        return emotionResultForGivenImg;
    }

    private EmotionFaceRecognitionResultsHolder()
    {
        emotionResultForGivenImg = new LinkedHashMap<>();
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
