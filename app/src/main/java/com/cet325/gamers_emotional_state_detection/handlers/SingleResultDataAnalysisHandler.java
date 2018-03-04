package com.cet325.gamers_emotional_state_detection.handlers;

import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SingleResultDataAnalysisHandler
{

    private LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> emotionsResultList;

    public SingleResultDataAnalysisHandler()
    {
        emotionsResultList = EmotionFaceRecognitionResultsHolder.getInstance().getAllEmotionRecognitionResults();
    }



    public void removeEmptyValues()
    {
        for(Iterator<Map.Entry<Integer, ArrayList<EmotionValuesDataset>>> it = emotionsResultList.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<Integer, ArrayList<EmotionValuesDataset>> e = it.next();
            if(e.getValue().size() == 0)
            {
                it.remove();
            }
        }

        Log.d("DevDebug:","SingleResultDataAnalysisHandler: Empty values are removed from the result list.");

    }
}
