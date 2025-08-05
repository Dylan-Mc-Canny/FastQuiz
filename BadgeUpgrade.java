package com.example.dissertation_tester;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.dissertation_tester.databinding.ActivityBadgeUpgradeBinding;

public class BadgeUpgrade extends AppCompatActivity {

    private ImageView badgeImageView;
    private TextView badgeTextView;

    //private final String gameKey = getIntent().getStringExtra("gameKey");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_upgrade);

        badgeImageView = findViewById(R.id.badgeImageView);
        badgeTextView = findViewById(R.id.badgeTextView);

        // Get the badge name from the Intent
        Intent intent = getIntent();
        String badgeName = intent.getStringExtra("badgeName");

        // Update UI based on the badge name
        if (badgeName != null) {
            badgeTextView.setText("You earned the " + badgeName + " badge!");

            // Set badge image based on the badge name
            switch (badgeName) {
                case "Gold":
                    badgeImageView.setImageResource(R.drawable.baseline_shield_24);
                    break;
                case "Silver":
                    badgeImageView.setImageResource(R.drawable.baseline_shield_silver);
                    break;
                case "Bronze":
                    badgeImageView.setImageResource(R.drawable.baseline_shield_bronze);
                    break;
                default:
                    badgeImageView.setImageResource(R.drawable.baseline_shield_bronze);
                    break;
            }
        }



        new Handler().postDelayed(() -> {
            finish();  // This goes back to the previous screen
        }, 5000); // 2000 milliseconds = 2 seconds
    }
}