package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    // --- DEKLARASI FIREBASE ---
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // --- INISIALISASI FIREBASE ---
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Cari folder "Users" lalu masuk ke folder dengan UID milik user yang sedang login
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        }

        inisialisasiViews();
        setupMenuAksi();
        setupFooterNavigasi();
    }

    // --- UPDATE DATA & BADGE SECARA REAL-TIME SAAT HALAMAN DIBUKA ---
    @Override
    protected void onResume() {
        super.onResume();
        loadDataDariFirebase(); // Panggil fungsi ambil data dari awan
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

    // --- LOGIKA BARU: TARIK DATA DARI FIREBASE REALTIME DATABASE ---
    private void loadDataDariFirebase() {
        if (userRef != null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Ambil data satu per satu dari Firebase
                        String nama = snapshot.child("nama").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String bio = snapshot.child("bio").getValue(String.class);
                        String fotoLink = snapshot.child("foto_profil").getValue(String.class);

                        // Tampilkan ke layar (Jika kosong, beri nilai default)
                        tvNamaUser.setText(nama != null ? nama : "User");
                        tvEmailUser.setText(email != null ? email : "Email");
                        tvBio.setText(bio != null ? bio : "Belum ada bio.");

                        if (fotoLink != null && !fotoLink.isEmpty()) {
                            ivProfile.setImageURI(android.net.Uri.parse(fotoLink));
                        } else {
                            ivProfile.setImageResource(R.drawable.user); // Pastikan ada R.drawable.user
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfilActivity.this, "Gagal mengambil profil: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
            // --- LOGOUT DARI FIREBASE ---
            if (mAuth != null) {
                mAuth.signOut();
            }

            // Hapus session login lokal untuk berjaga-jaga
            getSharedPreferences("LoginSession", MODE_PRIVATE).edit().clear().apply();

            // --- RESET SEMUA MEMORI DATAGLOBAL ---
            DataGlobal.listNotifikasi.clear();
            DataGlobal.listRiwayatGlobal.clear();
            DataGlobal.isInitialized = false;

            DataGlobal.statusSuhuBahaya = false;
            DataGlobal.statusTanahBahaya = false;
            DataGlobal.statusAirBahaya = false;
            DataGlobal.statusTdsBahaya = false;
          

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