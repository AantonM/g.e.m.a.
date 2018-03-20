package com.cet325.gamers_emotional_state_detection.managers;

import android.util.Log;

import com.cet325.gamers_emotional_state_detection.holders.AnalysedEmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;
import com.cet325.gamers_emotional_state_detection.holders.UserDetailsHolders;

public class HolderCleanerManager {

    AnalysedEmotionFaceRecognitionResultsHolder analysedEmotionFaceRecognitionResultsHolder;
    EmotionFaceRecognitionResultsHolder emotionFaceRecognitionResultsHolder;
    ImageHolder imageHolder;
    UserDetailsHolders userDetailsHolders;

    public HolderCleanerManager(){
        analysedEmotionFaceRecognitionResultsHolder = AnalysedEmotionFaceRecognitionResultsHolder.getInstance();
        emotionFaceRecognitionResultsHolder = EmotionFaceRecognitionResultsHolder.getInstance();
        imageHolder = ImageHolder.getInstance();
        userDetailsHolders = UserDetailsHolders.getInstance();
    }

    public void cleanHolders(){
        analysedEmotionFaceRecognitionResultsHolder.clean();
        emotionFaceRecognitionResultsHolder.clean();
        imageHolder.clean();
        userDetailsHolders.clean();

        Log.d("DevDebug", "Holder Cleaner Manager: Holders has been cleaned.");

    }

}
