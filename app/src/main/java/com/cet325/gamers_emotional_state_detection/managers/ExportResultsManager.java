package com.cet325.gamers_emotional_state_detection.managers;


import com.cet325.gamers_emotional_state_detection.handlers.ExportResultsHandler;

import org.json.JSONException;

import java.io.IOException;

public class ExportResultsManager {

    public ExportResultsManager(){}

    public void exportRawData(){
        ExportResultsHandler exportResultsHandler = new ExportResultsHandler();
        try {
            exportResultsHandler.exportRawDataToJson();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportAnalysedData(){
        ExportResultsHandler exportResultsHandler = new ExportResultsHandler();
        try {
            exportResultsHandler.exportAnalysedDataToJson();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
