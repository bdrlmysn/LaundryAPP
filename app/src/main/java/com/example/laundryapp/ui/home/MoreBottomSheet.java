package com.example.laundryapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.laundryapp.R;
import com.example.laundryapp.ui.login.LoginActivity;
import com.example.laundryapp.ui.report.ReportActivity;
import com.example.laundryapp.util.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MoreBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.sheet_more, container, false);

        v.findViewById(R.id.btnReports).setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), ReportActivity.class));
            dismiss();
        });

        v.findViewById(R.id.btnLogout).setOnClickListener(view -> {
            SessionManager session = new SessionManager(requireContext());
            session.logout();

            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            requireActivity().finish();
            dismiss();
        });

        return v;
    }
}
