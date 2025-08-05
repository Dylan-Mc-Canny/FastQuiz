package com.example.dissertation_tester;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dissertation_tester.HelperClasses.FastApiManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadPDF extends AppCompatActivity {
    FastApiManager fastApiManager = new FastApiManager(this);
    EditText gameTitle,pathTextContainer;
    Button submitButton;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    CardView uploadCard = findViewById(R.id.uploadCard);
    private Uri selectedPdfUri;
    private FirebaseAuth mAuth;
    ImageView exitGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        CardView selectPdfCard = findViewById(R.id.selectPdfCard);
        CardView uploadCard = findViewById(R.id.uploadCard);
        TextView pdfFileNameTextView = findViewById(R.id.selectPdfText);
        gameTitle = (EditText) findViewById(R.id.gameTitle);
        exitGame = (ImageView) findViewById(R.id.close_game);

        selectPdfCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(UploadPDF.this, "Select PDF clicked!", Toast.LENGTH_SHORT).show();

                // TODO: add your PDF picker logic here
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                String[] mimeTypes = {"application/pdf", "text/plain"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                pdfPickerLauncher.launch(intent);
            }
        });

        uploadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbStorageReferenceText = gameTitle.getText().toString().trim();

                // IMPROVED: Better validation with specific messages
                if (dbStorageReferenceText.isEmpty()) {
                    Toast.makeText(UploadPDF.this, "Please enter a game title", Toast.LENGTH_SHORT).show();
                    gameTitle.requestFocus(); // Focus on the field
                    return;
                }

                if (selectedPdfUri == null) {
                    Toast.makeText(UploadPDF.this, "Please select a PDF file first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // IMPROVED: Show loading state
                uploadCard.setEnabled(false);
                Toast.makeText(UploadPDF.this, "Uploading file, please wait...", Toast.LENGTH_SHORT).show();

                uploadPdf(selectedPdfUri, dbStorageReferenceText);
            }
        });



        /*submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String llmPromptText = promptTextContainer.getText().toString().trim();
                String dbStorageReferenceText = pathTextContainer.getText().toString().trim();

                if (!dbStorageReferenceText.isEmpty() && selectedPdfUri != null ) {
                    //FastApiQuestionGenerator(llmPromptText,dbStorageReferenceText);
                    uploadPdf(selectedPdfUri, dbStorageReferenceText);
                } else {
                    Toast.makeText(UploadPDF.this, "Please enter text", Toast.LENGTH_SHORT).show();
                }
            }
        });

         */

        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(UploadPDF.this, MainActivity.class);
                startActivity(i);
                finish();
            }//onClick
        });//setOnClickListener




        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedPdfUri = data.getData();

                            String fileName  = getFileName(selectedPdfUri);
                            pdfFileNameTextView.setText(fileName != null ? fileName : "Unknown file");

                            Log.d("PDF_URI", "Selected PDF URI: " + selectedPdfUri.toString());
                        }
                    }
                }
        );




