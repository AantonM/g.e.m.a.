package com.cet325.gamers_emotional_state_detection.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;
import com.cet325.gamers_emotional_state_detection.managers.EmotionFaceRecognitionManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameplayActivity extends AppCompatActivity implements OnDataSendToGameplayActivity{

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    EmotionFaceRecognitionManager emotionFaceRecognitionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        if(!haveNetworkConnection()){
            showNoInternetAllert();
        }else {

            startEmotionFaceRecognition();

            //TODO: delete
            final Button btnStop = (Button) findViewById(R.id.button2);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStop.setVisibility(View.INVISIBLE);
                    emotionFaceRecognitionManager.stopEmotionFaceRecognition();

                    Intent afterActionResultIntent = new Intent(v.getContext(), AfterActionResultActivity.class);
                    afterActionResultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(afterActionResultIntent);
                }
            });
            //************
        }
    }

    private void showNoInternetAllert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to the Internet and try again.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
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

        emotionFaceRecognitionManager = new EmotionFaceRecognitionManager(this);
        emotionFaceRecognitionManager.startEmotionFaceRecognition();
    }

    @Override
    public void pingActivityNewDataAvailable()
    {

        //TODO: display only the last results. and delete the holder iteration
        ScrollView scrlView = (ScrollView) findViewById(R.id.scrollResult);
        TextView txtResult = (TextView) findViewById(R.id.txtResult);
        txtResult.setText("");
        EmotionFaceRecognitionResultsHolder emotionFaceRecognitionResultsHolder = EmotionFaceRecognitionResultsHolder.getInstance();
        LinkedHashMap<Integer, ArrayList<EmotionValuesDataset>> result = emotionFaceRecognitionResultsHolder.getAllEmotionRecognitionResults();

        for(Map.Entry<Integer, ArrayList<EmotionValuesDataset>> e : result.entrySet()) {
            txtResult.append("\n\n\n" + "Image number: " + e.getKey() + "\n");
            if(e.getValue() != null) {
                for (EmotionValuesDataset set : e.getValue()) {
                    txtResult.append(set.getEmotionName() + " : " + set.getEmotionValue() + "\n");
                }
            }else{
                txtResult.append("[ ]");
            }
        }

        scrlView.computeScroll();
        scrlView.fullScroll(View.FOCUS_DOWN);
        //*********************************
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

    @Override
    public void onBackPressed() {
        EmotionFaceRecognitionResultsHolder emaHandler =  EmotionFaceRecognitionResultsHolder.getInstance();
        emaHandler.clean();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        ImageHolder imgHolder = ImageHolder.getInstance();
        imgHolder.clean();
        if(emotionFaceRecognitionManager!=null) {
            emotionFaceRecognitionManager.stopEmotionFaceRecognition();
        }
        super.onDestroy();
    }
}
