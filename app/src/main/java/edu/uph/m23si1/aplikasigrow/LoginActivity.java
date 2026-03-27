package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnMasuk, btnGoogle, btnFacebook;
    private TextView tvLupaSandi, tvDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        tvLupaSandi = findViewById(R.id.tvLupaSandi);
        tvDaftar = findViewById(R.id.tvDaftar);

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = etEmail.getText().toString().trim();
                String inputPassword = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(inputEmail)) {
                    etEmail.setError("Email tidak boleh kosong");
                    etEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(inputPassword)) {
                    etPassword.setError("Password tidak boleh kosong");
                    etPassword.requestFocus();
                    return;
                }

                // --- BAGIAN LOGIKA MULTI-USER ---

                // 1. Kita cari file pendaftaran spesifik berdasarkan email yang diinput
                String fileName = "iGrowPrefs_" + inputEmail.replace(".", "_");
                SharedPreferences userPrefs = getSharedPreferences(fileName, MODE_PRIVATE);

                String registeredEmail = userPrefs.getString("email", "");
                String registeredPassword = userPrefs.getString("password", "");

                // 2. Cek apakah akun ini ada di memori HP
                if (registeredEmail.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Akun ini belum terdaftar di perangkat ini!", Toast.LENGTH_LONG).show();
                } else if (inputEmail.equals(registeredEmail) && inputPassword.equals(registeredPassword)) {

                    // --- POIN PENTING: SIMPAN SESI LOGIN ---
                    // Kita simpan email yang aktif sekarang ke file "LoginSession"
                    // Ini kunci agar Profil & InfoTanaman tahu lemari mana yang harus dibuka
                    SharedPreferences session = getSharedPreferences("LoginSession", MODE_PRIVATE);
                    session.edit().putString("email", inputEmail).apply();

                    Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email atau Password salah!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvLupaSandi.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Fitur Lupa Sandi segera hadir", Toast.LENGTH_SHORT).show());
        btnGoogle.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Akun Google segera hadir", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(LoginActivity.this, "Akun Facebook segera hadir", Toast.LENGTH_SHORT).show());
    }
}