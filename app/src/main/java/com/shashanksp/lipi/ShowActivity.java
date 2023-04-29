package com.shashanksp.lipi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class ShowActivity extends AppCompatActivity {
    AutoCompleteTextView lang_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        lang_tv = findViewById(R.id.lang_btn);
        String[] lang  = new String[]{"En","Kan","Hin","Ta","Te"};
        ArrayAdapter<String> lang_adapter = new ArrayAdapter<>(this,R.layout.customdropdown,lang);
        lang_tv.setAdapter(lang_adapter);

        lang_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ShowActivity.this,"Language changed to "+lang_tv.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }
}