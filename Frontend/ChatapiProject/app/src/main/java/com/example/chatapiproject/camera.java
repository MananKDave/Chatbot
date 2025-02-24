package com.example.chatapiproject;
import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class camera extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imageView;
    Button button, cbutton;
    Uri imageUri;
    TextView textView, editText;
    Button answer;
    TextRecognizer textRecognizer;
    private TextToSpeech tts;
    String recognizedText;

    Date currentDate;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    String date, time, myString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        imageView = findViewById(R.id.image_view);
        button = findViewById(R.id.select_button);
        /*editText = findViewById(R.id.text_viewA);*/
        answer = findViewById(R.id.text_viewA);

        currentDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[] {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(camera.this);
                builder.setTitle("Select an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, PICK_IMAGE);
                        }
                    }
                });
                builder.show();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri uri = getImageUri(getApplicationContext(), imageBitmap);
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                try {
                    Bitmap bitmap = scaleBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), 800, 800);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setImageBitmap(bitmap);

                    if (textRecognizer.isOperational()) {
                        Frame imageFrame = new Frame.Builder()
                                .setBitmap(bitmap)
                                .build();
                        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < textBlocks.size(); i++) {
                            TextBlock textBlock = textBlocks.valueAt(i);
                            stringBuilder.append(textBlock.getValue());
                            stringBuilder.append(" ");
                        }
                        recognizedText = stringBuilder.toString();
                        recognizedText = recognizedText.trim();
                        recognizedText = recognizedText.replaceAll("\\s+", " ");
                        recognizedText = recognizedText.substring(0,1).toUpperCase() + recognizedText.substring(1);
                        recognizedText = recognizedText.trim() + ".";
                        date = dateFormat.format(currentDate);
                        time = timeFormat.format(currentDate);
                        answer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sendata(recognizedText, date, time);
                                answer.setBackgroundColor(Color.RED);
                                answer.setText("Processing...");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.main_activity2, menu);*/
        getMenuInflater().inflate(R.menu.refresh, menu);
        getMenuInflater().inflate(R.menu.microphone, menu);
        getMenuInflater().inflate(R.menu.history, menu);
        getMenuInflater().inflate(R.menu.logout, menu);

        //Refresh btn

        MenuItem refresh = menu.findItem(R.id.refresh_btn);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                recreate();
                Toast.makeText(camera.this, "Refresh", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //microphone btn

        MenuItem microphone = menu.findItem(R.id.microphone_btn);
        microphone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(camera.this, microphone.class);
                startActivity(i);
                return false;
            }
        });

        //history btn

        MenuItem history = menu.findItem(R.id.history_btn);
        history.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(camera.this, history.class);
                startActivity(i);
                return false;
            }
        });

        //logout btn

        MenuItem logout = menu.findItem(R.id.logout_btn);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(camera.this, MainActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                return false;
            }
        });
        return true;
    }

    public void sendata(String message, String date, String time){

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
            myString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "http://192.168.184.122:8000/app/testview/"; // <----enter your post url here
        StringRequest MyStringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //This code is executed if the server responds, whether or not the response contains data.
                        //The String 'response' contains the server's response.

                        if (response.equals("\"Done\"")){
                            returndata();
                        }else if(response.isEmpty()){
                            System.out.println("String is Empty");
                        }else{
                            System.out.println(false + "error");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(camera.this);
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
                MyData.put("name", myString);
                MyData.put("message", message);
                MyData.put("date", date);
                MyData.put("time", time);
                return MyData;
            }
            // set timeout value
            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                );
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    public void returndata(){

        RequestQueue MyRequestQueue1 = Volley.newRequestQueue(this);
        String url1 = "http://192.168.184.122:8000/app/testviewdata/"; // <----enter your post url here
        JsonObjectRequest MyStringRequest1 = new JsonObjectRequest(
                Request.Method.GET,
                url1,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //This code is executed if the server responds, whether or not the response contains data.
                        //The String 'response' contains the server's response.

                        answer.setBackgroundColor(Color.GREEN);
                        answer.setText("Available");
                        answer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    JSONArray dataArray = response.getJSONArray("chatgptreply");

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject dataObj = dataArray.getJSONObject(i);
                                        String message = dataObj.getString("message");
                                        String reply = dataObj.getString("reply");
                                        String links = dataObj.getString("links");
                                        Intent intent = new Intent(camera.this, cans.class);
                                        intent.putExtra("message", message);
                                        intent.putExtra("reply", reply);
                                        intent.putExtra("links", links);
                                        startActivity(intent);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        /*editText.setText(response);
                        speak(response);*/

                    }
                }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        MyRequestQueue1.add(MyStringRequest1);

    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceID");
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate the scale factor to fit the image inside the max width and max height
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Create a new bitmap with the scaled dimensions
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Recycle the old bitmap to free up memory
        bitmap.recycle();

        return scaledBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        textRecognizer.release();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

}








