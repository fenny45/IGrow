package edu.uph.m23si1.aplikasigrow;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.uph.m23si1.aplikasigrow.adapter.AdapterRiwayat;
import edu.uph.m23si1.aplikasigrow.model.ModelRiwayat;

public class GrafikActivity extends AppCompatActivity {

    private LinearLayout tabGrafik, tabRiwayat;
    private TextView tvTabGrafik, tvTabRiwayat;
    private View lineGrafik, lineRiwayat;
    private LinearLayout layoutGrafik, layoutRiwayat;

    private MaterialCardView cardFilter1, cardFilter2, cardFilter3, btnCalendar;
    private TextView tvFilter1, tvFilter2, tvFilter3, tvTanggalRiwayat;

    private LineChart chartCahaya, chartAir, chartSuhu, chartTanah, chartTds;
    private TextView tvValCahaya, tvValAir, tvValSuhu, tvValTanah, tvValTds;
    private TextView tvStatusAir, tvStatusTanah;

    private RecyclerView rvRiwayat;
    private LinearLayout navBeranda, navNotifikasi, navProfil;
    private MaterialCardView tvBadgeFooter;
    private TextView tvBadgeAngka;

    private DatabaseReference databaseRef;
    private int currentMode = 1;
    private boolean isModeGrafik = true;
    private Calendar selectedCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        databaseRef = FirebaseDatabase.getInstance().getReference("iGrow_Data/sensor");
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

        chartCahaya = findViewById(R.id.chartCahaya);
        chartAir = findViewById(R.id.chartAir);
        chartSuhu = findViewById(R.id.chartSuhu);
        chartTanah = findViewById(R.id.chartTanah);
        chartTds = findViewById(R.id.chartTds);

        tvValCahaya = findViewById(R.id.tvValCahaya);
        tvValAir = findViewById(R.id.tvValAir);
        tvValSuhu = findViewById(R.id.tvValSuhu);
        tvValTanah = findViewById(R.id.tvValTanah);
        tvValTds = findViewById(R.id.tvValTds);
        tvStatusAir = findViewById(R.id.tvStatusAir);
        tvStatusTanah = findViewById(R.id.tvStatusTanah);

        rvRiwayat = findViewById(R.id.rvRiwayat);
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

