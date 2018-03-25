package com.cet325.gamers_emotional_state_detection.datasets;

/**
 * Dataset object used to store emotional states by name and value
 */
public class EmotionValuesDataset {

    private String emotion_name;
    private Double emotion_value;

    /**
     * Constructor. Setting values
     *
     * @param emotion_name  String - the name of the emotion
     * @param emotion_value Double - the value of the emotion
     */
    public EmotionValuesDataset(String emotion_name, Double emotion_value) {
        this.emotion_name = emotion_name;
        this.emotion_value = emotion_value;
    }

    /**
     * Get the name of the emotions
     *
     * @return String - the name of the emotion
     */
    public String getEmotionName() {
        return this.emotion_name;
    }

    /**
     * Get the value of the emotions
     *
     * @return Double - the value of the emotion
     */
    public Double getEmotionValue() {
        return this.emotion_value;
    }

}
