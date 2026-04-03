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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnMasuk, btnGoogle, btnFacebook;
    private TextView tvLupaSandi, tvDaftar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        tvLupaSandi = findViewById(R.id.tvLupaSandi);
        tvDaftar = findViewById(R.id.tvDaftar);

        // Auto Login Cerdas Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
            return;
        }

        btnMasuk.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Lengkapi Email dan Password!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Memeriksa data ke Server...", Toast.LENGTH_SHORT).show();
            btnMasuk.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            btnMasuk.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Email atau Password salah!", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvDaftar.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        tvLupaSandi.setOnClickListener(v -> Toast.makeText(this, "Segera hadir", Toast.LENGTH_SHORT).show());
        btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Segera hadir", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Segera hadir", Toast.LENGTH_SHORT).show());
    }
}