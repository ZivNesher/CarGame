package com.example.cargame;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView[][] obstacles;
    private ImageView car;
    private int carPosition = 1; // Initially the car is at the third position
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initGameMatrix();

        MaterialButton buttonRight = findViewById(R.id.buttonRight);
        MaterialButton buttonLeft = findViewById(R.id.buttonLeft);
        // Set an OnClickListener on the button
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
        for (int i = 0; i < obstacles.length-1; i++) {
            for (int j = 0; j < obstacles[i].length; j++) {
                if (obstacles[i][j].getVisibility() == View.VISIBLE) {
                    obstacles[i][j].setVisibility(View.INVISIBLE);
                    if (i == 4) {
                        obstacles[i][j].setVisibility(View.INVISIBLE);
                    } else {
                        obstacles[i + 1][j].setVisibility(View.VISIBLE);
                    }
                }
            }
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

    private void moveCarRight() {
        if (carPosition == 2){
            obstacles[5][0].setVisibility(View.INVISIBLE);
            obstacles[5][1].setVisibility(View.INVISIBLE);
        }
        else {
            obstacles[5][carPosition].setVisibility(View.INVISIBLE);
            carPosition+=1;
            obstacles[5][carPosition].setVisibility(View.VISIBLE);
        }
    }
    private void moveCarLeft() {
        if (carPosition == 0){
            obstacles[5][1].setVisibility(View.INVISIBLE);
            obstacles[5][2].setVisibility(View.INVISIBLE);
        }
        //do nothing
        else {
            obstacles[5][carPosition].setVisibility(View.INVISIBLE);
            carPosition-=1;
            obstacles[5][carPosition].setVisibility(View.VISIBLE);
        }
    }
}