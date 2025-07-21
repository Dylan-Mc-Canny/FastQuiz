package com.example.dissertation_tester.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.example.dissertation_tester.MainActivity;
import com.example.dissertation_tester.MultipleChoiceGame;
import com.example.dissertation_tester.R;
import com.example.dissertation_tester.UploadPDF;

public class HomeFragment extends Fragment {

    Button playGameBtn,createQuestionBtn;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        playGameBtn = (Button) view.findViewById(R.id.PlayGameBtn);
        createQuestionBtn = (Button) view.findViewById(R.id.GenerateQuestionBtn);

        playGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MultipleChoiceGame.class);
                startActivity(i);
            }
        });

        createQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UploadPDF.class);
                startActivity(i);
            }
        });

        return view;
    }
}