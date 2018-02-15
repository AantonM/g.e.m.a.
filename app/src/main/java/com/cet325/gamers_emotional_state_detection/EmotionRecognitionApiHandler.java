package com.cet325.gamers_emotional_state_detection;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;

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

    public String runEmotionalFaceRecognition(Bitmap picture)
    {
        face_picture = picture;
        String emotionalState = null;

        if(face_picture!= null) {
            encodeToBase64();
            GetEmotionCall emotionCall = new GetEmotionCall();
            try {
                emotionalState = emotionCall.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return emotionalState;
    }


    // convert image to base 64 so that we can send the image to Emotion API
    private void encodeToBase64() {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            face_picture.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte64Image = baos.toByteArray();
    }

    private class GetEmotionCall extends AsyncTask<Void, Void, String> {
        GetEmotionCall() {
        }

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
                        if (scores.getDouble(scores.names().getString(j)) > max) {
                            max = scores.getDouble(scores.names().getString(j));
                            emotion = scores.names().getString(j);
                        }
                    }
                    emotions += emotion + "\n";
                }
                Log.d("DevDebug","EmotionRecognitionApiHandler: EmotionRecognitionApiHandler" + emotions);
            } catch (JSONException e) {
                Log.d("DevDebug","EmotionRecognitionApiHandler: No emotion detected. Try again later");
            }
        }
    }

}
