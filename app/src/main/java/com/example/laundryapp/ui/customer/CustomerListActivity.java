package com.example.laundryapp.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.CustomerDao;
import com.example.laundryapp.data.db.model.CustomerEntity;
import com.example.laundryapp.util.SessionManager;

import java.util.List;

public class CustomerListActivity extends AppCompatActivity implements CustomerManageAdapter.Listener {

    private CustomerDao dao;
    private CustomerManageAdapter adapter;
    private RecyclerView rv;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // optional: enforce login
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) { finish(); return; }

        setContentView(R.layout.activity_customer_list);

        dao = new CustomerDao(this);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        rv = findViewById(R.id.rvCustomers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerManageAdapter(this);
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddCustomer).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerFormActivity.class));
        });

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { load(); }
        });

        load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        List<CustomerEntity> list = dao.getAll(etSearch.getText().toString());
        adapter.submit(list);
    }

    @Override
    public void onEdit(CustomerEntity c) {
        Intent i = new Intent(this, CustomerFormActivity.class);
        i.putExtra("customer_id", c.id);
        startActivity(i);
    }

    @Override
    public void onDelete(CustomerEntity c) {
        dao.delete(c.id);
        load();
    }
}