/*public class camera extends AppCompatActivity {

    private AdjustableBoxImageView adjustableBoxImageView;
    private static final int PICK_IMAGE = 100;

    ImageView imageView;
    Button button, cbutton;
    Uri imageUri;
    TextView textView, edittext;
    TextRecognizer textRecognizer;
    private TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        *//*imageView = findViewById(R.id.image_view);*//*
        button = findViewById(R.id.select_button);
        *//*textView = findViewById(R.id.text_view);*//*
        edittext = findViewById(R.id.text_viewA);
        cbutton = findViewById(R.id.select_buttonC);

        adjustableBoxImageView = findViewById(R.id.adjustable_box_image_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(camera.this, ccamera.class);
                startActivity(i);
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        *//*getMenuInflater().inflate(R.menu.main_activity2, menu);*//*
        getMenuInflater().inflate(R.menu.refresh, menu);
        getMenuInflater().inflate(R.menu.microphone, menu);
        getMenuInflater().inflate(R.menu.history, menu);
        getMenuInflater().inflate(R.menu.logout, menu);

        //Refresh btn

        MenuItem refresh = menu.findItem(R.id.refresh_btn);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                recreate();
                Toast.makeText(camera.this, "Refresh", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //microphone btn

        MenuItem microphone = menu.findItem(R.id.microphone_btn);
        microphone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(camera.this, microphone.class);
                startActivity(i);
                return false;
            }
        });

        //history btn

        MenuItem history = menu.findItem(R.id.history_btn);
        history.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(camera.this, history.class);
                startActivity(i);
                return false;
            }
        });

        //logout btn

        MenuItem logout = menu.findItem(R.id.logout_btn);
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(camera.this, MainActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                return false;
            }
        });
        return true;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = scaleBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri), 800, 800);
                *//*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);*//*
                *//*imageView.setImageBitmap(bitmap);*//*
                adjustableBoxImageView.setImageBitmap(bitmap);

                if (textRecognizer.isOperational()) {
                    Frame imageFrame = new Frame.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < textBlocks.size(); i++) {
                        TextBlock textBlock = textBlocks.valueAt(i);
                        stringBuilder.append(textBlock.getValue());
                        stringBuilder.append(" ");
                    }
                    String recognizedText = stringBuilder.toString();
                    recognizedText = recognizedText.trim();
                    recognizedText = recognizedText.replaceAll("\\s+", " ");
                    recognizedText = recognizedText.substring(0,1).toUpperCase() + recognizedText.substring(1);
                    recognizedText = recognizedText.trim() + ".";
                    *//*textView.setText(recognizedText)*//*;
                    sendata(recognizedText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendata(String message){

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

            RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
            String url = "http://192.168.184.122:8000/app/testview/"; // <----enter your post url here
            StringRequest MyStringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //This code is executed if the server responds, whether or not the response contains data.
                            //The String 'response' contains the server's response.

                            edittext.setText(response);
                            speak(response);

                        }
                    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    MyData.put("name", myString);
                    MyData.put("message", message);
                    return MyData;
                }
            };
            MyRequestQueue.add(MyStringRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate the scale factor to fit the image inside the max width and max height
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Create a new bitmap with the scaled dimensions
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Recycle the old bitmap to free up memory
        bitmap.recycle();

        return scaledBitmap;
    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceID");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        textRecognizer.release();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}*/

//Gallery Image
/*public class camera extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;

    ImageView imageView;
    Button button;
    Uri imageUri;
    TextView textView;
    TextRecognizer textRecognizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        imageView = findViewById(R.id.image_view);
        button = findViewById(R.id.select_button);
        textView = findViewById(R.id.text_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = scaleBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri), 800, 800);
                *//*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);*//*
                imageView.setImageBitmap(bitmap);

                if (textRecognizer.isOperational()) {
                    Frame imageFrame = new Frame.Builder()
                            .setBitmap(bitmap)
                            .build();
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < textBlocks.size(); i++) {
                        TextBlock textBlock = textBlocks.valueAt(i);
                        stringBuilder.append(textBlock.getValue());
                        stringBuilder.append("\n");
                    }
                    String recognizedText = stringBuilder.toString();
                    textView.setText(recognizedText);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate the scale factor to fit the image inside the max width and max height
        float scaleFactor = Math.min((float) maxWidth / width, (float) maxHeight / height);

        // Create a new bitmap with the scaled dimensions
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Recycle the old bitmap to free up memory
        bitmap.recycle();

        return scaledBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        textRecognizer.release();
    }
}*/


// Live Camera
/*
public class camera extends AppCompatActivity {

    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);

        startCameraSource();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(camera.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}
*/
