package edu.uph.m23si1.aplikasigrow;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class TentangAplikasiActivity extends AppCompatActivity {

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Paksa aplikasi selalu di Mode Terang agar warna desain tidak kacau
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Sesuaikan nama R.layout dengan nama file XML milikmu
        setContentView(R.layout.activity_tentang_aplikasi);

        // Sembunyikan Action Bar bawaan Android yang berwarna biru/abu-abu di atas
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Hubungkan id tombol back dari XML
        btnBack = findViewById(R.id.btnBack);

        // Berikan aksi saat tombol back diklik
        btnBack.setOnClickListener(v -> {
            // Tutup halaman ini untuk kembali ke ProfilActivity
            finish();
            // Tambahkan animasi fade agar transisinya halus
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    // Tangani juga aksi saat pengguna menekan tombol "Back" fisik di HP mereka
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}