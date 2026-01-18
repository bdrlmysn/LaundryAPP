package com.example.laundryapp.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.laundryapp.R;
import com.example.laundryapp.ui.fragment.DashboardFragment;
import com.example.laundryapp.ui.fragment.HistoryFragment;
import com.example.laundryapp.ui.order.SelectCustomerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // default fragment
        loadFragment(new DashboardFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_dashboard) {
                selectedFragment = new DashboardFragment();
            }
            else if (item.getItemId() == R.id.menu_orders) {
                startActivity(new Intent(this, SelectCustomerActivity.class));
                return true; // stop di sini
            }
            else if (item.getItemId() == R.id.menu_history) {
                selectedFragment = new HistoryFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
