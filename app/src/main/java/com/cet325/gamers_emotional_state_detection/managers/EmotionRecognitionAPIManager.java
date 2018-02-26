package com.cet325.gamers_emotional_state_detection.managers;

import android.app.Activity;
import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.activities.GameplayActivity;
import com.cet325.gamers_emotional_state_detection.handlers.EmotionRecognitionApiHandler;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;

public class EmotionRecognitionAPIManager {

    private ImageHolder imageHolder;
    private EmotionRecognitionApiHandler erah;
    private int currentImageHolderSize;
    private boolean thereIsANewImage = false;
    private OnDataSendToGameplayActivity dataSendToActivity;

    public EmotionRecognitionAPIManager(Activity activity) {
        imageHolder = ImageHolder.getInstance();
        currentImageHolderSize = 0;

        dataSendToActivity = (GameplayActivity) activity;
    }

    public void createEmotionRequiest() {
        thereIsANewImage = checkForNewImage();
        if(thereIsANewImage)
        {
            erah = new EmotionRecognitionApiHandler();
            erah.runEmotionalFaceRecognition(imageHolder.getImages().get(currentImageHolderSize - 1), currentImageHolderSize, dataSendToActivity);
        }
    }

    private boolean checkForNewImage() {

        if(imageHolder.getImages().size() == currentImageHolderSize) {
            //there is NO new image
            return false;
        }
        else if(imageHolder.getImages().size() > currentImageHolderSize) {
            //there is new image
            currentImageHolderSize++;
            return true;
        }
        return false;
    }
}
