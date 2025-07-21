package com.example.dissertation_tester.HelperClasses;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dissertation_tester.LogInPage;
import com.example.dissertation_tester.MainActivity;
import com.example.dissertation_tester.MultipleChoiceGame;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FirebaseManager
{
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuthController ;
    private MultipleChoiceGame activeGame;
    Context context;
    public FirebaseManager(MultipleChoiceGame game) {
        this.activeGame = game;
    }
    public FirebaseManager(Context context) {
        this.context = context;
    }
    public void fetchQuestionsAndAnswers() {
        getQuestionsAndAnswersFromFirebase();
    }
    public void signInUser(Activity activity, String email, String password) {
        userAuthController = FirebaseAuth.getInstance();
        signInWithEmailAndPassword(activity, email, password);
    }
    public void checkAndRedirectIfNotLoggedIn(Context context) {
        if (isUserNull()) {
            redirectToLogin(context);
        }
    }
    public void registerNewUser(String email, String password, Activity activity) {
        userAuthController = FirebaseAuth.getInstance();
        createAccount(email, password, activity);
    }
    private void getQuestionsAndAnswersFromFirebase() {

        //get the instance of the database and the path to the question list
        // this is an asynchronous call so any method requiring the use of the database information has to be called here
        databaseReference = FirebaseDatabase.getInstance().getReference().child("vr");
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            //this takes the key and value and puts it into a hashmap to be used in the question and answer section
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                HashMap<String,String> database_qna_list = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String key = dataSnapshot.getKey();
                    String value = (String) dataSnapshot.getValue();

                    database_qna_list.put(key,value);
                }//for
                //calling the Game Loop
                activeGame.displayCurrentRoundQnA(database_qna_list);
            }//onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //if the call to the database fails
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }//onCancelled
        });//addValueEventListener
    }
    private void signInWithEmailAndPassword(Activity activity, String email, String password) {
        userAuthController.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulSignIn(activity);
                    } else {
                        showToast(activity, "Authentication failed.");
                    }
                });
    }
    private void handleSuccessfulSignIn(Activity activity) {
        FirebaseUser user = userAuthController.getCurrentUser();

        if (user != null && user.isEmailVerified()) {
            showToast(activity, "Sign in Successful");
            navigateToMainActivity(activity);
        } else {
            showToast(activity, "Please verify email");
        }
    }
    private void navigateToMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isUserNull() {
        userAuthController = FirebaseAuth.getInstance();
        return userAuthController.getCurrentUser() == null;
    }
    private void redirectToLogin(Context context) {
        Intent i = new Intent(context, LogInPage.class);
        context.startActivity(i);
    }
    private void createAccount(String email, String password,Activity activity) {
        userAuthController.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            sendEmailVerification(activity);
                            //showToast(activity,"success please sign in");
                            Intent i = new Intent(activity, LogInPage.class);
                            activity.startActivity(i);
                            // Sign in success, update UI with the signed-in user's information
                        } //if
                        else
                        {
                            // If sign in fails, display a message to the user.
                            showToast(activity,"Authentication failed.");
                        }//else
                    }//onComplete
                });//onCompleteListener
    }//createAccount
    private void sendEmailVerification(Activity activity) {
        final FirebaseUser user = userAuthController.getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(activity, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    showToast(activity,"Verification email sent to " + user.getEmail());
                }
                else
                {
                    showToast(activity,"Failed to send verification email.");
                }
            }//onComplete
        });//onCompleteListener
    }//sendEmailVerification



}
