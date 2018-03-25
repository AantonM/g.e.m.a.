package com.cet325.gamers_emotional_state_detection.managers;

import android.util.Log;

import com.cet325.gamers_emotional_state_detection.holders.AnalysedFacialEmotionRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.FacialEmotionRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;
import com.cet325.gamers_emotional_state_detection.holders.UserDetailsHolders;

/**
 * Manager that is responsible for the process of cleaning all Holder objects
 */
public class HolderCleanerManager {

    private AnalysedFacialEmotionRecognitionResultsHolder analysedFacialEmotionRecognitionResultsHolder;
    private FacialEmotionRecognitionResultsHolder facialEmotionRecognitionResultsHolder;
    private ImageHolder imageHolder;
    private UserDetailsHolders userDetailsHolders;

    /**
     * Constructor gets an instantance of all Holders.
     */
    public HolderCleanerManager() {
        analysedFacialEmotionRecognitionResultsHolder = AnalysedFacialEmotionRecognitionResultsHolder.getInstance();
        facialEmotionRecognitionResultsHolder = FacialEmotionRecognitionResultsHolder.getInstance();
        imageHolder = ImageHolder.getInstance();
        userDetailsHolders = UserDetailsHolders.getInstance();
    }

    /**
     * Method that cleans all the Holders.
     */
    public void cleanHolders() {
        analysedFacialEmotionRecognitionResultsHolder.clean();
        facialEmotionRecognitionResultsHolder.clean();
        imageHolder.clean();
        userDetailsHolders.clean();

        Log.d("DevDebug", "Holder Cleaner Manager: Holders has been cleaned.");

    }

}
