package com.cet325.gamers_emotional_state_detection.managers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.handlers.CameraHandler;

public class CameraManager {
    private CameraHandler camServ;
    private Handler handler;

    public CameraManager(Context applicationContext) {
        camServ = new CameraHandler(applicationContext);
        camServ.openCameraPicture();
    }

    public void startTakingPictures()
    {
        camServ.takePicture();
    }

    public void stopTakingPictures()
    {
        Log.d("DevDebug", "Camera Manager: Taking pictures stopped.");
        camServ.closeCameraPicture();
    }

}
