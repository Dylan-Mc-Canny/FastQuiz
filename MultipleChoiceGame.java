package com.example.dissertation_tester;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dissertation_tester.HelperClasses.FastApiManager;
import com.example.dissertation_tester.HelperClasses.FirebaseManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MultipleChoiceGame extends AppCompatActivity {
    Button ansBtnLowerLeft, ansBtnUpperLeft, ansBtnLowerRight,ansBtnUpperRight;
    TextView questionText;
    ImageView close_game_image;
    Handler handler = new Handler();
    static int count = 0;
     static int numberOfCorrectAnswers = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_game);

        numberOfCorrectAnswers = 0;

        //-----------------Initialize buttons and text----------------------//

        questionText = (TextView) findViewById(R.id.questionText);
        ansBtnLowerLeft = (Button) findViewById(R.id.ansBtnLowerLeft);
        ansBtnUpperLeft = (Button) findViewById(R.id.ansBtnUpperLeft);
        ansBtnLowerRight = (Button) findViewById(R.id.ansBtnLowerRight);
        ansBtnUpperRight = (Button) findViewById(R.id.ansBtnUpperRight);
        close_game_image = (ImageView) findViewById(R.id.close_game);


        close_game_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MultipleChoiceGame.this, MainActivity.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener


        FirebaseManager manager = new FirebaseManager(this);

        manager.fetchQuestionsAndAnswers();


    }
    public void displayCurrentRoundQnA(HashMap<String, String> database_qna_list) {
        //-----------------populate text logic----------------------//
        count++;

        List<String> question = new ArrayList<>(database_qna_list.keySet());  // parallel arrays containing the questions and answers
        List<String> answers = new ArrayList<>(database_qna_list.values());

        if (answers.size() < 4)
        {
            return; // error will have occurred
        }//if

        Random random = new Random();
        int correctIndex = random.nextInt(question.size());
        String correct_answer = answers.get(correctIndex);
        String currentQuestion = question.get(correctIndex);
        database_qna_list.remove(currentQuestion);


        ArrayList<String> current_game_answers = new ArrayList<>();  //create a new list that contains the correct answers plus 3 random incorrect answers
        current_game_answers.add(correct_answer);

        while (current_game_answers.size() < 4)
        {
            String randomAnswer = answers.get(random.nextInt(answers.size()));

            if (!current_game_answers.contains(randomAnswer))
            {
                current_game_answers.add(randomAnswer);
            }//if
        }//while

        set_game_btn_text(current_game_answers,currentQuestion);
        buttonControls(correct_answer,database_qna_list);

        //populate_current_round_btn_txt(database_qna_list);
    }//populate_current_round_btn_txt
    private void set_game_btn_text(ArrayList<String>current_round_answers, String current_round_question) {
        Collections.shuffle(current_round_answers);

        questionText.setText(current_round_question);
        ansBtnLowerLeft.setText(current_round_answers.get(0));
        ansBtnUpperLeft.setText(current_round_answers.get(1));
        ansBtnLowerRight.setText(current_round_answers.get(2));
        ansBtnUpperRight.setText(current_round_answers.get(3));
    }
    private void buttonControls(String correctAnswer,HashMap<String, String> database_qna_list) {
        View.OnClickListener listener = v ->
        {
            Button clickedButton = (Button) v;

            // Disable all buttons
            ansBtnLowerLeft.setEnabled(false);
            ansBtnUpperLeft.setEnabled(false);
            ansBtnLowerRight.setEnabled(false);
            ansBtnUpperRight.setEnabled(false);

            // Provide feedback
            if (clickedButton.getText().toString().equals(correctAnswer))
            {
                //mediaPlayer.start();
                numberOfCorrectAnswers++;

                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        clickedButton.setBackgroundColor(Color.GREEN);
                    }
                }, 100);

                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        clickedButton.setBackgroundColor(Color.rgb(134, 146, 247));
                    }
                }, 1000);// Correct answer
            }
            else
            {
                clickedButton.setBackgroundColor(Color.RED);
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        clickedButton.setBackgroundColor(Color.rgb(134, 146, 247));
                    }
                }, 1000);// Correct answer
            }//OnClickListener

            replayGameLogic(database_qna_list);

        };//OnClickListener


        ansBtnLowerLeft.setOnClickListener(listener);
        ansBtnUpperLeft.setOnClickListener(listener);
        ansBtnLowerRight.setOnClickListener(listener);
        ansBtnUpperRight.setOnClickListener(listener);
    }
    private void setAllBtnEnable() {
        ansBtnLowerLeft.setEnabled(true);
        ansBtnUpperLeft.setEnabled(true);
        ansBtnLowerRight.setEnabled(true);
        ansBtnUpperRight.setEnabled(true);
    }
    private void replayGameLogic(HashMap<String, String> database_qna_list){
        if (count < 10)
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //if max questions is not reached yet, then a new question is generated using the updated hashmap
                    //populate_current_round_btn_txt(database_qna_list_copy);


                    setAllBtnEnable();
                    displayCurrentRoundQnA(database_qna_list);

                }
            }, 1000);// Correct answer
        }//if
        else
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    count =0;
                    //once the game reaches the max questions it brings them to the game over page
                    Intent i = new Intent(MultipleChoiceGame.this, GameOverScreen.class);
                    startActivity(i);
                    finish();

                }
            }, 1000);//postDelayed
        }//else
    }
}
