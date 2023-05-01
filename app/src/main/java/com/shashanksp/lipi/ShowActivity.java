package com.shashanksp.lipi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class ShowActivity extends AppCompatActivity {
    AutoCompleteTextView lang_tv;
    private Uri imageUri;
    ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        lang_tv = findViewById(R.id.lang_btn);
        String[] lang  = new String[]{"En","Kan","Hin","Ta","Te"};
        ArrayAdapter<String> lang_adapter = new ArrayAdapter<>(this,R.layout.customdropdown,lang);
        lang_tv.setAdapter(lang_adapter);

        mImageView = findViewById(R.id.preview_IV);
        String imageUriString = getIntent().getStringExtra("img_uri");
        imageUri = Uri.parse(imageUriString);



        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lang_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ShowActivity.this,"Language changed to "+lang_tv.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        // Create an explicit Intent for the desired Activity
        Intent intent = new Intent(this, MainActivity.class);
        imageUri = null;
        startActivity(intent);

        // Call finish() to close the current Activity and remove it from the back stack
        finish();
    }
}