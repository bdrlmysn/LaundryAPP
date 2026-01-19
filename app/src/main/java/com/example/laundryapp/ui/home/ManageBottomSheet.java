package com.example.laundryapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laundryapp.R;
import com.example.laundryapp.ui.customer.CustomerListActivity;
import com.example.laundryapp.ui.service.ServiceListActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ManageBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sheet_manage, container, false);

        v.findViewById(R.id.btnCustomers).setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), CustomerListActivity.class));
            dismiss();
        });

        v.findViewById(R.id.btnServices).setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), ServiceListActivity.class));
            dismiss();
        });

        return v;
    }
}
