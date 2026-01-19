package com.example.laundryapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.OrderDao;
import com.example.laundryapp.data.db.model.OrderEntity;
import com.example.laundryapp.data.model.HistoryOrder;
import com.example.laundryapp.ui.history.HistoryAdapter;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.LaundryLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rv = view.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        load();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        if (getContext() == null) return;

        OrderDao dao = new OrderDao(getContext());
        long now = System.currentTimeMillis();

        List<OrderEntity> rows = dao.getHistory(0, now, "ALL");
        List<HistoryOrder> list = new ArrayList<>();

        for (OrderEntity o : rows) {
            String service = LaundryLabel.speedLabel(o.speed) + " - " + LaundryLabel.typeLabel(o.type);
            String detail = String.format(Locale.US, "%.1f kg", o.weight);
            String price = FormatUtil.rupiah(o.total);
            String status = LaundryLabel.statusLabel(o.status);
            String time = FormatUtil.dt(o.createdAt);

            list.add(new HistoryOrder(
                    o.orderCode,
                    o.customerName,
                    service,
                    detail,
                    price,
                    status,
                    o.paymentStatus,
                    time,
                    o.note
            ));
        }

        rv.setAdapter(new HistoryAdapter(list));
    }
}
