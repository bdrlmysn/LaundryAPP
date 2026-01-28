package com.example.laundryapp.ui.service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.model.ServiceEntity;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.LaundryLabel;

import java.util.ArrayList;
import java.util.List;

public class ServiceManageAdapter extends RecyclerView.Adapter<ServiceManageAdapter.VH> {

    public interface Listener {
        void onEdit(ServiceEntity s);
        void onDelete(ServiceEntity s);
    }

    private final Listener listener;
    private final List<ServiceEntity> items = new ArrayList<>();

    public ServiceManageAdapter(Listener l) { this.listener = l; }

    public void submit(List<ServiceEntity> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_manage, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        ServiceEntity s = items.get(pos);

        h.tvTitle.setText(LaundryLabel.speedLabel(s.speed) + " - " + LaundryLabel.typeLabel(s.type));
        h.tvPrice.setText(FormatUtil.rupiah(s.pricePerKg) + " / Kg");
        h.tvActive.setText(s.active ? "ACTIVE" : "INACTIVE");

        // NEW: estimasi pengerjaan (hanya 4 jam / 1 hari / 2 hari)
        if (h.tvServiceEstimate != null) {
            h.tvServiceEstimate.setText("Estimasi: " + speedDurationShort(s.speed));
        }

        h.btnEdit.setOnClickListener(v -> listener.onEdit(s));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(s));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvServiceEstimate, tvActive;
        ImageButton btnEdit, btnDelete;

        VH(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvServiceEstimate = v.findViewById(R.id.tvServiceEstimate); // sesuai XML kamu
            tvActive = v.findViewById(R.id.tvActive);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }

    // Mapping dari speed di database:
    // INSTANT -> 4 jam, KILAT -> 1 hari, REGULER -> 2 hari
    private String speedDurationShort(String speedCode) {
        if ("INSTANT".equalsIgnoreCase(speedCode)) return "4 jam";
        if ("KILAT".equalsIgnoreCase(speedCode)) return "1 hari";
        return "2 hari"; // REGULER + fallback aman
    }
}
