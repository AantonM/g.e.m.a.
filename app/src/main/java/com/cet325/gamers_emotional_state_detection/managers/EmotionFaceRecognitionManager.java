package com.cet325.gamers_emotional_state_detection.managers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class EmotionFaceRecognitionManager {
    private CameraManager camManager;
    private EmotionRecognitionManager emotionRecognitionManager;
    private Context context;
    private Handler handlerPictures;
    private Handler handlerEmotion;
    private String emotion;
    private List<String> lstEmotionResults;

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

    private Runnable rEmotionRecognition = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("DevDebug", "EmotFaceRecognitionMngr: Emotion recognition called.");
                emotion = emotionRecognitionManager.createEmotionRequiest();
                addNewEmotionToList(emotion);
            } finally {
                Log.d("DevDebug", "EmotFaceRecognitionMngr: Emotion list " + lstEmotionResults);
                handlerEmotion.postDelayed(rEmotionRecognition, 1000);
            }
        }
    };

    public EmotionFaceRecognitionManager(Context context) {
        this.context = context;
        handlerPictures = new Handler();
        handlerEmotion = new Handler();
        camManager = new CameraManager(context);
        lstEmotionResults = new ArrayList();
        emotionRecognitionManager = new EmotionRecognitionManager();

    }

    public void startEmotionFaceRecognition() {
        rPictureTaking.run();
        rEmotionRecognition.run();
    }

    public void stopEmotionFaceRecognition() {
        camManager.stopTakingPictures();
        handlerPictures.removeCallbacks(rPictureTaking);
        handlerEmotion.removeCallbacks(rEmotionRecognition);
        Log.d("DevDebug", "EmotionFaceRecognitionManager: Emotion face recognition stopped.");
    }

    private void addNewEmotionToList(String newEmotion){
        if(newEmotion != null) {
            lstEmotionResults.add(newEmotion);
        }
    }

}
