package edu.uph.m23si1.aplikasigrow; // WAJIB GANTI DENGAN PACKAGE KAMU

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.uph.m23si1.aplikasigrow.adapter.AdapterRiwayat;
import edu.uph.m23si1.aplikasigrow.model.ModelRiwayat;

public class GrafikActivity extends AppCompatActivity {

    // Tab Switcher
    private LinearLayout tabGrafik, tabRiwayat;
    private TextView tvTabGrafik, tvTabRiwayat;
    private View lineGrafik, lineRiwayat;
    private LinearLayout layoutGrafik, layoutRiwayat;

    // Filter Atas
    private MaterialCardView cardFilter1, cardFilter2, cardFilter3, btnCalendar;
    private TextView tvFilter1, tvFilter2, tvFilter3, tvTanggalRiwayat;

    // Komponen Grafik (Tanpa Cahaya)
    private LineChart chartAir, chartSuhu, chartTanah, chartTds;
    private TextView tvValAir, tvValSuhu, tvValTanah, tvValTds;
    private TextView tvStatusAir, tvStatusTanah;

    // Riwayat
    private RecyclerView rvRiwayat;

    // Footer Navigation
    private LinearLayout navBeranda, navNotifikasi, navProfil;
    private MaterialCardView tvBadgeFooter;
    private TextView tvBadgeAngka;

