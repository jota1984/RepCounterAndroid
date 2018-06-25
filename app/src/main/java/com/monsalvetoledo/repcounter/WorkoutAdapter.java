package com.monsalvetoledo.repcounter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkoutAdapter extends ArrayAdapter<Workout> {

    private ArrayList<Workout> mWorkouts;
    private Context mCtx;

    public WorkoutAdapter(ArrayList<Workout> workouts, Context ctx){
        super(ctx, R.layout.list_item_workout, workouts);
        mWorkouts = workouts;
        mCtx = ctx;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_workout,parent,false);
        }

        TextView tv_month = (TextView) convertView.findViewById(R.id.textview_month);
        TextView tv_day = (TextView) convertView.findViewById(R.id.textview_day);
        TextView tv_pushups = (TextView) convertView.findViewById(R.id.textview_pushup_count);
        TextView tv_squats = (TextView) convertView.findViewById(R.id.textview_squat_count);

        tv_month.setText(mWorkouts.get(position).month);
        tv_day.setText("" + mWorkouts.get(position).day);
        tv_pushups.setText("" + mWorkouts.get(position).pushups);
        tv_squats.setText("" + mWorkouts.get(position).squats);

        return convertView;
    }
}
