package com.cet325.gamers_emotional_state_detection.handlers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.cet325.gamers_emotional_state_detection.datasenders.OnDataSendToGameplayActivity;
import com.cet325.gamers_emotional_state_detection.datasets.EmotionValuesDataset;
import com.cet325.gamers_emotional_state_detection.holders.EmotionFaceRecognitionResultsHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;


public class EmotionRecognitionApiHandler
{
    private Bitmap face_picture;
    private byte[] byte64Image;
    private int pictureId;
    private ArrayList<EmotionValuesDataset> emotionvaluesdataset;
    private OnDataSendToGameplayActivity dataSendToActivity;

    public void runEmotionalFaceRecognition(Bitmap picture, int pictureId, OnDataSendToGameplayActivity dataSendToActivity)
    {
        this.dataSendToActivity = dataSendToActivity;

        this.face_picture = picture;
        this.pictureId = pictureId;

        if(face_picture!= null) {
            encodeToBase64();
            GetEmotionCall emotionCall = new GetEmotionCall();
            try {
                emotionCall.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // convert image to base 64 so that we can send the image to Emotion API
    private void encodeToBase64() {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            face_picture.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte64Image = baos.toByteArray();
    }

    private void saveEmotionRecognitionResultToHolder(ArrayList<EmotionValuesDataset> emotionalStates) {
        EmotionFaceRecognitionResultsHolder emotionFaceRecognitionResultsHolder = EmotionFaceRecognitionResultsHolder.getInstance();
        emotionFaceRecognitionResultsHolder.setEmotionResultForGivenImg(pictureId, emotionalStates);
    }

    private class GetEmotionCall extends AsyncTask<Void, Void, String> {

        // this function is called before the api call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DevDebug","EmotionRecognitionApiHandler: Getting results...");
        }

        // this function is called when the api call is made
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpclient = HttpClients.createDefault();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");

                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/octet-stream");
                // enter you subscription key here
                request.setHeader("Ocp-Apim-Subscription-Key", "b9aca58937cf424ca15ae04a3e4d4fd7");

                // Request body.The parameter of setEntity converts the image to base64
                request.setEntity(new ByteArrayEntity(byte64Image));

                // getting a response and assigning it to the string res
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);

                return res;

            }
            catch (Exception e){
                return "null";
            }
        }

        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(String result) {
            emotionvaluesdataset = new ArrayList<>();
            EmotionValuesDataset emotionValuesDataset;

            JSONArray jsonArray = null;
            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(result);
                String emotions = "";
                // get the scores object from the results
                for(int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject scores = jsonObject.getJSONObject("scores");
                    double max = 0;
                    String emotion = "";
                    for (int j = 0; j < scores.names().length(); j++) {

                        //save emotions into an arraylist
                        emotionValuesDataset = new EmotionValuesDataset(scores.names().getString(j)	,scores.getDouble(scores.names().getString(j)));
                        emotionvaluesdataset.add(emotionValuesDataset);

                        //calculating the strongest emotion for this picture
                        if (scores.getDouble(scores.names().getString(j)) > max) {
                            max = scores.getDouble(scores.names().getString(j));
                            emotion = scores.names().getString(j);
                        }
                    }
                    emotions += emotion + "\n";
                }

                //save emotional state result into a holder
                saveEmotionRecognitionResultToHolder(emotionvaluesdataset);

                //print the result into scroolView
                dataSendToActivity.sendData();

                Log.d("DevDebug","EmotionRecognitionApiHandler: result -  " + emotions);
            } catch (JSONException e) {
                Log.d("DevDebug","EmotionRecognitionApiHandler: No emotion detected. Try again later");
            }
        }
    }

}