    // Sistem & Database
    private DatabaseReference databaseRef;
    private FirebaseFirestore firestore;
    private String uidUser = "";
    private int currentMode = 1; // 1: Hari ini, 2: 7 Hari, 3: Bulan, 4: Kalender
    private boolean isModeGrafik = true;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) uidUser = currentUser.getUid();

        databaseRef = FirebaseDatabase.getInstance().getReference("iGrow_Data/sensor");
        firestore = FirebaseFirestore.getInstance();
        selectedCalendar = Calendar.getInstance();

        inisialisasiView();
        setupTabSwitcher();
        setupFilterInteraktif();
        setupFooterNavigasi();

        setFilterWarna(1);
        bacaDataFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadgeUI();
    }

    private void updateBadgeUI() {
        int count = DataGlobal.getJumlahBelumDibaca();
        if (count > 0) {
            tvBadgeFooter.setVisibility(View.VISIBLE);
            tvBadgeAngka.setText(String.valueOf(count));
        } else {
            tvBadgeFooter.setVisibility(View.GONE);
        }
    }

    private void inisialisasiView() {
        tabGrafik = findViewById(R.id.tabGrafik);
        tabRiwayat = findViewById(R.id.tabRiwayat);
        tvTabGrafik = findViewById(R.id.tvTabGrafik);
        tvTabRiwayat = findViewById(R.id.tvTabRiwayat);
        lineGrafik = findViewById(R.id.lineGrafik);
        lineRiwayat = findViewById(R.id.lineRiwayat);

        layoutGrafik = findViewById(R.id.layoutGrafik);
        layoutRiwayat = findViewById(R.id.layoutRiwayat);

        cardFilter1 = findViewById(R.id.cardFilter1);
        cardFilter2 = findViewById(R.id.cardFilter2);
        cardFilter3 = findViewById(R.id.cardFilter3);
        tvFilter1 = findViewById(R.id.tvFilter1);
        tvFilter2 = findViewById(R.id.tvFilter2);
        tvFilter3 = findViewById(R.id.tvFilter3);
        btnCalendar = findViewById(R.id.btnCalendar);

        tvTanggalRiwayat = findViewById(R.id.tvTanggalRiwayat);

        // Grafik (Cahaya Dihapus)
        chartAir = findViewById(R.id.chartAir);
        chartSuhu = findViewById(R.id.chartSuhu);
        chartTanah = findViewById(R.id.chartTanah);
        chartTds = findViewById(R.id.chartTds);

        tvValAir = findViewById(R.id.tvValAir);
        tvValSuhu = findViewById(R.id.tvValSuhu);
        tvValTanah = findViewById(R.id.tvValTanah);
        tvValTds = findViewById(R.id.tvValTds);
        tvStatusAir = findViewById(R.id.tvStatusAir);
        tvStatusTanah = findViewById(R.id.tvStatusTanah);

        rvRiwayat = findViewById(R.id.rvRiwayat);
        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));

        navBeranda = findViewById(R.id.navBeranda);
        navNotifikasi = findViewById(R.id.navNotifikasi);
        navProfil = findViewById(R.id.navProfil);
        tvBadgeFooter = findViewById(R.id.tvBadgeFooter);
        tvBadgeAngka = findViewById(R.id.tvBadgeAngka);
    }

    private void setupTabSwitcher() {
        tabGrafik.setOnClickListener(v -> {
            isModeGrafik = true;
            tvTabGrafik.setTextColor(Color.parseColor("#1ACC0A"));
            lineGrafik.setBackgroundColor(Color.parseColor("#1ACC0A"));
            tvTabRiwayat.setTextColor(Color.parseColor("#777777"));
            lineRiwayat.setBackgroundColor(Color.TRANSPARENT);

            layoutGrafik.setVisibility(View.VISIBLE);
            layoutRiwayat.setVisibility(View.GONE);

            tvFilter1.setText("Hari ini"); tvFilter2.setText("7 Hari"); tvFilter3.setText("Bulan");
            setFilterWarna(1);
        });

        tabRiwayat.setOnClickListener(v -> {
            isModeGrafik = false;
            tvTabRiwayat.setTextColor(Color.parseColor("#1ACC0A"));
            lineRiwayat.setBackgroundColor(Color.parseColor("#1ACC0A"));
            tvTabGrafik.setTextColor(Color.parseColor("#777777"));
            lineGrafik.setBackgroundColor(Color.TRANSPARENT);

            layoutRiwayat.setVisibility(View.VISIBLE);
            layoutGrafik.setVisibility(View.GONE);

            tvFilter1.setText("Hari ini"); tvFilter2.setText("7 Hari"); tvFilter3.setText("Bulan");
            setFilterWarna(1);
        });
    }

    private void setupFilterInteraktif() {
        cardFilter1.setOnClickListener(v -> setFilterWarna(1));
        cardFilter2.setOnClickListener(v -> setFilterWarna(2));
        cardFilter3.setOnClickListener(v -> setFilterWarna(3));
        btnCalendar.setOnClickListener(v -> bukaKalender());
    }

    private void bukaKalender() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(GrafikActivity.this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                    String formattedDate = sdf.format(selectedCalendar.getTime());

                    resetWarnaFilter();
                    tvTanggalRiwayat.setText(isModeGrafik ? "Data Tanggal: " + formattedDate : "Riwayat Tanggal: " + formattedDate);
                    currentMode = 4;
                    perbaruiDataTampilan();
                },
                selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void resetWarnaFilter() {
        cardFilter1.setCardBackgroundColor(Color.TRANSPARENT); tvFilter1.setTextColor(Color.parseColor("#777777"));
        cardFilter2.setCardBackgroundColor(Color.TRANSPARENT); tvFilter2.setTextColor(Color.parseColor("#777777"));
        cardFilter3.setCardBackgroundColor(Color.TRANSPARENT); tvFilter3.setTextColor(Color.parseColor("#777777"));
    }

    private void setFilterWarna(int posisiAktif) {
        currentMode = posisiAktif;
        resetWarnaFilter();
        String labelTanggal = "";

        if (posisiAktif == 1) {
            cardFilter1.setCardBackgroundColor(Color.parseColor("#D6FFD2")); tvFilter1.setTextColor(Color.parseColor("#17BA09"));
            labelTanggal = isModeGrafik ? "Data Hari Ini" : "Riwayat Hari Ini";
        } else if (posisiAktif == 2) {
            cardFilter2.setCardBackgroundColor(Color.parseColor("#D6FFD2")); tvFilter2.setTextColor(Color.parseColor("#17BA09"));
            labelTanggal = isModeGrafik ? "Data 7 Hari Terakhir" : "Riwayat 7 Hari Terakhir";
        } else if (posisiAktif == 3) {
            cardFilter3.setCardBackgroundColor(Color.parseColor("#D6FFD2")); tvFilter3.setTextColor(Color.parseColor("#17BA09"));
            labelTanggal = isModeGrafik ? "Data Bulan Ini" : "Riwayat Bulan Ini";
        }

        tvTanggalRiwayat.setText(labelTanggal);
        perbaruiDataTampilan();
    }

    private boolean isSameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance(); cal1.setTimeInMillis(time1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void perbaruiDataTampilan() {
        boolean isFutureDate = false;
        if (currentMode == 4) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0);
            Calendar selected = (Calendar) selectedCalendar.clone();
            selected.set(Calendar.HOUR_OF_DAY, 0); selected.set(Calendar.MINUTE, 0); selected.set(Calendar.SECOND, 0);
            if (selected.after(today)) isFutureDate = true;
        }

        // ===================================
        // JIKA DI TAB RIWAYAT
        // ===================================
        if (!isModeGrafik) {
            List<ModelRiwayat> dataTersaring = new ArrayList<>();
            List<ModelRiwayat> listRiwayatFinal = new ArrayList<>();
            long now = System.currentTimeMillis();
            long satuHariMs = 24L * 60 * 60 * 1000L;

            if (isFutureDate) {
                listRiwayatFinal.add(new ModelRiwayat(R.drawable.calendar, "#F2F7FF", "#858585", "Belum Ada Data", "Tidak ada data yang terekam karena tanggal ini belum terjadi.", 0));
            } else {
                for (ModelRiwayat data : DataGlobal.listRiwayatGlobal) {
                    if (data.getItemType() == ModelRiwayat.TYPE_HEADER) continue;

                    if (currentMode == 1) { // HARI INI
                        if (isSameDay(now, data.getTimestamp())) dataTersaring.add(data);
                    } else if (currentMode == 2) { // 7 HARI
                        if (now - data.getTimestamp() <= (7 * satuHariMs)) dataTersaring.add(data);
                    } else if (currentMode == 3) { // 30 HARI
                        if (now - data.getTimestamp() <= (30 * satuHariMs)) dataTersaring.add(data);
                    } else if (currentMode == 4) { // KALENDER
                        if (isSameDay(selectedCalendar.getTimeInMillis(), data.getTimestamp())) dataTersaring.add(data);
                    }
                }

                if (dataTersaring.isEmpty()) {
                    listRiwayatFinal.add(new ModelRiwayat(R.drawable.calendar, "#F2F7FF", "#858585", "Tidak Ada Riwayat", "Tidak ada data pada rentang waktu ini.", 0));
                } else {
                    Collections.sort(dataTersaring, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
                    String lastHeader = "";
                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
                    String todayStr = sdfDate.format(now);

                    for (ModelRiwayat item : dataTersaring) {
                        String itemDateStr = sdfDate.format(item.getTimestamp());
                        String headerText = itemDateStr.equals(todayStr) ? "Hari Ini" : itemDateStr;

                        if (!headerText.equals(lastHeader)) {
                            listRiwayatFinal.add(new ModelRiwayat(headerText));
                            lastHeader = headerText;
                        }
                        listRiwayatFinal.add(item);
                    }
                }
            }
            rvRiwayat.setAdapter(new AdapterRiwayat(listRiwayatFinal));
        }

        // ===================================
        // JIKA DI TAB GRAFIK
        // ===================================
        else {
            List<String> labelsX = getXAxisLabels(currentMode);
            if (isFutureDate) {
                setupChart(chartAir, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, currentMode), labelsX);
                setupChart(chartSuhu, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, currentMode), labelsX);
                setupChart(chartTanah, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, currentMode), labelsX);
                setupChart(chartTds, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, currentMode), labelsX);
            } else {
                setupChart(chartAir, "#3B82F6", "#DBEAFE", dataGrafikGenerator(labelsX.size(), 60, 100, currentMode), labelsX);
                setupChart(chartSuhu, "#EF4444", "#FEE2E2", dataGrafikGenerator(labelsX.size(), 20, 38, currentMode), labelsX);
                setupChart(chartTanah, "#8B5A2B", "#F5DEB3", dataGrafikGenerator(labelsX.size(), 10, 80, currentMode), labelsX);
                setupChart(chartTds, "#A855F7", "#F3E8FF", dataGrafikGenerator(labelsX.size(), 700, 1100, currentMode), labelsX);
            }
        }
    }

    private List<String> getXAxisLabels(int mode) {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH:00", Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM", new Locale("id", "ID"));
        Calendar now = Calendar.getInstance();

        if (mode == 1) { // HARI INI
            for (int i = 0; i < 6; i++) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 8 + (i * 2));
                labels.add(sdfHour.format(c.getTime()));
            }
        } else if (mode == 2) { // 7 HARI
            for (int i = 6; i >= 0; i--) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, -i);
                labels.add(sdfDate.format(c.getTime()));
            }
        } else if (mode == 3) { // BULAN
            int currentMonth = now.get(Calendar.MONTH);
            for (int i = 0; i <= currentMonth; i++) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, i);
                labels.add(sdfMonth.format(c.getTime()));
            }
        } else { // KALENDER
            for (int i = 0; i < 6; i++) {
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 8 + (i * 2));
                labels.add(sdfHour.format(selectedCalendar.getTime()));
            }
        }
        return labels;
    }

    private void setupChart(LineChart chart, String colorHex, String fillHex, ArrayList<Entry> entries, List<String> labels) {
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor(colorHex));
        dataSet.setCircleColor(Color.parseColor(colorHex));
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor(fillHex));

        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);
        chart.setData(lineData);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridColor(Color.parseColor("#F0F0F0"));
        chart.getAxisLeft().setTextColor(Color.parseColor("#A0A0A0"));
        chart.getAxisLeft().setAxisMinimum(0f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#A0A0A0"));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);

        chart.setScaleEnabled(false);
        chart.animateX(500);
        chart.invalidate();
    }

    private ArrayList<Entry> dataGrafikGenerator(int count, int min, int max, int mode) {
        ArrayList<Entry> e = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);

        for (int i = 0; i < count; i++) {
            float val = 0f;
            boolean isFuture = false;

            if (mode == 1) {
                int jamGrafik = 8 + (i * 2);
                if (jamGrafik > currentHour) {
                    isFuture = true;
                }
            }

            if (min == 0 && max == 0) {
                val = 0f;
            } else if (!isFuture) {
                val = min + (float)(Math.random() * (max - min));
            }

            e.add(new Entry((float)i, val));
        }
        return e;
    }

    private void bacaDataFirebase() {
        DataGlobal.initDataAwal();
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double air = getDouble(snapshot, "air");
                    double suhu = getDouble(snapshot, "suhu");
                    double tanah = getDouble(snapshot, "tanah");
                    double tds = getDouble(snapshot, "tds");

                    tvValAir.setText((int)air + "%");
                    tvValSuhu.setText(suhu + "°C");
                    tvValTanah.setText((int)tanah + "%");
                    tvValTds.setText((int)tds + " ppm");

                    // 1. AIR
                    if (air < 70) {
                        tvStatusAir.setText("Status: Kritis"); tvStatusAir.setTextColor(Color.parseColor("#FF4F3F"));
                        prosesNotif(1, "Air Tangki Menipis", "Kapasitas air sisa " + (int)air + "%.");
                        DataGlobal.statusAirBahaya = true;
                    } else {
                        tvStatusAir.setText("Status: Aman"); tvStatusAir.setTextColor(Color.parseColor("#858585"));
                        if (DataGlobal.statusAirBahaya) {
                            prosesNotif(2, "Kapasitas Air Normal", "Tangki air sudah terisi kembali.");
                            DataGlobal.statusAirBahaya = false;
                        }
                    }

                    // 2. TANAH
                    if (tanah < 24.8) {
                        tvStatusTanah.setText("Status: Kering"); tvStatusTanah.setTextColor(Color.parseColor("#FF4F3F"));
                        prosesNotif(1, "Kelembapan Tanah Rendah", "Tanah sangat kering berada di " + (int)tanah + "%.");
                        DataGlobal.statusTanahBahaya = true;
                    } else if (tanah > 31.8) {
                        tvStatusTanah.setText("Status: Basah"); tvStatusTanah.setTextColor(Color.parseColor("#FF4F3F"));
                        prosesNotif(1, "Kelembapan Tanah Berlebih", "Tanah sangat basah berada di " + (int)tanah + "%.");
                        DataGlobal.statusTanahBahaya = true;
                    } else {
                        tvStatusTanah.setText("Status: Normal"); tvStatusTanah.setTextColor(Color.parseColor("#858585"));
                        if (DataGlobal.statusTanahBahaya) {
                            prosesNotif(2, "Kelembapan Tanah Normal", "Tanah sudah pada kondisi ideal (" + (int)tanah + "%).");
                            DataGlobal.statusTanahBahaya = false;
                        }
                    }

                    // 3. SUHU
                    if (suhu < 10 || suhu > 38.0) {
                        prosesNotif(1, "Suhu Udara Tidak Normal", "Suhu saat ini " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = true;
                    } else if (DataGlobal.statusSuhuBahaya) {
                        prosesNotif(2, "Suhu Kembali Normal", "Suhu udara sudah stabil di " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = false;
                    }

                    // 4. TDS
                    if (tds < 840 || tds > 1050) {
                        prosesNotif(1, "Kadar Nutrisi Tidak Normal", "TDS berada di " + (int)tds + " PPM.");
                        DataGlobal.statusTdsBahaya = true;
                    } else if (DataGlobal.statusTdsBahaya) {
                        prosesNotif(2, "Kadar Nutrisi Normal", "Nutrisi tanaman (TDS) sudah optimal di " + (int)tds + " PPM.");
                        DataGlobal.statusTdsBahaya = false;
                    }

                    updateBadgeUI();

                    // Segarkan list/grafik saat ada update realtime
                    perbaruiDataTampilan();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Logika tambahan untuk MENYIMPAN RIWAYAT KE FIRESTORE
    private void prosesNotif(int type, String title, String desc) {
        // Cek dulu apakah notifikasi ini sudah ada di DataGlobal agar tidak spam berulang
        boolean isSudahAda = false;
        for (int i=0; i<DataGlobal.listNotifikasi.size(); i++) {
            if (DataGlobal.listNotifikasi.get(i).title.equals(title)) {
                isSudahAda = true; break;
            }
        }

        if (!isSudahAda) {
            // Tambahkan ke memori sementara (DataGlobal)
            DataGlobal.tambahNotifOtomatis(type, title, desc);

            // Simpan juga ke Firestore agar datanya tidak hilang saat aplikasi ditutup
            if (!uidUser.isEmpty()) {
                Map<String, Object> dataRiwayat = new HashMap<>();
                dataRiwayat.put("judul", title);
                dataRiwayat.put("deskripsi", desc);
                dataRiwayat.put("waktu", System.currentTimeMillis());

                firestore.collection("Users").document(uidUser)
                        .collection("RiwayatSensor")
                        .add(dataRiwayat);
            }
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

    private void setupFooterNavigasi() {
        navBeranda.setOnClickListener(v -> { startActivity(new Intent(GrafikActivity.this, DashboardActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish(); });
        navNotifikasi.setOnClickListener(v -> { startActivity(new Intent(GrafikActivity.this, NotifikasiActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish(); });
        navProfil.setOnClickListener(v -> { startActivity(new Intent(GrafikActivity.this, ProfilActivity.class)); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); finish(); });
    }

    @Override
    public void onBackPressed() { super.onBackPressed(); overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); }
}