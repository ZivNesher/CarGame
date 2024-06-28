package com.example.cargame;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameManager gameManager;
    private MSPV3 msp;
    private List<MSPV3.ScoreEntry> top10Scores;
    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastMoveTime = 0;
    private static final int MOVE_DELAY = 500; // Delay in milliseconds
    private boolean useSensor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu); // Set the menu layout as the initial view

        MSPV3.init(this);
        msp = MSPV3.getInstance();
        top10Scores = msp.getTop10Scores();
        if (top10Scores == null || top10Scores.isEmpty()) {
            top10Scores = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                top10Scores.add(new MSPV3.ScoreEntry("", 0));
            }
            msp.saveTop10Scores(top10Scores);
        }

        gameManager = new GameManager(this, this);

        MaterialButton playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModeSelectionDialog(); // Show the mode selection dialog when the play button is clicked
            }
        });

        MaterialButton topTenButton = findViewById(R.id.topTenButton);
        topTenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTopTenLayout();
            }
        });

        // Initialize sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (useSensor && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (useSensor && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMoveTime > MOVE_DELAY) {
                if (x > 1.5) {  // More sensitive left tilt
                    gameManager.moveCarLeft();
                    lastMoveTime = currentTime;
                } else if (x < -1.5) {  // More sensitive right tilt
                    gameManager.moveCarRight();
                    lastMoveTime = currentTime;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    private void showModeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose mode")
                .setItems(new String[]{"Fast", "Slow", "Sensor"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Fast
                                gameManager.setSpeed(400);
                                useSensor = false;
                                break;
                            case 1: // Slow
                                gameManager.setSpeed(1100);
                                useSensor = false;
                                break;
                            case 2: // Sensor
                                gameManager.setSpeed(1000);
                                useSensor = true;
                                break;
                        }
                        loadMainLayout(); // Load the main layout after selecting the mode
                    }
                })
                .show();
    }

    private void loadTopTenLayout() {
        setContentView(R.layout.top10);
        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMenuLayout();
            }
        });

        updateTop10UI();
    }

    private void loadMainLayout() {
        setContentView(R.layout.activity_main); // Load the main layout
        gameManager.initGameMatrix();

        MaterialButton buttonRight = findViewById(R.id.buttonRight);
        MaterialButton buttonLeft = findViewById(R.id.buttonLeft);

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.moveCarRight();
            }
        });
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameManager.moveCarLeft();
            }
        });

        gameManager.startGame();

        if (useSensor && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void showGameOverDialog(final int score) {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Your score: " + score + "\nDo you want to save your score?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showNameInputDialog(score);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loadMenuLayout();
                    }
                })
                .show();
    }

    private void showNameInputDialog(final int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                Log.d(TAG, "Name entered: " + name);
                saveScore(name, score);
                loadTopTenLayout(); // Switch to the top 10 layout after saving the score
            }
        });

        builder.create().show();
    }

    private void saveScore(String name, int score) {
        try {
            List<MSPV3.ScoreEntry> top10Scores = msp.getTop10Scores();

            top10Scores.add(new MSPV3.ScoreEntry(name, score));
            Collections.sort(top10Scores);
            if (top10Scores.size() > 10) {
                top10Scores = top10Scores.subList(0, 10);
            }
            msp.saveTop10Scores(top10Scores);
            Log.d(TAG, "Score saved: " + name + " - " + score);

            updateTop10UI();
        } catch (Exception e) {
            Log.e(TAG, "Error saving score: " + e.getMessage());
        }
    }

    private void updateTop10UI() {
        try {
            List<MSPV3.ScoreEntry> top10Scores = msp.getTop10Scores();
            for (int i = 0; i < top10Scores.size(); i++) {
                MSPV3.ScoreEntry score = top10Scores.get(i);
                String nameId = "name" + (i + 1);
                String scoreId = "score" + (i + 1);

                int resNameId = getResources().getIdentifier(nameId, "id", getPackageName());
                int resScoreId = getResources().getIdentifier(scoreId, "id", getPackageName());

                TextView nameTextView = findViewById(resNameId);
                TextView scoreTextView = findViewById(resScoreId);

                nameTextView.setText(score.getName());
                scoreTextView.setText(String.valueOf(score.getScore()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI: " + e.getMessage());
        }
    }

    private void loadMenuLayout() {
        setContentView(R.layout.menu);
        gameManager = new GameManager(this, this);

        MaterialButton playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModeSelectionDialog(); // Show the mode selection dialog when the play button is clicked
            }
        });

        MaterialButton topTenButton = findViewById(R.id.topTenButton);
        topTenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTopTenLayout();
            }
        });
    }
}
