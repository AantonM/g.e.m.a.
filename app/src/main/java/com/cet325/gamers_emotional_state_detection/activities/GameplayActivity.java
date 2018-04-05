package com.cet325.gamers_emotional_state_detection.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.game.Gameplay_PathOfAnth;
import com.cet325.gamers_emotional_state_detection.managers.FacialEmotionRecognitionManager;
import com.cet325.gamers_emotional_state_detection.managers.HolderCleanerManager;

import java.util.ArrayList;

/**
 * An activity responsible for the Gameplay page, including the game and the realtime
 * visualisation of the emotional state.
 * <p>
 * layout - activity_gameplay
 */
public class GameplayActivity extends AppCompatActivity implements OnDataSendToGameplayActivity, View.OnTouchListener {

    //permition to use the camera of the device
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private FacialEmotionRecognitionManager facialEmotionRecognitionManager;
    private Gameplay_PathOfAnth game;
    private Context cntx;


    /**
     * {@inheritDoc}
     * <p>
     * Called when the activity is starting.
     * Checks for internet connection and if there is starts the game and the emotional face recognition
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        cntx = this;

        //check if there is internet connection
        if (!haveNetworkConnection()) {
            showNoInternetAllert();
        } else {
            startEmotionFaceRecognition();
            startGame();
        }
    }

    /**
     * Checks if the device is connected to the internet.
     *
     * @return boolean -  true: connected, false: not connected
     */
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            //Check if there is wifi conections
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            //checks for 3G/4G connections
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Show an  alertbox that there is no internet connection and finish this activity.
     */
    private void showNoInternetAllert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to the Internet and try again.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //end the activity
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * After checking/requiesting camera permistions start the Facial Emotion Recognition
     * using the FacialEmotionRecognitionManager.
     */
    private void startEmotionFaceRecognition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CAMERA_PERMISSION);
            return;
        }

        facialEmotionRecognitionManager = new FacialEmotionRecognitionManager(this);
        facialEmotionRecognitionManager.startEmotionFaceRecognition();
    }

    /**
     * Start the game.
     */
    private void startGame() {
        //get the level of the game from the user input activity or use default easy
        Intent intent = getIntent();
        int level_difficulty = intent.getIntExtra("level_difficulty", 1);

        //call the game
        game = new Gameplay_PathOfAnth(this, level_difficulty);
        game.setOnTouchListener(this);

        //display the game into the relative layout (id - gameLayout)
        RelativeLayout gameLayout = (RelativeLayout) findViewById(R.id.gameLayout);
        gameLayout.addView(game);

    }

    /**
     * Display the received emotional values on the screen.
     *
     * @param emotionvaluesdataset - arraylist of EmotionValuesDataset with the name and value of the emotion
     */
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

        //check if the received data is not null
        if (emotionvaluesdataset == null) {

            //display a message saying that no face was recognised
            tableRowVaues.setVisibility(View.INVISIBLE);
            tableRowError.setVisibility(View.VISIBLE);
        } else {

            //display the results
            tableRowVaues.setVisibility(View.VISIBLE);
            tableRowError.setVisibility(View.INVISIBLE);

            //iterate through each emotion
            for (EmotionValuesDataset emotion : emotionvaluesdataset) {
                switch (emotion.getEmotionName()) {
                    case "anger":
                        txtEmotionAngryValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "contempt":
                        txtEmotionContemptValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "disgust":
                        txtEmotionDisgussedValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "fear":
                        txtEmotionFearValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "happiness":
                        txtEmotionHappyValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "neutral":
                        txtEmotionNeutrallValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "sadness":
                        txtEmotionSadValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                    case "surprise":
                        txtEmotionSurprisedValue.setText(String.valueOf(emotion.getEmotionValue()));
                        break;
                }

            }
        }
        Log.d("DevDebug", "GameplayActivity: Result has been displayed");
    }

    /**
     * {@inheritDoc}
     *
     * @param requestCode  - The request code passed in.
     * @param permissions  - The requested permissions. Never null.
     * @param grantResults - The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param menu - The options menu in which you place your items.
     * @return boolean - true: for the menu to be displayed; false: it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gameplay, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param item - The menu item that was selected.
     * @return boolean - false: to allow normal menu processing to
     * proceed, true: to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //get the id of the selected item
        int id = item.getItemId();

        //if the stopGameplay btn is pressed
        if (id == R.id.btn_stopGameplay) {

            //stop the facial emotion recognitions
            if (facialEmotionRecognitionManager != null) {
                facialEmotionRecognitionManager.stopEmotionFaceRecognition();
            }

            //stop the game
            if (game != null) {
                game.stop();
            }

            //call the ResultsActivity
            Intent ResultIntent = new Intent(cntx, ResultsActivity.class);
            ResultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(ResultIntent);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * When the back button is pressed stop the facial emotion recognition, stop the game
     * clear the holders and return back to the HomeActivity
     */
    @Override
    public void onBackPressed() {

        //stop the facial emotion recognition
        if (facialEmotionRecognitionManager != null) {
            facialEmotionRecognitionManager.stopEmotionFaceRecognition();
        }

        //stop the game
        if (game != null) {
            game.stop();
        }

        //clear the holders
        HolderCleanerManager holderCleanerManager = new HolderCleanerManager();
        holderCleanerManager.cleanHolders();

        //call the HomeActivity
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Stop the facial emotion recognition, stop the game
     * clear the holders and destroy.
     */
    @Override
    protected void onDestroy() {
        if (facialEmotionRecognitionManager != null) {
            facialEmotionRecognitionManager.stopEmotionFaceRecognition();
        }

        if (game != null) {
            game.stop();
        }
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     *
     * @param v     - The view the touch event has been dispatched to.
     * @param event - The MotionEvent object containing full information about
     *              the event.
     * @return boolean - true: if the listener has consumed the event, false: otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return game.touchListener(v, event);
    }
}
