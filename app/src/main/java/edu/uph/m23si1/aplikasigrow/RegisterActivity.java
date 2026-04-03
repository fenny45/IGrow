package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etPassword, etUlangiPassword;
    private MaterialButton btnDaftarSekarang, btnGoogle, btnFacebook;
    private TextView tvMasukDisini;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        // Kita buat folder khusus "Users" di Firebase Realtime Database
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUlangiPassword = findViewById(R.id.etUlangiPassword);
        btnDaftarSekarang = findViewById(R.id.btnDaftarSekarang);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        tvMasukDisini = findViewById(R.id.tvDaftar);

        btnDaftarSekarang.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String ulangiPassword = etUlangiPassword.getText().toString().trim();

            if (TextUtils.isEmpty(nama)) { etNama.setError("Nama tidak boleh kosong"); etNama.requestFocus(); return; }
            if (TextUtils.isEmpty(email)) { etEmail.setError("Email tidak boleh kosong"); etEmail.requestFocus(); return; }
            if (TextUtils.isEmpty(password) || password.length() < 6) { etPassword.setError("Password minimal 6 karakter!"); etPassword.requestFocus(); return; }
            if (!password.equals(ulangiPassword)) { etUlangiPassword.setError("Password tidak sama!"); etUlangiPassword.requestFocus(); return; }

            Toast.makeText(this, "Mendaftarkan ke Server...", Toast.LENGTH_SHORT).show();
            btnDaftarSekarang.setEnabled(false);

            // 1. Daftarkan Email ke Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();

                                // 2. Simpan Biodata ke Firebase Realtime Database
                                HashMap<String, String> biodataMap = new HashMap<>();
                                biodataMap.put("nama", nama);
                                biodataMap.put("email", email);
                                biodataMap.put("kontak", "");
                                biodataMap.put("usia", "");
                                biodataMap.put("alamat", "");
                                biodataMap.put("gender", "Perempuan");
                                biodataMap.put("bio", "Halo! Saya pengguna baru iGrow.");
                                biodataMap.put("foto_profil", "");

                                userRef.child(uid).setValue(biodataMap).addOnCompleteListener(dbTask -> {
                                    mAuth.signOut(); // Cegah auto-login
                                    Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil! Silakan Login", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                });
                            }
                        } else {
                            btnDaftarSekarang.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvMasukDisini.setOnClickListener(v -> finish());
        btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Segera hadir", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Segera hadir", Toast.LENGTH_SHORT).show());
    }
}