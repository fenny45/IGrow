package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvHello, tvPesanPeringatan;
    private MaterialCardView cardWarning;
    private ImageView btnCloseWarning;

    // Info Tanaman Dashboard
    private MaterialCardView cardInfoTanaman;
    private TextView tvNamaTanamanDash, tvUsiaTanamanDash;

    // Sensor & Aktuator
    private TextView tvNilaiSuhu, tvTingkatSuhu, badgeSuhu, tvRentangSuhu;
    private ProgressBar pbSuhu;
    private TextView tvNilaiTanah, tvTingkatTanah, badgeTanah, tvRentangTanah;
    private ProgressBar pbTanah;
    private TextView tvNilaiAir, tvTingkatAir, badgeAir, tvRentangAir;
    private ProgressBar pbAir;
    private TextView tvNilaiTds, tvTingkatTds, badgeTds, tvRentangTds;
    private ProgressBar pbTds;

    private SwitchCompat switchPompa;
    private TextView tvStatusPompaValue, btnPompaAuto, btnPompaManual;

    private LinearLayout navBeranda, navGrafik, navNotifikasi, navProfil;
    private MaterialCardView tvBadgeFooter;
    private TextView tvBadgeAngka;

    // Database
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private String uidUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) uidUser = currentUser.getUid();

        databaseRef = FirebaseDatabase.getInstance().getReference("iGrow_Data");

        inisialisasiViews();
        tampilkanDataUserDanTanaman();
        setupTombolDanFooter();
        mulaiDengarFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadgeNotifikasi();
    }

    private void updateBadgeNotifikasi() {
        int unreadCount = DataGlobal.getJumlahBelumDibaca();
        if (unreadCount > 0) {
            tvBadgeFooter.setVisibility(View.VISIBLE);
            tvBadgeAngka.setText(String.valueOf(unreadCount));
        } else {
            tvBadgeFooter.setVisibility(View.GONE);
        }
    }

    private void inisialisasiViews() {
        tvHello = findViewById(R.id.tvHello);
        cardWarning = findViewById(R.id.cardWarning);
        tvPesanPeringatan = findViewById(R.id.tvPesanPeringatan);
        btnCloseWarning = findViewById(R.id.btnCloseWarning);

        cardInfoTanaman = findViewById(R.id.cardInfoTanaman);
        tvNamaTanamanDash = findViewById(R.id.tvNamaTanamanDash);
        tvUsiaTanamanDash = findViewById(R.id.tvUsiaTanamanDash);

        tvNilaiSuhu = findViewById(R.id.tvNilaiSuhu);
        tvTingkatSuhu = findViewById(R.id.tvTingkatSuhu);
        badgeSuhu = findViewById(R.id.badgeSuhu);
        pbSuhu = findViewById(R.id.pbSuhu);
        tvRentangSuhu = findViewById(R.id.tvRentangSuhu);

        tvNilaiTanah = findViewById(R.id.tvNilaiTanah);
        tvTingkatTanah = findViewById(R.id.tvTingkatTanah);
        badgeTanah = findViewById(R.id.badgeTanah);
        pbTanah = findViewById(R.id.pbTanah);
        tvRentangTanah = findViewById(R.id.tvRentangTanah);

        tvNilaiAir = findViewById(R.id.tvNilaiAir);
        tvTingkatAir = findViewById(R.id.tvTingkatAir);
        badgeAir = findViewById(R.id.badgeAir);
        pbAir = findViewById(R.id.pbAir);
        tvRentangAir = findViewById(R.id.tvRentangAir);

        tvNilaiTds = findViewById(R.id.tvNilaiTds);
        tvTingkatTds = findViewById(R.id.tvTingkatTds);
        badgeTds = findViewById(R.id.badgeTds);
        pbTds = findViewById(R.id.pbTds);
        tvRentangTds = findViewById(R.id.tvRentangTds);

        switchPompa = findViewById(R.id.switchPompa);
        tvStatusPompaValue = findViewById(R.id.tvStatusPompaValue);
        btnPompaAuto = findViewById(R.id.btnPompaAuto);
        btnPompaManual = findViewById(R.id.btnPompaManual);

        navBeranda = findViewById(R.id.navBeranda);
        navGrafik = findViewById(R.id.navGrafik);
        navNotifikasi = findViewById(R.id.navNotifikasi);
        navProfil = findViewById(R.id.navProfil);

        tvBadgeFooter = findViewById(R.id.tvBadgeFooter);
        tvBadgeAngka = findViewById(R.id.tvBadgeAngka);

        cardWarning.setVisibility(View.GONE);
        btnCloseWarning.setOnClickListener(v -> cardWarning.setVisibility(View.GONE));
    }

    private void tampilkanDataUserDanTanaman() {
        if (uidUser.isEmpty()) return;

        // Ambil Nama User dari Realtime DB (Karena profilnya disana)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uidUser);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nama = snapshot.child("nama").getValue(String.class);
                    if (tvHello != null) tvHello.setText("Hello, " + (nama != null ? nama : "User"));
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Ambil Data Info Tanaman DARI FIRESTORE (Sesuai Permintaan)
        FirebaseFirestore.getInstance().collection("Users").document(uidUser).collection("Tanaman").document("Info")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) return;

                    if (snapshot != null && snapshot.exists()) {
                        String namaTanaman = snapshot.getString("nama_tanaman");
                        String umur = snapshot.getString("umur");

                        tvNamaTanamanDash.setText(namaTanaman != null && !namaTanaman.isEmpty() ? namaTanaman : "Belum diset");
                        tvUsiaTanamanDash.setText("Usia: " + (umur != null && !umur.isEmpty() ? umur : "-"));
                    } else {
                        tvNamaTanamanDash.setText("Belum Ada Tanaman");
                        tvUsiaTanamanDash.setText("Usia: -");
                    }
                });

        if (cardInfoTanaman != null) {
            cardInfoTanaman.setOnClickListener(v -> {
                startActivity(new Intent(DashboardActivity.this, InfoTanamanActivity.class));
            });
        }
    }

    private void setupTombolDanFooter() {
        switchPompa.setOnCheckedChangeListener((btn, isChecked) -> {
            if (btn.isPressed()) databaseRef.child("aktuator/pompa/status").setValue(isChecked);
        });
        btnPompaAuto.setOnClickListener(v -> databaseRef.child("aktuator/pompa/mode").setValue("auto"));
        btnPompaManual.setOnClickListener(v -> databaseRef.child("aktuator/pompa/mode").setValue("manual"));

        navBeranda.setOnClickListener(v -> Toast.makeText(this, "Anda sudah di Beranda", Toast.LENGTH_SHORT).show());
        navGrafik.setOnClickListener(v -> { startActivity(new Intent(this, GrafikActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); });
        navNotifikasi.setOnClickListener(v -> { startActivity(new Intent(this, NotifikasiActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); });
        navProfil.setOnClickListener(v -> { startActivity(new Intent(this, ProfilActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); });
    }

    private void mulaiDengarFirebase() {
        DataGlobal.initDataAwal();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                try {
                    double suhu = getDouble(snapshot, "sensor/suhu");
                    double tanah = getDouble(snapshot, "sensor/tanah");
                    double air = getDouble(snapshot, "sensor/air");
                    double tds = getDouble(snapshot, "sensor/tds");

                    if (DataGlobal.lastAlertSuhu == -999) DataGlobal.lastAlertSuhu = suhu;
                    if (DataGlobal.lastAlertTanah == -999) DataGlobal.lastAlertTanah = tanah;
                    if (DataGlobal.lastAlertAir == -999) DataGlobal.lastAlertAir = air;
                    if (DataGlobal.lastAlertTds == -999) DataGlobal.lastAlertTds = tds;

                    StringBuilder isiPeringatan = new StringBuilder();
                    boolean adaBahayaHalamanIni = false;

                    // 1. SUHU
                    tvNilaiSuhu.setText((int)suhu + "°C");
                    pbSuhu.setProgress((int) ((suhu / 50.0) * 100));
                    tvTingkatSuhu.setText("Tingkat: " + pbSuhu.getProgress() + "%");

                    if (suhu > 38.0) {
                        String rentangSuhu = String.format("+ %.1f°C ", (suhu - 38));
                        setKondisiMerahAtauHijau(badgeSuhu, tvRentangSuhu, "T. Tinggi", rentangSuhu, false);
                        isiPeringatan.append("• Suhu Terlalu Panas!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusSuhuBahaya || Math.abs(suhu - DataGlobal.lastAlertSuhu) >= 2.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Terlalu Panas", "Suhu melonjak ke " + suhu + "°C.");
                            DataGlobal.statusSuhuBahaya = true; DataGlobal.lastAlertSuhu = suhu;
                        }
                    } else if (suhu < 10.0) {
                        String rentangSuhu = String.format("- %.1f°C ", (10 - suhu));
                        setKondisiMerahAtauHijau(badgeSuhu, tvRentangSuhu, "Warning", rentangSuhu, false);
                        isiPeringatan.append("• Suhu Terlalu Dingin!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusSuhuBahaya || Math.abs(suhu - DataGlobal.lastAlertSuhu) >= 2.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Terlalu Dingin", "Suhu drop ke " + suhu + "°C.");
                            DataGlobal.statusSuhuBahaya = true; DataGlobal.lastAlertSuhu = suhu;
                        }
                    } else {
                        setKondisiMerahAtauHijau(badgeSuhu, tvRentangSuhu, "Normal", "+ 0°C ", true);
                        if (DataGlobal.statusSuhuBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Suhu Udara Normal", "Suhu stabil di " + suhu + "°C.");
                            DataGlobal.statusSuhuBahaya = false; DataGlobal.lastAlertSuhu = suhu;
                        }
                    }

                    // 2. TANAH
                    tvNilaiTanah.setText((int)tanah + "%");
                    pbTanah.setProgress((int) tanah);
                    tvTingkatTanah.setText("Tingkat: " + (int)tanah + "%");

                    if (tanah < 24.8) {
                        String rentangTanah = String.format("- %.1f%% ", (24.8 - tanah));
                        setKondisiMerahAtauHijau(badgeTanah, tvRentangTanah, "Kering", rentangTanah, false);
                        isiPeringatan.append("• Tanah Sangat Kering!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusTanahBahaya || Math.abs(tanah - DataGlobal.lastAlertTanah) >= 5.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Tanah Sangat Kering", "Kelembapan drop ke " + (int)tanah + "%.");
                            DataGlobal.statusTanahBahaya = true; DataGlobal.lastAlertTanah = tanah;
                        }
                    } else if (tanah > 31.8) {
                        String rentangTanah = String.format("+ %.1f%% ", (tanah - 31.8));
                        setKondisiMerahAtauHijau(badgeTanah, tvRentangTanah, "Basah", rentangTanah, false);
                        isiPeringatan.append("• Tanah Terlalu Basah!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusTanahBahaya || Math.abs(tanah - DataGlobal.lastAlertTanah) >= 5.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Tanah Terlalu Basah", "Kelembapan naik ke " + (int)tanah + "%.");
                            DataGlobal.statusTanahBahaya = true; DataGlobal.lastAlertTanah = tanah;
                        }
                    } else {
                        setKondisiMerahAtauHijau(badgeTanah, tvRentangTanah, "Normal", "+ 0% ", true);
                        if (DataGlobal.statusTanahBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Kelembapan Tanah Normal", "Tanah ideal di (" + (int)tanah + "%).");
                            DataGlobal.statusTanahBahaya = false; DataGlobal.lastAlertTanah = tanah;
                        }
                    }

                    // 3. AIR
                    tvNilaiAir.setText((int)air + "%");
                    pbAir.setProgress((int) air);
                    tvTingkatAir.setText("Tingkat: " + (int)air + "%");

                    if (air < 70) {
                        String rentangAir = String.format("- %d%% ", (70 - (int)air));
                        setKondisiMerahAtauHijau(badgeAir, tvRentangAir, "Warning", rentangAir, false);
                        isiPeringatan.append("• Air Tangki Menipis!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusAirBahaya || Math.abs(air - DataGlobal.lastAlertAir) >= 10.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Air Tangki Menipis", "Air sisa " + (int)air + "%.");
                            DataGlobal.statusAirBahaya = true; DataGlobal.lastAlertAir = air;
                        }
                    } else {
                        setKondisiMerahAtauHijau(badgeAir, tvRentangAir, "Normal", "+ 0% ", true);
                        if (DataGlobal.statusAirBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Air Tangki Normal", "Tangki terisi penuh.");
                            DataGlobal.statusAirBahaya = false; DataGlobal.lastAlertAir = air;
                        }
                    }

                    // 4. TDS
                    tvNilaiTds.setText((int)tds + " PPM");
                    pbTds.setProgress((int) ((tds / 1500.0) * 100));
                    tvTingkatTds.setText("Tingkat: " + pbTds.getProgress() + "%");

                    if (tds < 840) {
                        String rentangTds = String.format("- %d PPM ", (840 - (int)tds));
                        setKondisiMerahAtauHijau(badgeTds, tvRentangTds, "Rendah", rentangTds, false);
                        isiPeringatan.append("• Nutrisi TDS Rendah!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusTdsBahaya || Math.abs(tds - DataGlobal.lastAlertTds) >= 50) {
                            DataGlobal.tambahNotifOtomatis(1, "Nutrisi TDS Rendah", "TDS menurun ke " + (int)tds + " PPM.");
                            DataGlobal.statusTdsBahaya = true; DataGlobal.lastAlertTds = tds;
                        }
                    } else if (tds > 1050) {
                        String rentangTds = String.format("+ %d PPM ", ((int)tds - 1050));
                        setKondisiMerahAtauHijau(badgeTds, tvRentangTds, "T. Tinggi", rentangTds, false);
                        isiPeringatan.append("• Nutrisi TDS Berlebih!\n"); adaBahayaHalamanIni = true;
                        if (!DataGlobal.statusTdsBahaya || Math.abs(tds - DataGlobal.lastAlertTds) >= 50) {
                            DataGlobal.tambahNotifOtomatis(1, "Nutrisi TDS Berlebih", "TDS melebihi rentang ke " + (int)tds + " PPM.");
                            DataGlobal.statusTdsBahaya = true; DataGlobal.lastAlertTds = tds;
                        }
                    } else {
                        setKondisiMerahAtauHijau(badgeTds, tvRentangTds, "Normal", "+ 0 PPM ", true);
                        if (DataGlobal.statusTdsBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Nutrisi TDS Normal", "TDS optimal di " + (int)tds + " PPM.");
                            DataGlobal.statusTdsBahaya = false; DataGlobal.lastAlertTds = tds;
                        }
                    }

                    cardWarning.setVisibility(adaBahayaHalamanIni ? View.VISIBLE : View.GONE);
                    if (adaBahayaHalamanIni) tvPesanPeringatan.setText(isiPeringatan.toString().trim());

                    updateBadgeNotifikasi();

                    // --- SAKLAR POMPA ---
                    String modePompa = snapshot.child("aktuator/pompa/mode").getValue(String.class);
                    Boolean statPompa = snapshot.child("aktuator/pompa/status").getValue(Boolean.class);
                    if (modePompa == null) modePompa = "manual";
                    if (statPompa == null) statPompa = false;

                    if ("auto".equals(modePompa)) {
                        switchPompa.setClickable(false);
                        boolean tanahKering = (tanah < 24.8);
                        boolean tangkiAdaAir = (air > 0);
                        boolean pompaHarusNyala = (tanahKering && tangkiAdaAir);

                        if (statPompa != pompaHarusNyala) {
                            databaseRef.child("aktuator/pompa/status").setValue(pompaHarusNyala);
                        }
                        statPompa = pompaHarusNyala;
                    } else {
                        switchPompa.setClickable(true);
                    }

                    switchPompa.setChecked(statPompa);
                    tvStatusPompaValue.setText(statPompa ? "ON" : "OFF");
                    tvStatusPompaValue.setTextColor(statPompa ? Color.parseColor("#107432") : Color.parseColor("#FF4F3F"));
                    setModeAktif(btnPompaAuto, btnPompaManual, modePompa);

                } catch (Exception e) { e.printStackTrace(); }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private double getDouble(DataSnapshot snapshot, String path) {
        if (!snapshot.child(path).exists()) return 0.0;
        Object val = snapshot.child(path).getValue();
        if (val instanceof Long) return ((Long) val).doubleValue();
        if (val instanceof Double) return (Double) val;
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        if (val instanceof String) { try { return Double.parseDouble((String) val); } catch (Exception e) { return 0.0; } }
        return 0.0;
    }

    private void setKondisiMerahAtauHijau(TextView badge, TextView rentangText, String tulisanBadge, String tulisanRentang, boolean isNormal) {
        badge.setText(tulisanBadge); rentangText.setText(tulisanRentang);
        MaterialCardView parentCard = (MaterialCardView) badge.getParent();
        if (isNormal) {
            badge.setTextColor(Color.parseColor("#107432")); rentangText.setTextColor(Color.parseColor("#107432"));
            parentCard.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#A5FF9D")));
        } else {
            badge.setTextColor(Color.parseColor("#FF4F3F")); rentangText.setTextColor(Color.parseColor("#FF4F3F"));
            parentCard.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFB9B2")));
        }
    }

    private void setModeAktif(TextView btnAuto, TextView btnManual, String mode) {
        MaterialCardView cardAuto = (MaterialCardView) btnAuto.getParent();
        MaterialCardView cardManual = (MaterialCardView) btnManual.getParent();

        if ("auto".equals(mode)) {
            btnAuto.setTextColor(Color.parseColor("#107432"));
            cardAuto.setStrokeColor(Color.parseColor("#107432"));
            cardAuto.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D6FFD2")));

            btnManual.setTextColor(Color.parseColor("#858585"));
            cardManual.setStrokeColor(Color.parseColor("#E0E0E0"));
            cardManual.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
        } else {
            btnAuto.setTextColor(Color.parseColor("#858585"));
            cardAuto.setStrokeColor(Color.parseColor("#E0E0E0"));
            cardAuto.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));

            btnManual.setTextColor(Color.parseColor("#107432"));
            cardManual.setStrokeColor(Color.parseColor("#107432"));
            cardManual.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D6FFD2")));
        }
    }

    @Override public void onBackPressed() { super.onBackPressed(); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); }
}