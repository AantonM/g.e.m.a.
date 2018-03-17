package com.cet325.gamers_emotional_state_detection.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;
import com.microsoft.projectoxford.face.contract.Face;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import com.microsoft.projectoxford.face.*;
import com.microsoft.projectoxford.face.contract.*;
import com.microsoft.projectoxford.face.rest.ClientException;

public class EmotionRecognitionApiHandler
{
    private Bitmap face_picture;
    private ByteArrayInputStream sentImage;
    private int pictureId;
    private ArrayList<EmotionValuesDataset> emotionvaluesdataset;
    private OnDataSendToGameplayActivity dataSendToActivity;
    private GetEmotionCall emotionCall;
    private boolean realtimeResultDisplay;

    public void runEmotionalFaceRecognition(Bitmap picture, int pictureId, OnDataSendToGameplayActivity dataSendToActivity)
    {
        this.dataSendToActivity = dataSendToActivity;

        this.face_picture = picture;
        this.pictureId = pictureId;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences((Context) dataSendToActivity);
        realtimeResultDisplay = prefs.getBoolean("displayResultsRealtime", false);


        if(face_picture!= null) {
            encodeToBase64();
            emotionCall = new GetEmotionCall();
            try {
                emotionCall.execute(sentImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // convert image to base 64 so that we can send the image to Emotion API
    private void encodeToBase64() {

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         face_picture.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
         sentImage = new ByteArrayInputStream(baos.toByteArray());
    }

    public void stopEmotionalFaceRecognition()
    {
        emotionCall.cancel(true);
    }

    private class GetEmotionCall extends AsyncTask<InputStream, String, Face[]> {

        // this function is called before the api call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DevDebug","EmotionRecognitionApiHandler: Getting results...");
        }

        // this function is called when the api call is made
        @Override
        protected Face[] doInBackground(InputStream... params) {
            FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0", "e1078de486574726866bcfebd0f90036");

            // the only attribute wanted is the emotional state - FaceAttributeType.Emotion
            FaceServiceClient.FaceAttributeType[] expectedFaceAttributes = new FaceServiceClient.FaceAttributeType[]{FaceServiceClient.FaceAttributeType.Emotion};

            try {
                //API call
                return faceServiceClient.detect( params[0], false, false, expectedFaceAttributes );
            } catch (ClientException e) {
                Log.e("ClientException", e.toString());
                return null;
            } catch (IOException e) {
                Log.e("IOException", e.toString());
                e.printStackTrace();
                return null;
            }
        }

        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(Face[] result) {
            emotionvaluesdataset = new ArrayList<>();

            try {

                if(result.length > 0 && result != null)
                {
                    saveEmotionalValues(result);
                }
                else if(result.length == 0)
                {
                    saveEmptyEmotionalValues();
                }
                else if(result == null)
                {
                    Log.d("DevDebug","EmotionRecognitionApiHandler: A problem with the API has occured.");
                }

            } catch (Exception e) {
                Log.e("DevDebug", "EmotionRecognitionApiHandler: " + e.toString());
                Log.d("DevDebug","EmotionRecognitionApiHandler: No emotion detected.");
            }
        }

        private void saveEmotionalValues(Face[] result){
            emotionvaluesdataset = new ArrayList<>();

            //save emotions into an arraylist
            emotionvaluesdataset.add(new EmotionValuesDataset("anger", result[0].faceAttributes.emotion.anger));
            emotionvaluesdataset.add(new EmotionValuesDataset("contempt", result[0].faceAttributes.emotion.contempt));
            emotionvaluesdataset.add(new EmotionValuesDataset("disgust", result[0].faceAttributes.emotion.disgust));
            emotionvaluesdataset.add(new EmotionValuesDataset("fear", result[0].faceAttributes.emotion.fear));
            emotionvaluesdataset.add(new EmotionValuesDataset("happiness", result[0].faceAttributes.emotion.happiness));
            emotionvaluesdataset.add(new EmotionValuesDataset("neutral", result[0].faceAttributes.emotion.neutral));
            emotionvaluesdataset.add(new EmotionValuesDataset("sadness", result[0].faceAttributes.emotion.sadness));
            emotionvaluesdataset.add(new EmotionValuesDataset("surprise", result[0].faceAttributes.emotion.surprise));


            //save emotional state result into a holder
            saveEmotionRecognitionResultToHolder(emotionvaluesdataset);

            //print the result into scroolView
            if (realtimeResultDisplay) {
                dataSendToActivity.pingActivityNewDataAvailable();
            }

            Log.d("DevDebug", "EmotionRecognitionApiHandler: result -  " + emotionvaluesdataset.toString());
        }

        private void saveEmptyEmotionalValues() {
            emotionvaluesdataset = new ArrayList<>();

            //save emotional state result into a holder
            saveEmotionRecognitionResultToHolder(null);

            //print the result into scroolView
            if (realtimeResultDisplay) {
                dataSendToActivity.pingActivityNewDataAvailable();
            }

            Log.d("DevDebug", "EmotionRecognitionApiHandler: No emotion detected. Empty values are set.");
        }

        private void saveEmotionRecognitionResultToHolder(ArrayList<EmotionValuesDataset> emotionalStates) {
            EmotionFaceRecognitionResultsHolder emotionFaceRecognitionResultsHolder = EmotionFaceRecognitionResultsHolder.getInstance();
            emotionFaceRecognitionResultsHolder.addNewEmotionResult(pictureId, emotionalStates);
        }

    }

}
