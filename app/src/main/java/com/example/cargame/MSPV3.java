package com.example.cargame;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MSPV3 {

    private static MSPV3 mspv3;
    private SharedPreferences prefs;
    private static final String TOP10_SCORES_KEY = "top10_scores";
    private static final String TAG = "MSPV3";

    private MSPV3(Context context) {
        prefs = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (mspv3 == null) {
            mspv3 = new MSPV3(context);
        }
    }

    public static MSPV3 getInstance() {
        return mspv3;
    }

    public void saveTop10Scores(List<ScoreEntry> top10Scores) {
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();
        for (ScoreEntry entry : top10Scores) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", entry.getName());
                jsonObject.put("score", entry.getScore());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Error saving score: " + e.getMessage());
            }
        }
        editor.putString(TOP10_SCORES_KEY, jsonArray.toString());
        editor.apply();
        Log.d(TAG, "Scores saved: " + jsonArray.toString());
    }

    public List<ScoreEntry> getTop10Scores() {
        List<ScoreEntry> scoreEntries = new ArrayList<>();
        String jsonString = prefs.getString(TOP10_SCORES_KEY, null);
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    int score = jsonObject.getInt("score");
                    scoreEntries.add(new ScoreEntry(name, score));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error retrieving scores: " + e.getMessage());
            }
        }
        return scoreEntries;
    }

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        String name;
        int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }
}
