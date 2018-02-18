package com.cet325.gamers_emotional_state_detection.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.managers.EmotionFaceRecognitionManager;

public class GameplayActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    EmotionFaceRecognitionManager emotionFaceRecognitionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        startEmotionFaceRecognition();
    }

    private void startEmotionFaceRecognition()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_CAMERA_PERMISSION);
            return;
        }

        emotionFaceRecognitionManager = new EmotionFaceRecognitionManager(getApplicationContext());
        emotionFaceRecognitionManager.startEmotionFaceRecognition();


    //TODO: delete
        final Button btnStop = (Button) findViewById(R.id.button2);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStop.setVisibility(View.INVISIBLE);
                emotionFaceRecognitionManager.stopEmotionFaceRecognition();
                printResult();
            }
        });
    }

    //TODO:delete
    private void printResult() {
        TextView txtResult = (TextView) findViewById(R.id.txtEmotionValue);
        EmotionFaceRecognitionResultsHolder emotionFaceRecognitionResultsHolder = EmotionFaceRecognitionResultsHolder.getInstance();
        txtResult.setText(emotionFaceRecognitionResultsHolder.getEmotionResultForGivenImg().toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
