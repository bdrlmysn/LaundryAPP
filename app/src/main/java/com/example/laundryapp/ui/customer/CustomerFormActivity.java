package com.example.laundryapp.ui.customer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.CustomerDao;
import com.example.laundryapp.data.db.model.CustomerEntity;

public class CustomerFormActivity extends AppCompatActivity {

    private CustomerDao dao;
    private long customerId = 0;

    private EditText etName, etPhone, etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_form);

        dao = new CustomerDao(this);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        Button btnSave = findViewById(R.id.btnSave);

        customerId = getIntent().getLongExtra("customer_id", 0);

        if (customerId > 0) {
            CustomerEntity c = dao.getById(customerId);
            if (c != null) {
                etName.setText(c.name);
                etPhone.setText(c.phone);
                etAddress.setText(c.address);
            }
            setTitle("Edit Customer");
        } else {
            setTitle("Add Customer");
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Nama & No HP wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (customerId > 0) {
                dao.update(customerId, name, phone, address);
            } else {
                dao.insert(name, phone, address);
            }
            finish();
        });
    }
}
