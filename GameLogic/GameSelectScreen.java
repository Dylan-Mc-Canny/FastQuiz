package com.example.dissertation_tester.GameLogic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dissertation_tester.GamesAdapter;
import com.example.dissertation_tester.DailyStreakFunctions;
import com.example.dissertation_tester.MainActivity;
import com.example.dissertation_tester.QuestionAnswerActivity;
import com.example.dissertation_tester.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameSelectScreen extends AppCompatActivity {

    private DatabaseReference mDatabase;
    ImageView close_game_image;
    ListView l;
    public static String gameKey ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select_screen);


        close_game_image = (ImageView) findViewById(R.id.close_game);
        close_game_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(GameSelectScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("Games");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> gameList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();

                    // IMPROVED: Filter out null, empty, and whitespace-only keys
                    if (key != null && !key.trim().isEmpty()) {
                        gameList.add(key);
                    }
                }

                // IMPROVED: Only setup RecyclerView if we have valid games
                if (!gameList.isEmpty()) {
                    setupGamesRecyclerView(gameList);
                } else {
                    // IMPROVED: Handle empty state gracefully
                    handleNoGamesAvailable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // IMPROVED: Add proper error handling
                Log.e("GameSelectScreen", "Firebase error: " + error.getMessage());
                handleFirebaseError(error);
            }
        });
    }

    private void setupGamesRecyclerView(List<String> leaderboardList) {
        RecyclerView recyclerView = findViewById(R.id.gamesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GamesAdapter adapter = new GamesAdapter(leaderboardList, gameKey -> {
            Intent intent = new Intent(GameSelectScreen.this, QuestionAnswerActivity.class);
            intent.putExtra("gameKey", gameKey);
            startActivity(intent);
            finish();
        });

        recyclerView.setAdapter(adapter);
    }

    private void handleNoGamesAvailable() {
        RecyclerView recyclerView = findViewById(R.id.gamesRecyclerView);
        // Clear any existing adapter
        recyclerView.setAdapter(null);

        // You could show an empty state view here
        // For example: showEmptyStateMessage("No games available");
    }

    private void handleFirebaseError(DatabaseError error) {
        // Show user-friendly error message
        // You could use a Toast, Snackbar, or update UI to show error state
        Toast.makeText(this, "Unable to load games. Please try again.", Toast.LENGTH_SHORT).show();

        // Optionally, you could retry the connection or navigate back
        // For critical errors, you might want to finish() the activity
    }

    private void retryLoadingGames() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference()
                    .child("user").child(uid).child("Games");
            // Re-attach listener...
        }
    }
}