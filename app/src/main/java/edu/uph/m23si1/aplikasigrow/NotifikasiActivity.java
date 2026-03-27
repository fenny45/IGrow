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

        // Fokus memantau langsung ke dalam folder sensor
        databaseRef = FirebaseDatabase.getInstance().getReference("iGrow_Data/sensor");

        DataGlobal.initDataAwal(); // Panggil pusat memori

        inisialisasiViews();
        setupRecyclerView();
        setupTombolAksi();
        setupFooter();
        pantauSensorDariFirebase();

        hitungBadgeUnread();
    }

    // --- FUNGSI BARU: Menyegarkan badge notifikasi saat kembali ke halaman ini ---
    @Override
    protected void onResume() {
        super.onResume();
        hitungBadgeUnread();
        if(adapter != null) adapter.notifyDataSetChanged();
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
                // HANYA menghapus dari layar notifikasi, Riwayat di Grafik tetap aman
                DataGlobal.listNotifikasi.remove(position);
                adapter.notifyItemRemoved(position);
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
                if(snapshot.exists()) {
                    double suhu = getDouble(snapshot, "suhu");
                    double tanah = getDouble(snapshot, "tanah");
                    double air = getDouble(snapshot, "air");
                    double tds = getDouble(snapshot, "tds");
                    double cahaya = getDouble(snapshot, "cahaya");

                    int ukuranAwal = DataGlobal.listNotifikasi.size();

                    // --- LOGIKA NOTIFIKASI SENSOR ---
                    // Suhu
                    if (suhu < 10 || suhu > 38) {
                        DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Tidak Normal", "Suhu saat ini " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = true;
                    } else if (DataGlobal.statusSuhuBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Suhu Kembali Normal", "Suhu udara sudah stabil di " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = false;
                    }

                    // Tanah
                    if (tanah < 24.8 || tanah > 31.8) {
                        DataGlobal.tambahNotifOtomatis(1, "Kelembapan Tanah Tidak Normal", "Tanah berada di angka " + (int)tanah + "%.");
                        DataGlobal.statusTanahBahaya = true;
                    } else if (DataGlobal.statusTanahBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Kelembapan Tanah Normal", "Tanah sudah cukup air (" + (int)tanah + "%).");
                        DataGlobal.statusTanahBahaya = false;
                    }

                    // Air
                    if (air < 70) {
                        DataGlobal.tambahNotifOtomatis(1, "Air Tangki Menipis", "Kapasitas air sisa " + (int)air + "%.");
                        DataGlobal.statusAirBahaya = true;
                    } else if (DataGlobal.statusAirBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Pasokan Air Normal", "Tangki air sudah terisi kembali.");
                        DataGlobal.statusAirBahaya = false;
                    }

                    // Cahaya
                    if (cahaya < 40000 || cahaya > 100000) {
                        DataGlobal.tambahNotifOtomatis(1, "Intensitas Cahaya Tidak Ideal", "Cahaya terdeteksi " + (int)cahaya + " Lux.");
                        DataGlobal.statusCahayaBahaya = true;
                    } else if (DataGlobal.statusCahayaBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Cahaya Kembali Normal", "Intensitas cahaya sudah ideal (" + (int)cahaya + " Lux).");
                        DataGlobal.statusCahayaBahaya = false;
                    }

                    // --- UPDATE TAMPILAN SECARA REAL-TIME ---

                    // 1. Update daftar list jika ada notifikasi baru
                    if(DataGlobal.listNotifikasi.size() > ukuranAwal) {
                        adapter.notifyDataSetChanged();
                        rvNotifikasi.scrollToPosition(0);
                    }

                    // 2. SELALU Update angka badge di footer setiap kali Firebase berubah
                    // Ini kuncinya agar badge langsung muncul/berubah tanpa perlu pindah footer
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hitungBadgeUnread();
                        }
                    });
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
        navBeranda.setOnClickListener(v -> {
            startActivity(new Intent(NotifikasiActivity.this, DashboardActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        navGrafik.setOnClickListener(v -> {
            startActivity(new Intent(NotifikasiActivity.this, GrafikActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
        navProfil.setOnClickListener(v -> {
            startActivity(new Intent(NotifikasiActivity.this, ProfilActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}