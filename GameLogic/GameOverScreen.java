package com.example.dissertation_tester.GameLogic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dissertation_tester.BadgeUpgrade;
import com.example.dissertation_tester.DailyStreakFunctions;
import com.example.dissertation_tester.MainActivity;
import com.example.dissertation_tester.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GameOverScreen extends AppCompatActivity {

    ImageView close_game_image;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String updatedBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_screen);
        String gameKey = getIntent().getStringExtra("gameKey");

        close_game_image = (ImageView) findViewById(R.id.close_game);
        checkAndUpdateScore();

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

        // Calculate percentage
        int percentage = (MultipleChoiceGame.numberOfCorrectAnswers * 100) /current_game.MAX_QUESTION ;

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
        else if (percentage > 0)
        {
            Star1.setColorFilter(Color.rgb(255,215,0));
            Star2.setColorFilter(Color.rgb(128,128,128));
            Star3.setColorFilter(Color.rgb(128,128,128));
        }
        else
        {
            Star1.setColorFilter(Color.rgb(128,128,128));
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
                intent.putExtra("gameKey", gameKey);
                startActivity(intent);
                finish();
            }
        });



    }

    private void checkAndUpdateScore(){
        String gameKey = getIntent().getStringExtra("gameKey");

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user").child(userId).child("Games").child(gameKey);
        DatabaseReference newUserRef = FirebaseDatabase.getInstance().getReference("user").child(userId);

        newUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long currentAnswers = snapshot.child("TotalCorrectAnswers").getValue(Long.class);
                    int current = currentAnswers != null ? currentAnswers.intValue() : 0;

                    int updatedAnswers = current + MultipleChoiceGame.numberOfCorrectAnswers;

                    Map<String, Object> update = new HashMap<>();
                    update.put("TotalCorrectAnswers", updatedAnswers);

                    newUserRef.updateChildren(update)
                            .addOnSuccessListener(aVoid -> Log.d("Firebase", "numberOfAnswers updated"))
                            .addOnFailureListener(e -> Log.e("Firebase", "Update failed: " + e.getMessage()));
                } else {
                    Log.e("Firebase", "User path does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });







        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int correctAnswers = 0;
                String currentBadge = "";

                if (snapshot.exists()) {
                    GameData existingData = snapshot.getValue(GameData.class);
                    if (existingData != null) {
                        correctAnswers = existingData.getNumberOfAnswers();
                        currentBadge = existingData.getBadge();
                    }
                }

                int updatedScore = correctAnswers + MultipleChoiceGame.numberOfCorrectAnswers;
                String updatedBadge = currentBadge;

                Handler handler = new Handler();
                if (updatedScore >= 30 &&  currentBadge.equals("silver")) {
                    updatedBadge = "gold";
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(GameOverScreen.this, BadgeUpgrade.class);
                            i.putExtra("badgeName","gold");
                            startActivity(i);
                        }
                    },1000);
                }
                else if (updatedScore >= 20 &&  currentBadge.equals("bronze") ) {
                    updatedBadge = "silver";
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(GameOverScreen.this, BadgeUpgrade.class);
                            i.putExtra("badgeName","silver");
                            startActivity(i);
                        }
                    },1000);
                }
                else if (updatedScore >= 10 && currentBadge.equals("Default")) {
                    updatedBadge = "bronze";

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(GameOverScreen.this, BadgeUpgrade.class);
                            i.putExtra("badgeName","bronze");
                            startActivity(i);
                        }
                    },1000);

                }

                GameData newGameData = new GameData(updatedScore,updatedBadge);


                userRef.setValue(newGameData);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to read data", error.toException());
            }
        });
    }
}