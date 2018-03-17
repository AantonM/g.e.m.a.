package com.cet325.gamers_emotional_state_detection.managers;


import com.cet325.gamers_emotional_state_detection.handlers.SingleResultDataAnalysisHandler;

public class SingleResultDataAnalysisManager {

    private SingleResultDataAnalysisHandler singleResultDataAnalysisHandler;

    public SingleResultDataAnalysisManager()
    {
        singleResultDataAnalysisHandler = new SingleResultDataAnalysisHandler();
    }

    public void startAnalysis()
    {
        singleResultDataAnalysisHandler.executeMultyLayerDataFusion();
    }
}