/*
        Button selectPdfButton = findViewById(R.id.selectPdfButton);
        selectPdfButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            pdfPickerLauncher.launch(intent);
        });*/
    }
    private void FastApiQuestionGenerator(String prompt,String path) {
        Log.e("fetchQuizQuestion","Running");
        // Call the FastApiManager method to send request and handle response
        fastApiManager.quizApiCommunicationManager(prompt,path);

        Log.e("fetchQuizQuestion","Complete");
    }
    private void uploadPdf(Uri pdfUri, String path) {
        Log.e("TEST", "Upload starting");
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();

        // IMPROVED: Validate user authentication
        if (userId == null) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Please sign in to upload files", Toast.LENGTH_LONG).show();
                uploadCard.setEnabled(true); // Re-enable button
            });
            return;
        }

        String fileName = getFileName(pdfUri);
        String mimeType = getContentResolver().getType(pdfUri);
        if (mimeType == null) {
            if (fileName != null && fileName.endsWith(".txt")) {
                mimeType = "text/plain";
            } else {
                mimeType = "application/pdf";
            }
        }

        try {
            InputStream inputStream = getContentResolver().openInputStream(pdfUri);
            byte[] pdfBytes = null;
            if (inputStream != null) {
                pdfBytes = new byte[inputStream.available()];
                inputStream.read(pdfBytes);
                inputStream.close();
            }

            if (pdfBytes == null) {
                Log.e("UploadPDF", "Failed to read PDF bytes");
                // IMPROVED: User feedback for file read error
                runOnUiThread(() -> {
                    Toast.makeText(this, "Could not read the selected file. Please try again.", Toast.LENGTH_LONG).show();
                    uploadCard.setEnabled(true);
                });
                return;
            }

            // IMPROVED: File size validation
            if (pdfBytes.length > 10 * 1024 * 1024) { // 10MB limit
                runOnUiThread(() -> {
                    Toast.makeText(this, "File too large. Please select a file under 10MB.", Toast.LENGTH_LONG).show();
                    uploadCard.setEnabled(true);
                });
                return;
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("path", path)
                    .addFormDataPart(
                            "file",
                            fileName != null ? fileName : "upload.pdf",
                            RequestBody.create(pdfBytes, MediaType.parse(mimeType))
                    )
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/upload-pdf/")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("UploadPDF", "Upload failed: " + e.getMessage());
                    // IMPROVED: Better error handling with user feedback
                    runOnUiThread(() -> {
                        String errorMessage = "Upload failed. ";
                        if (e.getMessage() != null) {
                            if (e.getMessage().contains("timeout")) {
                                errorMessage += "Connection timeout. Please check your internet and try again.";
                            } else if (e.getMessage().contains("network")) {
                                errorMessage += "Network error. Please check your connection.";
                            } else {
                                errorMessage += "Please try again.";
                            }
                        } else {
                            errorMessage += "Please try again.";
                        }
                        Toast.makeText(UploadPDF.this, errorMessage, Toast.LENGTH_LONG).show();
                        uploadCard.setEnabled(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        uploadCard.setEnabled(true); // Re-enable button

                        if (response.isSuccessful()) {
                            // IMPROVED: Success feedback with next steps
                            Toast.makeText(UploadPDF.this,
                                    "File uploaded successfully! Questions are being generated...",
                                    Toast.LENGTH_LONG).show();

                            // IMPROVED: Auto-navigate back or show completion
                            // Optional: Navigate back to main activity after success
                            // finish();

                        } else {
                            // IMPROVED: Specific error messages based on response code
                            String errorMessage;
                            switch (response.code()) {
                                case 413:
                                    errorMessage = "File too large. Please select a smaller file.";
                                    break;
                                case 415:
                                    errorMessage = "File type not supported. Please select a PDF or text file.";
                                    break;
                                case 400:
                                    errorMessage = "Invalid file or game title. Please check and try again.";
                                    break;
                                case 500:
                                    errorMessage = "Server error. Please try again later.";
                                    break;
                                default:
                                    errorMessage = "Upload failed (Error " + response.code() + "). Please try again.";
                            }
                            Toast.makeText(UploadPDF.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });

                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        Log.d("UploadPDF", "Response: " + responseBody);
                    } catch (IOException e) {
                        Log.e("UploadPDF", "Error reading response: " + e.getMessage());
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            // IMPROVED: File access error feedback
            Toast.makeText(this, "Could not access the selected file. Please try again.", Toast.LENGTH_LONG).show();
            uploadCard.setEnabled(true);
            return;
        }
    }

        private String getFileName (Uri uri){
            String result = null;
            if (uri.getScheme().equals("content")) {
                try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            result = cursor.getString(nameIndex);
                        }
                    }
                } catch (Exception e) {
                    // IMPROVED: Handle cursor errors gracefully
                    Log.e("UploadPDF", "Error getting filename: " + e.getMessage());
                }
            }
            if (result == null) {
                result = uri.getLastPathSegment();
            }
            return result;
        }
}