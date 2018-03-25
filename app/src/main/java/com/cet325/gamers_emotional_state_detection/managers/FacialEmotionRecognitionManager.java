package com.cet325.gamers_emotional_state_detection.managers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Manager that is responsible for the CameraManager and the FacialEmotionAPIManager and the
 * the process synchonising the two managers to work in background
 */
public class FacialEmotionRecognitionManager {
    private CameraManager camManager;
    private FacialEmotionAPIManager facialEmotionAPIManager;
    private Handler handlerPictures;
    private Handler handlerEmotion;

    /**
     * Runnable that executes the startTakingPictures process in background
     * continuously within a set of an interval
     */
    private Runnable rPictureTaking = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("DevDebug", "EmotFaceRecognitionMngr: Take picture called.");
                camManager.startTakingPictures();
            } finally {
                handlerPictures.postDelayed(rPictureTaking, 3000);
            }
        }
    };

    /**
     * Runnable that executes the createEmotionRequiest process in background
     * continuously within a set of an interval
     */
    private Runnable rEmotionRecognition = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("DevDebug", "EmotFaceRecognitionMngr: Emotion recognition called.");
                facialEmotionAPIManager.createEmotionRequiest();
            } finally {
                handlerEmotion.postDelayed(rEmotionRecognition, 200);
            }
        }
    };

    /**
     * Constructor that initialises the Managers.
     *
     * @param context Activity: context
     */
    public FacialEmotionRecognitionManager(Activity context) {
        handlerPictures = new Handler();
        handlerEmotion = new Handler();
        camManager = new CameraManager((Context) context);
        facialEmotionAPIManager = new FacialEmotionAPIManager((Activity) context);

    }

    /**
     * Method that executes the Runnables.
     */
    public void startEmotionFaceRecognition() {
        rPictureTaking.run();
        rEmotionRecognition.run();
    }

    /**
     * Method that stops the Runnables.
     */
    public void stopEmotionFaceRecognition() {
        camManager.stopTakingPictures();
        facialEmotionAPIManager.stopFacialRecognition();
        handlerPictures.removeCallbacks(rPictureTaking);
        handlerEmotion.removeCallbacks(rEmotionRecognition);
        Log.d("DevDebug", "EmotionFaceRecognitionManager: Emotion face recognition stopped.");
    }
}
