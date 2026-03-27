package edu.uph.m23si1.aplikasigrow.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
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
import edu.uph.m23si1.aplikasigrow.model.ModelRiwayat;

public class AdapterRiwayat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ModelRiwayat> list;

    public AdapterRiwayat(List<ModelRiwayat> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getItemType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Jika Tipe Header (Teks Tanggal)
        if (viewType == ModelRiwayat.TYPE_HEADER) {
            TextView tvHeader = new TextView(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Memberi jarak spasi
            params.setMargins(0, 48, 0, 16);
            tvHeader.setLayoutParams(params);

            tvHeader.setTextSize(16f);
            tvHeader.setTextColor(Color.parseColor("#777777"));
            tvHeader.setTypeface(null, Typeface.BOLD);

            return new HeaderViewHolder(tvHeader);
        }
        // Jika Tipe Data (Kotak Riwayat)
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat, parent, false);
            return new DataViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelRiwayat model = list.get(position);

        if (getItemViewType(position) == ModelRiwayat.TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvHeader.setText(model.getHeaderTanggal());
        } else {
            DataViewHolder dataHolder = (DataViewHolder) holder;

            dataHolder.tvTitle.setText(model.getTitle());
            dataHolder.tvDesc.setText(model.getDesc());

            // Pasang Ikon
            dataHolder.ivIcon.setImageResource(model.getIconRes());

            try {
                // Background kotak tetap berubah warna
                dataHolder.cardIconBg.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(model.getBgColorHex())));

                // MENGHAPUS TINT/COLOR FILTER AGAR WARNA ASLI GAMBAR MUNCUL
                dataHolder.ivIcon.setColorFilter(null);
            } catch (Exception e) {
                dataHolder.cardIconBg.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                dataHolder.ivIcon.setColorFilter(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Holder Khusus Teks Tanggal
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView;
        }
    }

    // Holder Khusus Kotak Data
    static class DataViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardIconBg;
        ImageView ivIcon;
        TextView tvTitle, tvDesc;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            cardIconBg = itemView.findViewById(R.id.cardIconBg);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
        }
    }
}