package com.example.dissertation_tester.GameLogic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.example.dissertation_tester.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MultipleChoiceGame extends AppCompatActivity {
    private Button ansBtnLowerLeft, ansBtnUpperLeft, ansBtnLowerRight,ansBtnUpperRight;
    private TextView questionText;
    private ImageView close_game_image;
    private Handler handler = new Handler();
    private static int count = 0;
    protected static int numberOfCorrectAnswers = 0;
    ImageView flagButton;
    private static String correctAnswerGlobal;
    protected int MAX_QUESTION = 5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice_game);

        numberOfCorrectAnswers = 0;
        String gameKey = getIntent().getStringExtra("gameKey");

        //-----------------Initialize buttons and text----------------------//

        questionText = (TextView) findViewById(R.id.questionText);
        ansBtnLowerLeft = (Button) findViewById(R.id.ansBtnLowerLeft);
        ansBtnUpperLeft = (Button) findViewById(R.id.ansBtnUpperLeft);
        ansBtnLowerRight = (Button) findViewById(R.id.ansBtnLowerRight);
        ansBtnUpperRight = (Button) findViewById(R.id.ansBtnUpperRight);
        close_game_image = (ImageView) findViewById(R.id.close_game);
        flagButton = (ImageView) findViewById(R.id.btnFlag);


        close_game_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(MultipleChoiceGame.this, GameSelectScreen.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        flagButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                sendFlagRequest(uid,gameKey,questionText.getText().toString(),correctAnswerGlobal,"question and answer is vague");
            }
        });


        FirebaseManager manager = new FirebaseManager(this);

        manager.fetchQuestionsAndAnswers(gameKey);


    }

    public void sendFlagRequest(String userId, String gameName, String question, String answer, String tag) {
        OkHttpClient client = new OkHttpClient();

        // Build JSON payload
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            json.put("game_name", gameName);
            json.put("question", question);
            json.put("answer", answer);
            json.put("tag", tag);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8000/flag-question") // Replace with your FastAPI backend URL
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure (network error, etc)
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Handle successful response (optional update UI or notify user)
                    System.out.println("Flag sent successfully: " + responseBody);
                } else {
                    // Handle unsuccessful response, e.g., server error
                    System.err.println("Server error: " + response.code());
                }
            }
        });
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
        correctAnswerGlobal = correct_answer;
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
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.correct_answer3);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


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
                mediaPlayer.start();
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, 200));
                } else
                {
                    //deprecated in API 26
                    vibrator.vibrate(500);
                }// Wrong answer

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
         String gameKey = getIntent().getStringExtra("gameKey");

        if (count < MAX_QUESTION)
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
                    i.putExtra("gameKey", gameKey);
                    startActivity(i);
                    finish();

                }
            }, 1000);//postDelayed
        }//else
    }

    //display the game name in the top of screen
    // pass through game name for the title which then gets passed through to game over screem
}
