package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, ivProfile;
    private MaterialCardView btnUploadFoto, btnBatal, btnSimpan;
    private EditText etNama, etEmail, etKontak, etUsia, etAlamat, etBio;
    private Spinner spinnerGender;

    private String uriFotoTersimpan = "";
    private FirebaseAuth mAuth;

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

        inisialisasiViews();
        setupSpinnerGender();
        loadDataTerdahulu();
        setupAksiTombol();
    }

    private SharedPreferences getPrefs() {
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String userEmail = loginSession.getString("email", "default");
        String fileName = "iGrowPrefs_" + userEmail.replace(".", "_");
        return getSharedPreferences(fileName, MODE_PRIVATE);
    }

    private void inisialisasiViews() {
        btnBack = findViewById(R.id.btnBack);
        ivProfile = findViewById(R.id.ivProfile);
        btnUploadFoto = findViewById(R.id.btnUploadFoto);

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);

        // --- EMAIL SEKARANG DIBUKA KEMBALI AGAR BISA DIEDIT ---
        etEmail.setEnabled(true);
        etEmail.setFocusableInTouchMode(true);
        etEmail.setFocusable(true);

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

    private void loadDataTerdahulu() {
        SharedPreferences prefs = getPrefs();

        etNama.setText(prefs.getString("nama", "USER"));
        etKontak.setText(prefs.getString("kontak", ""));
        etUsia.setText(prefs.getString("usia", ""));
        etAlamat.setText(prefs.getString("alamat", ""));
        etBio.setText(prefs.getString("bio", "Halo! Saya pengguna baru iGrow."));
        etEmail.setText(prefs.getString("email", "User@gmail.com"));

        String genderTersimpan = prefs.getString("gender", "Perempuan");
        if (genderTersimpan.equals("Laki-laki")) spinnerGender.setSelection(1);
        else spinnerGender.setSelection(0);

        uriFotoTersimpan = prefs.getString("foto_profil", "");
        if (!uriFotoTersimpan.isEmpty()) {
            ivProfile.setImageURI(Uri.parse(uriFotoTersimpan));
        } else {
            ivProfile.setImageResource(R.drawable.user); // Pastikan R.drawable.user ada
        }
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

        btnSimpan.setOnClickListener(v -> {
            String emailYangDiinput = etEmail.getText().toString().trim();

            if (emailYangDiinput.isEmpty()) {
                etEmail.setError("Email tidak boleh kosong");
                etEmail.requestFocus();
                return;
            }

            simpanDataLokalCerdas(emailYangDiinput);
        });
    }

    // --- MIGRASI DATA JIKA EMAIL BERUBAH ---
    private void simpanDataLokalCerdas(String emailBaru) {
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String emailLama = loginSession.getString("email", "default");

        // Jika user mengganti emailnya
        if (!emailLama.equals(emailBaru)) {
            // 1. Panggil Lemari Lama
            String oldFileName = "iGrowPrefs_" + emailLama.replace(".", "_");
            SharedPreferences oldPrefs = getSharedPreferences(oldFileName, MODE_PRIVATE);

            // Ambil password lama agar tidak hilang! (Sangat Krusial)
            String passwordLama = oldPrefs.getString("password", "");

            // 2. Buat Lemari Baru
            String newFileName = "iGrowPrefs_" + emailBaru.replace(".", "_");
            SharedPreferences newPrefs = getSharedPreferences(newFileName, MODE_PRIVATE);
            SharedPreferences.Editor newEditor = newPrefs.edit();

            // 3. Pindahkan semua data ke lemari baru
            newEditor.putString("nama", etNama.getText().toString());
            newEditor.putString("email", emailBaru);
            newEditor.putString("password", passwordLama); // Masukkan password ke lemari baru
            newEditor.putString("kontak", etKontak.getText().toString());
            newEditor.putString("usia", etUsia.getText().toString());
            newEditor.putString("alamat", etAlamat.getText().toString());
            newEditor.putString("bio", etBio.getText().toString());
            newEditor.putString("gender", spinnerGender.getSelectedItem().toString());
            newEditor.putString("foto_profil", uriFotoTersimpan);
            newEditor.apply();

            // 4. Update Sesi Login agar aplikasi sadar emailnya sudah baru
            loginSession.edit().putString("email", emailBaru).apply();

            // 5. Hapus lemari lama (Opsional, agar memori HP tidak penuh)
            oldPrefs.edit().clear().apply();

        } else {
            // Jika user HANYA mengganti nama/bio dll (Email tetap sama)
            SharedPreferences.Editor editor = getPrefs().edit();
            editor.putString("nama", etNama.getText().toString());
            editor.putString("email", emailBaru);
            editor.putString("kontak", etKontak.getText().toString());
            editor.putString("usia", etUsia.getText().toString());
            editor.putString("alamat", etAlamat.getText().toString());
            editor.putString("bio", etBio.getText().toString());
            editor.putString("gender", spinnerGender.getSelectedItem().toString());
            editor.putString("foto_profil", uriFotoTersimpan);
            editor.apply();
        }

        Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
        tutupHalaman();
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