package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;

public class InfoTanamanActivity extends AppCompatActivity {

    // Tombol (MaterialCardView karena di XML pakai MaterialCardView untuk background bundarnya)
    private MaterialCardView btnBack, btnEditTanaman;

    // Gambar
    private ImageView imgTanaman;

    // Teks Informasi
    private TextView tvTipeLingkungan, tvSpesies, tvNamaTanaman;
    private TextView tvUmurTanaman, tvLokasiTanam, tvJadwalPenyiraman;
    private TextView tvTahapPertumbuhan, tvKategoriTanaman, tvCatatanTanaman;

    // Nama file memori HP khusus untuk data tanaman
    private static final String PREF_TANAMAN = "iGrowTanamanPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Paksa aplikasi ke mode terang
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tanaman);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        inisialisasiViews();
        setupAksiTombol();
    }

    // --- SANGAT PENTING: AGAR DATA AUTO-REFRESH SAAT KEMBALI DARI HALAMAN EDIT ---
    @Override
    protected void onResume() {
        super.onResume();
        loadDataTanaman();
    }

    // Ganti cara panggil SharedPreferences yang lama
// Dari: getSharedPreferences("iGrowPrefs", MODE_PRIVATE);
// Menjadi ini:

    private SharedPreferences getPrefs() {
        // Ambil email user yang sedang login sekarang (Misal dari FirebaseAuth atau Intent Login)
        // Kita gunakan email sebagai pembeda nama file agar unik per user
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String userEmail = loginSession.getString("email", "default");

        // Nama file sekarang menjadi: iGrowPrefs_user_email_com
        String fileName = "iGrowPrefs_" + userEmail.replace(".", "_");
        return getSharedPreferences(fileName, MODE_PRIVATE);
    }

    private SharedPreferences getTanamanPrefs() {
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String userEmail = loginSession.getString("email", "default");

        String fileName = "iGrowTanamanPrefs_" + userEmail.replace(".", "_");
        return getSharedPreferences(fileName, MODE_PRIVATE);
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

    // --- MENGAMBIL DATA DARI MEMORI HP ---
    private void loadDataTanaman() {
        // PERBAIKAN: Gunakan fungsi getTanamanPrefs() agar mengambil lemari unik user
        SharedPreferences prefs = getTanamanPrefs();

        // Nilai default untuk user yang baru mendaftar
        String nama = prefs.getString("nama_tanaman", "Belum Ada Tanaman");
        String spesies = prefs.getString("spesies", "-");
        String tempat = prefs.getString("tipe_lingkungan", "-");
        String umur = prefs.getString("umur", "-");
        String lokasi = prefs.getString("lokasi", "-");
        String jadwal = prefs.getString("jadwal", "-");
        String tahap = prefs.getString("tahap", "-");
        String kategori = prefs.getString("kategori", "-");
        String catatan = prefs.getString("catatan", "Silahkan klik tombol edit untuk mengisi data.");

        // Pasang ke TextView
        tvNamaTanaman.setText(nama);
        tvSpesies.setText(spesies);
        tvTipeLingkungan.setText(tempat);
        tvUmurTanaman.setText(umur);
        tvLokasiTanam.setText(lokasi);
        tvJadwalPenyiraman.setText(jadwal);
        tvTahapPertumbuhan.setText(tahap);
        tvKategoriTanaman.setText(kategori);
        tvCatatanTanaman.setText(catatan);

        // Gambar Ladang
        String uriFoto = prefs.getString("foto_tanaman", "");
        if (!uriFoto.isEmpty()) {
            imgTanaman.setImageURI(android.net.Uri.parse(uriFoto));
        } else {
            imgTanaman.setImageResource(R.drawable.ladang);
        }
    }

    private void setupAksiTombol() {
        // Tombol Back
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Tombol Edit Tanaman (Pindah ke Halaman Edit)
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