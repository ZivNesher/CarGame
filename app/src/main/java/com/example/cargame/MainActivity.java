package com.example.cargame;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView[][] obstacles;
    private int carPosition = 1; // Initially the car is at the third position
    private Handler handler = new Handler();
    private Runnable runnable;
    private Random random = new Random();
    private boolean previousRowEmpty = true;
    private int lives = 3;
    private MaterialTextView scoreValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initGameMatrix();

        MaterialButton buttonRight = findViewById(R.id.buttonRight);
        MaterialButton buttonLeft = findViewById(R.id.buttonLeft);

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCarRight();
            }
        });
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCarLeft();
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                moveObstacles();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void moveObstacles() {
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
        if (obstacles[4][carPosition].getVisibility() == View.VISIBLE) {
            lives -= 1;
            if(lives == 2) {
                ImageView heart3 = findViewById(R.id.heart3);
                heart3.setImageResource(R.drawable.nolive);
            } else if(lives == 1) {
                ImageView heart2 = findViewById(R.id.heart2);
                heart2.setImageResource(R.drawable.nolive);
            } else if(lives == 0) {
                ImageView heart1 = findViewById(R.id.heart1);
                heart1.setImageResource(R.drawable.nolive);
            }
            if (lives == 0) {
                handler.removeCallbacks(runnable);
                Log.d("GameStatus", "Game Over");
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
        scoreValue = findViewById(R.id.ScoreValue);
        int currentScore = Integer.parseInt(scoreValue.getText().toString());
        if (lives > 0) {
            currentScore += 10;
        }
        updateScore(String.valueOf(currentScore));
        if(lives == 0) {
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initGameMatrix() {
        obstacles = new ImageView[][]{
                {findViewById(R.id.l1o1), findViewById(R.id.l1o2), findViewById(R.id.l1o3)},
                {findViewById(R.id.l2o1), findViewById(R.id.l2o2), findViewById(R.id.l2o3)},
                {findViewById(R.id.l3o1), findViewById(R.id.l3o2), findViewById(R.id.l3o3)},
                {findViewById(R.id.l4o1), findViewById(R.id.l4o2), findViewById(R.id.l4o3)},
                {findViewById(R.id.l5o1), findViewById(R.id.l5o2), findViewById(R.id.l5o3)},
                {findViewById(R.id.l6o1), findViewById(R.id.l6o2), findViewById(R.id.l6o3)}
        };
    }
    private void updateScore(String newScore) {
        scoreValue.setText(newScore);
    }

    private void moveCarRight() {
        if (carPosition < 2) {
            obstacles[5][carPosition].setVisibility(View.INVISIBLE);
            carPosition += 1;
            obstacles[5][carPosition].setVisibility(View.VISIBLE);
        }
    }

    private void moveCarLeft() {
        if (carPosition > 0) {
            obstacles[5][carPosition].setVisibility(View.INVISIBLE);
            carPosition -= 1;
            obstacles[5][carPosition].setVisibility(View.VISIBLE);
        }
    }
}


