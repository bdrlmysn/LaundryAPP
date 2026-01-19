package com.example.laundryapp.ui.service;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.ServiceDao;
import com.example.laundryapp.data.db.model.ServiceEntity;
import com.example.laundryapp.util.SessionManager;

import java.util.List;

public class ServiceListActivity extends AppCompatActivity implements ServiceManageAdapter.Listener {

    private ServiceDao dao;
    private ServiceManageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) { finish(); return; }

        setContentView(R.layout.activity_service_list);

        dao = new ServiceDao(this);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvServices);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceManageAdapter(this);
        rv.setAdapter(adapter);

        findViewById(R.id.btnAddService).setOnClickListener(v -> {
            startActivity(new Intent(this, ServiceFormActivity.class));
        });

        load();
    }

    @Override protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        List<ServiceEntity> list = dao.getAll(false);
        adapter.submit(list);
    }

    @Override
    public void onEdit(ServiceEntity s) {
        Intent i = new Intent(this, ServiceFormActivity.class);
        i.putExtra("service_id", s.id);
        startActivity(i);
    }

    @Override
    public void onDelete(ServiceEntity s) {
        dao.delete(s.id);
        load();
    }
}
