package com.example.laundryapp.ui.service;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.ServiceDao;
import com.example.laundryapp.data.db.model.ServiceEntity;
import com.example.laundryapp.util.LaundryLabel;

public class ServiceFormActivity extends AppCompatActivity {

    private ServiceDao dao;
    private long serviceId = 0;

    private Spinner spSpeed, spType;
    private EditText etPrice;
    private CheckBox cbActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_form);

        dao = new ServiceDao(this);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        spSpeed = findViewById(R.id.spSpeed);
        spType = findViewById(R.id.spType);
        etPrice = findViewById(R.id.etPrice);
        cbActive = findViewById(R.id.cbActive);
        Button btnSave = findViewById(R.id.btnSave);

        spSpeed.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"REGULER","KILAT","INSTANT"}));

        // ✅ FIX: gunakan yang benar "CUCI_SETRIKA"
        spType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{LaundryLabel.TYPE_CUCI_SETRIKA, LaundryLabel.TYPE_CUCI_SAJA, LaundryLabel.TYPE_SETRIKA_SAJA}));

        serviceId = getIntent().getLongExtra("service_id", 0);
        if (serviceId > 0) {
            ServiceEntity s = dao.getById(serviceId);
            if (s != null) {
                setSpinnerValue(spSpeed, s.speed);

                // ✅ NORMALIZE: kalau DB masih typo, tampilkan sebagai yang benar
                String fixedType = LaundryLabel.normalizeType(s.type);
                setSpinnerValue(spType, fixedType);

                etPrice.setText(String.valueOf(s.pricePerKg));
                cbActive.setChecked(s.active);
            }
            setTitle("Edit Service");
        } else {
            cbActive.setChecked(true);
            setTitle("Add Service");
        }

        btnSave.setOnClickListener(v -> {
            String speed = spSpeed.getSelectedItem().toString();
            String type = spType.getSelectedItem().toString();

            // ✅ NORMALIZE: jaga-jaga
            type = LaundryLabel.normalizeType(type);

            int price;
            try {
                price = Integer.parseInt(etPrice.getText().toString().trim());
            } catch (Exception e) {
                Toast.makeText(this, "Harga tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean active = cbActive.isChecked();

            if (serviceId > 0) {
                dao.update(serviceId, speed, type, price, active);
            } else {
                long id = dao.insert(speed, type, price, active);
                if (id == -1) {
                    Toast.makeText(this, "Kombinasi speed+type sudah ada", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            finish();
        });
    }

    private void setSpinnerValue(Spinner sp, String value) {
        if (value == null) return;
        for (int i = 0; i < sp.getCount(); i++) {
            if (value.equals(sp.getItemAtPosition(i))) {
                sp.setSelection(i);
                return;
            }
        }
    }
}
