package com.example.dissertation_tester.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dissertation_tester.Login.LogInPage;
import com.example.dissertation_tester.GameLogic.MultipleChoiceGame;
import com.example.dissertation_tester.MainActivity;
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
import java.util.Objects;

public class FirebaseManager
{
    private DatabaseReference databaseReference;
    private FirebaseAuth userAuthController ;
    private MultipleChoiceGame activeGame;
    Context context;

    public static FirebaseManager instance;


    //////-------------------------------------------------------------------------------------------------/////
    //Public Wrapper Functions
    public FirebaseManager(MultipleChoiceGame game) {
        this.activeGame = game;
    }
    public FirebaseManager(Context context) {
        this.context = context;
    }
    public void fetchQuestionsAndAnswers(String gamePath) {
        getUserBadgeLevel(gamePath);
    }
    public void signInUser(Activity activity, String email, String password) {
        userAuthController = FirebaseAuth.getInstance();
        signInWithEmailAndPassword(activity, email, password);
    }
    public boolean checkAndRedirectIfNotLoggedIn(Context context) {
        if (isUserNull()) {
            redirectToLogin(context);
            return false;
        }
        else
        {
            return true;
        }
    }
    public void registerNewUser(String email, String password, Activity activity) {
        userAuthController = FirebaseAuth.getInstance();
        createAccount(email, password, activity);
    }
    public void initiatePasswordReset(String email, Activity activity) {
        sendPasswordReset(email, activity);
    }

    public void getUserBadgeLevel(String gamePath)
    {
        String userID = getUserId();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(userID).child("Games").child(gamePath).child("badge");


        databaseReference.addValueEventListener(new ValueEventListener()
        {
            //this takes the key and value and puts it into a hashmap to be used in the question and answer section
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (Objects.equals(snapshot.getKey(), "badge"))
                {
                    String badge = (String) snapshot.getValue();

                    getQuestionsAndAnswersFromFirebase(gamePath,badge);
                }
                //calling the Game Loop
            }//onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                //if the call to the database fails
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }//onCancelled
        });//addValueEventListener

    }

    private String getUserId()
    {
        userAuthController = FirebaseAuth.getInstance();
        return userAuthController.getUid();

    }

    public boolean logout() {
    FirebaseAuth.getInstance().signOut();
    return true;
}

    //////-------------------------------------------------------------------------------------------------/////
    //Private Logic Functions
    private void getQuestionsAndAnswersFromFirebase(String gamePath, String badge) {

        userAuthController = FirebaseAuth.getInstance();
        String userId = userAuthController.getUid();

        //get the instance of the database and the path to the question list
        // this is an asynchronous call so any method requiring the use of the database information has to be called here
        databaseReference = FirebaseDatabase.getInstance().getReference().child("userQuestions").child(userId).child("Games").child(gamePath).child("questions").child(badge);
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
    public boolean isUserNull() {
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
                            createUserDataNode();
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
    private void sendPasswordReset(String email,Activity activity) {
        //Get firebase instance
        userAuthController = FirebaseAuth.getInstance();

        //send reset link to the email provided
        //todo - check the email against the database and only send if it exists

        userAuthController.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    showToast(activity, "Email has been sent");

                    //direct user back to the login page
                    Intent i = new Intent(activity, LogInPage.class);
                    activity.startActivity(i);
                }//if
            }//onComplete
        });//addOnCompleteListener
    }//sendPasswordReset

    public void createUserDataNode() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    if (user != null) {
        String uid = user.getUid();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");
        // Create a node with the UID as the key and set initial data (empty map)
        database.child(uid).child("Games").setValue("");
        database.child(uid).child("TotalCorrectAnswers").setValue(0);
        database.child(uid).child("LargestDailyStreak").setValue(0);
        database.child(uid).child("CurrentDailyStreak").setValue(0);
    }
}


}//FirebaseManager
