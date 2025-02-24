package com.example.chatapiproject;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class subhistory extends AppCompatActivity {

    String reply0, links, message;
    TextView textreply, textlinks;
    private TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_history);

        Intent intent = getIntent();
        message = intent.getStringExtra("message");
        reply0 = intent.getStringExtra("reply");
        links = intent.getStringExtra("links");

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // set language for text-to-speech
                    tts.setLanguage(Locale.US);

                }
            }
        });

        textreply = findViewById(R.id.shans);
        textlinks = findViewById(R.id.shansl);
        textreply.setText(reply0);
        textlinks.setText(links);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.main_activity2, menu);*/
        getMenuInflater().inflate(R.menu.home, menu);
        getMenuInflater().inflate(R.menu.speak, menu);
        getMenuInflater().inflate(R.menu.pdf, menu);

        //Refresh btn

        MenuItem speakbtn = menu.findItem(R.id.speak_btn);
        speakbtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                speak(textreply.getText().toString());
                return false;
            }
        });


        //Home btn

        MenuItem home = menu.findItem(R.id.home_btn);
        home.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(subhistory.this, microphone.class);
                startActivity(i);
                return false;
            }
        });

        //pdf btn

        MenuItem pdf = menu.findItem(R.id.pdf_btn);
        pdf.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AlertDialog.Builder builder = new AlertDialog.Builder(subhistory.this);
                builder.setTitle("Enter File Name");

                // Set up the input
                final EditText input = new EditText(subhistory.this);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        saveAsPdf(text, reply0, links);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });
        return true;
    }

    private void saveAsPdf(String fileName, String data1, String data2) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

// Set up the paint for drawing text
        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);

// Set up the text layout parameters
        float x = 50;
        float y = 50;
        int maxWidth = pageInfo.getPageWidth() - 100; // leave some margins
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        float spacingMultiplier = 1.0f;
        float spacingAddition = 0.0f;
        boolean includePadding = false;

// Draw the first input string
        StaticLayout staticLayout1 = new StaticLayout(data1, paint, maxWidth, alignment,
                spacingMultiplier, spacingAddition, includePadding);
        if (staticLayout1.getHeight() > pageInfo.getPageHeight() - y) {
            document.finishPage(page);
            page = document.startPage(pageInfo);
            y = 50;
        }
        page.getCanvas().save();
        page.getCanvas().translate(x, y);
        staticLayout1.draw(page.getCanvas());
        page.getCanvas().restore();

// Move down the y-coordinate for the next block of text
        y += staticLayout1.getHeight() + 20;

// Draw the second input string
        StaticLayout staticLayout2 = new StaticLayout(data2, paint, maxWidth, alignment,
                spacingMultiplier, spacingAddition, includePadding);
        if (staticLayout2.getHeight() > pageInfo.getPageHeight() - y) {
            document.finishPage(page);
            page = document.startPage(pageInfo);
            y = 50;
        }
        page.getCanvas().save();
        page.getCanvas().translate(x, y);
        staticLayout2.draw(page.getCanvas());
        page.getCanvas().restore();

// Finish the PDF page
        document.finishPage(page);



        // Get the content resolver
        ContentResolver resolver = getContentResolver();

        // Define the file metadata
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        // Insert the file into the MediaStore database
        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        }

        // Write the PDF file to the OutputStream obtained from the ContentResolver
        try (OutputStream outputStream = resolver.openOutputStream(uri)) {
            document.writeTo(outputStream);
            Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show();

            // Show the saved PDF in the Android navigation bar
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(uri, "application/pdf");
            viewIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(viewIntent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
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
