package com.example.dissertation_tester;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dissertation_tester.QuestionAnswerAdapter;
import com.example.dissertation_tester.QuestionAnswerItem;
import com.example.dissertation_tester.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuestionAnswerAdapter adapter;
    private List<QuestionAnswerItem> questionAnswerList;
    private DatabaseReference mDatabase;
    private String gameKey;
    private FloatingActionButton fabAddQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        // Get the game key from intent
        gameKey = getIntent().getStringExtra("gameKey");
        if (gameKey == null) {
            Toast.makeText(this, "Game key not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("userQuestions").child(uid).child("Games").child(gameKey).child("questions").child("bronze");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewQuestions);
        fabAddQuestion = findViewById(R.id.fabAddQuestion);

        // Setup RecyclerView
        questionAnswerList = new ArrayList<>();
        adapter = new QuestionAnswerAdapter(questionAnswerList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup FAB click listener
        fabAddQuestion.setOnClickListener(v -> showAddQuestionDialog());

        // Load questions from Firebase
        loadQuestionsFromFirebase();
    }

    private void loadQuestionsFromFirebase() {
        // Add logging to debug
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = "userQuestions/" + uid + "/Games/" + gameKey + "/questions"+ "/bronze";
        Toast.makeText(this, "Loading from path: " + path, Toast.LENGTH_LONG).show();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(QuestionAnswerActivity.this,
                        "Data exists: " + dataSnapshot.exists() +
                                ", Children count: " + dataSnapshot.getChildrenCount(),
                        Toast.LENGTH_LONG).show();

                questionAnswerList.clear();

                if (!dataSnapshot.exists()) {
                    Toast.makeText(QuestionAnswerActivity.this,
                            "No data found at this path", Toast.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String question = snapshot.getKey();
                    String answer = snapshot.getValue(String.class);

                    Toast.makeText(QuestionAnswerActivity.this,
                            "Found: Q=" + question + ", A=" + answer, Toast.LENGTH_SHORT).show();

                    if (question != null && answer != null) {
                        questionAnswerList.add(new QuestionAnswerItem(question, answer));
                    }
                }
                adapter.notifyDataSetChanged();

                Toast.makeText(QuestionAnswerActivity.this,
                        "Total questions loaded: " + questionAnswerList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuestionAnswerActivity.this,
                        "Failed to load questions: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddQuestionDialog() {
        showEditDialog("Add Question", "", "", new DialogCallback() {
            @Override
            public void onSave(String question, String answer) {
                addQuestionToFirebase(question, answer);
            }
        });
    }

    public void showEditQuestionDialog(String currentQuestion, String currentAnswer) {
        showEditDialog("Edit Question", currentQuestion, currentAnswer, new DialogCallback() {
            @Override
            public void onSave(String question, String answer) {
                // Delete old question and add new one
                deleteQuestionFromFirebase(currentQuestion);
                addQuestionToFirebase(question, answer);
            }
        });
    }

    private void showEditDialog(String title, String currentQuestion, String currentAnswer, DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_question, null);

        EditText etQuestion = dialogView.findViewById(R.id.etQuestion);
        EditText etAnswer = dialogView.findViewById(R.id.etAnswer);

        etQuestion.setText(currentQuestion);
        etAnswer.setText(currentAnswer);

        builder.setView(dialogView)
                .setTitle(title)
                .setPositiveButton("Save", (dialog, which) -> {
                    String question = etQuestion.getText().toString().trim();
                    String answer = etAnswer.getText().toString().trim();

                    if (question.isEmpty() || answer.isEmpty()) {
                        Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    callback.onSave(question, answer);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addQuestionToFirebase(String question, String answer) {
        mDatabase.child(question).setValue(answer)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Question added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add question: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void deleteQuestionFromFirebase(String question) {
        mDatabase.child(question).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Question deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to delete question: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void showDeleteConfirmationDialog(String question) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Delete", (dialog, which) -> deleteQuestionFromFirebase(question))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private interface DialogCallback {
        void onSave(String question, String answer);
    }
}