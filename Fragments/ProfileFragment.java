package com.example.dissertation_tester.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dissertation_tester.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    private DatabaseReference mDatabase;
    TextView totalCorrectQuestions,currentStreak,highestStreak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout gamesContainer = view.findViewById(R.id.gamesContainer);
        totalCorrectQuestions = view.findViewById(R.id.questionsCorrect);
        currentStreak = view.findViewById(R.id.currentStreak);
        highestStreak = view.findViewById(R.id.highestStreak);







        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("user").child(uid).child("Games");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gamesContainer.removeAllViews();

                LayoutInflater inflater = LayoutInflater.from(getContext());

                for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                    String gameName = gameSnapshot.getKey();
                    Long answeredCorrectly = gameSnapshot.child("numberOfCorrectAnswers").getValue(Long.class);
                    String badgeLevel = gameSnapshot.child("badge").getValue(String.class);

                    // Inflate your game item layout
                    View gameItem = inflater.inflate(R.layout.game_item, gamesContainer, false);

                    // Find views and set data
                    TextView gameTitle = gameItem.findViewById(R.id.gameTitle);
                    ImageView badge = gameItem.findViewById(R.id.badgeImage);
                    TextView correctAnswersText = gameItem.findViewById(R.id.correctAnswersText);
                    correctAnswersText.setText(String.valueOf(answeredCorrectly));

                    gameTitle.setText(gameName);

                    if ("gold".equalsIgnoreCase(badgeLevel)) {
                        badge.setImageResource(R.drawable.baseline_shield_24);
                    } else if ("silver".equalsIgnoreCase(badgeLevel)) {
                        badge.setImageResource(R.drawable.baseline_shield_silver);
                    } else {
                        badge.setImageResource(R.drawable.baseline_shield_bronze); // default
                    }

                    // Add to container
                    gamesContainer.addView(gameItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("user").child(uid);

// Read the total correct answers from Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("user").child(uid);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Long totalCorrect = dataSnapshot.child("TotalCorrectAnswers").getValue(Long.class);
                    Long longestStreak = dataSnapshot.child("LargestDailyStreak").getValue(Long.class);
                    Long currentStreakint = dataSnapshot.child("CurrentDailyStreak").getValue(Long.class);

                    totalCorrectQuestions.setText(totalCorrect != null ? String.valueOf(totalCorrect) : "0");
                    highestStreak.setText(longestStreak != null ? String.valueOf(longestStreak) + "\uD83D\uDD25" : "0");
                    currentStreak.setText(currentStreakint != null ? String.valueOf(currentStreakint): "0");

                } else {
                    totalCorrectQuestions.setText("0");
                    highestStreak.setText("0");
                    currentStreak.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
                totalCorrectQuestions.setText("0");
                highestStreak.setText("0");
                currentStreak.setText("0");
            }
        });




        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("user").child(uid);







        return view;
    }
}