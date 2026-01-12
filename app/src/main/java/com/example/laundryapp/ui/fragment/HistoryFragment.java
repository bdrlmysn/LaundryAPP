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
import com.example.laundryapp.data.model.HistoryOrder;
import com.example.laundryapp.ui.history.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView rv = view.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<HistoryOrder> dummy = new ArrayList<>();
        dummy.add(new HistoryOrder(
                "#ORD-4829",
                "Sarah Jenkins",
                "Express Kiloan",
                "3.5 kg • 4 Hours",
                "Rp 52.500",
                "Processing",
                "10:30 AM"
        ));
        dummy.add(new HistoryOrder(
                "#ORD-4828",
                "Michael Chen",
                "Standard Kiloan",
                "6.2 kg • 2 Days",
                "Rp 43.400",
                "Ready for Pickup",
                "09:15 AM"
        ));

        rv.setAdapter(new HistoryAdapter(dummy));
        return view;
    }
}
