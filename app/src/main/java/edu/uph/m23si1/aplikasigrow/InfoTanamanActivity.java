package edu.uph.m23si1.aplikasigrow; // WAJIB GANTI SESUAI PACKAGE KAMU

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoTanamanActivity extends AppCompatActivity {

    private MaterialCardView btnBack, btnEditTanaman;
    private ImageView imgTanaman;

    private TextView tvTipeLingkungan, tvSpesies, tvNamaTanaman;
    private TextView tvUmurTanaman, tvLokasiTanam, tvJadwalPenyiraman;
    private TextView tvTahapPertumbuhan, tvKategoriTanaman, tvCatatanTanaman;

    // --- DEKLARASI FIRESTORE ---
    private FirebaseAuth mAuth;
    private DocumentReference tanamanRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tanaman);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Membaca dari struktur FIRESTORE yang benar
            tanamanRef = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUser.getUid())
                    .collection("Tanaman")
                    .document("Info");
        }

        inisialisasiViews();
        setupAksiTombol();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataTanamanFirestore();
    }

    private void inisialisasiViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEditTanaman = findViewById(R.id.btnEditTanaman);
        imgTanaman = findViewById(R.id.imgTanaman);

        tvTipeLingkungan = findViewById(R.id.tvTipeLingkungan);
        tvSpesies = findViewById(R.id.tvSpesies);
        tvNamaTanaman = findViewById(R.id.tvNamaTanaman);

        tvUmurTanaman = findViewById(R.id.tvUmurTanaman);
        tvLokasiTanam = findViewById(R.id.tvLokasiTanam);
        tvJadwalPenyiraman = findViewById(R.id.tvJadwalPenyiraman);
        tvTahapPertumbuhan = findViewById(R.id.tvTahapPertumbuhan);
        tvKategoriTanaman = findViewById(R.id.tvKategoriTanaman);
        tvCatatanTanaman = findViewById(R.id.tvCatatanTanaman);
    }

    private void loadDataTanamanFirestore() {
        if (tanamanRef == null) return;

        // addSnapshotListener digunakan agar data update secara realtime otomatis
        tanamanRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) return;

            if (snapshot != null && snapshot.exists()) {
                tvNamaTanaman.setText(getTeks(snapshot.getString("nama_tanaman"), "Belum Ada Tanaman"));
                tvSpesies.setText(getTeks(snapshot.getString("spesies"), "-"));
                tvTipeLingkungan.setText(getTeks(snapshot.getString("tipe_lingkungan"), "-"));
                tvUmurTanaman.setText(getTeks(snapshot.getString("umur"), "-"));
                tvLokasiTanam.setText(getTeks(snapshot.getString("lokasi"), "-"));
                tvJadwalPenyiraman.setText(getTeks(snapshot.getString("jadwal"), "-"));
                tvTahapPertumbuhan.setText(getTeks(snapshot.getString("tahap"), "-"));
                tvKategoriTanaman.setText(getTeks(snapshot.getString("kategori"), "-"));
                tvCatatanTanaman.setText(getTeks(snapshot.getString("catatan"), "Silahkan klik tombol edit untuk mengisi data."));
            } else {
                tvNamaTanaman.setText("Belum Ada Tanaman");
                tvSpesies.setText("-");
                tvTipeLingkungan.setText("-");
                tvUmurTanaman.setText("-");
                tvLokasiTanam.setText("-");
                tvJadwalPenyiraman.setText("-");
                tvTahapPertumbuhan.setText("-");
                tvKategoriTanaman.setText("-");
                tvCatatanTanaman.setText("Silahkan klik tombol edit untuk mengisi data.");
            }
            imgTanaman.setImageResource(R.drawable.ladang); // Default image
        });
    }

    // Fungsi pembantu khusus Firestore untuk menghindari error null
    private String getTeks(String val, String nilaiDefault) {
        return (val != null && !val.isEmpty()) ? val : nilaiDefault;
    }

    private void setupAksiTombol() {
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnEditTanaman.setOnClickListener(v -> {
            Intent intent = new Intent(InfoTanamanActivity.this, EditInfoTanamanActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}