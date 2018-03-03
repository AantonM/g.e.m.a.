package com.cet325.gamers_emotional_state_detection.managers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.activities.GameplayActivity;

import java.util.ArrayList;
import java.util.List;

public class EmotionFaceRecognitionManager {
    private CameraManager camManager;
    private EmotionRecognitionAPIManager emotionRecognitionAPIManager;
    private Handler handlerPictures;
    private Handler handlerEmotion;
    private String emotion;

    //Picture taking runnable
    private Runnable rPictureTaking = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("DevDebug", "EmotFaceRecognitionMngr: Take picture called.");
                camManager.startTakingPictures();
            } finally {
                handlerPictures.postDelayed(rPictureTaking, 5000);
            }
        }
    };

    //EFR runnable
    private Runnable rEmotionRecognition = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("DevDebug", "EmotFaceRecognitionMngr: Emotion recognition called.");
                emotionRecognitionAPIManager.createEmotionRequiest();
            } finally {
                handlerEmotion.postDelayed(rEmotionRecognition, 1000);
            }
        }
    };

    public EmotionFaceRecognitionManager(Activity context) {
        handlerPictures = new Handler();
        handlerEmotion = new Handler();
        camManager = new CameraManager((Context) context);
        emotionRecognitionAPIManager = new EmotionRecognitionAPIManager((Activity) context);

    }

    public void startEmotionFaceRecognition() {
        rPictureTaking.run();
        rEmotionRecognition.run();
    }

    public void stopEmotionFaceRecognition() {
        camManager.stopTakingPictures();
        emotionRecognitionAPIManager.stopFacialRecognition();
        handlerPictures.removeCallbacks(rPictureTaking);
        handlerEmotion.removeCallbacks(rEmotionRecognition);
        Log.d("DevDebug", "EmotionFaceRecognitionManager: Emotion face recognition stopped.");
    }
}
