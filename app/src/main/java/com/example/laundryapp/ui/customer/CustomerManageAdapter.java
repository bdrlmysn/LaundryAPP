package com.example.laundryapp.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.model.CustomerEntity;
import com.example.laundryapp.util.FormatUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomerManageAdapter extends RecyclerView.Adapter<CustomerManageAdapter.VH> {

    public interface Listener {
        void onEdit(CustomerEntity c);
        void onDelete(CustomerEntity c);
    }

    private final Listener listener;
    private final List<CustomerEntity> items = new ArrayList<>();

    public CustomerManageAdapter(Listener l) {
        this.listener = l;
    }

    public void submit(List<CustomerEntity> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer_manage, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CustomerEntity c = items.get(position);
        h.tvName.setText(c.name);
        h.tvPhone.setText(c.phone);
        h.tvAvatar.setText(FormatUtil.initials(c.name));

        h.btnEdit.setOnClickListener(v -> listener.onEdit(c));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(c));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvPhone;
        ImageButton btnEdit, btnDelete;

        VH(View v) {
            super(v);
            tvAvatar = v.findViewById(R.id.tvAvatar);
            tvName = v.findViewById(R.id.tvName);
            tvPhone = v.findViewById(R.id.tvPhone);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
