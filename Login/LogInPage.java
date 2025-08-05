package com.example.dissertation_tester.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.example.dissertation_tester.R;

public class LogInPage extends AppCompatActivity {
    private TextView forgotPasswordLink;
    private EditText enterEmailContainer, enterPasswdContainer;
    private Button loginBtn, RegisterBtn;
    private final FirebaseManager manager = new FirebaseManager(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        enterEmailContainer = (EditText) findViewById(R.id.user_email_verification);
        enterPasswdContainer = (EditText) findViewById(R.id.user_password_verification);
        forgotPasswordLink = (TextView) findViewById(R.id.forgotPasswordTextPrompt);
        loginBtn =(Button) findViewById(R.id.btn_login);




        RegisterBtn = (Button) findViewById(R.id.btn_register);
        RegisterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LogInPage.this, RegisterPage.class);
                startActivity(i);
                finish();
            }//onClick
        });//onClickListener


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = enterEmailContainer.getText().toString();
                String password = enterPasswdContainer.getText().toString();
                if (email.equals("") || password.equals(""))
                {
                    Toast.makeText(LogInPage.this, "Fields Empty", Toast.LENGTH_SHORT).show();
                }else
                {
                    manager.signInUser(LogInPage.this,email, password);
                }
            }//onClick
        });//onClickListener


        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LogInPage.this, ForgotPassword.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener
    }//onCreate
}//LogInPage
