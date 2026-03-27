package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    // Waktu loading dalam milidetik (3000 ms = 3 detik)
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Menghilangkan ActionBar (opsional, agar full screen)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Handler untuk delay dan pindah ke halaman Login
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent untuk pindah dari SplashActivity ke LoginActivity
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);

                // Menutup SplashActivity agar tidak bisa dikembalikan dengan tombol Back
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}