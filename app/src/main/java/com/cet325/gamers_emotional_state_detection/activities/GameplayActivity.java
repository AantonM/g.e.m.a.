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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.game.Gameplay_PathOfAnth;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;
import com.cet325.gamers_emotional_state_detection.managers.EmotionFaceRecognitionManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameplayActivity extends AppCompatActivity implements OnDataSendToGameplayActivity, View.OnTouchListener {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    EmotionFaceRecognitionManager emotionFaceRecognitionManager;
    Gameplay_PathOfAnth game;
    Context cntx;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        cntx = this;

        if(!haveNetworkConnection()){
            showNoInternetAllert();
        }else {
            startEmotionFaceRecognition();
            startGame();
        }
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


    private void startGame() {
        game = new Gameplay_PathOfAnth(this);
        game.setOnTouchListener(this);

        RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        gameLayout.addView(game);

    }

    @Override
    public void pingActivityNewDataAvailable(ArrayList<EmotionValuesDataset> emotionvaluesdataset) {
        TableRow tableRowVaues = (TableRow) findViewById(R.id.realTimeEmotionsValues);
        TableRow tableRowError = (TableRow) findViewById(R.id.realTimeEmotionsError);

        TextView txtEmotionAngryValue = (TextView) findViewById(R.id.txtEmotionAngryValue);
        TextView txtEmotionContemptValue = (TextView) findViewById(R.id.txtEmotionContemptValue);
        TextView txtEmotionDisgussedValue = (TextView) findViewById(R.id.txtEmotionDisgussedValue);
        TextView txtEmotionFearValue = (TextView) findViewById(R.id.txtEmotionFearValue);
        TextView txtEmotionHappyValue = (TextView) findViewById(R.id.txtEmotionHappyValue);
        TextView txtEmotionNeutrallValue = (TextView) findViewById(R.id.txtEmotionNeutrallValue);
        TextView txtEmotionSadValue = (TextView) findViewById(R.id.txtEmotionSadValue);
        TextView txtEmotionSurprisedValue = (TextView) findViewById(R.id.txtEmotionSurprisedValue);

        if(emotionvaluesdataset == null){
            tableRowVaues.setVisibility(View.INVISIBLE);
            tableRowError.setVisibility(View.VISIBLE);
        }else{
            tableRowVaues.setVisibility(View.VISIBLE);
            tableRowError.setVisibility(View.INVISIBLE);

            for(EmotionValuesDataset emotion : emotionvaluesdataset){

                switch(emotion.getEmotionName()){
                    case "anger": txtEmotionAngryValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "contempt": txtEmotionContemptValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "disgust": txtEmotionDisgussedValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "fear": txtEmotionFearValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "happiness": txtEmotionHappyValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "neutral": txtEmotionNeutrallValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "sadness": txtEmotionSadValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "surprise": txtEmotionSurprisedValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                }

            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(id == R.id.btn_stopGameplay) {
            emotionFaceRecognitionManager.stopEmotionFaceRecognition();
            game.stop();

            Intent afterActionResultIntent = new Intent(cntx, AfterActionResultActivity.class);
            afterActionResultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(afterActionResultIntent);
        }
        return true;
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

        if(game != null){
            game.stop();
        }
        super.onDestroy();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return game.touchListener(v,event);
    }
}
