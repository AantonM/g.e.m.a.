package com.cet325.gamers_emotional_state_detection.datasenders;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;

/**
 * Interface used to send realtime data to the Activity to be displayed.
 */
public interface OnDataSendToGameplayActivity {

    void pingActivityNewDataAvailable(ArrayList<EmotionValuesDataset> emotionvaluesdataset);

}