package edu.uph.m23si1.aplikasigrow; // WAJIB GANTI SESUAI PACKAGE KAMU

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditInfoTanamanActivity extends AppCompatActivity {

    private MaterialCardView btnBack, btnBatal, btnSimpan;
    private TextView tvLabelNama, tvLabelTipe, tvLabelSpesies;
    private EditText etNamaTanaman, etSpesies, etUmurTanaman, etLokasiTanam;
    private EditText etJadwalPenyiraman, etTahapPertumbuhan, etCatatanTanaman;
    private Spinner spinnerTempat, spinnerKategori;

    // --- DEKLARASI FIRESTORE ---
    private FirebaseAuth mAuth;
    private DocumentReference tanamanRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_tanaman);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Mengarahkan penyimpanan ke Firebase Firestore
            tanamanRef = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(currentUser.getUid())
                    .collection("Tanaman")
                    .document("Info");
        }

        inisialisasiViews();
        setupSpinners();
        loadDataDariFirestore();
        setupAksiTombol();
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

    private void loadDataDariFirestore() {
        if (tanamanRef == null) return;

        tanamanRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String nama = snapshot.getString("nama_tanaman");
                String spesies = snapshot.getString("spesies");
                String tempat = snapshot.getString("tipe_lingkungan");
                String kategori = snapshot.getString("kategori");

                if (nama == null || nama.isEmpty()) nama = "Belum Ada Tanaman";
                if (spesies == null || spesies.isEmpty()) spesies = "-";
                if (tempat == null || tempat.isEmpty()) tempat = "Outdoor";
                if (kategori == null || kategori.isEmpty()) kategori = "Sayur";

                tvLabelNama.setText(nama);
                tvLabelSpesies.setText(spesies);
                tvLabelTipe.setText(tempat);

                etNamaTanaman.setText(nama.equals("Belum Ada Tanaman") ? "" : nama);
                etSpesies.setText(spesies.equals("-") ? "" : spesies);

                etUmurTanaman.setText(snapshot.getString("umur"));
                etLokasiTanam.setText(snapshot.getString("lokasi"));
                etJadwalPenyiraman.setText(snapshot.getString("jadwal"));
                etTahapPertumbuhan.setText(snapshot.getString("tahap"));

                String catatan = snapshot.getString("catatan");
                etCatatanTanaman.setText(catatan != null ? catatan : "Silahkan isi informasi tanaman Anda.");

                if (tempat.equals("Indoor")) spinnerTempat.setSelection(1);
                else if (tempat.equals("Greenhouse")) spinnerTempat.setSelection(2);
                else spinnerTempat.setSelection(0);

                if (kategori.equals("Buah")) spinnerKategori.setSelection(1);
                else if (kategori.equals("Hias")) spinnerKategori.setSelection(2);
                else if (kategori.equals("Obat")) spinnerKategori.setSelection(3);
                else spinnerKategori.setSelection(0);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAksiTombol() {
        btnBack.setOnClickListener(v -> tutupHalaman());
        btnBatal.setOnClickListener(v -> tutupHalaman());

        btnSimpan.setOnClickListener(v -> {
            if (tanamanRef != null) {
                // Kunci tombol agar tidak ditekan berkali-kali
                btnSimpan.setEnabled(false);
                Toast.makeText(this, "Menyimpan data ke server...", Toast.LENGTH_SHORT).show();

                Map<String, Object> data = new HashMap<>();
                data.put("nama_tanaman", etNamaTanaman.getText().toString());
                data.put("spesies", etSpesies.getText().toString());
                data.put("tipe_lingkungan", spinnerTempat.getSelectedItem().toString());
                data.put("umur", etUmurTanaman.getText().toString());
                data.put("lokasi", etLokasiTanam.getText().toString());
                data.put("jadwal", etJadwalPenyiraman.getText().toString());
                data.put("tahap", etTahapPertumbuhan.getText().toString());
                data.put("kategori", spinnerKategori.getSelectedItem().toString());
                data.put("catatan", etCatatanTanaman.getText().toString());

                // Proses menyimpan ke Firestore dengan respon Sukses & Gagal yang jelas
                tanamanRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            // JIKA BERHASIL TERSIMPAN DI SERVER
                            Toast.makeText(this, "Berhasil diperbarui!", Toast.LENGTH_SHORT).show();

                            // Memaksa pindah ke halaman Info Tanaman
                            Intent pindah = new Intent(EditInfoTanamanActivity.this, InfoTanamanActivity.class);
                            pindah.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(pindah);

                            // Tutup halaman edit
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // JIKA GAGAL TERSIMPAN (Koneksi buruk / Rules diblokir)
                            btnSimpan.setEnabled(true);
                            Toast.makeText(this, "ERROR Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });

            } else {
                Toast.makeText(this, "Sesi Firebase tidak valid, coba Login ulang.", Toast.LENGTH_LONG).show();
            }
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