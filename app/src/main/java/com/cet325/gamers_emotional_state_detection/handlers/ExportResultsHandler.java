package com.cet325.gamers_emotional_state_detection.handlers;


import android.os.Environment;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.AnalysedEmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.UserDetailsHolders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExportResultsHandler {

    public ExportResultsHandler() {
    }

    public void exportRawDataToJson() throws JSONException, IOException {

        LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionsResultList = EmotionFaceRecognitionResultsHolder.getInstance().getAllEmotionRecognitionResults();
        UserDetailsHolders userDetailsHolders = UserDetailsHolders.getInstance();
        String userID = userDetailsHolders.getUserId();
        String gameDifficulty = userDetailsHolders.getGameDifficulty();
        String notes = userDetailsHolders.getNotes();

        //user details json
        JSONObject jsonUserObject = new JSONObject();
        jsonUserObject.put("user ID", userID);
        jsonUserObject.put("difficulty", gameDifficulty);
        jsonUserObject.put("notes", notes);

        //all frames results json
        JSONArray jsonFramesArray = new JSONArray();

        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> allFramesList = emotionsResultList.entrySet().iterator(); allFramesList.hasNext(); ) {
            JSONObject jsonResultsObject = new JSONObject();
            JSONArray jsonEmotionsArray = new JSONArray();

            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> singleFrame = allFramesList.next();

            if (singleFrame.getValue() != null) {
                for (EmotionValuesDataset emotion : singleFrame.getValue()) {
                    JSONObject jsonEmotionObject = new JSONObject();
                    jsonEmotionObject.put(emotion.getEmotionName(), emotion.getEmotionValue());
                    jsonEmotionsArray.put(jsonEmotionObject);
                }
            } else {
                JSONObject jsonEmotionObject = new JSONObject();
                jsonEmotionObject.put("[]", "[]");
            }

            jsonResultsObject.put("frame", singleFrame.getKey());
            jsonResultsObject.put("emotions", jsonEmotionsArray);

            jsonFramesArray.put(jsonResultsObject);
        }

        jsonUserObject.put("result", jsonFramesArray);

        saveToFile(jsonUserObject.toString(4), userID, false);

    }

    public void exportAnalysedDataToJson() throws JSONException, IOException {
        LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> analysedEmotionsResultList = AnalysedEmotionFaceRecognitionResultsHolder.getInstance().getAllAnalysedEmotionRecognitionResults();
        HashMap<String, Double> summedEmotionsResults = AnalysedEmotionFaceRecognitionResultsHolder.getInstance().getSummedEmotionValues();

        UserDetailsHolders userDetailsHolders = UserDetailsHolders.getInstance();
        String userID = userDetailsHolders.getUserId();
        String gameDifficulty = userDetailsHolders.getGameDifficulty();
        String notes = userDetailsHolders.getNotes();

        //user details json
        JSONObject jsonUserObject = new JSONObject();
        jsonUserObject.put("user ID", userID);
        jsonUserObject.put("difficulty", gameDifficulty);
        jsonUserObject.put("notes", notes);

        //summed emotions values results
        JSONArray jsonSummedEmotionValuesArray = new JSONArray();
        for (Map.Entry<String, Double> summedEmotion : summedEmotionsResults.entrySet()) {
            JSONObject jsonEmotionObject = new JSONObject();
            jsonEmotionObject.put(summedEmotion.getKey(), summedEmotion.getValue());
            jsonSummedEmotionValuesArray.put(jsonEmotionObject);
        }
        jsonUserObject.put("calculated emotion's values", jsonSummedEmotionValuesArray);

        //all frames results json
        JSONArray jsonFramesArray = new JSONArray();

        for (Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> allFramesList = analysedEmotionsResultList.entrySet().iterator(); allFramesList.hasNext(); ) {
            JSONObject jsonResultsObject = new JSONObject();
            JSONArray jsonEmotionsArray = new JSONArray();

            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> singleFrame = allFramesList.next();

            if (singleFrame.getValue() != null) {
                for (EmotionValuesDataset emotion : singleFrame.getValue()) {
                    JSONObject jsonEmotionObject = new JSONObject();
                    jsonEmotionObject.put(emotion.getEmotionName(), emotion.getEmotionValue());
                    jsonEmotionsArray.put(jsonEmotionObject);
                }
            } else {
                JSONObject jsonEmotionObject = new JSONObject();
                jsonEmotionObject.put("[]", "[]");
            }

            jsonResultsObject.put("frame", singleFrame.getKey());
            jsonResultsObject.put("emotions", jsonEmotionsArray);

            jsonFramesArray.put(jsonResultsObject);
        }

        jsonUserObject.put("result", jsonFramesArray);

        saveToFile(jsonUserObject.toString(4), userID, true);
    }


    private void saveToFile(String resultsDataJson, String userID, boolean isDataAnalysed) throws IOException {

        FileWriter fw;

        File folder = new File(Environment.getExternalStorageDirectory() + "/Download/GEMA_output");
        boolean success = true;

        if (!folder.exists()) {
                 success = folder.mkdir();
        }

        if(success){
            if(isDataAnalysed){
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/GEMA_output/analysed_results_ID_" + userID + ".json");
            }else{
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/GEMA_output/results_ID_" + userID + ".json");
            }
        }else{
            if(isDataAnalysed){
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/analysed_results_ID_" + userID + ".json");
            }else{
                fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/results_ID_" + userID + ".json");
            }
        }


        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(resultsDataJson);
        bw.close();
    }

}
