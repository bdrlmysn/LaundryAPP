package com.example.laundryapp.ui.order;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.model.Customer;

import java.util.ArrayList;
import java.util.List;

public class SelectCustomerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_customer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // tombol back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
        RecyclerView rv = findViewById(R.id.rvCustomers);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<Customer> list = new ArrayList<>();
        list.add(new Customer("John Doe", "(555) 123-4567", "2 days ago", "JD"));
        list.add(new Customer("Sarah Smith", "(555) 987-6543", "Yesterday", "SS"));
        list.add(new Customer("Michael Jordan", "(555) 555-0199", "1 week ago", "MJ"));

        rv.setAdapter(new CustomerAdapter(list));
    }
}
