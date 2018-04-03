package com.cet325.gamers_emotional_state_detection.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.FacialEmotionRecognitionResultsHolder;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * EmotionRecognitionApiHandler is responsible to handle all the work regarding the
 * facial emotion recognition for a given image using FaceAPI.
 * <p>
 * link: www.azure.microsoft.com/en-gb/services/cognitive-services/face/
 */
public class EmotionRecognitionApiHandler {
    //Image variables
    private static Bitmap face_picture;
    private static ByteArrayInputStream sentImage;
    private static int pictureId;
    private static String timestamp;

    //Emotions variables
    private static ArrayList<EmotionValuesDataset> emotionvaluesdataset;
    private static OnDataSendToGameplayActivity dataSendToActivity;
    private static GetEmotionCall emotionCall;
    private static boolean realtimeResultDisplay;

    /**
     * Method reads the inputed image data and execute the facial emotion recognition on an AsyncTask
     *
     * @param picture            Bitmap - the picture
     * @param pictureId          int - the picture ID
     * @param timestamp          String - timestamp of the image
     * @param dataSendToActivity OnDataSendToGameplayActivity - interface to transfer data back to Activity
     */
    public void runEmotionalFaceRecognition(Bitmap picture, int pictureId, String timestamp, OnDataSendToGameplayActivity dataSendToActivity) {
        this.dataSendToActivity = dataSendToActivity;
        this.face_picture = picture;
        this.pictureId = pictureId;
        this.timestamp = timestamp;

        //get preferences for realtime results visualisation
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences((Context) dataSendToActivity);
        realtimeResultDisplay = prefs.getBoolean("displayResultsRealtime", false);

        //check if there is a picture
        if (face_picture != null) {

            //encode the picture to Base64
            encodeToBase64();
            //call the API
            emotionCall = new GetEmotionCall();
            try {
                emotionCall.execute(sentImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that converts the image to base 64 - required format from the FaceAPI
     */
    private void encodeToBase64() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        face_picture.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        sentImage = new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Stop the facial emotion recognition
     */
    public void stopFacialEmotionRecognition() {
        emotionCall.cancel(true);
    }

    /**
     * AsyncTask that send pictures to FaceAPI and received the emotional state of those pictures
     * and saves the results to a holder
     */
    private static class GetEmotionCall extends AsyncTask<InputStream, String, Face[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DevDebug", "EmotionRecognitionApiHandler: Getting results...");
        }

        /**
         * {@inheritDoc}
         * <p>
         * Send the images to the API in background
         */
        @Override
        protected Face[] doInBackground(InputStream... params) {

            //Connect to the API
            FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://westcentralus.api.cognitive.microsoft.com/face/v1.0", "e1078de486574726866bcfebd0f90036");

            // The FaceAPI performs a full evaluation of the send image, but this project only
            // wants the API to analyse the emotional state so we pass the required analysis - FaceAttributeType.Emotion
            FaceServiceClient.FaceAttributeType[] expectedFaceAttributes = new FaceServiceClient.FaceAttributeType[]{FaceServiceClient.FaceAttributeType.Emotion};

            try {
                //API call
                return faceServiceClient.detect(params[0], false, false, expectedFaceAttributes);
            } catch (ClientException e) {
                Log.e("ClientException", e.toString());
                return null;
            } catch (IOException e) {
                Log.e("IOException", e.toString());
                e.printStackTrace();
                return null;
            }
        }

        /**
         * {@inheritDoc}
         * <p>
         * The results from the FaceAPI are saved to the holder.
         */
        @Override
        protected void onPostExecute(Face[] result) {
            emotionvaluesdataset = new ArrayList<>();

            try {

                if (result.length > 0 && result != null) {
                    saveEmotionalValues(result);
                } else if (result.length == 0) {
                    saveEmptyEmotionalValues();
                } else if (result == null) {
                    Log.d("DevDebug", "EmotionRecognitionApiHandler: A problem with the API has occured.");
                }

            } catch (Exception e) {
                Log.e("DevDebug", "EmotionRecognitionApiHandler: " + e.toString());
                Log.d("DevDebug", "EmotionRecognitionApiHandler: No emotion detected.");
            }
        }

        /**
         * Read the results from the API and send them for a saving into the holder
         *
         * @param result - Face[]: results
         */
        private void saveEmotionalValues(Face[] result) {
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

            //print the result into scroolView if realtimeResultDisplay is enabled
            if (realtimeResultDisplay) {
                dataSendToActivity.pingActivityNewDataAvailable(emotionvaluesdataset);
            }

            Log.d("DevDebug", "EmotionRecognitionApiHandler: result -  " + emotionvaluesdataset.toString());
        }

        /**
         * Create empty results and send it for a saving into the holder
         */
        private void saveEmptyEmotionalValues() {
            emotionvaluesdataset = new ArrayList<>();

            //save emotional state result into a holder
            saveEmotionRecognitionResultToHolder(null);

            //print the result into scroolView
            if (realtimeResultDisplay) {
                dataSendToActivity.pingActivityNewDataAvailable(null);
            }

            Log.d("DevDebug", "EmotionRecognitionApiHandler: No emotion detected. Empty values are set.");
        }

        /**
         * Save the results into a holder (FacialEmotionRecognitionResultsHolder)
         *
         * @param emotionalStates - ArrayList<EmotionValuesDataset>: the emotional values
         */
        private void saveEmotionRecognitionResultToHolder(ArrayList<EmotionValuesDataset> emotionalStates) {
            FacialEmotionRecognitionResultsHolder facialEmotionRecognitionResultsHolder = FacialEmotionRecognitionResultsHolder.getInstance();
            facialEmotionRecognitionResultsHolder.addNewEmotionResult(pictureId, emotionalStates, timestamp);
        }

    }

}
