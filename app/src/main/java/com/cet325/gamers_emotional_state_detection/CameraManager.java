package com.cet325.gamers_emotional_state_detection;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class CameraManager {

    CameraHandler camServ;
    Handler handler;

    public CameraManager(Context applicationContext) {
        camServ = new CameraHandler(applicationContext);
        camServ.openCameraPicture();
        handler = new Handler();
    }

    private Runnable rPictureTaking = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("DevDebug", "Camera Manager: Take picture called.");
                camServ.takePicture();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                handler.postDelayed(rPictureTaking, 5000);
            }
        }
    };

    public void startTakingPictures()
    {
        rPictureTaking.run();
    }

    public void stopTakingPictures()
    {
        Log.d("DevDebug", "Camera Manager: Taking pictures stopped.");
        camServ.closeCameraPicture();
        handler.removeCallbacks(rPictureTaking);
    }

}
