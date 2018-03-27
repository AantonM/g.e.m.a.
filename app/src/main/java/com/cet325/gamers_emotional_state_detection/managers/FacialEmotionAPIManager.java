package com.cet325.gamers_emotional_state_detection.managers;

import android.app.Activity;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.activities.GameplayActivity;
import com.cet325.gamers_emotional_state_detection.handlers.EmotionRecognitionApiHandler;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;

/**
 * Manager that is responsible for the EmotionRecognitionApiHandler and
 * the process of stating and stopping the calls to the FaceAPI
 */
public class FacialEmotionAPIManager {

    private ImageHolder imageHolder;
    private EmotionRecognitionApiHandler erah;
    private int imageNumber;
    private OnDataSendToGameplayActivity dataSendToActivity;

    /**
     * Contructor gets the imageHolder istance
     * and initializes the data sender to the activity interface
     *
     * @param activity Activity
     */
    public FacialEmotionAPIManager(Activity activity) {
        imageHolder = ImageHolder.getInstance();
        imageNumber = 0;
        dataSendToActivity = (GameplayActivity) activity;
    }

    /**
     * Method that starts the EmotionalFaceRecognition process
     * if there is a new image available
     */
    public void createEmotionRequiest() {
        if(thereIsANewImage())
        {
            erah = new EmotionRecognitionApiHandler();
            erah.runEmotionalFaceRecognition(imageHolder.getImage(), imageNumber, imageHolder.getTimestamp(), dataSendToActivity);
        }
    }

    /**
     * Method that checks if there is a new image
     * stored in the holder
     *
     * @return boolean - true: if there is a new image, false:
     */
    private boolean thereIsANewImage() {

        if(imageHolder.getImageNumber() == imageNumber) {
            //there is NO new image
            return false;
        }
        else if(imageHolder.getImageNumber() > imageNumber) {
            //there is new image
            imageNumber++;
            return true;
        }
        return false;
    }

    /**
     * Method that stops the API to from the Handlers
     */
    public void stopFacialRecognition()
    {
        Log.d("DevDebug", "EmotionRecognitionAPIManager: API recognition stopped.");
        if(erah != null){
            erah.stopFacialEmotionRecognition();
        }
    }
}
