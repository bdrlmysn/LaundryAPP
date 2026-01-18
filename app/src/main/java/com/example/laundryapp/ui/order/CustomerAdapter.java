package com.example.laundryapp.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.model.Customer;

import java.util.List;
import android.content.Intent;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.VH> {

    private final List<Customer> list;

    public CustomerAdapter(List<Customer> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Customer c = list.get(pos);
        h.tvAvatar.setText(c.initials);
        h.tvName.setText(c.name);
        h.tvPhone.setText(c.phone);
        h.tvLastOrder.setText("Last order: " + c.lastOrder);

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewOrderActivity.class);
            intent.putExtra("customer", c); // kirim data customer
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvPhone, tvLastOrder;

        VH(View v) {
            super(v);
            tvAvatar = v.findViewById(R.id.tvAvatar);
            tvName = v.findViewById(R.id.tvName);
            tvPhone = v.findViewById(R.id.tvPhone);
            tvLastOrder = v.findViewById(R.id.tvLastOrder);
        }
    }
}
