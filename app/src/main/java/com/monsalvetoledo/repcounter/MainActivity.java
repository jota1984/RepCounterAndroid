package com.monsalvetoledo.repcounter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "RepCounter";
    private WorkoutAdapter mWorkoutAdapter;


    private class FetchWorkouts extends AsyncTask<Void, Void, Workout[]> {
        private final String TAG = "FetchWorkouts";
        private Workout[] parseWorkouts(String jsonStr) throws JSONException {

            JSONObject workoutsJson = new JSONObject(jsonStr);
            JSONArray workoutsArray = workoutsJson.getJSONArray("workouts");

            Workout[] workouts = new Workout[workoutsArray.length()];

            for(int i = 0; i < workoutsArray.length(); i++){
                JSONObject workoutJson = workoutsArray.getJSONObject(i);
                int id = (int) workoutJson.get("id");
                int pushups = (int) workoutJson.get("pushups");
                int squats = (int) workoutJson.get("squats");
                String dateStr = (String) workoutJson.get("start_time");
                Workout workout = new Workout(id, dateStr, pushups, squats);
                workouts[i] = workout;
            }

            return workouts;
        }
        @Override
        protected Workout[] doInBackground(Void... voids) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String workoutsJsonStr;

            try {

                // Make  HTTP request
                URL url = new URL("http://18.222.128.16:3000/workouts.json");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
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

                workoutsJsonStr = buffer.toString();

                Log.v(TAG,workoutsJsonStr);
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
                return parseWorkouts(workoutsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Workout[] strs) {
            super.onPostExecute(strs);
            mWorkoutAdapter.clear();
            mWorkoutAdapter.addAll(strs);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // update with real data
        new FetchWorkouts().execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                new FetchWorkouts().execute();
                return true;
            case R.id.action_new_workout:
                Intent intent = new Intent(this, WorkoutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create dummy data
        Workout workout = new Workout(1,"Nov",28,12,12);
        ArrayList<Workout> workouts = new ArrayList<>();
        workouts.add(workout);

        // set adapter with dummy data
        mWorkoutAdapter = new WorkoutAdapter( workouts, this );
        ListView listView = findViewById(R.id.listview_workouts);
        listView.setAdapter(mWorkoutAdapter);

    }
}
