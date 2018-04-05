package com.cet325.gamers_emotional_state_detection.managers;

import android.content.Context;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.handlers.CameraHandler;

/**
 * Manager that is responsible for the CameraHandler and
 * the process of stating and stopping the picture taking process
 */
public class CameraManager {
    private CameraHandler camServ;

    /**
     * Constructor that initializes the handler
     *
     * @param applicationContext Context:context
     */
    public CameraManager(Context applicationContext) {
        camServ = new CameraHandler(applicationContext);
        camServ.openCameraPicture();
    }

    /**
     * Method that calls the Handler to take a picture
     */
    public void startTakingPictures() {
        camServ.takePicture();
    }

    /**
     * Method that calls the Handler to close the camera
     */
    public void stopTakingPictures() {
        Log.d("DevDebug", "Camera Manager: Taking pictures stopped.");
        camServ.closeCameraPicture();
    }

}
