package com.cet325.gamers_emotional_state_detection.holders;


import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton holder object that stores the facial emotion recognition results after the analysis
 * also it stores a timestamp of each frame and a number of the emotion value which is then used to
 * calculate the percentage of an emotion during the whole gameplay.
 */
public class AnalysedFacialEmotionRecognitionResultsHolder {

    private static AnalysedFacialEmotionRecognitionResultsHolder single_instance = null;

    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> facialEmotionRecognitionResults;
    private LinkedHashMap<Integer, String> timestampList;

    private HashMap<String, Double> summedEmotionValues;

    /**
     * Constructor that initialises the tables used to store the data
     */
    private AnalysedFacialEmotionRecognitionResultsHolder() {
        facialEmotionRecognitionResults = new LinkedHashMap<>();
        timestampList = new LinkedHashMap<>();
    }

    /**
     * Method that stores a new record of analysed facial emotion recognition results
     *
     * @param imageId        - int: the id of the image
     * @param emotionsResult - ArrayList<EmotionValuesDataset>: the emotions values
     * @param timestamp      - String: a timestamp of frame that those record were taken
     */
    public void addNewEmotionResult(int imageId, ArrayList<EmotionValuesDataset> emotionsResult, String timestamp) {
        facialEmotionRecognitionResults.put(imageId, emotionsResult);
        timestampList.put(imageId, timestamp);
    }

    /**
     * Method that returns a table of all stored facial emotion recognition results.
     *
     * @return LinkedHashMap: the result
     */
    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getAllAnalysedEmotionRecognitionResults() {
        return facialEmotionRecognitionResults;
    }

    /**
     * Method that returns the timestamp for given frame (image) id
     *
     * @param imageId int: the id of the frame/image
     * @return String: the timestamp
     */
    public String getTimestampForGivenImageID(int imageId) {
        return timestampList.get(imageId);
    }

    /**
     * Method that sets the summed values of the facial emotion recognition results
     *
     * @param summedEmotionValues HashMap<String, Double>: the summed table
     */
    public void setSummedEmotionValues(HashMap<String, Double> summedEmotionValues) {
        this.summedEmotionValues = summedEmotionValues;
    }

    /**
     * Method that returns the summed values of the facial emotion recognition results
     *
     * @return HashMap: summed emotion values
     */
    public HashMap<String, Double> getSummedEmotionValues() {
        return summedEmotionValues;
    }

    /**
     * Method that calculate the percentage that each emotion represents in the summed table and
     * returns the results from the calculation
     *
     * @return HashMap: percentage of emotions
     */
    public HashMap<String, Double> getPercentageOfMainEmotionValues() {
        HashMap<String, Double> resultInPercentage = new HashMap<>();

        Double summedEmotionValue = 0.0;

        for (Map.Entry<String, Double> firstEntry : summedEmotionValues.entrySet()) {
            summedEmotionValue += firstEntry.getValue();
        }

        for (Map.Entry<String, Double> secondEntry : summedEmotionValues.entrySet()) {
            Double percentage = (secondEntry.getValue() * 100) / summedEmotionValue;
            resultInPercentage.put(secondEntry.getKey(), percentage);
        }

        return resultInPercentage;
    }

    /**
     * Method that initialises the singleton object
     *
     * @return AnalysedFacialEmotionRecognitionResultsHolder: the current instance of the object
     */
    public static AnalysedFacialEmotionRecognitionResultsHolder getInstance() {
        if (single_instance == null)
            single_instance = new AnalysedFacialEmotionRecognitionResultsHolder();

        return single_instance;
    }

    /**
     * Clear the holder object
     */
    public static void clean() {
        single_instance = null;
    }
}
