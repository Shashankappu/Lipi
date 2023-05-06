package com.shashanksp.lipi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;

import com.google.mlkit.nl.translate.Translator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;



import java.io.IOException;

public class ShowActivity extends AppCompatActivity {
    private Uri imageUri;
    ImageView mImageView;
    TextView result_tv;
    Spinner langBtn;
    private String to;
    Bitmap bitmap;
    String translated_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        result_tv = findViewById(R.id.result_tv);
        langBtn =  findViewById(R.id.language_btn);
        result_tv.setText(R.string.kannada_lorem);
        String[] lang  = new String[]{"Kan","En","Hin","Ta","Te"};

        //Getting image from Home Actvity
        mImageView = findViewById(R.id.preview_IV);
        String imageUriString = getIntent().getStringExtra("img_uri");
        imageUri = Uri.parse(imageUriString);

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Log.d("setting image","Image set Successfully");
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        //getting text from image
//        getTextFromImage(bitmap);
//        Toast.makeText(this,result_tv.getText(),Toast.LENGTH_LONG).show();




        // for Spinner implementation
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,lang);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langBtn.setAdapter(adapter);

        //Spinner Handling Clicks
        langBtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
                String selectedItem = (String) parent.getItemAtPosition(position);
                to = getLanguageCode(selectedItem);
                result_tv.setText(R.string.kannada_lorem);
                translateText(to,result_tv.getText().toString());
                Toast.makeText(ShowActivity.this,"Language changed to "+to,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });

   }

    //To get the language Code for Translation
    public String getLanguageCode(String langcode){
        switch(langcode){
            case "En" : return TranslateLanguage.ENGLISH;

            case "Kan":return TranslateLanguage.KANNADA;

            case "Hin":return TranslateLanguage.HINDI;

            case "Ta":return TranslateLanguage.TAMIL;

            case "Te": return TranslateLanguage.TELUGU;

            default: return TranslateLanguage.KANNADA;
        }
    }

    //Translating the Text to any Languages
    private void translateText(String tocode, String prev_text){

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.KANNADA)
                .setTargetLanguage(tocode)
                .build();
        Translator translator =  Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translator.translate(prev_text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translated_text = s;
                        result_tv.setText(translated_text);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowActivity.this,"Translation Error!",Toast.LENGTH_LONG).show();
                    }
                }
                );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowActivity.this,"Failed to Download Language Model",Toast.LENGTH_LONG).show();
            }
        });
    }

    // for handling Back Press on the activity
    @Override
    public void onBackPressed() {
        // Create an explicit Intent for the desired Activity
        Intent intent = new Intent(this, MainActivity.class);
        imageUri = null;
        startActivity(intent);
        finish();
    }



}