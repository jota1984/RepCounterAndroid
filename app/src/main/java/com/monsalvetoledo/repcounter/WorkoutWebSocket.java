package com.monsalvetoledo.repcounter;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;

public class WorkoutWebSocket extends WebSocketClient {

    private WorkoutActivity mActivity;
    private final String TAG = "WorkoutWebSocket";
    private final String SUBSCRIBE_CMD =
            "{\"command\": \"subscribe\",\"identifier\":\"{\\\"channel\\\":\\\"CurrentWorkoutChannel\\\"}\"}";

    public WorkoutWebSocket(WorkoutActivity activity, URI uri, Map <String,String> headers ){
        super(uri,headers);
        mActivity = activity;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.i(TAG, "Opened");
        this.send(SUBSCRIBE_CMD);
    }

    @Override
    public void onMessage(String s) {
        final String serverStr = s;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject(serverStr).getJSONObject("message");
                    if (json.has("squats")){
                        int squats = json.getInt("squats");
                        TextView tv = mActivity.findViewById(R.id.textview_squat_count_detail);
                        mActivity.playDing();
                        tv.setText("" + squats);
                        Log.i(TAG, "squats " + squats);
                    } else if ( json.has("pushups")){
                        int pushups = json.getInt("pushups");
                        TextView tv = mActivity.findViewById(R.id.textview_pushup_count_detail);
                        mActivity.playDing();
                        tv.setText("" + pushups);
                        Log.i(TAG, "pushups " + pushups);
                    }
                } catch (JSONException e) {
                    Log.w(TAG, "Json parse failed");
                }
            }
        });
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Log.i(TAG, "Closed " + s);
    }

    @Override
    public void onError(Exception e) {
        Log.i(TAG, "Error " + e.getMessage());
    }
}
