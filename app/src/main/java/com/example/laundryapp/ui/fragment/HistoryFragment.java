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
import com.example.laundryapp.data.repository.OrderRepository;
import com.example.laundryapp.ui.history.HistoryAdapter;

public class HistoryFragment extends Fragment {

    RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rv = view.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        rv.setAdapter(new HistoryAdapter(OrderRepository.getAll()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh ketika balik dari Payment / Detail
        rv.setAdapter(new HistoryAdapter(OrderRepository.getAll()));
    }
}