            tvFilter1.setText("Semua"); tvFilter2.setText("Hari ini"); tvFilter3.setText("7 Hari");
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
                    tvTanggalRiwayat.setText("Riwayat Tanggal: " + formattedDate);
                    perbaruiDataTampilan(4);
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
            labelTanggal = isModeGrafik ? "Hari ini, Data Terbaru" : "Semua Riwayat Alat";
        } else if (posisiAktif == 2) {
            cardFilter2.setCardBackgroundColor(Color.parseColor("#D6FFD2")); tvFilter2.setTextColor(Color.parseColor("#17BA09"));
            labelTanggal = isModeGrafik ? "Rekapan 7 Hari Terakhir" : "Riwayat Hari Ini";
        } else if (posisiAktif == 3) {
            cardFilter3.setCardBackgroundColor(Color.parseColor("#D6FFD2")); tvFilter3.setTextColor(Color.parseColor("#17BA09"));
            labelTanggal = isModeGrafik ? "Rekapan Per Bulan" : "Riwayat 7 Hari Terakhir";
        }

        tvTanggalRiwayat.setText(labelTanggal);
        perbaruiDataTampilan(currentMode);
    }

    private boolean isSameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance(); cal1.setTimeInMillis(time1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void perbaruiDataTampilan(int mode) {
        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));
        List<ModelRiwayat> listRiwayatFinal = new ArrayList<>();

        boolean isFutureDate = false;
        if (mode == 4) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0);
            Calendar selected = (Calendar) selectedCalendar.clone();
            selected.set(Calendar.HOUR_OF_DAY, 0); selected.set(Calendar.MINUTE, 0); selected.set(Calendar.SECOND, 0);
            if (selected.after(today)) {
                isFutureDate = true;
            }
        }

        if (isFutureDate) {
            listRiwayatFinal.add(new ModelRiwayat(R.drawable.calendar, "#F2F7FF", "#858585", "Belum Ada Data", "Tidak ada data yang terekam karena tanggal ini belum terjadi.", 0));
        } else if (!isModeGrafik) {
            List<ModelRiwayat> dataTersaring = new ArrayList<>();
            long now = System.currentTimeMillis();
            long satuHariMs = 24L * 60 * 60 * 1000L;

            for (ModelRiwayat data : DataGlobal.listRiwayatGlobal) {
                if (data.getItemType() == ModelRiwayat.TYPE_HEADER) continue;

                if (mode == 1) { // SEMUA
                    dataTersaring.add(data);
                } else if (mode == 2) { // HARI INI
                    if (isSameDay(now, data.getTimestamp())) dataTersaring.add(data);
                } else if (mode == 3) { // 7 HARI
                    if (now - data.getTimestamp() <= (7 * satuHariMs)) dataTersaring.add(data);
                } else if (mode == 4) { // KALENDER
                    if (isSameDay(selectedCalendar.getTimeInMillis(), data.getTimestamp())) dataTersaring.add(data);
                }
            }

            if (dataTersaring.isEmpty()) {
                listRiwayatFinal.add(new ModelRiwayat(R.drawable.calendar, "#F2F7FF", "#858585", "Belum Ada Data", "Tidak ada riwayat pada rentang waktu ini.", 0));
            } else {
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
        } else {
            // Tab Grafik Keterangan
            listRiwayatFinal.add(new ModelRiwayat(R.drawable.sun, "#FFF5CC", "#ECB800", "Pemantauan Cahaya", "Grafik cahaya terpantau stabil pada rentang waktu ini.", 0));
            listRiwayatFinal.add(new ModelRiwayat(R.drawable.water_waves, "#DBEAFE", "#3B82F6", "Pemantauan Air", "Tingkat air selalu terjaga di atas batas kritis.", 0));
        }

        rvRiwayat.setAdapter(new AdapterRiwayat(listRiwayatFinal));

        // --- MENGISI TAB GRAFIK ---
        List<String> labelsX = getXAxisLabels(mode);
        if (isFutureDate) {
            setupChart(chartCahaya, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, mode), labelsX);
            setupChart(chartAir, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, mode), labelsX);
            setupChart(chartSuhu, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, mode), labelsX);
            setupChart(chartTanah, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, mode), labelsX);
            setupChart(chartTds, "#D3D3D3", "#F0F0F0", dataGrafikGenerator(labelsX.size(), 0, 0, mode), labelsX);
        } else {
            setupChart(chartCahaya, "#ECB800", "#FFF5CC", dataGrafikGenerator(labelsX.size(), 1000, 90000, mode), labelsX);
            setupChart(chartAir, "#3B82F6", "#DBEAFE", dataGrafikGenerator(labelsX.size(), 60, 100, mode), labelsX);
            setupChart(chartSuhu, "#EF4444", "#FEE2E2", dataGrafikGenerator(labelsX.size(), 20, 38, mode), labelsX);
            setupChart(chartTanah, "#8B5A2B", "#F5DEB3", dataGrafikGenerator(labelsX.size(), 10, 80, mode), labelsX);
            setupChart(chartTds, "#A855F7", "#F3E8FF", dataGrafikGenerator(labelsX.size(), 700, 1100, mode), labelsX);
        }
    }

    private List<String> getXAxisLabels(int mode) {
        List<String> labels = new ArrayList<>();
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH:00", Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM", new Locale("id", "ID"));
        Calendar now = Calendar.getInstance();

        if (isModeGrafik) {
            if (mode == 1) { // HARI INI (Jam 08:00 sampai 18:00)
                for (int i = 0; i < 6; i++) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 8 + (i * 2));
                    labels.add(sdfHour.format(c.getTime()));
                }
            } else if (mode == 2) { // 7 HARI LALU
                for (int i = 6; i >= 0; i--) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_YEAR, -i);
                    labels.add(sdfDate.format(c.getTime()));
                }
            } else if (mode == 3) { // BULAN (Jan sampai Sekarang)
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
        } else {
            // MODE RIWAYAT
            if (mode == 1) {
                int currentMonth = now.get(Calendar.MONTH);
                for (int i = 0; i <= currentMonth; i++) { Calendar c = Calendar.getInstance(); c.set(Calendar.MONTH, i); labels.add(sdfMonth.format(c.getTime())); }
            } else if (mode == 2) {
                for (int i = 5; i >= 0; i--) { Calendar c = Calendar.getInstance(); c.add(Calendar.HOUR_OF_DAY, -i); labels.add(sdfHour.format(c.getTime())); }
            } else if (mode == 3) {
                for (int i = 6; i >= 0; i--) { Calendar c = Calendar.getInstance(); c.add(Calendar.DAY_OF_YEAR, -i); labels.add(sdfDate.format(c.getTime())); }
            } else {
                for (int i = 0; i < 6; i++) { selectedCalendar.set(Calendar.HOUR_OF_DAY, 8 + (i * 2)); labels.add(sdfHour.format(selectedCalendar.getTime())); }
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

    // =================================================================
    // FUNGSI PENGHASIL DATA GRAFIK CERDAS (TIDAK MENEMBUS MASA DEPAN)
    // =================================================================
    private ArrayList<Entry> dataGrafikGenerator(int count, int min, int max, int mode) {
        ArrayList<Entry> e = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);

        for (int i = 0; i < count; i++) {
            float val = 0f;
            boolean isFuture = false;

            if (isModeGrafik && mode == 1) {
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
                    double cahaya = getDouble(snapshot, "cahaya");
                    double air = getDouble(snapshot, "air");
                    double suhu = getDouble(snapshot, "suhu");
                    double tanah = getDouble(snapshot, "tanah");
                    double tds = getDouble(snapshot, "tds");

                    tvValCahaya.setText(String.format("%,d Lux", (int)cahaya).replace(',', '.'));
                    tvValAir.setText((int)air + "%");
                    tvValSuhu.setText(suhu + "°C");
                    tvValTanah.setText((int)tanah + "%");
                    tvValTds.setText((int)tds + " ppm");

                    // ================= LOGIKA SINKRON DENGAN DASHBOARD =================

                    // 1. AIR
                    if (air < 70) {
                        tvStatusAir.setText("Status: Kritis"); tvStatusAir.setTextColor(Color.parseColor("#FF4F3F"));
                        DataGlobal.tambahNotifOtomatis(1, "Air Tangki Menipis", "Kapasitas air sisa " + (int)air + "%.");
                        DataGlobal.statusAirBahaya = true;
                    } else {
                        tvStatusAir.setText("Status: Aman"); tvStatusAir.setTextColor(Color.parseColor("#858585"));
                        if (DataGlobal.statusAirBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Kapasitas Air Normal", "Tangki air sudah terisi kembali.");
                            DataGlobal.statusAirBahaya = false;
                        }
                    }

                    // 2. TANAH
                    if (tanah < 24.8) {
                        tvStatusTanah.setText("Status: Perlu Siram"); tvStatusTanah.setTextColor(Color.parseColor("#FF4F3F"));
                        DataGlobal.tambahNotifOtomatis(1, "Kelembapan Tanah Rendah", "Kelembapan turun jadi " + (int)tanah + "%. Disarankan untuk menyiram.");
                        DataGlobal.statusTanahBahaya = true;
                    } else if (tanah > 31.8) {
                        tvStatusTanah.setText("Status: Terlalu Basah"); tvStatusTanah.setTextColor(Color.parseColor("#FF4F3F"));
                        DataGlobal.tambahNotifOtomatis(1, "Kelembapan Tanah Berlebih", "Tanah sangat basah (" + (int)tanah + "%).");
                        DataGlobal.statusTanahBahaya = true;
                    } else {
                        tvStatusTanah.setText("Status: Optimal"); tvStatusTanah.setTextColor(Color.parseColor("#858585"));
                        if (DataGlobal.statusTanahBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Kelembapan Tanah Normal", "Tanah sudah cukup air (" + (int)tanah + "%).");
                            DataGlobal.statusTanahBahaya = false;
                        }
                    }

                    // 3. SUHU
                    if (suhu < 10 || suhu > 38.0) {
                        DataGlobal.tambahNotifOtomatis(1, "Suhu Udara Tidak Normal", "Suhu saat ini " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = true;
                    } else if (DataGlobal.statusSuhuBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Suhu Kembali Normal", "Suhu udara sudah stabil di " + suhu + "°C.");
                        DataGlobal.statusSuhuBahaya = false;
                    }

                    // 4. TDS
                    if (tds < 840 || tds > 1050) {
                        DataGlobal.tambahNotifOtomatis(1, "Kadar Nutrisi Tidak Normal", "TDS berada di " + (int)tds + " PPM.");
                        DataGlobal.statusTdsBahaya = true;
                    } else if (DataGlobal.statusTdsBahaya) {
                        DataGlobal.tambahNotifOtomatis(2, "Kadar Nutrisi Normal", "Nutrisi tanaman (TDS) sudah optimal di " + (int)tds + " PPM.");
                        DataGlobal.statusTdsBahaya = false;
                    }

                    // 5. CAHAYA (SINKRON: < 80rb Warning, > 100rb Tinggi)
                    if (cahaya < 80000) {
                        DataGlobal.tambahNotifOtomatis(1, "Cahaya Kurang", "Intensitas cahaya hanya " + (int)cahaya + " Lux.");
                        DataGlobal.statusCahayaBahaya = true;
                    } else if (cahaya > 100000) {
                        DataGlobal.tambahNotifOtomatis(1, "Cahaya Terik Ekstrem", "Cahaya terlalu panas (" + (int)cahaya + " Lux).");
                        DataGlobal.statusCahayaBahaya = true;
                    } else {
                        // KONDISI OPTIMAL (80.000 - 100.000)
                        if (DataGlobal.statusCahayaBahaya) {
                            DataGlobal.tambahNotifOtomatis(2, "Cahaya Kembali Normal", "Cahaya sudah berada di titik optimal.");
                            DataGlobal.statusCahayaBahaya = false;
                        }
                    }

                    // Update Badge Footer secara Real-time
                    updateBadgeUI();

                    // Segarkan list di Tab Riwayat agar langsung muncul notif barunya
                    if(!isModeGrafik) perbaruiDataTampilan(currentMode);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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