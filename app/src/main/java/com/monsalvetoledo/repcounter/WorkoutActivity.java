package com.monsalvetoledo.repcounter;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WorkoutActivity extends AppCompatActivity {

    private final String TAG = "WorkoutActivity";
    private String mWebSocketUriStr = "ws://18.222.128.16:3000/cable";
    private String mCreateWorkoutUriStr = "http://18.222.128.16:3000/workouts.json";
    private String mOrigin = "http://18.222.128.16:3000";
    private WorkoutWebSocket mWebSocket;
    private SoundPool mSoundPool;
    public int mDingSound;

    private class CreateWorkout extends AsyncTask<Void,Void,Workout>{
        private final String TAG = "CreateWorkout";

        private Workout parseWorkout(String jsonStr) throws JSONException{
            JSONObject workoutJson = new JSONObject(jsonStr).getJSONObject("workout");

            int pushups = (int) workoutJson.get("pushups");
            int squats = (int) workoutJson.get("squats");
            String dateStr = (String) workoutJson.get("start_time");

            Workout workout = new Workout(dateStr, pushups, squats);

            return workout;
        }

        @Override
        protected Workout doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String workoutJsonStr;

            Log.v(TAG, "Creating workout");

            try {

                // Make  HTTP request
                URL url = new URL(mCreateWorkoutUriStr);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                // Read into buffer
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                workoutJsonStr = buffer.toString();

                Log.v(TAG,workoutJsonStr);
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return parseWorkout(workoutJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Workout workout) {

            if (workout == null ){
                Log.e(TAG,"Error creating workout");
            }
            TextView tv_pushups = findViewById(R.id.textview_pushup_count_detail);
            TextView tv_squats = findViewById(R.id.textview_squat_count_detail);

            // update the views with the values from the newly created workout
            tv_pushups.setText("" + workout.getPushups());
            tv_squats.setText("" + workout.getSquats());
        }
    }

    public void playDing(){
        mSoundPool.play(mDingSound,1.0f,1.0f,1,0,0);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");
        mWebSocket.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");

        // prepare URI for WebWocket
        URI uri;
        try {
            uri = new URI(mWebSocketUriStr);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        // prepare headers for websocket
        // ActionCable requires origin header
        Map<String,String> httpHeaders = new HashMap<String, String>();
        httpHeaders.put("Origin",mOrigin);

        // create and connect WebSocket
        mWebSocket = new WorkoutWebSocket(this,uri,httpHeaders);
        mWebSocket.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // init end workout button
        Button button = findViewById(R.id.button_end_workout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // init sound pool for rep count alerts
        mSoundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        mDingSound = mSoundPool.load(this, R.raw.ding, 1);

        // Create AsyncTask that POSTs new workout
        new CreateWorkout().execute();
        Log.v(TAG,"onCreate");
    }
}
