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

    public static boolean statusSuhuBahaya = false;
    public static boolean statusTanahBahaya = false;
    public static boolean statusAirBahaya = false;
    public static boolean statusTdsBahaya = false;

    // Pelacak angka terakhir
    public static double lastAlertSuhu = -999;
    public static double lastAlertTanah = -999;
    public static double lastAlertAir = -999;
    public static double lastAlertTds = -999;

    public static void initDataAwal() {
        if (!isInitialized) {
            long waktuSekarang = System.currentTimeMillis();
            listRiwayatGlobal.add(new ModelRiwayat(R.drawable.water_waves, "#DBEAFE", "#3B82F6", "Sistem Dimulai", "Aplikasi IGrow terhubung dengan alat.", waktuSekarang));
            isInitialized = true;
        }
    }

    public static void tambahNotifOtomatis(int type, String title, String desc) {
        // Cek Anti-Spam: Jika judul DAN isinya sama persis dengan yang ada, baru ditolak
        for (ModelNotifikasi n : listNotifikasi) {
            if (n.title.equals(title) && n.desc.equals(desc)) return;
        }

        // KITA TIDAK LAGI MENGHAPUS NOTIFIKASI LAMA DI SINI
        // Semua data yang masuk akan ditumpuk dari atas!

        listNotifikasi.add(0, new ModelNotifikasi("auto", type, title, desc, "Baru saja", false));

        int icon = R.drawable.warning;
        String bgColor = "#FFE1AD";
        String txtColor = "#DD961C";
        String t = title.toLowerCase();

        if (t.contains("air") || t.contains("tangki")) {
            icon = R.drawable.water_waves; bgColor = "#DBEAFE"; txtColor = "#3B82F6";
        } else if (t.contains("suhu") || t.contains("panas") || t.contains("dingin")) {
            icon = R.drawable.temperature; bgColor = "#FEE2E2"; txtColor = "#EF4444";
        } else if (t.contains("tanah") || t.contains("kering") || t.contains("basah")) {
            icon = R.drawable.meter; bgColor = "#FFE1AD"; txtColor = "#DD961C";
        } else if (t.contains("tds") || t.contains("nutrisi")) {
            icon = R.drawable.potion; bgColor = "#F3E8FF"; txtColor = "#A855F7";
        }

        if (t.contains("normal") || t.contains("ideal") || t.contains("optimal")) {
            bgColor = "#D6FFD2"; txtColor = "#107432";
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", new Locale("id", "ID"));
        String timeStr = sdf.format(c.getTime());

        listRiwayatGlobal.add(0, new ModelRiwayat(icon, bgColor, txtColor, title, "[" + timeStr + "] " + desc, c.getTimeInMillis()));
    }

    public static int getJumlahBelumDibaca() {
        int count = 0;
        for (ModelNotifikasi n : listNotifikasi) {
            if (!n.isRead) count++;
        }
        return count;
    }
}