package com.shashanksp.lipi;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.io.IOException;

import developers.mobile.abt.FirebaseAbt;

public class ShowActivity extends AppCompatActivity {
    AutoCompleteTextView lang_btn;
    private Uri imageUri;
    ImageView mImageView;
    TextView result_tv;
    private int from,to = 0;
    String translated_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        lang_btn = findViewById(R.id.lang_btn);
        result_tv = findViewById(R.id.result_tv);
        result_tv.setText(R.string.loremipsum);
        String[] lang  = new String[]{"En","Kan","Hin","Ta","Te"};
       // String[] all_lang  = new String[]{"English","Kannada","Hindi","Tamil","Telugu"};


        ArrayAdapter<String> lang_adapter = new ArrayAdapter<>(this,R.layout.customdropdown,lang);
        lang_btn.setAdapter(lang_adapter);

        mImageView = findViewById(R.id.preview_IV);
        String imageUriString = getIntent().getStringExtra("img_uri");
        imageUri = Uri.parse(imageUriString);



        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        from = getLanguageCode(lang_btn.getText().toString());
        lang_btn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                to = getLanguageCode(lang_btn.getText().toString());
                translateText(from,to,result_tv.getText().toString());

                Toast.makeText(ShowActivity.this,"Language changed to "+lang_btn.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        // Create an explicit Intent for the desired Activity
        Intent intent = new Intent(this, MainActivity.class);
        imageUri = null;
        startActivity(intent);
        finish();
    }
    public int getLanguageCode(String langcode){
        switch(langcode){
            case "En" : return FirebaseTranslateLanguage.EN;

            case "Kan":return FirebaseTranslateLanguage.KN;

            case "Hin":return FirebaseTranslateLanguage.HI;

            case "Ta":return FirebaseTranslateLanguage.TA;

            case "Te": return FirebaseTranslateLanguage.TE;

            default: return 0;
        }
    }
    private void translateText(int fromcode,int tocode,String prev_text){
//        translated_text = "Translating...";
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromcode)
                .setTargetLanguage(tocode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
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

}