package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, ivProfile;
    private MaterialCardView btnUploadFoto, btnBatal, btnSimpan;
    private EditText etNama, etEmail, etKontak, etUsia, etAlamat, etBio;
    private Spinner spinnerGender;

    private String uriFotoTersimpan = "";

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private final ActivityResultLauncher<Intent> ambilFotoGaleri = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        ivProfile.setImageURI(imageUri);
                        uriFotoTersimpan = imageUri.toString();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inisialisasiViews();
        setupSpinnerGender();
        loadDataDariFirebase();
        setupAksiTombol();
    }

    private void inisialisasiViews() {
        btnBack = findViewById(R.id.btnBack);
        ivProfile = findViewById(R.id.ivProfile);
        btnUploadFoto = findViewById(R.id.btnUploadFoto);

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);

        // --- KUNCI EMAIL AGAR TIDAK BISA DIEDIT (READ-ONLY) ---
        etEmail.setEnabled(false);
        etEmail.setFocusable(false);
        etEmail.setFocusableInTouchMode(false);
        etEmail.setCursorVisible(false);

        etKontak = findViewById(R.id.etKontak);
        spinnerGender = findViewById(R.id.spinnerGender);
        etUsia = findViewById(R.id.etUsia);
        etAlamat = findViewById(R.id.etAlamat);
        etBio = findViewById(R.id.etBio);

        btnBatal = findViewById(R.id.btnBatal);
        btnSimpan = findViewById(R.id.btnSimpan);
    }

    private void setupSpinnerGender() {
        String[] pilihanGender = {"Perempuan", "Laki-laki"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pilihanGender);
        spinnerGender.setAdapter(adapter);
    }

    private void loadDataDariFirebase() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etNama.setText(snapshot.child("nama").getValue(String.class));
                    etEmail.setText(snapshot.child("email").getValue(String.class));
                    etKontak.setText(snapshot.child("kontak").getValue(String.class));
                    etUsia.setText(snapshot.child("usia").getValue(String.class));
                    etAlamat.setText(snapshot.child("alamat").getValue(String.class));
                    etBio.setText(snapshot.child("bio").getValue(String.class));

                    String gender = snapshot.child("gender").getValue(String.class);
                    if (gender != null && gender.equals("Laki-laki")) spinnerGender.setSelection(1);
                    else spinnerGender.setSelection(0);

                    uriFotoTersimpan = snapshot.child("foto_profil").getValue(String.class);
                    if (uriFotoTersimpan != null && !uriFotoTersimpan.isEmpty()) {
                        ivProfile.setImageURI(Uri.parse(uriFotoTersimpan));
                    } else {
                        ivProfile.setImageResource(R.drawable.user); // Pastikan R.drawable.user ada
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupAksiTombol() {
        btnBack.setOnClickListener(v -> tutupHalaman());
        btnBatal.setOnClickListener(v -> tutupHalaman());

        btnUploadFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            ambilFotoGaleri.launch(intent);
        });

        btnSimpan.setOnClickListener(v -> simpanKeFirebase());
    }

    // --- LOGIKA SIMPAN SEDERHANA (TANPA GANTI EMAIL) ---
    private void simpanKeFirebase() {
        // Kumpulkan data profil (Email tidak perlu disimpan ulang karena tidak berubah)
        HashMap<String, Object> dataUpdate = new HashMap<>();
        dataUpdate.put("nama", etNama.getText().toString());
        dataUpdate.put("kontak", etKontak.getText().toString());
        dataUpdate.put("usia", etUsia.getText().toString());
        dataUpdate.put("alamat", etAlamat.getText().toString());
        dataUpdate.put("bio", etBio.getText().toString());
        dataUpdate.put("gender", spinnerGender.getSelectedItem().toString());
        dataUpdate.put("foto_profil", uriFotoTersimpan != null ? uriFotoTersimpan : "");

        btnSimpan.setEnabled(false);

        // Langsung lempar ke Database
        userRef.updateChildren(dataUpdate).addOnCompleteListener(dbTask -> {
            if (dbTask.isSuccessful()) {
                Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                tutupHalaman();
            } else {
                btnSimpan.setEnabled(true);
                Toast.makeText(this, "Gagal menyimpan profil", Toast.LENGTH_SHORT).show();
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