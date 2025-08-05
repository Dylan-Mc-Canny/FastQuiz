package com.example.dissertation_tester.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.example.dissertation_tester.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText userInputEmail;
    Button submitBtn;
    FirebaseManager manager = new FirebaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        userInputEmail = (EditText) findViewById(R.id.emailContainer);


        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                manager.initiatePasswordReset(userInputEmail.getText().toString(),ForgotPassword.this);
            }//onClick
        });//setOnClickListener
    }//onCreate
}
