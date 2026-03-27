package edu.uph.m23si1.aplikasigrow.model;
public class ModelRiwayat {
    // Penanda untuk membedakan Header (Tanggal) dan Kotak Data
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_DATA = 1;

    private int itemType;
    private String headerTanggal;

    // Atribut untuk kotak data normal / info kosong
    private int iconRes;
    private String bgColorHex;
    private String iconColorHex;
    private String title;
    private String desc;

    // Variabel krusial untuk mencatat waktu asli (Agar bisa difilter 7 hari / hari ini)
    private long timestamp;

    // =========================================================
    // CONSTRUCTOR 1: KHUSUS UNTUK MEMBUAT PEMISAH TANGGAL
    // Hanya butuh teks (Misal: "Hari Ini" atau "18 Maret 2026")
    // =========================================================
    public ModelRiwayat(String headerTanggal) {
        this.itemType = TYPE_HEADER;
        this.headerTanggal = headerTanggal;
        this.timestamp = 0; // Header tidak butuh waktu milidetik
    }

    // =========================================================
    // CONSTRUCTOR 2: KHUSUS UNTUK KOTAK DATA & INFO KOSONG
    // Butuh icon, warna, judul, deskripsi, dan waktu (timestamp)
    // =========================================================
    public ModelRiwayat(int iconRes, String bgColorHex, String iconColorHex, String title, String desc, long timestamp) {
        this.itemType = TYPE_DATA;
        this.iconRes = iconRes;
        this.bgColorHex = bgColorHex;
        this.iconColorHex = iconColorHex;
        this.title = title;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    // =========================================================
    // GETTER (Fungsi Pengambil Data untuk Adapter & Grafik)
    // =========================================================
    public int getItemType() { return itemType; }
    public String getHeaderTanggal() { return headerTanggal; }

    public int getIconRes() { return iconRes; }
    public String getBgColorHex() { return bgColorHex; }
    public String getIconColorHex() { return iconColorHex; }
    public String getTitle() { return title; }
    public String getDesc() { return desc; }
    public long getTimestamp() { return timestamp; }
}