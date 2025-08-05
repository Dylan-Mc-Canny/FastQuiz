package com.example.dissertation_tester.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dissertation_tester.GameLogic.GameSelectScreen;
import com.example.dissertation_tester.R;
import com.example.dissertation_tester.UploadPDF;

public class HomeFragment extends Fragment {

    Button playGameBtn,createQuestionBtn;

    private CardView createQuestionsCard;
    private CardView playGameCard;

    TextView StreakContainer;




    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        StreakContainer = (TextView) view.findViewById(R.id.streakText);

        SharedPreferences prefs = requireContext().getSharedPreferences("StreakPrefs", Context.MODE_PRIVATE);
        int streak = prefs.getInt("currentStreak", 0);
        StreakContainer.setText("\uD83D\uDD25 " + String.valueOf(streak));




        initViews(view);

        // Set click listeners
        setupClickListeners();


        return view;
    }

    private void initViews(View view) {
        createQuestionsCard = view.findViewById(R.id.createQuestionsCard);
        playGameCard = view.findViewById(R.id.playGameCard);
    }

    private void setupClickListeners() {
        // Create Questions Card Click
        createQuestionsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add click animation
                animateCardClick(v);

                // Start Create Questions Activity
                Intent intent = new Intent(getActivity(), UploadPDF.class);
                startActivity(intent);

                // Optional: Add transition animation
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        // Play Quiz Card Click
        playGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add click animation
                animateCardClick(v);

                // Start Play Game Activity
                Intent intent = new Intent(getActivity(), GameSelectScreen.class);
                startActivity(intent);

                // Optional: Add transition animation
                if (getActivity() != null) {
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });
    }

    // Optional: Add a subtle click animation
    private void animateCardClick(View view) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();
    }
}
