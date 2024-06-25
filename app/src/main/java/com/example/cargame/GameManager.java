package com.example.cargame;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import java.util.Random;

public class GameManager {
    private ImageView[][] obstacles;
    private int carPosition = 1; // Initially the car is at the third position
    private Handler handler = new Handler();
    private Runnable runnable;
    private Random random = new Random();
    private boolean previousRowEmpty = true;
    private int lives = 3;
    private MaterialTextView scoreValue;
    private Context context;
    private MainActivity activity;

    public GameManager(Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void initGameMatrix() {
        obstacles = new ImageView[][]{
                {activity.findViewById(R.id.l1o1), activity.findViewById(R.id.l1o2), activity.findViewById(R.id.l1o3)},
                {activity.findViewById(R.id.l2o1), activity.findViewById(R.id.l2o2), activity.findViewById(R.id.l2o3)},
                {activity.findViewById(R.id.l3o1), activity.findViewById(R.id.l3o2), activity.findViewById(R.id.l3o3)},
                {activity.findViewById(R.id.l4o1), activity.findViewById(R.id.l4o2), activity.findViewById(R.id.l4o3)},
                {activity.findViewById(R.id.l5o1), activity.findViewById(R.id.l5o2), activity.findViewById(R.id.l5o3)},
                {activity.findViewById(R.id.l6o1), activity.findViewById(R.id.l6o2), activity.findViewById(R.id.l6o3)}
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
                handler.postDelayed(this, 500);
            }
        };
        handler.post(runnable);
    }

    public void stopGame() {
        handler.removeCallbacks(runnable);
    }

    private void resetGame() {
        lives = 3;
        carPosition = 1;
        previousRowEmpty = true;
        updateScore("0");
        resetHearts();
        clearObstacles();
    }

    private void clearObstacles() {
        for (int i = 0; i < obstacles.length; i++) {
            for (int j = 0; j < obstacles[i].length; j++) {
                obstacles[i][j].setVisibility(View.INVISIBLE);
            }
        }
        obstacles[5][carPosition].setVisibility(View.VISIBLE);
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
        if (carPosition < 2) {
            obstacles[5][carPosition].setVisibility(View.INVISIBLE);
            carPosition += 1;
            obstacles[5][carPosition].setVisibility(View.VISIBLE);
        }
    }

    public void moveCarLeft() {
        if (carPosition > 0) {
            obstacles[5][carPosition].setVisibility(View.INVISIBLE);
            carPosition -= 1;
            obstacles[5][carPosition].setVisibility(View.VISIBLE);
        }
    }

    private void moveObstacles() {
        if (lives > 0) {
            // Move existing obstacles down
            for (int i = obstacles.length - 2; i >= 0; i--) {
                for (int j = 0; j < obstacles[i].length; j++) {
                    if (obstacles[i][j].getVisibility() == View.VISIBLE) {
                        obstacles[i + 1][j].setVisibility(View.VISIBLE);
                        obstacles[i][j].setVisibility(View.INVISIBLE);
                    }
                }
            }


            // Check if the car is hit by an obstacle
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (obstacles[4][carPosition].getVisibility() == View.VISIBLE) {
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
                }
            }

            // Clear the last row
            for (int j = 0; j < obstacles[obstacles.length - 1].length; j++) {
                if (j != carPosition) { // Do not clear the car position
                    obstacles[obstacles.length - 1][j].setVisibility(View.INVISIBLE);
                }
            }

            // Add a random obstacle to the first row if the previous row was empty
            if (previousRowEmpty) {
                int randomColumn = random.nextInt(obstacles[0].length);
                for (int j = 0; j < obstacles[0].length; j++) {
                    obstacles[0][j].setVisibility(j == randomColumn ? View.VISIBLE : View.INVISIBLE);
                }
                previousRowEmpty = false;
            } else {
                // Ensure the first row is empty
                for (int j = 0; j < obstacles[0].length; j++) {
                    obstacles[0][j].setVisibility(View.INVISIBLE);
                }
                previousRowEmpty = true;
            }

            // Debugging: Log the visibility of obstacles
            for (int i = 0; i < obstacles.length; i++) {
                for (int j = 0; j < obstacles[i].length; j++) {
                    Log.d("ObstacleStatus", "obstacles[" + i + "][" + j + "] visibility: " + (obstacles[i][j].getVisibility() == View.VISIBLE ? "VISIBLE" : "INVISIBLE"));
                }
            }

            int currentScore = Integer.parseInt(scoreValue.getText().toString());
            if (lives > 0) {
                currentScore += 10;
            }
            updateScore(String.valueOf(currentScore));
            if (lives == 0) {
                Toast.makeText(context, "Game Over", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateScore(String newScore) {
        scoreValue.setText(newScore);
    }
}
