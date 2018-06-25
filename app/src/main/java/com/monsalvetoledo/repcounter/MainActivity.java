package com.monsalvetoledo.repcounter;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WorkoutAdapter mWorkoutAdapter;

    private class FetchWorkouts extends AsyncTask<Void, Void, Workout[]> {
        private Workout[] parseWorkouts(String jsonStr) throws JSONException {

            JSONObject workoutsJson = new JSONObject(jsonStr);
            JSONArray workoutsArray = workoutsJson.getJSONArray("workouts");
            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", " Dec"};

            Workout[] workouts = new Workout[workoutsArray.length()];

            for(int i = 0; i < workoutsArray.length(); i++){
                JSONObject workoutJson = workoutsArray.getJSONObject(i);
                int pushups = (int) workoutJson.get("pushups");
                int squats = (int) workoutJson.get("squats");
                String dateStr = (String) workoutJson.get("start_time");
                Date date;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(dateStr);
                    int month = date.getMonth();
                    int day = date.getDay();

                    Workout workout = new Workout(months[month],day, pushups, squats);
                    workouts[i] = workout;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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

                Log.v("FetchWorkouts",workoutsJsonStr);
            } catch (IOException e) {
                Log.e("FetchWorkouts", "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("FetchWorkouts", "Error closing stream", e);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                new FetchWorkouts().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Workout workout = new Workout("Nov",28,12,12);
        ArrayList<Workout> workouts = new ArrayList<>();
        workouts.add(workout);

        mWorkoutAdapter = new WorkoutAdapter( workouts, this );

        ListView listView = findViewById(R.id.listview_workouts);

        listView.setAdapter(mWorkoutAdapter);


    }
}
