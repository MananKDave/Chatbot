package com.example.chatapiproject;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class history extends AppCompatActivity {

    ListView lv;
    ArrayList<HashMap<String,String>> arrayList;
    SimpleAdapter adapter;
    LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        File cacheDir = getCacheDir();
        File cacheFile = new File(cacheDir, "myString");

        try {

            FileReader reader = new FileReader(cacheFile);
            char[] buffer = new char[1024];
            int n;
            StringBuilder builder = new StringBuilder();
            while ((n = reader.read(buffer)) != -1) {
                builder.append(buffer, 0, n);
            }
            reader.close();
            String myString = builder.toString();

            arrayList=new ArrayList<>();
            lv = (ListView)findViewById(R.id.listview_ahistory);

            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            String url = "http://192.168.184.122:8000/app/testviewhistory/"; // <----enter your post url here
            JsonObjectRequest MyStringRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //This code is executed if the server responds, whether or not the response contains data.
                            //The String 'response' contains the server's response.
                            try {
                                JSONArray dataArray = response.getJSONArray(myString);
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataObj = dataArray.getJSONObject(i);
                                    String date = dataObj.getString("Date");
                                    String time = dataObj.getString("Time");
                                    String message = dataObj.getString("Message");
                                    String reply = dataObj.getString("Reply");
                                    String links = dataObj.getString("Links");

                                    HashMap<String, String> data = new HashMap<>();
                                    data.put("date", date);
                                    data.put("time", time);
                                    data.put("message", message);
                                    arrayList.add(data);



                                    adapter = new SimpleAdapter(history.this, arrayList, R.layout.listview_history
                                            ,new String[]{"date", "time", "message"},new int[]{R.id.hdate, R.id.htime, R.id.hmessage});
                                    lv.setAdapter(adapter);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            MyRequestQueue.add(MyStringRequest);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    RequestQueue MyRequestQueue = Volley.newRequestQueue(history.this);
                    String url = "http://192.168.184.122:8000/app/testviewhistorylocation/"; // <----enter your post url here
                    JsonObjectRequest MyStringRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            url,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //This code is executed if the server responds, whether or not the response contains data.
                                    //The String 'response' contains the server's response.
                                    try {
                                        // Parse the JSON response into a JSONObject
                                        JSONObject jsonObject = new JSONObject(String.valueOf(response));

                                        // Extract the data for each user
                                        JSONArray userArray = jsonObject.getJSONArray(myString);

                                        // Process the data for Manan
                                        JSONObject Obj = userArray.getJSONObject(i);
                                        JSONArray data = Obj.getJSONArray(String.valueOf(i));

                                        for (int i = 0; i < data.length(); i++) {
                                            JSONObject messageObj = data.getJSONObject(i);
                                            String date = messageObj.getString("Date");
                                            String time = messageObj.getString("Time");
                                            String message = messageObj.getString("Message");
                                            String reply = messageObj.getString("Reply");
                                            String links = messageObj.getString("Links");

                                            Intent intent = new Intent(history.this, subhistory.class);
                                            intent.putExtra("message", message);
                                            intent.putExtra("reply", reply);
                                            intent.putExtra("links", links);
                                            startActivity(intent);


                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                    });
                    MyRequestQueue.add(MyStringRequest);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.main_activity2, menu);*/
        getMenuInflater().inflate(R.menu.refresh, menu);
        getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.logout, menu);

        //Refresh btn

        MenuItem refresh = menu.findItem(R.id.refresh_btn);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                recreate();
                Toast.makeText(history.this, "Refresh", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //home btn

        MenuItem home = menu.findItem(R.id.home_btn);
        home.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(history.this, microphone.class);
                startActivity(i);
                return false;
            }
        });

        //logout btn

        MenuItem logout = menu.findItem(R.id.logout_btn);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(history.this, MainActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                return false;
            }
        });
        return true;
    }
}
