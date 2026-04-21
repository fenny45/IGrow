package edu.uph.m23si1.aplikasigrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uph.m23si1.aplikasigrow.adapter.AdapterNotifikasi;
import edu.uph.m23si1.aplikasigrow.model.ModelNotifikasi;

public class NotifikasiActivity extends AppCompatActivity {

    private RecyclerView rvNotifikasi;
    private MaterialCardView btnMarkAllRead, btnDeleteAll;
    private MaterialCardView tvBadgeFooter;
    private TextView tvBadgeAngka;
    private LinearLayout navBeranda, navGrafik, navProfil;

    private AdapterNotifikasi adapter;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifikasi);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        databaseRef = FirebaseDatabase.getInstance().getReference("iGrow_Data/sensor");
        DataGlobal.initDataAwal();

        inisialisasiViews();
        setupRecyclerView();
        setupTombolAksi();
        setupFooter();

        pantauSensorDariFirebase();
        hitungBadgeUnread();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hitungBadgeUnread();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void inisialisasiViews() {
        rvNotifikasi = findViewById(R.id.rvNotifikasi);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        tvBadgeFooter = findViewById(R.id.tvBadgeFooter);
        tvBadgeAngka = findViewById(R.id.tvBadgeAngka);

        navBeranda = findViewById(R.id.navBeranda);
        navGrafik = findViewById(R.id.navGrafik);
        navProfil = findViewById(R.id.navProfil);
    }

    private void setupRecyclerView() {
        rvNotifikasi.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdapterNotifikasi(DataGlobal.listNotifikasi, new AdapterNotifikasi.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                DataGlobal.listNotifikasi.remove(position);
                adapter.notifyDataSetChanged();
                hitungBadgeUnread();
            }

            @Override
            public void onMarkReadClick(int position) {
                DataGlobal.listNotifikasi.get(position).isRead = true;
                adapter.notifyItemChanged(position);
                hitungBadgeUnread();
            }
        });
        rvNotifikasi.setAdapter(adapter);
    }

    private void setupTombolAksi() {
        btnMarkAllRead.setOnClickListener(v -> {
            for (ModelNotifikasi n : DataGlobal.listNotifikasi) n.isRead = true;
            adapter.notifyDataSetChanged();
            hitungBadgeUnread();
            Toast.makeText(this, "Semua ditandai telah dibaca", Toast.LENGTH_SHORT).show();
        });

        btnDeleteAll.setOnClickListener(v -> {
            DataGlobal.listNotifikasi.clear();
            adapter.notifyDataSetChanged();
            hitungBadgeUnread();
            Toast.makeText(this, "Semua notifikasi dibersihkan", Toast.LENGTH_SHORT).show();
        });
    }

    private void pantauSensorDariFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double suhu = getDouble(snapshot, "suhu");
                    double tanah = getDouble(snapshot, "tanah");
                    double air = getDouble(snapshot, "air");
                    double tds = getDouble(snapshot, "tds");

                    if (DataGlobal.lastAlertSuhu == -999) DataGlobal.lastAlertSuhu = suhu;
                    if (DataGlobal.lastAlertTanah == -999) DataGlobal.lastAlertTanah = tanah;
                    if (DataGlobal.lastAlertAir == -999) DataGlobal.lastAlertAir = air;
                    if (DataGlobal.lastAlertTds == -999) DataGlobal.lastAlertTds = tds;

                    boolean adaUpdateBaru = false;

                    // ================= 1. SUHU =================
                    if (suhu > 38.0) {
                        if (!DataGlobal.statusSuhuBahaya || Math.abs(suhu - DataGlobal.lastAlertSuhu) >= 2.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Terlalu Panas", "Suhu melonjak ke " + suhu + "°C.");
                            DataGlobal.statusSuhuBahaya = true;
                            DataGlobal.lastAlertSuhu = suhu;
                            adaUpdateBaru = true;
                        }
                    } else if (suhu < 10.0) {
                        if (!DataGlobal.statusSuhuBahaya || Math.abs(suhu - DataGlobal.lastAlertSuhu) >= 2.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Terlalu Dingin", "Suhu drop ke " + suhu + "°C.");
                            DataGlobal.statusSuhuBahaya = true;
                            DataGlobal.lastAlertSuhu = suhu;
                            adaUpdateBaru = true;
                        }
                    } else if (DataGlobal.statusSuhuBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Suhu Udara Normal", "Suhu stabil di " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = false;
                        DataGlobal.lastAlertSuhu = suhu;
                        adaUpdateBaru = true;
                    }

                    // ================= 2. TANAH =================
                    if (tanah < 24.8) {
                        if (!DataGlobal.statusTanahBahaya || Math.abs(tanah - DataGlobal.lastAlertTanah) >= 5.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Tanah Sangat Kering", "Kelembapan drop ke " + (int)tanah + "%.");
                            DataGlobal.statusTanahBahaya = true;
                            DataGlobal.lastAlertTanah = tanah;
                            adaUpdateBaru = true;
                        }
                    } else if (tanah > 31.8) {
                        if (!DataGlobal.statusTanahBahaya || Math.abs(tanah - DataGlobal.lastAlertTanah) >= 5.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Tanah Terlalu Basah", "Kelembapan naik ke " + (int)tanah + "%.");
                            DataGlobal.statusTanahBahaya = true;
                            DataGlobal.lastAlertTanah = tanah;
                            adaUpdateBaru = true;
                        }
                    } else if (DataGlobal.statusTanahBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Kelembapan Tanah Normal", "Tanah ideal di (" + (int)tanah + "%).");
                        DataGlobal.statusTanahBahaya = false;
                        DataGlobal.lastAlertTanah = tanah;
                        adaUpdateBaru = true;
                    }

                    // ================= 3. AIR =================
                    if (air < 70) {
                        if (!DataGlobal.statusAirBahaya || Math.abs(air - DataGlobal.lastAlertAir) >= 10.0) {
                            DataGlobal.tambahNotifOtomatis(1, "Air Tangki Menipis", "Air sisa " + (int)air + "%.");
                            DataGlobal.statusAirBahaya = true;
                            DataGlobal.lastAlertAir = air;
                            adaUpdateBaru = true;
                        }
                    } else if (DataGlobal.statusAirBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Air Tangki Normal", "Tangki terisi penuh.");
                        DataGlobal.statusAirBahaya = false;
                        DataGlobal.lastAlertAir = air;
                        adaUpdateBaru = true;
                    }

                    // ================= 4. TDS =================
                    if (tds < 840) {
                        if (!DataGlobal.statusTdsBahaya || Math.abs(tds - DataGlobal.lastAlertTds) >= 50) {
                            DataGlobal.tambahNotifOtomatis(1, "Nutrisi TDS Rendah", "TDS anjlok ke " + (int)tds + " PPM.");
                            DataGlobal.statusTdsBahaya = true;
                            DataGlobal.lastAlertTds = tds;
                            adaUpdateBaru = true;
                        }
                    } else if (tds > 1050) {
                        if (!DataGlobal.statusTdsBahaya || Math.abs(tds - DataGlobal.lastAlertTds) >= 50) {
                            DataGlobal.tambahNotifOtomatis(1, "Nutrisi TDS Berlebih", "TDS melonjak ke " + (int)tds + " PPM.");
                            DataGlobal.statusTdsBahaya = true;
                            DataGlobal.lastAlertTds = tds;
                            adaUpdateBaru = true;
                        }
                    } else if (DataGlobal.statusTdsBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Nutrisi TDS Normal", "TDS optimal di " + (int)tds + " PPM.");
                        DataGlobal.statusTdsBahaya = false;
                        DataGlobal.lastAlertTds = tds;
                        adaUpdateBaru = true;
                    }

                    if (adaUpdateBaru) {
                        adapter.notifyDataSetChanged();
                        rvNotifikasi.scrollToPosition(0);
                        hitungBadgeUnread();
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void hitungBadgeUnread() {
        int unreadCount = DataGlobal.getJumlahBelumDibaca();
        if (unreadCount > 0) {
            tvBadgeFooter.setVisibility(View.VISIBLE);
            tvBadgeAngka.setText(String.valueOf(unreadCount));
        } else {
            tvBadgeFooter.setVisibility(View.GONE);
        }
    }

    private double getDouble(DataSnapshot snapshot, String path) {
        if (!snapshot.child(path).exists()) return 0.0;
        Object val = snapshot.child(path).getValue();
        if (val instanceof Long) return ((Long) val).doubleValue();
        if (val instanceof Double) return (Double) val;
        if (val instanceof Integer) return ((Integer) val).doubleValue();
        if (val instanceof String) {
            try { return Double.parseDouble((String) val); } catch (Exception e) { return 0.0; }
        }
        return 0.0;
    }

    private void setupFooter() {
        navBeranda.setOnClickListener(v -> { startActivity(new Intent(NotifikasiActivity.this, DashboardActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish(); });
        navGrafik.setOnClickListener(v -> { startActivity(new Intent(NotifikasiActivity.this, GrafikActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish(); });
        navProfil.setOnClickListener(v -> { startActivity(new Intent(NotifikasiActivity.this, ProfilActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish(); });
    }

    @Override
    public void onBackPressed() { super.onBackPressed(); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); }
}