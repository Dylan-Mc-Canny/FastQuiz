package com.example.dissertation_tester;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameOverScreen extends AppCompatActivity {

    ImageView close_game_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);

        close_game_image = (ImageView) findViewById(R.id.close_game);


        close_game_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(GameOverScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener

        MultipleChoiceGame current_game = new MultipleChoiceGame();

        // Get data from intent
        int correctAnswers = MultipleChoiceGame.numberOfCorrectAnswers;
        int totalQuestions = 3;

        // Calculate percentage
        int percentage = (correctAnswers * 100) / totalQuestions;

        // Determine star rating
        ImageView Star1 = findViewById(R.id.Star1);
        ImageView Star2 = findViewById(R.id.Star2);
        ImageView Star3 = findViewById(R.id.Star3);

        //set the colours of the stars
        if (percentage >= 80)
        {
            Star1.setColorFilter(Color.rgb(255,215,0));
            Star2.setColorFilter(Color.rgb(255,215,0));
            Star3.setColorFilter(Color.rgb(255,215,0));
        }
        else if (percentage >= 50)
        {
            Star1.setColorFilter(Color.rgb(255,215,0));
            Star2.setColorFilter(Color.rgb(255,215,0));
            Star3.setColorFilter(Color.rgb(128,128,128));
        }
        else
        {
            Star1.setColorFilter(Color.rgb(255,215,0));
            Star2.setColorFilter(Color.rgb(128,128,128));
            Star3.setColorFilter(Color.rgb(128,128,128));
        }


        // Display score
        String score_text = "You scored " + percentage + "%!";
        TextView tvScore = findViewById(R.id.Score);
        tvScore.setText(score_text);

        // Restart button
        Button btnRestart = findViewById(R.id.btnRestart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(GameOverScreen.this, MultipleChoiceGame.class);
                startActivity(intent);
                finish();
            }
        });



    }
}