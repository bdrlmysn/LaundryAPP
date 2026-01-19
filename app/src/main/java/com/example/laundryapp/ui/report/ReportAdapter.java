package com.example.laundryapp.ui.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.model.OrderEntity;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.LaundryLabel;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.VH> {

    private final List<OrderEntity> items = new ArrayList<>();

    public void submit(List<OrderEntity> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_order, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        OrderEntity o = items.get(pos);
        h.tvOrderCode.setText(o.orderCode);
        h.tvCustomer.setText(o.customerName);
        h.tvTime.setText(FormatUtil.dt(o.createdAt));
        h.tvService.setText(LaundryLabel.speedLabel(o.speed) + " - " + LaundryLabel.typeLabel(o.type));
        h.tvTotal.setText(FormatUtil.rupiah(o.total));
        h.tvStatus.setText(LaundryLabel.statusLabel(o.status) + " • " + o.paymentStatus);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvOrderCode, tvCustomer, tvTime, tvService, tvStatus, tvTotal;
        VH(View v) {
            super(v);
            tvOrderCode = v.findViewById(R.id.tvOrderCode);
            tvCustomer = v.findViewById(R.id.tvCustomer);
            tvTime = v.findViewById(R.id.tvTime);
            tvService = v.findViewById(R.id.tvService);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvTotal = v.findViewById(R.id.tvTotal);
        }
    }
}
