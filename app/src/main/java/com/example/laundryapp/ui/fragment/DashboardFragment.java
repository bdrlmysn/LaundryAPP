package com.example.laundryapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.laundryapp.ui.order.RecentOrderAdapter;

// TODO: ganti sesuai activity add order kamu yang layout-nya activity_new_order.xml
import com.example.laundryapp.ui.order.SelectCustomerActivity;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        SessionManager session = new SessionManager(requireContext());
        OrderDao dao = new OrderDao(requireContext());

        RecyclerView rv = v.findViewById(R.id.rvRecentOrders);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            RecentOrderAdapter adapter = new RecentOrderAdapter(item -> {
                // opsional: klik item buka detail
                // startActivity(new Intent(getContext(), OrderDetailActivity.class).putExtra("order_code", item.orderCode));
            });
            rv.setAdapter(adapter);

            // ambil 3 terbaru
            adapter.submit(dao.getRecent(3));
        }



        // ===== Tombol navigasi (safe) ====

        long startToday = DateRangeUtil.startOfToday();
        long endToday = DateRangeUtil.endOfToday();

        // ===== Revenue Today (PAID) =====
        int revenueToday = dao.getTotalPaidRevenue(startToday, endToday);
        TextView tvRevenueToday = v.findViewById(R.id.tvRevenueToday);
        if (tvRevenueToday != null) {
            tvRevenueToday.setText(FormatUtil.rupiah(revenueToday));
        }

        // ===== Total Processed Today (SUM KG) =====
        TextView tvTotalProcessedToday = v.findViewById(R.id.tvTotalProcessedToday);
        if (tvTotalProcessedToday != null) {
            double kgToday = dao.getTotalWeight(startToday, endToday);
            tvTotalProcessedToday.setText(String.format("%.1f kg", kgToday));
        }

        // ===== Standard Revenue = REGULER (PAID) =====
        TextView tvStandardRevenue = v.findViewById(R.id.tvStandardRevenue);
        if (tvStandardRevenue != null) {
            int standardToday = dao.getPaidRevenueBySpeed(startToday, endToday, "REGULER");
            tvStandardRevenue.setText(FormatUtil.rupiah(standardToday));
        }

        // ===== Express Revenue = KILAT + INSTANT (PAID) =====
        TextView tvExpressRevenue = v.findViewById(R.id.tvExpressRevenue);
        if (tvExpressRevenue != null) {
            int expressToday = dao.getPaidRevenueBySpeed(startToday, endToday, "KILAT", "INSTANT");
            tvExpressRevenue.setText(FormatUtil.rupiah(expressToday));
        }

        // ===== Persen kenaikan pendapatan vs kemarin =====
        TextView tvRevenueChange = v.findViewById(R.id.tvRevenueChange);
        if (tvRevenueChange != null) {
            int revenueYesterday = dao.getTotalPaidRevenue(DateRangeUtil.startOfYesterday(), DateRangeUtil.endOfYesterday());

            int pct;
            if (revenueYesterday <= 0) {
                pct = (revenueToday > 0) ? 100 : 0;
            } else {
                pct = Math.round(((revenueToday - revenueYesterday) * 100f) / revenueYesterday);
            }

            String arrow = (pct >= 0) ? "▲" : "▼";
            tvRevenueChange.setText(arrow + " " + Math.abs(pct) + "% vs yesterday");
        }

        // ===== Klik New Kiloan Order -> buka AddOrderActivity =====
        View cardNewOrder = v.findViewById(R.id.cardNewOrder);
        if (cardNewOrder != null) {
            cardNewOrder.setOnClickListener(x -> {
                startActivity(new Intent(getContext(), SelectCustomerActivity.class));
            });
        }

        // ===== Revenue This Month =====
        TextView tvRevenueMonth = v.findViewById(R.id.tvRevenueMonth);
        if (tvRevenueMonth != null) {
            int monthRevenue = dao.getTotalPaidRevenue(DateRangeUtil.startOfThisMonth(), DateRangeUtil.endOfThisMonth());
            tvRevenueMonth.setText(FormatUtil.rupiah(monthRevenue));
        }

        // ===== Klik View All -> scroll ke bawah (Total Revenue This Month) =====
        TextView tvViewAll = v.findViewById(R.id.tvViewAll);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(x -> {
                if (getActivity() instanceof DashboardNav) {
                    ((DashboardNav) getActivity()).openHistory();
                } else {
                    Toast.makeText(getContext(), "Host belum support openHistory()", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return v;
    }
    public interface DashboardNav {
        void openHistory();
    }

}
