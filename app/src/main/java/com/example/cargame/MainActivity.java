package com.example.cargame;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GameManager gameManager;
    public static final String SP_KEY_NAME = "NAME";
    public static final String SP_KEY_SCORE = "SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MSPV3.init(this); // Initialize MSPV3
        setContentView(R.layout.menu); // Set the menu layout as the initial view

        gameManager = new GameManager(this, this);
        v3();

        MaterialButton playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMainLayout(); // Switch to the main layout when the play button is clicked
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

    private void v3() {
        try {
            MSPV3.getInstance().saveScore("Ziv", 1000);

            List<MSPV3.ScoreEntry> scores = MSPV3.getInstance().getTopScores();
            for (MSPV3.ScoreEntry score : scores) {
                Log.d("pttt", "Name: " + score.name + ", Score: " + score.score);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error in v3 method", e);
        }
    }

    private void loadTopTenLayout() {
        setContentView(R.layout.top10);
        gameManager.loadTopTenScores();
        MaterialButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMenuLayout();
            }
        });
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
    }

    public void showGameOverDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Do you want to play again?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameManager.startGame();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loadMenuLayout();
                    }
                })
                .show();
    }

    private void loadMenuLayout() {
        setContentView(R.layout.menu);
        gameManager = new GameManager(this, this);

        MaterialButton playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMainLayout(); // Switch to the main layout when the play button is clicked
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
