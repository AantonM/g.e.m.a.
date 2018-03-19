package com.cet325.gamers_emotional_state_detection.datasenders;

import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;

import java.util.ArrayList;

public interface OnDataSendToGameplayActivity {

    void pingActivityNewDataAvailable(ArrayList<EmotionValuesDataset> emotionvaluesdataset);

}