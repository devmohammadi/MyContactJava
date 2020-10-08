package com.fmohammadi.mycontact.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fmohammadi.mycontact.R;

public class StartActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this , LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}