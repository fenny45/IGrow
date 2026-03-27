package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;

public class ProfilActivity extends AppCompatActivity {

    // Navigasi & Badge Footer
    private LinearLayout navBeranda, navGrafik, navNotifikasi, navProfil;
    private MaterialCardView tvBadgeFooter;
    private TextView tvBadgeAngka;

    // Menu Profil & Tombol
    private ImageView btnEditProfileTop;
    private MaterialCardView btnInfoTanaman, btnPrivasi, btnBahasa, btnKeamanan, btnTentang, btnLogout;

    // Teks & Gambar Profil Dinamis
    private TextView tvNamaUser, tvEmailUser, tvBio;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        inisialisasiViews();
        setupMenuAksi();
        setupFooterNavigasi();
    }

    private SharedPreferences getPrefs() {
        // Ambil email user yang sedang login sekarang
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String userEmail = loginSession.getString("email", "default");

        // Nama file sekarang menjadi: iGrowPrefs_user_email_com
        String fileName = "iGrowPrefs_" + userEmail.replace(".", "_");
        return getSharedPreferences(fileName, MODE_PRIVATE);
    }

    // --- UPDATE DATA & BADGE SECARA REAL-TIME SAAT HALAMAN DIBUKA ---
    @Override
    protected void onResume() {
        super.onResume();
        loadDataProfil();
        updateBadgeNotifikasi();
    }

    private void inisialisasiViews() {
        navBeranda = findViewById(R.id.navBeranda);
        navGrafik = findViewById(R.id.navGrafik);
        navNotifikasi = findViewById(R.id.navNotifikasi);
        navProfil = findViewById(R.id.navProfil);
        tvBadgeFooter = findViewById(R.id.tvBadgeFooter);
        tvBadgeAngka = findViewById(R.id.tvBadgeAngka);

        btnEditProfileTop = findViewById(R.id.btnEditProfileTop);
        btnInfoTanaman = findViewById(R.id.btnInfoTanaman);
        btnPrivasi = findViewById(R.id.btnPrivasi);
        btnBahasa = findViewById(R.id.btnBahasa);
        btnKeamanan = findViewById(R.id.btnKeamanan);
        btnTentang = findViewById(R.id.btnTentang);
        btnLogout = findViewById(R.id.btnLogout);

        tvNamaUser = findViewById(R.id.tvNamaUser);
        tvEmailUser = findViewById(R.id.tvEmailUser);
        tvBio = findViewById(R.id.tvBio);
        ivProfile = findViewById(R.id.ivProfile);
    }

    private void updateBadgeNotifikasi() {
        int badgeGlobal = DataGlobal.getJumlahBelumDibaca();

        if (badgeGlobal > 0) {
            tvBadgeFooter.setVisibility(View.VISIBLE);
            tvBadgeAngka.setText(String.valueOf(badgeGlobal));
        } else {
            tvBadgeFooter.setVisibility(View.GONE);
        }
    }

    private void loadDataProfil() {
        SharedPreferences prefs = getPrefs(); // Menggunakan file unik user

        String nama = prefs.getString("nama", "User Baru");
        String email = prefs.getString("email", "Email belum diset");
        String bio = prefs.getString("bio", "Belum ada bio.");
        String fotoLink = prefs.getString("foto_profil", "");

        tvNamaUser.setText(nama);
        tvEmailUser.setText(email);
        tvBio.setText(bio);

        if (!fotoLink.isEmpty()) {
            ivProfile.setImageURI(android.net.Uri.parse(fotoLink));
        } else {
            ivProfile.setImageResource(R.drawable.user); // Pastikan ada R.drawable.user di folder drawable
        }
    }

    private void setupMenuAksi() {
        btnEditProfileTop.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        btnInfoTanaman.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, InfoTanamanActivity.class);
            startActivity(intent);
        });

        btnPrivasi.setOnClickListener(v -> Toast.makeText(ProfilActivity.this, "Fitur Privasi Akun belum tersedia", Toast.LENGTH_SHORT).show());
        btnBahasa.setOnClickListener(v -> Toast.makeText(ProfilActivity.this, "Fitur ganti bahasa belum tersedia", Toast.LENGTH_SHORT).show());
        btnKeamanan.setOnClickListener(v -> Toast.makeText(ProfilActivity.this, "Fitur Keamanan belum tersedia", Toast.LENGTH_SHORT).show());

        btnTentang.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, TentangAplikasiActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Hapus session login
            SharedPreferences session = getSharedPreferences("LoginSession", MODE_PRIVATE);
            session.edit().clear().apply();

            // --- RESET SEMUA MEMORI DATAGLOBAL ---
            // Agar jika ada user lain yang login di HP ini, datanya tidak tercampur
            DataGlobal.listNotifikasi.clear();
            DataGlobal.listRiwayatGlobal.clear();
            DataGlobal.isInitialized = false;

            DataGlobal.statusSuhuBahaya = false;
            DataGlobal.statusTanahBahaya = false;
            DataGlobal.statusAirBahaya = false;
            DataGlobal.statusTdsBahaya = false;
            DataGlobal.statusCahayaBahaya = false;

            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity(); // Menutup semua halaman di belakang layar
        });
    }

    private void setupFooterNavigasi() {
        navBeranda.setOnClickListener(v -> {
            startActivity(new Intent(ProfilActivity.this, DashboardActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        navGrafik.setOnClickListener(v -> {
            startActivity(new Intent(ProfilActivity.this, GrafikActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        navNotifikasi.setOnClickListener(v -> {
            startActivity(new Intent(ProfilActivity.this, NotifikasiActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}