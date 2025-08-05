package com.example.dissertation_tester.HelperClasses;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class FastApiManager  {
    private final Activity activityContext;
    public FastApiManager (Activity currentActivityInstance) {
        this.activityContext = currentActivityInstance;
    }//FastApiManager
    public void quizApiCommunicationManager(String questionPrompt,String Path) {
        //Message for LLM to respond to
        String llmInputMessage = questionPrompt + "20 PhD level questions and one/two word answers in JSON with the questions as the keys and answers as the value no ['$', '#', '[', ']', '/', '.'] values ";

        //format the llm input message correctly to send to FastApi Endpoint
        Request formattedJsonRequestMessage = jsonMessageFormatter(llmInputMessage,Path);

        //send llm prompt and handle the response message
        apiRequestAndResponseManager(formattedJsonRequestMessage);
    }//quizApiCommunicationManager
    private Request jsonMessageFormatter(String llmPrompt,String path) {

        final String JSON_KEY = "message";
        final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
        final String ENDPOINT_URL = "http://10.0.2.2:8000/chat";

        //Build the json body in the correct format with llmInputMessage
        JSONObject jsonRequestMessageFormat = new JSONObject();
        try {
            jsonRequestMessageFormat.put(JSON_KEY, llmPrompt);
            jsonRequestMessageFormat.put("path", path);
        }//try
        catch (JSONException exceptionCatcher) {
            exceptionCatcher.printStackTrace();  // Handle exception appropriately
        }//catch

        // Convert JSONObject to string for the request body
        String jsonRequestMessage = jsonRequestMessageFormat.toString();

        //create the request body using the media type and formatted request message
        RequestBody jsonRequestBody  = RequestBody.create(jsonRequestMessage, JSON_MEDIA_TYPE);

        //return the request object including the url of the endpoint
        return new Request.Builder().url(ENDPOINT_URL).post(jsonRequestBody).build();
    }//jsonMessageFormatter
    private void apiRequestAndResponseManager(Request formattedHttpRequestObject) {

        final OkHttpClient httpCommunicationManager = new OkHttpClient();
        final String TAG = "FastApiManager";
        //JSONArray innerJson;

        //send the http request to the FastAPI endpoint
        httpCommunicationManager.newCall(formattedHttpRequestObject).enqueue(new Callback() {

            //if the request fails this code executes
            @Override
            public void onFailure(Call _httpRequestCall, IOException exceptionCatcher) {
                Log.e(TAG, "HTTP Request Failed", exceptionCatcher);
                //activityContext.runOnUiThread(() ->
                        //responseTextView.setText("Request failed: " + exceptionCatcher.getMessage()));
            }//onFailure

            //if successful this executes
            @Override
            public void onResponse(Call _httpRequestCall, Response jsonResponseMessage) throws IOException {

                String responseText = jsonResponseMessage.body().string();
                try {
                    JSONObject jsonLlmResponseObject = new JSONObject(responseText);
                    String replyString = jsonLlmResponseObject.getString("reply");
                    Log.e(TAG,replyString);

                }//try
                catch (JSONException exceptionCatcher) {
                    //activityContext.runOnUiThread(() ->
                            //responseTextView.setText("JSON parse error")
                    //);
                }//catch
            }//onResponse
        });//httpCommunicationManager
    }//apiRequestAndResponseManager
}//FastApiManager


