package com.example.laundryapp.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.laundryapp.R;
import com.example.laundryapp.ui.customer.CustomerListActivity;
import com.example.laundryapp.ui.fragment.DashboardFragment;
import com.example.laundryapp.ui.fragment.HistoryFragment;
import com.example.laundryapp.ui.login.LoginActivity;
import com.example.laundryapp.ui.order.SelectCustomerActivity;
import com.example.laundryapp.ui.report.ReportActivity;
import com.example.laundryapp.ui.service.ServiceListActivity;
import com.example.laundryapp.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private SessionManager session;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottomNav);

        // default dashboard (dashboard tidak diubah)
        loadFragment(new DashboardFragment());
        bottomNav.setSelectedItemId(R.id.menu_dashboard);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_dashboard) {
                loadFragment(new DashboardFragment());
                return true;
            }

            if (id == R.id.menu_orders) {
                startActivity(new Intent(this, SelectCustomerActivity.class));
                bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.menu_dashboard));
                return true;
            }

            if (id == R.id.menu_history) {
                loadFragment(new HistoryFragment());
                return true;
            }

            if (id == R.id.menu_manage) {
                new ManageBottomSheet().show(getSupportFragmentManager(), "manage_sheet");
                bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.menu_dashboard));
                return true;
            }

            if (id == R.id.menu_more) {
                new MoreBottomSheet().show(getSupportFragmentManager(), "more_sheet");
                bottomNav.post(() -> bottomNav.setSelectedItemId(R.id.menu_dashboard));
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
