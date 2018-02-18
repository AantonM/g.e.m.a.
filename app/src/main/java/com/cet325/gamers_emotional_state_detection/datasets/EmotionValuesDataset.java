package com.cet325.gamers_emotional_state_detection.datasets;

public class EmotionValuesDataset {

    private String emotion_name;
    private Double emotion_value;

    public EmotionValuesDataset(String emotion_name, Double emotion_value) {
        this.emotion_name=emotion_name;
        this.emotion_value=emotion_value;
    }
    public String getEmotionName() {
        return this.emotion_name;
    }
    public Double getEmotionValue() {
        return this.emotion_value;
    }

}
