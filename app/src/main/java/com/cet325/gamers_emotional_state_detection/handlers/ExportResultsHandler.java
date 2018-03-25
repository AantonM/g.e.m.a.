package com.cet325.gamers_emotional_state_detection.handlers;


import android.os.Environment;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedFacialEmotionRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.FacialEmotionRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.UserDetailsHolders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ExportResultsHandler is responsible to handle all the work regarding the
 * exportation of the results data into a Json file
 */
public class ExportResultsHandler {

    /**
     * Constructor.
     */
    public ExportResultsHandler() {
    }

    /**
     * Method that reads the raw data (before analysis) from the holder object (FacialEmotionRecognitionResultsHolder)
     * and saves into a JsonOnject, and then sends it for a file saving
     *
     * @throws JSONException
     * @throws IOException
     */
    public void exportRawDataToJson() throws JSONException, IOException {

        //gets all of the raw data.
        LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionsResultList = FacialEmotionRecognitionResultsHolder.getInstance().getAllEmotionRecognitionResults();
        //gets the user details
        UserDetailsHolders userDetailsHolders = UserDetailsHolders.getInstance();
        String userID = userDetailsHolders.getUserId();
        String gameDifficulty = userDetailsHolders.getGameDifficulty();
        String notes = userDetailsHolders.getNotes();
        String timestamp = userDetailsHolders.getTimestamp();

        //user details json
        JSONObject jsonUserObject = new JSONObject();
        jsonUserObject.put("user ID", userID);
        jsonUserObject.put("difficulty", gameDifficulty);
        jsonUserObject.put("notes", notes);
        jsonUserObject.put("timestamp", timestamp);


        //all frames results json
        JSONArray jsonFramesArray = new JSONArray();

        //iterates through all frames
        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> allFramesList = emotionsResultList.entrySet().iterator(); allFramesList.hasNext(); ) {
            JSONObject jsonResultsObject = new JSONObject();
            JSONArray jsonEmotionsArray = new JSONArray();

            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> singleFrame = allFramesList.next();

            //if the frame has emotion values
            if (singleFrame.getValue() != null) {
                //iterate through all emotion values and saves them into Json object
                for (EmotionValuesDataset emotion : singleFrame.getValue()) {
                    JSONObject jsonEmotionObject = new JSONObject();
                    jsonEmotionObject.put(emotion.getEmotionName(), emotion.getEmotionValue());
                    jsonEmotionsArray.put(jsonEmotionObject);
                }
            } else {
                //save empty value into the Json object
                JSONObject jsonEmotionObject = new JSONObject();
                jsonEmotionObject.put("[]", "[]");
            }

            //append the emotion values to the json
            jsonResultsObject.put("frame", singleFrame.getKey());
            jsonResultsObject.put("emotions", jsonEmotionsArray);

            jsonFramesArray.put(jsonResultsObject);
        }

        jsonUserObject.put("result", jsonFramesArray);

        //send the jsonObject for file saving
        saveToFile(jsonUserObject.toString(4), userID, timestamp, false);

    }

    /**
     * Method that reads the analysed data from the holder object (AnalysedEmotionFaceRecognitionResultsHolder)
     * and saves into a JsonOnject, and then sends it for a file saving
     *
     * @throws JSONException
     * @throws IOException
     */
    public void exportAnalysedDataToJson() throws JSONException, IOException {
        //gets all of the analysed data.
        LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> analysedEmotionsResultList = AnalysedFacialEmotionRecognitionResultsHolder.getInstance().getAllAnalysedEmotionRecognitionResults();
        HashMap<String, Double> summedEmotionsResults = AnalysedFacialEmotionRecognitionResultsHolder.getInstance().getPercentageOfMainEmotionValues();

        //gets the user details
        UserDetailsHolders userDetailsHolders = UserDetailsHolders.getInstance();
        String userID = userDetailsHolders.getUserId();
        String gameDifficulty = userDetailsHolders.getGameDifficulty();
        String notes = userDetailsHolders.getNotes();
        String timestamp = userDetailsHolders.getTimestamp();

        //user details json
        JSONObject jsonUserObject = new JSONObject();
        jsonUserObject.put("user ID", userID);
        jsonUserObject.put("difficulty", gameDifficulty);
        jsonUserObject.put("notes", notes);
        jsonUserObject.put("timestamp", timestamp);

        //summed emotions values results
        JSONArray jsonSummedEmotionValuesArray = new JSONArray();
        //iterate throught the percentage of emotion values
        for (Map.Entry<String, Double> summedEmotion : summedEmotionsResults.entrySet()) {
            JSONObject jsonEmotionObject = new JSONObject();

            DecimalFormat format = new DecimalFormat("0.00");
            String formattedValue = format.format(summedEmotion.getValue());

            jsonEmotionObject.put(summedEmotion.getKey(), formattedValue);
            jsonSummedEmotionValuesArray.put(jsonEmotionObject);
        }
        jsonUserObject.put("percentage emotion's values", jsonSummedEmotionValuesArray);

        //all frames results json
        JSONArray jsonFramesArray = new JSONArray();

        //iterates through all frames
        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> allFramesList = analysedEmotionsResultList.entrySet().iterator(); allFramesList.hasNext(); ) {
            JSONObject jsonResultsObject = new JSONObject();
            JSONArray jsonEmotionsArray = new JSONArray();

            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> singleFrame = allFramesList.next();

            //if the frame has emotion values
            if (singleFrame.getValue() != null) {
                //iterate through all emotion values and saves them into Json object
                for (EmotionValuesDataset emotion : singleFrame.getValue()) {
                    JSONObject jsonEmotionObject = new JSONObject();
                    jsonEmotionObject.put(emotion.getEmotionName(), emotion.getEmotionValue());
                    jsonEmotionsArray.put(jsonEmotionObject);
                }
            } else {
                //save empty value into the Json object
                JSONObject jsonEmotionObject = new JSONObject();
                jsonEmotionObject.put("[]", "[]");
            }

            //append the emotion values to the json
            jsonResultsObject.put("frame", singleFrame.getKey());
            jsonResultsObject.put("emotions", jsonEmotionsArray);

            jsonFramesArray.put(jsonResultsObject);
        }

        jsonUserObject.put("result", jsonFramesArray);

        //send the jsonObject for file saving
        saveToFile(jsonUserObject.toString(4), userID, timestamp, true);
    }

    /**
     * Save the result data into Json File
     *
     * @param resultsDataJson - String: the data that needs to be saved
     * @param userID          - String: the id of the user
     * @param timestamp       - String: timestamp of the gameplay
     * @param isDataAnalysed  - boolean: true:data is analysed; false: raw data
     * @throws IOException
     */
    private void saveToFile(String resultsDataJson, String userID, String timestamp, boolean isDataAnalysed) throws IOException {

        FileWriter fw;

        //create output folder
        File folder = new File(Environment.getExternalStorageDirectory() + "/Download/GEMA_output");
        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }

        //create the outputfile
        if (success) {
            if (isDataAnalysed) {
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/GEMA_output/analysed_results_" + userID + "_" + timestamp + ".json");
            } else {
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/GEMA_output/raw_results_" + userID + "_" + timestamp + ".json");
            }
        } else {
            if (isDataAnalysed) {
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/analysed_results_" + userID + "_" + timestamp + ".json");
            } else {
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/raw_results_" + userID + "_" + timestamp + ".json");
            }
        }

        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(resultsDataJson);
        bw.close();
    }

}
