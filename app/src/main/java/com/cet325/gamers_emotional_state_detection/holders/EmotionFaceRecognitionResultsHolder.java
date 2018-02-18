package com.cet325.gamers_emotional_state_detection.holders;

import java.util.LinkedHashMap;

public class EmotionFaceRecognitionResultsHolder {

    private static EmotionFaceRecognitionResultsHolder single_instance = null;

    private LinkedHashMap<Integer, String> emotionResultForGivenImg;

    public void setEmotionResultForGivenImg(int imageId, String emotionResult) {
        emotionResultForGivenImg.put(imageId, emotionResult);
    }

    public LinkedHashMap<Integer, String> getEmotionResultForGivenImg() {
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


}
