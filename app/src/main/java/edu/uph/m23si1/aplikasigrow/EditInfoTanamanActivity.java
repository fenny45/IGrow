package edu.uph.m23si1.aplikasigrow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;

public class EditInfoTanamanActivity extends AppCompatActivity {

    private MaterialCardView btnBack, btnBatal, btnSimpan;
    private TextView tvLabelNama, tvLabelTipe, tvLabelSpesies;
    private EditText etNamaTanaman, etSpesies, etUmurTanaman, etLokasiTanam;
    private EditText etJadwalPenyiraman, etTahapPertumbuhan, etCatatanTanaman;
    private Spinner spinnerTempat, spinnerKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_tanaman);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        inisialisasiViews();
        setupSpinners();
        loadDataTerdahulu();
        setupAksiTombol();
    }

    // --- FUNGSI KUNCI LEMARI TANAMAN UNIK (SANGAT PENTING) ---
    private SharedPreferences getTanamanPrefs() {
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String userEmail = loginSession.getString("email", "default");
        // Nama file unik: iGrowTanamanPrefs_user_email_com
        String fileName = "iGrowTanamanPrefs_" + userEmail.replace(".", "_");
        return getSharedPreferences(fileName, MODE_PRIVATE);
    }

    private void inisialisasiViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBatal = findViewById(R.id.btnBatal);
        btnSimpan = findViewById(R.id.btnSimpan);

        tvLabelNama = findViewById(R.id.tvLabelNama);
        tvLabelTipe = findViewById(R.id.tvLabelTipe);
        tvLabelSpesies = findViewById(R.id.tvLabelSpesies);

        etNamaTanaman = findViewById(R.id.etNamaTanaman);
        etSpesies = findViewById(R.id.etSpesies);
        spinnerTempat = findViewById(R.id.spinnerTempat);
        etUmurTanaman = findViewById(R.id.etUmurTanaman);
        etLokasiTanam = findViewById(R.id.etLokasiTanam);
        etJadwalPenyiraman = findViewById(R.id.etJadwalPenyiraman);
        etTahapPertumbuhan = findViewById(R.id.etTahapPertumbuhan);
        spinnerKategori = findViewById(R.id.spinnerKategori);
        etCatatanTanaman = findViewById(R.id.etCatatanTanaman);
    }

    private void setupSpinners() {
        String[] pilihanTempat = {"Outdoor", "Indoor", "Greenhouse"};
        ArrayAdapter<String> adapterTempat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pilihanTempat);
        spinnerTempat.setAdapter(adapterTempat);

        String[] pilihanKategori = {"Sayur", "Buah", "Hias", "Obat"};
        ArrayAdapter<String> adapterKategori = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pilihanKategori);
        spinnerKategori.setAdapter(adapterKategori);
    }

    private void loadDataTerdahulu() {
        // Mengambil dari lemari unik milik user yang sedang login
        SharedPreferences prefs = getTanamanPrefs();

        // Menggunakan Nilai Default yang umum untuk User Baru
        String nama = prefs.getString("nama_tanaman", "Belum Ada Tanaman");
        String spesies = prefs.getString("spesies", "-");
        String tempat = prefs.getString("tipe_lingkungan", "Outdoor");
        String kategori = prefs.getString("kategori", "Sayur");

        // Update Label Pratinjau
        tvLabelNama.setText(nama);
        tvLabelSpesies.setText(spesies);
        tvLabelTipe.setText(tempat);

        // Update Form Input
        etNamaTanaman.setText(nama);
        etSpesies.setText(spesies);
        etUmurTanaman.setText(prefs.getString("umur", "-"));
        etLokasiTanam.setText(prefs.getString("lokasi", "-"));
        etJadwalPenyiraman.setText(prefs.getString("jadwal", "-"));
        etTahapPertumbuhan.setText(prefs.getString("tahap", "-"));
        etCatatanTanaman.setText(prefs.getString("catatan", "Silahkan isi informasi tanaman Anda."));

        // Atur Spinner Posisi
        if (tempat.equals("Indoor")) spinnerTempat.setSelection(1);
        else if (tempat.equals("Greenhouse")) spinnerTempat.setSelection(2);
        else spinnerTempat.setSelection(0);

        if (kategori.equals("Buah")) spinnerKategori.setSelection(1);
        else if (kategori.equals("Hias")) spinnerKategori.setSelection(2);
        else if (kategori.equals("Obat")) spinnerKategori.setSelection(3);
        else spinnerKategori.setSelection(0);
    }

    private void setupAksiTombol() {
        btnBack.setOnClickListener(v -> tutupHalaman());
        btnBatal.setOnClickListener(v -> tutupHalaman());

        btnSimpan.setOnClickListener(v -> {
            // Simpan kembali ke lemari unik user
            SharedPreferences.Editor editor = getTanamanPrefs().edit();

            editor.putString("nama_tanaman", etNamaTanaman.getText().toString());
            editor.putString("spesies", etSpesies.getText().toString());
            editor.putString("tipe_lingkungan", spinnerTempat.getSelectedItem().toString());
            editor.putString("umur", etUmurTanaman.getText().toString());
            editor.putString("lokasi", etLokasiTanam.getText().toString());
            editor.putString("jadwal", etJadwalPenyiraman.getText().toString());
            editor.putString("tahap", etTahapPertumbuhan.getText().toString());
            editor.putString("kategori", spinnerKategori.getSelectedItem().toString());
            editor.putString("catatan", etCatatanTanaman.getText().toString());

            editor.apply();

            Toast.makeText(this, "Informasi Tanaman berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            tutupHalaman();
        });
    }

    private void tutupHalaman() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}