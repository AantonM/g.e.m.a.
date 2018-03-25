package com.cet325.gamers_emotional_state_detection.managers;

import com.cet325.gamers_emotional_state_detection.handlers.SingleResultDataAnalysisHandler;

/**
 * Manager that is responsible for the SingleResultDataAnalysisHandler and
 * the process analysing data
 */
public class SingleResultDataAnalysisManager {

    private SingleResultDataAnalysisHandler singleResultDataAnalysisHandler;

    /**
     * Constructor.
     */
    public SingleResultDataAnalysisManager() {
        singleResultDataAnalysisHandler = new SingleResultDataAnalysisHandler();
    }

    /**
     * Method that executes the data analysis.
     */
    public void startAnalysis() {
        singleResultDataAnalysisHandler.executeMultyLayerDataFusion();
    }
}
