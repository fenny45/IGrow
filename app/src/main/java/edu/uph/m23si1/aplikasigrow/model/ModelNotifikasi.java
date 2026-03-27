package edu.uph.m23si1.aplikasigrow.model;


public class ModelNotifikasi {
    public String id;
    public int type; // 1 = Warning (Orange), 2 = Normal/Success (Hijau), 3 = Info (Biru)
    public String title;
    public String desc;
    public String time;
    public boolean isRead;

    public ModelNotifikasi(String id, int type, String title, String desc, String time, boolean isRead) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.time = time;
        this.isRead = isRead;
    }
}
