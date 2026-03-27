package edu.uph.m23si1.aplikasigrow.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import edu.uph.m23si1.aplikasigrow.R;
import edu.uph.m23si1.aplikasigrow.model.ModelNotifikasi;

public class AdapterNotifikasi extends RecyclerView.Adapter<AdapterNotifikasi.ViewHolder> {

    private List<ModelNotifikasi> listNotifikasi;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onMarkReadClick(int position);
    }

    public AdapterNotifikasi(List<ModelNotifikasi> listNotifikasi, OnItemClickListener listener) {
        this.listNotifikasi = listNotifikasi;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifikasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelNotifikasi item = listNotifikasi.get(position);

        // 1. Memasang Teks
        holder.tvTitle.setText(item.title);
        holder.tvDesc.setText(item.desc);
        holder.tvTime.setText(item.time);

        // 2. Logika Titik Merah (Tanda Belum Dibaca)
        if (item.isRead) {
            holder.dotUnread.setVisibility(View.GONE);
        } else {
            holder.dotUnread.setVisibility(View.VISIBLE);
        }

        // 3. Gambar Ikon Asli & Warna Garis
        // Jika namanya berbeda, ubah bagian R.drawable.nama_gambarmu di bawah ini.
        if (item.type == 1) {
            // Tipe Warning (Garis Orange, Ikon Tanda Seru)
            holder.cardNotif.setStrokeColor(Color.parseColor("#FF9340"));
            // HAPUS TINT LAMA: holder.ivIcon.setColorFilter(null);
            holder.ivIcon.clearColorFilter(); // Menghapus filter warna paksaan
            holder.ivIcon.setImageResource(R.drawable.warning); // Ganti dengan nama ikon tanda seru aslimu

        } else if (item.type == 2) {
            // Tipe Normal/Success (Garis Hijau, Ikon Centang)
            holder.cardNotif.setStrokeColor(Color.parseColor("#107432"));
            holder.ivIcon.clearColorFilter();
            holder.ivIcon.setImageResource(R.drawable.check); // GANTI INI dengan ikon centang hijaumu!

        } else {
            // Tipe Info (Garis Biru, Ikon Huruf i)
            holder.cardNotif.setStrokeColor(Color.parseColor("#0580F4"));
            holder.ivIcon.clearColorFilter();
            holder.ivIcon.setImageResource(R.drawable.information); // GANTI INI dengan ikon info birumu!
        }

        // 4. Aksi Tombol Hapus (Tong Sampah)
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });

        // 5. Aksi Klik Kotak Notifikasi (Tandai telah dibaca)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && !item.isRead) {
                listener.onMarkReadClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNotifikasi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardNotif, dotUnread;
        ImageView ivIcon, btnDelete;
        TextView tvTitle, tvDesc, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNotif = itemView.findViewById(R.id.cardNotif);
            dotUnread = itemView.findViewById(R.id.dotUnread);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
