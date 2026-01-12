package com.example.laundryapp.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

import com.example.laundryapp.R;
import com.example.laundryapp.data.model.HistoryOrder;
import com.example.laundryapp.ui.order.OrderDetailActivity;


import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<HistoryOrder> list;

    public HistoryAdapter(List<HistoryOrder> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        HistoryOrder o = list.get(pos);

        h.tvOrderId.setText(o.orderId);
        h.tvCustomer.setText(o.customer);
        h.tvService.setText(o.service);
        h.tvDetail.setText(o.detail);
        h.tvPrice.setText(o.price);
        h.tvStatus.setText(o.status);
        h.tvTime.setText(o.time);

        // 👉 CLICK LISTENER DI SINI
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
            intent.putExtra("order", o);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomer, tvService, tvDetail, tvPrice, tvStatus, tvTime;

        ViewHolder(View v) {
            super(v);
            tvOrderId = v.findViewById(R.id.tvOrderId);
            tvCustomer = v.findViewById(R.id.tvCustomer);
            tvService = v.findViewById(R.id.tvService);
            tvDetail = v.findViewById(R.id.tvDetail);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
