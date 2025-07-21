package com.example.dissertation_tester;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    EditText emailTextContainer, passwordTextContainer, confirmPasswordTextContainer;
    Button registerBtn;
    FirebaseManager manager = new FirebaseManager(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        emailTextContainer = (EditText) findViewById(R.id.registerEmailContainer);
        passwordTextContainer = (EditText) findViewById(R.id.passwdContainer);
        confirmPasswordTextContainer = (EditText) findViewById(R.id.confirmPasswdContainer) ;
        registerBtn = (Button) findViewById(R.id.registerBtn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String userInputEmail = emailTextContainer.getText().toString();
                String userInputPassword = String.valueOf(passwordTextContainer.getText().toString());
                String userInputConfirmPassword = String.valueOf(confirmPasswordTextContainer.getText().toString());

                if (!userInputPassword.equals(userInputConfirmPassword))
                {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }//if
                else
                {
                    manager.registerNewUser(userInputEmail,userInputPassword,RegisterPage.this);
                }//else
            }//onClick
        });//onClickListener
    }
}
