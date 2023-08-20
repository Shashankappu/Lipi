package com.shashanksp.lipi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private Button gallery_btn, camera_btn;

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102 ;
    private static final int GALLERY_REQUEST_CODE = 103 ;
    Intent shw_img_intent,showIntent;
    private Uri mImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gallery_btn = findViewById(R.id.gallery_btn);
        camera_btn = findViewById(R.id.camera_btn);
//        know_btn = findViewById(R.id.know_btn);
//        //mImageView = findViewById(R.id.photo_IV);


        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK);
                galleryintent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryintent,GALLERY_REQUEST_CODE);
            }

        });
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });

//        know_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this,"Please choose any one of the option below",Toast.LENGTH_LONG).show();
//            }
//        });

    }
    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_PERM_CODE);
        }else{
            openCamera();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
            }else{
                Toast.makeText(MainActivity.this,"Camera Permission is required!!",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                Bitmap image = (Bitmap) data.getExtras().get("data");

                showIntent = new Intent(MainActivity.this, ShowActivity.class);

                // Pass the captured image URI as an extra
                Uri imageUri = getImageUri(this, image);
                showIntent.putExtra("img_uri", imageUri.toString());
                // Start the next activity
                startActivity(showIntent);
                finish();
            }
        }
        else if(requestCode == GALLERY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                mImageUri = data.getData();
                shw_img_intent = new Intent(MainActivity.this,ShowActivity.class);
                shw_img_intent.putExtra("img_uri",mImageUri.toString());
                if(mImageUri!=null){
                    startActivity(shw_img_intent);
                    finish();
                }else{
                    Toast.makeText(MainActivity.this,"Please choose an image to continue",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
    }
    private Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }
}