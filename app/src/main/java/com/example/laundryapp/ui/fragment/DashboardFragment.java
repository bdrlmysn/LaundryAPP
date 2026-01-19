package com.example.laundryapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.OrderDao;
import com.example.laundryapp.ui.customer.CustomerListActivity;
import com.example.laundryapp.ui.login.LoginActivity;
import com.example.laundryapp.ui.report.ReportActivity;
import com.example.laundryapp.ui.service.ServiceListActivity;
import com.example.laundryapp.util.DateRangeUtil;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.SessionManager;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        SessionManager session = new SessionManager(requireContext());

        // Tombol navigasi
// Tombol navigasi (safe: kalau view tidak ada, tidak crash)
        View btnCustomers = v.findViewById(R.id.btnCustomers);
        if (btnCustomers != null) {
            btnCustomers.setOnClickListener(x ->
                    startActivity(new Intent(getContext(), CustomerListActivity.class)));
        }

        View btnServices = v.findViewById(R.id.btnServices);
        if (btnServices != null) {
            btnServices.setOnClickListener(x ->
                    startActivity(new Intent(getContext(), ServiceListActivity.class)));
        }

        View btnReports = v.findViewById(R.id.btnReports);
        if (btnReports != null) {
            btnReports.setOnClickListener(x ->
                    startActivity(new Intent(getContext(), ReportActivity.class)));
        }

        View btnLogout = v.findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(x -> {
                session.logout();
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                requireActivity().finish();
            });
        }


        // DAO
        OrderDao dao = new OrderDao(requireContext());

        // Revenue Today
        TextView tvRevenueToday = v.findViewById(R.id.tvRevenueToday);
        if (tvRevenueToday != null) {
            int revenueToday = dao.getTotalPaidRevenue(DateRangeUtil.startOfToday(), DateRangeUtil.endOfToday());
            tvRevenueToday.setText(FormatUtil.rupiah(revenueToday));
        }

        // Standard Revenue (sementara 0 dulu sampai ada query/kolom pembeda)
        TextView tvStandardRevenue = v.findViewById(R.id.tvStandardRevenue);
        if (tvStandardRevenue != null) {
            tvStandardRevenue.setText(FormatUtil.rupiah(0));
        }

        // Express Revenue (sementara 0 dulu sampai ada query/kolom pembeda)
        TextView tvExpressRevenue = v.findViewById(R.id.tvExpressRevenue);
        if (tvExpressRevenue != null) {
            tvExpressRevenue.setText(FormatUtil.rupiah(0));
        }

        return v;
    }
}
