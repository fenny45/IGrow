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

public class RegisterActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etPassword, etUlangiPassword;
    private MaterialButton btnDaftarSekarang, btnGoogle, btnFacebook;
    private TextView tvMasukDisini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUlangiPassword = findViewById(R.id.etUlangiPassword);
        btnDaftarSekarang = findViewById(R.id.btnDaftarSekarang);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        tvMasukDisini = findViewById(R.id.tvDaftar);

        btnDaftarSekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String ulangiPassword = etUlangiPassword.getText().toString().trim();

                // 1. Validasi standar
                if (TextUtils.isEmpty(nama)) {
                    etNama.setError("Nama tidak boleh kosong");
                    etNama.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email tidak boleh kosong");
                    etEmail.requestFocus();
                    return;
                }
                if (!email.toLowerCase().endsWith("@gmail.com")) {
                    etEmail.setError("Email harus menggunakan domain @gmail.com");
                    etEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password tidak boleh kosong");
                    etPassword.requestFocus();
                    return;
                }
                if (!password.equals(ulangiPassword)) {
                    etUlangiPassword.setError("Password tidak sama!");
                    etUlangiPassword.requestFocus();
                    return;
                }

                // --- 2. LOGIKA MULTI-USER (SINKRON DENGAN LOGIN) ---

                // Membuat nama file unik berdasarkan email: iGrowPrefs_user_email_com
                String fileName = "iGrowPrefs_" + email.replace(".", "_");
                SharedPreferences userPrefs = getSharedPreferences(fileName, MODE_PRIVATE);
                SharedPreferences.Editor editor = userPrefs.edit();

                // Simpan data pendaftaran ke dalam lemari khusus email ini
                editor.putString("nama", nama);
                editor.putString("email", email);
                editor.putString("password", password);

                // Tambahkan data profil default agar tidak kosong saat login pertama kali
                editor.putString("bio", "Halo! Saya pengguna baru iGrow.");
                editor.putString("foto_profil", "");

                editor.apply();

                Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil! Silakan Login", Toast.LENGTH_SHORT).show();

                // Kembali ke halaman Login
                finish();
            }
        });

        tvMasukDisini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Fitur Pajangan
        btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Akun Google segera hadir", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Akun Facebook segera hadir", Toast.LENGTH_SHORT).show());
    }
}