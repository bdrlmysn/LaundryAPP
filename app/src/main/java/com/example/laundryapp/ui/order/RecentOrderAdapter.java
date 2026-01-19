package com.example.laundryapp.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.model.OrderEntity;
import com.example.laundryapp.util.FormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderAdapter.VH> {

    public interface OnItemClick {
        void onClick(OrderEntity item);
    }

    private final List<OrderEntity> data = new ArrayList<>();
    private final OnItemClick listener;

    public RecentOrderAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void submit(List<OrderEntity> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_order, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        OrderEntity o = data.get(position);

        h.tvTop.setText(o.orderCode + " • " + (o.customerName != null ? o.customerName : "-"));
        h.tvMid.setText((o.speed != null ? o.speed : "-") + " • " +
                (o.type != null ? o.type : "-") + " • " +
                String.format(Locale.US, "%.1f Kg", o.weight));

        h.tvBottom.setText(FormatUtil.rupiah(o.total) + " • " +
                (o.paymentStatus != null ? o.paymentStatus : "-") + " • " +
                (o.status != null ? o.status : "-"));

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(o);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTop, tvMid, tvBottom;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTop = itemView.findViewById(R.id.tvTop);
            tvMid = itemView.findViewById(R.id.tvMid);
            tvBottom = itemView.findViewById(R.id.tvBottom);
        }
    }
}
