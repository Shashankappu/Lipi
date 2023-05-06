package com.shashanksp.lipi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.Task;
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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;


import java.io.IOException;

public class ShowActivity extends AppCompatActivity {
    private Uri imageUri;
    ImageView mImageView;
    TextView result_tv;
    Spinner langBtn;
    private String to,from;
    Bitmap bitmap;
    String translated_text;
    InputImage inputImage;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        result_tv = findViewById(R.id.result_tv);
        langBtn = findViewById(R.id.language_btn);
        String[] lang = new String[]{"Hin","En","Kan","Ta","Te"};

        //Getting image from Home Activity
        mImageView = findViewById(R.id.preview_IV);
        String imageUriString = getIntent().getStringExtra("img_uri");
        imageUri = Uri.parse(imageUriString);

        textRecognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            inputImage = InputImage.fromFilePath(this, imageUri);
            Log.d("setting image", "Image set Successfully");
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //getting text from image
        getTextFromImage(inputImage);

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
                String cur_text = result_tv.getText().toString();
                to = getLanguageCode(selectedItem);
                translateText(to,cur_text);
                Toast.makeText(ShowActivity.this, "Language changed to " + to, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });

    }

    private void getTextFromImage(InputImage inputImage) {
        Task<Text> extracted_text = textRecognizer.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String recognizeText = text.getText();
                        result_tv.setText(recognizeText);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowActivity.this, "Recognition Error!" + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    //To get the language Code for Translation
    public String getLanguageCode(String langcode) {
        switch (langcode) {
            case "En":
                return TranslateLanguage.ENGLISH;

            case "Kan":
                return TranslateLanguage.KANNADA;

            case "Hin":
                return TranslateLanguage.HINDI;

            case "Ta":
                return TranslateLanguage.TAMIL;

            case "Te":
                return TranslateLanguage.TELUGU;

            default:
                return TranslateLanguage.ENGLISH;
        }
    }

    //Translating the Text to any Languages
    private void translateText(String tocode, String cur_text) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.HINDI)
                .setTargetLanguage(tocode)
                .build();
        Translator translator = Translation.getClient(options);
        getLifecycle().addObserver(translator);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translator.translate(cur_text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translated_text = s;
                        result_tv.setText(translated_text);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowActivity.this, "Translation Error!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowActivity.this, "Failed to Download Language Model", Toast.LENGTH_LONG).show();
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

//    public void getLanguage(String cur_text) {
//
//        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
//        languageIdentifier.identifyPossibleLanguages(cur_text)
//                .addOnSuccessListener(new OnSuccessListener<List<IdentifiedLanguage>>() {
//                    @Override
//                    public void onSuccess(List<IdentifiedLanguage> identifiedLanguages) {
//                        for (IdentifiedLanguage identifiedLanguage : identifiedLanguages) {
//                            String language = identifiedLanguage.getLanguageTag();
//                            float confidence = identifiedLanguage.getConfidence();
//                            Log.i("lang detection", language + " (" + confidence + ")");
//                        }
//                    }
//                })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//        }


}