package com.example.chatapiproject;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private TextToSpeech tts;
    EditText email, pass;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email_edittext);
        pass = findViewById(R.id.password_edittext);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // set language for text-to-speech
                    tts.setLanguage(Locale.US);

                }
            }
        });

    }
    public void handlelog(View view) {
        String emaill = email.getText().toString();
        String passss = pass.getText().toString();

        if(!emaill.isEmpty()){
            if(!passss.isEmpty()) {
                RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
                String url = "http://192.168.184.122:8000/app/testviewlogin/"; // <----enter your post url here
                StringRequest MyStringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //This code is executed if the server responds, whether or not the response contains data.
                                //The String 'response' contains the server's response.
                                if(response.equals("\"No Account\"") || response.equals("\"Password Doesn't Match\"")){
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                                    String a = response.replace('"', ' ');
                                    String b = a.replace("Welcome", "");
                                    String c = b.trim();
                                    File cacheDir = getCacheDir();
                                    File cacheFile = new File(cacheDir, "myString");
                                    try {
                                        FileWriter writer = new FileWriter(cacheFile);
                                        writer.write(c);
                                        writer.flush();
                                        writer.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Intent i = new Intent(MainActivity.this, microphone.class);
                                    startActivity(i);

                                    speak(response);
                                }
                            }
                        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = "An error occurred while processing your request. Please try again later.";
                        if (error instanceof NetworkError) {
                            message = "Network error. Please check your internet connection and try again.";
                        } else if (error instanceof ServerError) {
                            message = "Server error. Please contact support.";
                        } else if (error instanceof TimeoutError) {
                            message = "Request timed out. Please try again later.";
                        } else if (error instanceof AuthFailureError) {
                            message = "Authentication error. Please log in again.";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(message)
                                .setTitle("Error")
                                .setPositiveButton("OK", null)
                                .create()
                                .show();
                        VolleyLog.e(TAG, message);

                    }
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("email", emaill);
                        MyData.put("password", passss);
                        return MyData;
                    }
                };
                MyRequestQueue.add(MyStringRequest);
            }else{
                Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
        }
    }

    public void handlesignup(View view) {
        Intent i = new Intent(MainActivity.this, signup.class);
        startActivity(i);
    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceID");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
