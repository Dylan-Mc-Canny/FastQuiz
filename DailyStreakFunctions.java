package com.example.dissertation_tester;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyStreakFunctions {

    private static final String PREFS_NAME = "StreakPrefs";
    private static final String LAST_DATE_KEY = "lastCheckInDate";
    private static final String STREAK_KEY = "currentStreak";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private Context context;

    public DailyStreakFunctions(Context context) {
        this.context = context;
    }


    public void fetchStreakFromFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("CurrentDailyStreak");


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long streakValue = snapshot.getValue(Long.class);
                    if (streakValue != null) {
                        int currentStreak = streakValue.intValue();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putInt("currentStreak", currentStreak).apply();

                        updateStreak(currentStreak);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Log or handle error
            }
        });
    }

    private void updateStreak(int streak) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastDateStr = prefs.getString(LAST_DATE_KEY, null);
        //int streak = prefs.getInt(STREAK_KEY, 0);

        Calendar today = Calendar.getInstance();
        String todayStr = dateFormat.format(today.getTime());

        if (lastDateStr == null) {
            // First-time check-in
            streak = 1;
        } else {
            try {
                Date lastDate = dateFormat.parse(lastDateStr);
                Calendar lastCheckIn = Calendar.getInstance();
                lastCheckIn.setTime(lastDate);

                // If streak is 0, start it at 1
                if (streak == 0) {
                    streak = 1;
                } else {
                    // Check if already checked in today
                    if (isSameDay(today, lastCheckIn)) {
                        // Do nothing
                    } else if (isYesterday(today, lastCheckIn)) {
                        streak++;
                    } else {
                        streak = 1; // Reset
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                streak = 1; // Fallback reset
            }
        }

        // Save current date and streak
        prefs.edit()
                .putString(LAST_DATE_KEY, todayStr)
                .putInt(STREAK_KEY, streak)
                .apply();


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("user").child(uid).child("CurrentDailyStreak").setValue(streak);

        updateHighestStreakIfNeeded(streak);

    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isYesterday(Calendar today, Calendar last) {
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        return isSameDay(yesterday, last);
    }


    private void updateHighestStreakIfNeeded(int currentStreak) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(uid).child("LargestDailyStreak");

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Long highestStreakLong = snapshot.getValue(Long.class);
                int highestStreak = highestStreakLong != null ? highestStreakLong.intValue() : 0;

                if (currentStreak > highestStreak) {
                    // Update locally
                    prefs.edit().putInt("highestDailyStreak", currentStreak).apply();

                    // Update Firebase
                    userRef.setValue(currentStreak);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}

