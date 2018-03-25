package com.cet325.gamers_emotional_state_detection.holders;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Singleton holder object that stores the facial emotion recognition results before the analysis
 * also it stores a timestamp of each frame
 */
public class FacialEmotionRecognitionResultsHolder {

    private static FacialEmotionRecognitionResultsHolder single_instance = null;


    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionFaceRecognitionResults;
    private LinkedHashMap<Integer, String> timestampList;

    /**
     * Constructor that initialises the tables used to store the data
     */
    private FacialEmotionRecognitionResultsHolder()
    {
        emotionFaceRecognitionResults = new LinkedHashMap<>();
        timestampList = new LinkedHashMap<>();
    }

    /**
     * Method that stores a new record of facial emotion recognition results
     *
     * @param imageId        - int: the id of the image
     * @param emotionsResult - ArrayList<EmotionValuesDataset>: the emotions values
     * @param timestamp      - String: a timestamp of frame that those record were taken
     */
    public void addNewEmotionResult(int imageId, ArrayList<EmotionValuesDataset> emotionsResult,String timestamp) {
        emotionFaceRecognitionResults.put(imageId, emotionsResult);
        timestampList.put(imageId, timestamp);
    }

    /**
     * Method that returns a table of all stored facial emotion recognition results.
     *
     * @return LinkedHashMap: the result
     */
    public LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> getAllEmotionRecognitionResults() {
        return emotionFaceRecognitionResults;
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
     * Method that initialises the singleton object
     *
     * @return FacialEmotionRecognitionResultsHolder: the current instance of the object
     */
    public static FacialEmotionRecognitionResultsHolder getInstance()
    {
        if (single_instance == null)
            single_instance = new FacialEmotionRecognitionResultsHolder();

        return single_instance;
    }


    /**
     * Clear the holder object
     */
    public static void clean()
    {
        single_instance = null;
    }


}
