package edu.uph.m23si1.aplikasigrow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import edu.uph.m23si1.aplikasigrow.model.ModelNotifikasi;
import edu.uph.m23si1.aplikasigrow.model.ModelRiwayat;

public class DataGlobal {
    public static List<ModelNotifikasi> listNotifikasi = new ArrayList<>();
    public static List<ModelRiwayat> listRiwayatGlobal = new ArrayList<>();
    public static boolean isInitialized = false;

    // --- VARIABEL PENANDA STATUS (Wajib ada agar tidak spam notif normal) ---
    public static boolean statusSuhuBahaya = false;
    public static boolean statusTanahBahaya = false;
    public static boolean statusAirBahaya = false;
    public static boolean statusTdsBahaya = false;
    public static boolean statusCahayaBahaya = false;

    public static void initDataAwal() {
        if (!isInitialized) {
            long waktuSekarang = System.currentTimeMillis();
            listRiwayatGlobal.add(new ModelRiwayat(R.drawable.water_waves, "#DBEAFE", "#3B82F6", "Sistem Dimulai", "Aplikasi IGrow terhubung dengan alat.", waktuSekarang));
            isInitialized = true;
        }
    }

    public static void tambahNotifOtomatis(int type, String title, String desc) {
        // Cek Anti-Spam (Jangan masukkan jika judul yang sama persis sudah ada)
        for (ModelNotifikasi n : listNotifikasi) {
            if (n.title.equals(title)) return;
        }

        // Tambah ke daftar Notifikasi (layar Notifikasi)
        listNotifikasi.add(0, new ModelNotifikasi("auto", type, title, desc, "Baru saja", false));

        // Tentukan Ikon & Warna Default untuk Tab Riwayat
        int icon = R.drawable.warning; // Pastikan ada R.drawable.warning
        String bgColor = "#FFE1AD";
        String txtColor = "#DD961C";

        String t = title.toLowerCase();

        // 1. DETEKSI SENSOR: Tentukan Ikon dan Warna Warning-nya
        if (t.contains("air")) {
            icon = R.drawable.water_waves;
            bgColor = "#DBEAFE";
            txtColor = "#3B82F6";
        }
        else if (t.contains("suhu")) {
            icon = R.drawable.temperature;
            bgColor = "#FEE2E2";
            txtColor = "#EF4444";
        }
        else if (t.contains("tanah")) {
            icon = R.drawable.meter;
            bgColor = "#FFE1AD";
            txtColor = "#DD961C";
        }
        else if (t.contains("tds") || t.contains("nutrisi")) {
            icon = R.drawable.potion;
            bgColor = "#F3E8FF";
            txtColor = "#A855F7";
        }
        else if (t.contains("cahaya") || t.contains("terik") || t.contains("gelap") || t.contains("kurang")) {
            icon = R.drawable.sun;
            bgColor = "#FFF5CC";
            txtColor = "#ECB800";
        }

        // 2. TIMPA WARNA JIKA KONDISINYA AMAN (Ikon tetap memakai ikon sensor di atas)
        if (t.contains("normal") || t.contains("ideal") || t.contains("optimal")) {
            // Perhatikan: icon tidak diubah lagi di sini agar sesuai dengan sensornya
            bgColor = "#D6FFD2";
            txtColor = "#107432";
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));
        String timeStr = sdf.format(c.getTime());

        // Tambah ke Tab Riwayat (GrafikActivity)
        listRiwayatGlobal.add(0, new ModelRiwayat(icon, bgColor, txtColor, title, "[" + timeStr + "] " + desc, c.getTimeInMillis()));
    }

    public static int getJumlahBelumDibaca() {
        int count = 0;
        for (ModelNotifikasi n : listNotifikasi) { if (!n.isRead) count++; }
        return count;
    }
}