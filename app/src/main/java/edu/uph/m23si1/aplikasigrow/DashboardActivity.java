package edu.uph.m23si1.aplikasigrow; 

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    // --- DEKLARASI VARIABEL TAMPILAN ---
    private TextView tvHello, tvPesanPeringatan;
    private MaterialCardView cardWarning;
    private ImageView btnCloseWarning;

    private TextView tvNilaiCahaya, tvKondisiCahaya, tvStatusCahaya;
    private ProgressBar pbCahaya;

    private TextView tvNilaiSuhu, tvTingkatSuhu, badgeSuhu, tvRentangSuhu;
    private ProgressBar pbSuhu;

    private TextView tvNilaiTanah, tvTingkatTanah, badgeTanah, tvRentangTanah;
    private ProgressBar pbTanah;

    private TextView tvNilaiAir, tvTingkatAir, badgeAir, tvRentangAir;
    private ProgressBar pbAir;

    private TextView tvNilaiTds, tvTingkatTds, badgeTds, tvRentangTds;
    private ProgressBar pbTds;

    private SwitchCompat switchPompa, switchParanet;
    private TextView tvStatusPompaValue, btnPompaAuto, btnPompaManual;
    private TextView tvStatusParanetValue, btnParanetAuto, btnParanetManual;

    private LinearLayout navBeranda, navGrafik, navNotifikasi, navProfil;
    private MaterialCardView tvBadgeFooter;
    private TextView tvBadgeAngka;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        databaseRef = FirebaseDatabase.getInstance().getReference("iGrow_Data");

        inisialisasiViews();
        tampilkanNama();
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

        tvNilaiCahaya = findViewById(R.id.tvNilaiCahaya);
        tvKondisiCahaya = findViewById(R.id.tvKondisiCahaya);
        tvStatusCahaya = findViewById(R.id.tvStatusCahaya);
        pbCahaya = findViewById(R.id.pbCahaya);

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

        switchParanet = findViewById(R.id.switchParanet);
        tvStatusParanetValue = findViewById(R.id.tvStatusParanetValue);
        btnParanetAuto = findViewById(R.id.btnParanetAuto);
        btnParanetManual = findViewById(R.id.btnParanetManual);

        navBeranda = findViewById(R.id.navBeranda);
        navGrafik = findViewById(R.id.navGrafik);
        navNotifikasi = findViewById(R.id.navNotifikasi);
        navProfil = findViewById(R.id.navProfil);

        tvBadgeFooter = findViewById(R.id.tvBadgeFooter);
        tvBadgeAngka = findViewById(R.id.tvBadgeAngka);

        cardWarning.setVisibility(View.GONE);
        btnCloseWarning.setOnClickListener(v -> cardWarning.setVisibility(View.GONE));
    }

    private void tampilkanNama() {
        SharedPreferences loginSession = getSharedPreferences("LoginSession", MODE_PRIVATE);
        String userEmail = loginSession.getString("email", "default");
        String fileName = "iGrowPrefs_" + userEmail.replace(".", "_");
        SharedPreferences prefs = getSharedPreferences(fileName, MODE_PRIVATE);
        tvHello.setText("Hello, " + prefs.getString("nama", "User"));
    }

    private void setupTombolDanFooter() {
        switchPompa.setOnCheckedChangeListener((btn, isChecked) -> {
            if (btn.isPressed()) databaseRef.child("aktuator/pompa/status").setValue(isChecked);
        });
        btnPompaAuto.setOnClickListener(v -> databaseRef.child("aktuator/pompa/mode").setValue("auto"));
        btnPompaManual.setOnClickListener(v -> databaseRef.child("aktuator/pompa/mode").setValue("manual"));

        switchParanet.setOnCheckedChangeListener((btn, isChecked) -> {
            if (btn.isPressed()) databaseRef.child("aktuator/paranet/status").setValue(isChecked);
        });
        btnParanetAuto.setOnClickListener(v -> databaseRef.child("aktuator/paranet/mode").setValue("auto"));
        btnParanetManual.setOnClickListener(v -> databaseRef.child("aktuator/paranet/mode").setValue("manual"));

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
                if (!snapshot.exists()) { buatDataAwal(); return; }
                try {
                    double suhu = getDouble(snapshot, "sensor/suhu");
                    double tanah = getDouble(snapshot, "sensor/tanah");
                    double air = getDouble(snapshot, "sensor/air");
                    double tds = getDouble(snapshot, "sensor/tds");
                    double cahaya = getDouble(snapshot, "sensor/cahaya");

                    StringBuilder isiPeringatan = new StringBuilder();
                    boolean adaBahayaHalamanIni = false;

                    // ================= 1. SUHU =================
                    tvNilaiSuhu.setText((int)suhu + "°C");
                    pbSuhu.setProgress((int) ((suhu / 50.0) * 100));
                    tvTingkatSuhu.setText("Tingkat: " + pbSuhu.getProgress() + "%");

                    if (suhu < 10 || suhu > 38) {
                        String rentangSuhu = suhu < 10 ? String.format("- %.1f°C ", (10 - suhu)) : String.format("+ %.1f°C ", (suhu - 38));
                        setKondisiMerahAtauHijau(badgeSuhu, tvRentangSuhu, suhu < 10 ? "Warning" : "T. Tinggi", rentangSuhu, false);

                        isiPeringatan.append("• Masalah Suhu Udara!\n"); adaBahayaHalamanIni = true;
                        DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Tidak Normal", "Suhu saat ini " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = true;
                    } else {
                        setKondisiMerahAtauHijau(badgeSuhu, tvRentangSuhu, "Normal", "+ 0°C ", true);
                        if (DataGlobal.statusSuhuBahaya) { DataGlobal.tambahNotifOtomatis(2, "Suhu Kembali Normal", "Suhu udara sudah stabil di " + suhu + "°C."); DataGlobal.statusSuhuBahaya = false; }
                    }

                    // ================= 2. TANAH (UPDATE KERING & BASAH) =================
                    tvNilaiTanah.setText((int)tanah + "%");
                    pbTanah.setProgress((int) tanah);
                    tvTingkatTanah.setText("Tingkat: " + (int)tanah + "%");

                    if (tanah < 24.8) {
                        String rentangTanah = String.format("- %.1f%% ", (24.8 - tanah));
                        setKondisiMerahAtauHijau(badgeTanah, tvRentangTanah, "Kering", rentangTanah, false);

                        isiPeringatan.append("• Tanah Terlalu Kering!\n");
                        adaBahayaHalamanIni = true;
                        DataGlobal.tambahNotifOtomatis(1, "Kelembapan Tanah Rendah", "Tanah sangat kering berada di " + (int)tanah + "%.");
                        DataGlobal.statusTanahBahaya = true;

                    } else if (tanah > 31.8) {
                        String rentangTanah = String.format("+ %.1f%% ", (tanah - 31.8));
                        setKondisiMerahAtauHijau(badgeTanah, tvRentangTanah, "Basah", rentangTanah, false);

                        isiPeringatan.append("• Tanah Terlalu Basah!\n");
                        adaBahayaHalamanIni = true;
                        DataGlobal.tambahNotifOtomatis(1, "Kelembapan Tanah Berlebih", "Tanah sangat basah berada di " + (int)tanah + "%.");
                        DataGlobal.statusTanahBahaya = true;

                    } else {
                        setKondisiMerahAtauHijau(badgeTanah, tvRentangTanah, "Normal", "+ 0% ", true);
                        if (DataGlobal.statusTanahBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Kelembapan Tanah Normal", "Tanah sudah pada kondisi ideal (" + (int)tanah + "%).");
                            DataGlobal.statusTanahBahaya = false;
                        }
                    }

                    // ================= 3. AIR =================
                    tvNilaiAir.setText((int)air + "%");
                    pbAir.setProgress((int) air);
                    tvTingkatAir.setText("Tingkat: " + (int)air + "%");

                    if (air < 70) {
                        String rentangAir = String.format("- %d%% ", (70 - (int)air));
                        setKondisiMerahAtauHijau(badgeAir, tvRentangAir, "Warning", rentangAir, false);

                        isiPeringatan.append("• Air Tangki Menipis!\n"); adaBahayaHalamanIni = true;
                        DataGlobal.tambahNotifOtomatis(1, "Air Tangki Menipis", "Kapasitas air sisa " + (int)air + "%.");
                        DataGlobal.statusAirBahaya = true;
                    } else {
                        setKondisiMerahAtauHijau(badgeAir, tvRentangAir, "Normal", "+ 0% ", true);
                        if (DataGlobal.statusAirBahaya) { DataGlobal.tambahNotifOtomatis(2, "Pasokan Air Normal", "Tangki air sudah terisi kembali."); DataGlobal.statusAirBahaya = false; }
                    }

                    // ================= 4. TDS =================
                    tvNilaiTds.setText((int)tds + " PPM");
                    pbTds.setProgress((int) ((tds / 1500.0) * 100));
                    tvTingkatTds.setText("Tingkat: " + pbTds.getProgress() + "%");

                    if (tds < 840 || tds > 1050) {
                        String rentangTds = tds < 840 ? String.format("- %d PPM ", (840 - (int)tds)) : String.format("+ %d PPM ", ((int)tds - 1050));
                        setKondisiMerahAtauHijau(badgeTds, tvRentangTds, tds < 840 ? "Warning" : "T. Tinggi", rentangTds, false);

                        isiPeringatan.append("• Masalah Nutrisi (TDS)!\n"); adaBahayaHalamanIni = true;
                        DataGlobal.tambahNotifOtomatis(1, "Kadar Nutrisi Tidak Normal", "TDS berada di " + (int)tds + " PPM.");
                        DataGlobal.statusTdsBahaya = true;
                    } else {
                        setKondisiMerahAtauHijau(badgeTds, tvRentangTds, "Normal", "+ 0 PPM ", true);
                        if (DataGlobal.statusTdsBahaya) { DataGlobal.tambahNotifOtomatis(2, "Kadar Nutrisi Normal", "Nutrisi tanaman (TDS) sudah optimal di " + (int)tds + " PPM."); DataGlobal.statusTdsBahaya = false; }
                    }

                    // ================= 5. CAHAYA =================
                    tvNilaiCahaya.setText(String.format("%,d", (int)cahaya).replace(',', '.'));
                    pbCahaya.setProgress((int) ((cahaya / 120000.0) * 100));

                    if (cahaya < 80000) {
                        tvKondisiCahaya.setText("Redup");
                        tvStatusCahaya.setText("WARNING");
                        tvStatusCahaya.setTextColor(Color.parseColor("#FF4F3F"));
                        isiPeringatan.append("• Cahaya Kurang / Redup!\n");
                        adaBahayaHalamanIni = true;

                        DataGlobal.tambahNotifOtomatis(1, "Cahaya Kurang", "Intensitas cahaya hanya " + (int)cahaya + " Lux.");
                        DataGlobal.statusCahayaBahaya = true;
                    }
                    else if (cahaya > 100000) {
                        tvKondisiCahaya.setText("Sangat Terang");
                        tvStatusCahaya.setText("T. Terang");
                        tvStatusCahaya.setTextColor(Color.parseColor("#FF4F3F"));
                        isiPeringatan.append("• Cahaya Terlalu Tinggi!\n");
                        adaBahayaHalamanIni = true;

                        DataGlobal.tambahNotifOtomatis(1, "Cahaya Terik Ekstrem", "Cahaya terlalu Terang (" + (int)cahaya + " Lux).");
                        DataGlobal.statusCahayaBahaya = true;
                    }
                    else {
                        tvKondisiCahaya.setText("Terang");
                        tvStatusCahaya.setText("NORMAL");
                        tvStatusCahaya.setTextColor(Color.parseColor("#107432"));

                        if (DataGlobal.statusCahayaBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Cahaya Kembali Normal", "Cahaya sudah berada di titik optimal.");
                            DataGlobal.statusCahayaBahaya = false;
                        }
                    }

                    // Tampilkan Banner Merah jika ada yang berstatus BahayaHalamanIni
                    cardWarning.setVisibility(adaBahayaHalamanIni ? View.VISIBLE : View.GONE);
                    if (adaBahayaHalamanIni) tvPesanPeringatan.setText(isiPeringatan.toString().trim());

                    updateBadgeNotifikasi();

                    // --- SAKLAR POMPA OTOMATIS ---
                    String modePompa = snapshot.child("aktuator/pompa/mode").getValue(String.class);
                    Boolean statPompa = snapshot.child("aktuator/pompa/status").getValue(Boolean.class);
                    if (modePompa == null) modePompa = "manual";
                    if (statPompa == null) statPompa = false;

                    if ("auto".equals(modePompa)) {
                        switchPompa.setClickable(false); // Kunci switch manual

                        // Pompa menyala JIKA tanah kering (< 24.8) DAN air di tangki ada (> 0)
                        boolean tanahKering = (tanah < 24.8);
                        boolean tangkiAdaAir = (air > 0);

                        boolean pompaHarusNyala = (tanahKering && tangkiAdaAir);

                        if (statPompa != pompaHarusNyala) {
                            databaseRef.child("aktuator/pompa/status").setValue(pompaHarusNyala);
                        }
                        statPompa = pompaHarusNyala;
                    } else {
                        switchPompa.setClickable(true); // Buka kunci manual
                    }

                    // Update tampilan Pompa
                    switchPompa.setChecked(statPompa);
                    tvStatusPompaValue.setText(statPompa ? "ON" : "OFF");
                    tvStatusPompaValue.setTextColor(statPompa ? Color.parseColor("#107432") : Color.parseColor("#FF4F3F"));
                    setModeAktif(btnPompaAuto, btnPompaManual, modePompa);


                    // --- SAKLAR PARANET OTOMATIS ---
                    String modeParanet = snapshot.child("aktuator/paranet/mode").getValue(String.class);
                    Boolean statParanet = snapshot.child("aktuator/paranet/status").getValue(Boolean.class);
                    if (modeParanet == null) modeParanet = "manual";
                    if (statParanet == null) statParanet = false;

                    if ("auto".equals(modeParanet)) {
                        switchParanet.setClickable(false); // Kunci switch manual
                        boolean paranetHarusNyala = (suhu > 38);
                        if (statParanet != paranetHarusNyala) {
                            databaseRef.child("aktuator/paranet/status").setValue(paranetHarusNyala);
                        }
                        statParanet = paranetHarusNyala;
                    } else {
                        switchParanet.setClickable(true); // Buka kunci manual
                    }

                    // Update tampilan Paranet
                    switchParanet.setChecked(statParanet);
                    tvStatusParanetValue.setText(statParanet ? "ON" : "OFF");
                    tvStatusParanetValue.setTextColor(statParanet ? Color.parseColor("#107432") : Color.parseColor("#FF4F3F"));
                    setModeAktif(btnParanetAuto, btnParanetManual, modeParanet);

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
            // --- JIKA MODE AUTO AKTIF ---
            btnAuto.setTextColor(Color.parseColor("#107432")); // Teks Hijau
            cardAuto.setStrokeColor(Color.parseColor("#107432")); // Garis Hijau
            cardAuto.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D6FFD2"))); // Background Hijau Muda

            // Tombol Manual dimatikan warnanya
            btnManual.setTextColor(Color.parseColor("#858585")); // Teks Abu-abu
            cardManual.setStrokeColor(Color.parseColor("#E0E0E0")); // Garis Abu-abu tipis
            cardManual.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT)); // Background Bening

        } else {
            // --- JIKA MODE MANUAL AKTIF ---
            btnAuto.setTextColor(Color.parseColor("#858585"));
            cardAuto.setStrokeColor(Color.parseColor("#E0E0E0"));
            cardAuto.setCardBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));

            // Tombol Manual dihidupkan warnanya
            btnManual.setTextColor(Color.parseColor("#107432"));
            cardManual.setStrokeColor(Color.parseColor("#107432"));
            cardManual.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D6FFD2")));
        }
    }

    private void buatDataAwal() {
        databaseRef.child("sensor/suhu").setValue(25);
        databaseRef.child("sensor/tanah").setValue(30);
        databaseRef.child("sensor/air").setValue(90);
        databaseRef.child("sensor/tds").setValue(900);
        databaseRef.child("sensor/cahaya").setValue(80000);
        databaseRef.child("aktuator/pompa/status").setValue(false);
        databaseRef.child("aktuator/pompa/mode").setValue("auto");
        databaseRef.child("aktuator/paranet/status").setValue(false);
        databaseRef.child("aktuator/paranet/mode").setValue("auto");
    }

    @Override public void onBackPressed() { super.onBackPressed(); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); }
}