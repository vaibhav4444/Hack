package com.hackathon.connect.myapplication.utils;

/**
 * Created by Vaib on 03-05-2016.
 */
import android.os.AsyncTask;
import android.util.Log;


import com.hackathon.connect.myapplication.listener.AsyncResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MongoLabUtil {
    public final String LOG_TAG = MongoLabUtil.class.getName();
    public MongoLabUtil(){

    }
    public void sendHTTPData(JSONObject json, AsyncResponse asyncResponse, String url) {
      new PostDataAsyncTask(asyncResponse, json, url).execute();

    }
    public  void getData(AsyncResponse asyncResponse, String url) {

        new GetDataAsyncTask(asyncResponse, url).execute();

    }
    private class PostDataAsyncTask extends AsyncTask<JSONObject, Void,String>
    {
        private AsyncResponse asyncResponse;
        private JSONObject jsonObject;
        private String mUrl;
        public PostDataAsyncTask(AsyncResponse asyncResponse, JSONObject jsonObject, String url){
            this.asyncResponse = asyncResponse;
            this.jsonObject = jsonObject;
            this.mUrl = url;
        }
        @Override
        protected String doInBackground (JSONObject...jsonObjects){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(jsonObject.toString());
            streamWriter.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response + "\n");
                }
                bufferedReader.close();

                // Log.d("test", stringBuilder.toString());
                return stringBuilder.toString();
            } else {
                // Log.e("test", connection.getResponseMessage());
                return null;
            }
        } catch (Exception exception) {
            Log.e(LOG_TAG, exception.toString());
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

        @Override
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            asyncResponse.processFinish(s);

        }
    };
    private class GetDataAsyncTask extends AsyncTask<JSONObject, Void,String>
    {
        private AsyncResponse asyncResponse;
        private StringBuffer response = new StringBuffer();
        private String mUrl;
        public GetDataAsyncTask(AsyncResponse asyncResponse, String url){
            this.asyncResponse = asyncResponse;
            mUrl = url;
        }
        @Override
        protected String doInBackground (JSONObject...jsonObjects){
            HttpURLConnection connection = null;
            try {
                URL obj = new URL(mUrl);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                //con.setRequestProperty("User-Agent", USER_AGENT);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + mUrl);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                System.out.println(response.toString());
            } catch (Exception exception) {
                Log.e(LOG_TAG, exception.toString());
                return null;
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            asyncResponse.processFinish(s);

        }
    };

}
