package com.example.cargame;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {
    private ImageView[][] obstacles;
    private int carPosition = 2; // Initially the car is at the third position
    private Handler handler = new Handler();
    private Runnable runnable;
    private Random random = new Random();
    private boolean previousRowEmpty = true;
    private int lives = 3;
    private MaterialTextView scoreValue;
    private Context context;
    private MainActivity activity;
    private int speed = 1000;
    private int carLane = 8;

    public GameManager(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void initGameMatrix() {
        obstacles = new ImageView[][]{
                {activity.findViewById(R.id.l1o1), activity.findViewById(R.id.l1o2), activity.findViewById(R.id.l1o3), activity.findViewById(R.id.l1o4), activity.findViewById(R.id.l1o5)},
                {activity.findViewById(R.id.l2o1), activity.findViewById(R.id.l2o2), activity.findViewById(R.id.l2o3), activity.findViewById(R.id.l2o4), activity.findViewById(R.id.l2o5)},
                {activity.findViewById(R.id.l3o1), activity.findViewById(R.id.l3o2), activity.findViewById(R.id.l3o3), activity.findViewById(R.id.l3o4), activity.findViewById(R.id.l3o5)},
                {activity.findViewById(R.id.l4o1), activity.findViewById(R.id.l4o2), activity.findViewById(R.id.l4o3), activity.findViewById(R.id.l4o4), activity.findViewById(R.id.l4o5)},
                {activity.findViewById(R.id.l5o1), activity.findViewById(R.id.l5o2), activity.findViewById(R.id.l5o3), activity.findViewById(R.id.l5o4), activity.findViewById(R.id.l5o5)},
                {activity.findViewById(R.id.l6o1), activity.findViewById(R.id.l6o2), activity.findViewById(R.id.l6o3), activity.findViewById(R.id.l6o4), activity.findViewById(R.id.l6o5)},
                {activity.findViewById(R.id.l7o1), activity.findViewById(R.id.l7o2), activity.findViewById(R.id.l7o3), activity.findViewById(R.id.l7o4), activity.findViewById(R.id.l7o5)},
                {activity.findViewById(R.id.l8o1), activity.findViewById(R.id.l8o2), activity.findViewById(R.id.l8o3), activity.findViewById(R.id.l8o4), activity.findViewById(R.id.l8o5)},
                {activity.findViewById(R.id.l9o1), activity.findViewById(R.id.l9o2), activity.findViewById(R.id.l9o3), activity.findViewById(R.id.l9o4), activity.findViewById(R.id.l9o5)}
        };
        scoreValue = activity.findViewById(R.id.ScoreValue);
    }

    public void startGame() {
        stopGame();
        resetGame();
        runnable = new Runnable() {
            @Override
            public void run() {
                moveObstacles();
                handler.postDelayed(this, speed);
            }
        };
        handler.post(runnable);
    }

    public void stopGame() {
        handler.removeCallbacks(runnable);
    }

    private void resetGame() {
        lives = 3;
        carPosition = 2;
        previousRowEmpty = true;
        updateScore("0");
        resetHearts();
        clearObstacles();
    }

    private void moveObstacles() {
        int currentScore = Integer.parseInt(scoreValue.getText().toString());
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (lives > 0) {
            // Move existing obstacles down
            for (int i = obstacles.length - 2; i >= 0; i--) {
                for (int j = 0; j < obstacles[i].length; j++) {
                    if (obstacles[i][j].getVisibility() == View.VISIBLE) {
                        obstacles[i + 1][j].setVisibility(View.VISIBLE);
                        obstacles[i][j].setVisibility(View.INVISIBLE);
                    }
                    obstacles[carLane][j].setImageResource(R.drawable.car);
                }
            }

            // Check if the car is hit by an obstacle
            if (obstacles[carLane-1][carPosition].getVisibility() == View.VISIBLE) {
                lives -= 1;
                v.vibrate(400);
                if (lives == 2) {
                    ImageView heart3 = activity.findViewById(R.id.heart3);
                    heart3.setImageResource(R.drawable.nolive);
                    Toast.makeText(context, "Crashed", Toast.LENGTH_SHORT).show();
                } else if (lives == 1) {
                    ImageView heart2 = activity.findViewById(R.id.heart2);
                    heart2.setImageResource(R.drawable.nolive);
                    Toast.makeText(context, "OOPS! watch out", Toast.LENGTH_SHORT).show();
                } else if (lives == 0) {
                    ImageView heart1 = activity.findViewById(R.id.heart1);
                    heart1.setImageResource(R.drawable.nolive);
                }
                if (lives == 0) {
                    stopGame();
                    Log.d("GameStatus", "Game Over");
                    activity.showGameOverDialog();
                    saveScore("Player", currentScore); // Save the score with a placeholder name "Player"
                }
            }

            // Clear the last row
            for (int j = 0; j < obstacles[obstacles.length - 1].length; j++) {
                if (j != carPosition) { // Do not clear the car position
                    obstacles[obstacles.length - 1][j].setVisibility(View.INVISIBLE);
                }
            }
            addRandomObstacle(currentScore);

            if (lives > 0) {
                currentScore += 10;
                // Increase speed every 50 points max speed is 100
                if (currentScore % 50 == 0 && speed > 300) {
                    speed -= 50;
                }
            }

            updateScore(String.valueOf(currentScore));
            if (lives == 0) {
                Toast.makeText(context, "Game Over", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addRandomObstacle(int currentScore) {
        // Add a random obstacle to the first row if the previous row was empty
        if (previousRowEmpty) {
            int randomColumn = random.nextInt(obstacles[0].length);
            for (int j = 0; j < obstacles[0].length; j++) {
                obstacles[0][j].setVisibility(j == randomColumn ? View.VISIBLE : View.INVISIBLE);
            }
        } else {
            // Ensure the first row is empty
            for (int j = 0; j < obstacles[0].length; j++) {
                obstacles[0][j].setVisibility(View.INVISIBLE);
            }
        }
        previousRowEmpty = !previousRowEmpty;
    }

    private void updateScore(String newScore) {
        scoreValue.setText(newScore);
    }

    private void clearObstacles() {
        for (int i = 0; i < obstacles.length; i++) {
            for (int j = 0; j < obstacles[i].length; j++) {
                obstacles[i][j].setVisibility(View.INVISIBLE);
            }
        }
        obstacles[carLane][carPosition].setVisibility(View.VISIBLE);
    }

    private void resetHearts() {
        ImageView heart1 = activity.findViewById(R.id.heart1);
        ImageView heart2 = activity.findViewById(R.id.heart2);
        ImageView heart3 = activity.findViewById(R.id.heart3);

        heart1.setImageResource(R.drawable.live);
        heart2.setImageResource(R.drawable.live);
        heart3.setImageResource(R.drawable.live);
    }

    public void moveCarRight() {
        if (carPosition < 4) {
            obstacles[carLane][carPosition].setVisibility(View.INVISIBLE);
            carPosition += 1;
            obstacles[carLane][carPosition].setVisibility(View.VISIBLE);
        }
    }

    public void moveCarLeft() {
        if (carPosition > 0) {
            obstacles[carLane][carPosition].setVisibility(View.INVISIBLE);
            carPosition -= 1;
            obstacles[carLane][carPosition].setVisibility(View.VISIBLE);
        }
    }

    private void saveScore(String name, int score) {
        // Save the score using MSPV3
        MSPV3.getInstance().saveScore(name, score);
    }

    public void loadTopTenScores() {
        List<MSPV3.ScoreEntry> topScores = MSPV3.getInstance().getTopScores();

        LinearLayout top10Container = activity.findViewById(R.id.top10Container);

        for (int i = 0; i < 10; i++) {
            LinearLayout row = (LinearLayout) top10Container.getChildAt(i);
            MaterialTextView nameView = (MaterialTextView) row.getChildAt(0);
            MaterialTextView scoreView = (MaterialTextView) row.getChildAt(1);

            if (i < topScores.size()) {
                nameView.setText(topScores.get(i).name);
                scoreView.setText(String.valueOf(topScores.get(i).score));
            } else {
                nameView.setText("---");
                scoreView.setText("---");
            }
        }
    }
}
