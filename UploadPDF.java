package com.example.dissertation_tester;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dissertation_tester.HelperClasses.FastApiManager;

public class UploadPDF extends AppCompatActivity {
    FastApiManager fastApiManager = new FastApiManager(this);
    EditText promptTextContainer,pathTextContainer;
    Button submitButton;

    ImageView exitGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        promptTextContainer = (EditText) findViewById(R.id.editTextText);
        pathTextContainer = (EditText) findViewById(R.id.path);
        submitButton = findViewById(R.id.submitBtn);
        exitGame = (ImageView) findViewById(R.id.close_game);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String llmPromptText = promptTextContainer.getText().toString().trim();
                String dbStorageReferenceText = pathTextContainer.getText().toString().trim();

                if (!llmPromptText.isEmpty() && !dbStorageReferenceText.isEmpty() ) {
                    FastApiQuestionGenerator(llmPromptText,dbStorageReferenceText);
                } else {
                    Toast.makeText(UploadPDF.this, "Please enter text", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exitGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(UploadPDF.this, MainActivity.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener





    }
    private void FastApiQuestionGenerator(String prompt,String path) {
        Log.e("fetchQuizQuestion","Running");
        // Call the FastApiManager method to send request and handle response
        fastApiManager.quizApiCommunicationManager(prompt,path);

        Log.e("fetchQuizQuestion","Complete");
    }
}