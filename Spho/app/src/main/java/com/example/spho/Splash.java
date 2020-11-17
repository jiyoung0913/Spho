package com.example.spho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    handler = new Handler();
    runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(Splash.this,LoginActivity.class));
            finish();
        }
    };
    handler.postDelayed(runnable,3000);

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}