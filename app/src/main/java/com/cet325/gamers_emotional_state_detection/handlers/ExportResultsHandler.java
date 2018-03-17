package com.cet325.gamers_emotional_state_detection.handlers;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.UserDetailsHolders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ExportResultsHandler {

    public ExportResultsHandler(){}

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

        for(Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> allFramesList = emotionsResultList.entrySet().iterator(); allFramesList.hasNext(); )
        {
            JSONObject jsonResultsObject = new JSONObject();
            JSONArray jsonEmotionsArray = new JSONArray();

            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> singleFrame = allFramesList.next();

            if(singleFrame.getValue() != null) {
                for (EmotionValuesDataset emotion : singleFrame.getValue()) {
                    JSONObject jsonEmotionObject = new JSONObject();
                    jsonEmotionObject.put(emotion.getEmotionName(), emotion.getEmotionValue());
                    jsonEmotionsArray.put(jsonEmotionObject);
                }
            }else
            {
                JSONObject jsonEmotionObject = new JSONObject();
                jsonEmotionObject.put("[]","[]");
            }

            jsonResultsObject.put("frame", singleFrame.getKey());
            jsonResultsObject.put("emotions", jsonEmotionsArray);

            jsonFramesArray.put(jsonResultsObject);
        }

        jsonUserObject.put("result", jsonFramesArray);

        saveToFile(jsonUserObject.toString(4), userID);

    }

    private void saveToFile(String resultsDataJson, String userID) throws IOException {


        // Adding the output files into a subfolder inside Downloads - not working
        // File folder = new File(Environment.getExternalStorageDirectory() + "/Download/GEMA_output");
        // FileWriter fw;
        //
        // boolean success = true;
        //
        // if (!folder.exists()) {
        //     success = folder.mkdir();
        // }
        //
        // if (success) {
        //     fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/GEMA_output/results_ID_"+ 1 + ".json");
        // } else {
        //     fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/results_ID_"+ 1 + ".json");
        // }

        FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/Download/results_ID_"+ userID + ".json");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(resultsDataJson);
        bw.close();
    }

}
