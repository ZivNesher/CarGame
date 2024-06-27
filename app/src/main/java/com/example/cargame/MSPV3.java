package com.example.cargame;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MSPV3 {

    private static MSPV3 mspv3;
    private SharedPreferences prefs;
    private static final String SCORES_KEY = "scores";
    private static final String NAMES_KEY = "names";

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

    public void saveScore(String name, int score) {
        Set<String> names = prefs.getStringSet(NAMES_KEY, new HashSet<>());
        Set<String> scores = prefs.getStringSet(SCORES_KEY, new HashSet<>());

        names.add(name);
        scores.add(String.valueOf(score));

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(NAMES_KEY, names);
        editor.putStringSet(SCORES_KEY, scores);
        editor.apply();
    }

    public List<ScoreEntry> getTopScores() {
        Set<String> names = prefs.getStringSet(NAMES_KEY, new HashSet<>());
        Set<String> scores = prefs.getStringSet(SCORES_KEY, new HashSet<>());

        List<ScoreEntry> scoreEntries = new ArrayList<>();
        if (!names.isEmpty() && !scores.isEmpty()) {
            List<String> nameList = new ArrayList<>(names);
            List<String> scoreList = new ArrayList<>(scores);

            for (int i = 0; i < nameList.size(); i++) {
                scoreEntries.add(new ScoreEntry(nameList.get(i), Integer.parseInt(scoreList.get(i))));
            }
        }
        Collections.sort(scoreEntries, Collections.reverseOrder());
        return scoreEntries;
    }

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        String name;
        int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(this.score, other.score);
        }
    }
}
