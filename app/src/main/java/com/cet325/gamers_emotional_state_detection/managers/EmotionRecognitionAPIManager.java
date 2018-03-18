package com.cet325.gamers_emotional_state_detection.managers;

import android.app.Activity;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.activities.GameplayActivity;
import com.cet325.gamers_emotional_state_detection.handlers.EmotionRecognitionApiHandler;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;

public class EmotionRecognitionAPIManager {

    private ImageHolder imageHolder;
    private EmotionRecognitionApiHandler erah;
    private int currentImageHolderSize;
    private int imageNumber;
    private OnDataSendToGameplayActivity dataSendToActivity;

    public EmotionRecognitionAPIManager(Activity activity) {
        imageHolder = ImageHolder.getInstance();
        currentImageHolderSize = 0;
        imageNumber = 0;
        dataSendToActivity = (GameplayActivity) activity;
    }

    public void createEmotionRequiest() {
        if(thereIsANewImage())
        {
            erah = new EmotionRecognitionApiHandler();
            erah.runEmotionalFaceRecognition(imageHolder.getImage(), imageNumber, dataSendToActivity);
        }
    }

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

    public void stopFacialRecognition()
    {
        Log.d("DevDebug", "EmotionRecognitionAPIManager: API recognition stopped.");
        if(erah != null){
            erah.stopEmotionalFaceRecognition();
        }
    }
}
