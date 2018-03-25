package com.cet325.gamers_emotional_state_detection.managers;


import com.cet325.gamers_emotional_state_detection.handlers.ExportResultsHandler;
import org.json.JSONException;
import java.io.IOException;

/**
 * Manager that is responsible for the ExportResultsHandler and
 * the process of exporting data to Json file
 */
public class ExportResultsManager {

    /**
     * Constructor.
     */
    public ExportResultsManager() {
    }

    /**
     * Method that calls the Handler to export raw data.
     */
    public void exportRawData() {
        ExportResultsHandler exportResultsHandler = new ExportResultsHandler();
        try {
            exportResultsHandler.exportRawDataToJson();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that calls the Handler to export analysed data.
     */
    public void exportAnalysedData() {
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
