package com.example.chatapiproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    EditText name, email, pass, cpass;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.rname_edittext);
        email = findViewById(R.id.remail_edittext);
        pass = findViewById(R.id.rpassword_edittext);
        cpass = findViewById(R.id.rcpassword_edittext);

    }

    public void handlelogin(View view) {
        String nameee = name.getText().toString();
        String emaill = email.getText().toString();
        String passss = pass.getText().toString();
        String cpasss = cpass.getText().toString();

        if(!nameee.isEmpty()){
            if(!emaill.isEmpty()){
                if(!passss.isEmpty()) {
                    if(!cpasss.isEmpty()) {
                        if(passss.equals(cpasss)){
                            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
                            String url = "http://192.168.184.122:8000/app/testviewregister/"; // <----enter your post url here
                            StringRequest MyStringRequest = new StringRequest(
                                    Request.Method.POST,
                                    url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            //This code is executed if the server responds, whether or not the response contains data.
                                            //The String 'response' contains the server's response.
                                            if(response.equals("\"Account Exists\"")){
                                                Toast.makeText(signup.this, response, Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(signup.this, response, Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(signup.this, MainActivity.class);
                                                startActivity(i);
                                            }
                                        }
                                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                                }
                            }) {
                                protected Map<String, String> getParams() {
                                    Map<String, String> MyData = new HashMap<String, String>();
                                    MyData.put("name", nameee);
                                    MyData.put("email", emaill);
                                    MyData.put("password", passss);
                                    MyData.put("cpassword", cpasss);
                                    return MyData;
                                }
                            };
                            MyRequestQueue.add(MyStringRequest);
                        }else{
                            Toast.makeText(signup.this, "Incorrect Confirm Password", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(signup.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(signup.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(signup.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(signup.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
    }
}
