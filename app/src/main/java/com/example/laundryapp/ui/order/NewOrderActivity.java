package com.example.laundryapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.CustomerDao;
import com.example.laundryapp.data.db.dao.ServiceDao;
import com.example.laundryapp.data.db.model.CustomerEntity;
import com.example.laundryapp.data.db.model.ServiceEntity;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.LaundryLabel;

import java.util.List;
import java.util.Locale;

public class NewOrderActivity extends AppCompatActivity {

    private long customerId;
    private CustomerEntity customer;

    private double weight = 1.0;
    private int lastSubtotal = 0;
    private ServiceEntity selectedService;

    private Spinner spinnerSpeed, spinnerType, spinnerParfum;
    private EditText etWeight, etNote;
    private TextView tvEstimate;
    private TextView tvServiceEstimate; // NEW

    private CustomerDao customerDao;
    private ServiceDao serviceDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        customerId = getIntent().getLongExtra("customer_id", 0);
        if (customerId <= 0) { finish(); return; }

        customerDao = new CustomerDao(this);
        serviceDao = new ServiceDao(this);

        customer = customerDao.getById(customerId);
        if (customer == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvCustomerName)).setText(customer.name);
        ((TextView) findViewById(R.id.tvCustomerPhone)).setText(customer.phone);

        initViews();
        loadSpeeds();
        updateTotal();
    }

    private void initViews() {
        // ID mengikuti layout activity_new_order.xml yang sudah ada
        spinnerSpeed = findViewById(R.id.spinnerLaundryType);
        spinnerType  = findViewById(R.id.spinnerService);
        spinnerParfum = findViewById(R.id.spinnerParfum);

        etWeight = findViewById(R.id.etWeight);
        etNote   = findViewById(R.id.etNote);
        tvEstimate = findViewById(R.id.tvEstimate);

        // NEW: TextView estimasi pengerjaan
        tvServiceEstimate = findViewById(R.id.tvServiceEstimate);

        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);

        // default parfum
        spinnerParfum.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Tanpa Parfum", "Parfum Sakura", "Parfum Tea"}
        ));

        spinnerParfum.setOnItemSelectedListener(SimpleListener.onChange(this::updateTotal));

        // saat speed berubah -> load types + update estimasi pengerjaan
        spinnerSpeed.setOnItemSelectedListener(SimpleListener.onChange(() -> {
            loadTypesBySpeed();
            updateServiceEstimate(); // NEW
        }));

        // saat type berubah -> update total (tidak mempengaruhi estimasi pengerjaan)
        spinnerType.setOnItemSelectedListener(SimpleListener.onChange(this::updateTotal));

        btnPlus.setOnClickListener(v -> {
            weight += 0.1;
            etWeight.setText(String.format(Locale.US, "%.1f", weight));
        });

        btnMinus.setOnClickListener(v -> {
            if (weight > 0.1) {
                weight -= 0.1;
                etWeight.setText(String.format(Locale.US, "%.1f", weight));
            }
        });

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try { weight = Double.parseDouble(s.toString()); }
                catch (Exception e) { weight = 0; }
                updateTotal();
            }
        });

        Button btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(v -> {
            if (selectedService == null) {
                Toast.makeText(this, "Pilih layanan dulu", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weight <= 0) {
                Toast.makeText(this, "Berat tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra("customer_id", customerId);
            i.putExtra("service_id", selectedService.id);
            i.putExtra("weight", weight);
            i.putExtra("parfum", spinnerParfum.getSelectedItem().toString());
            i.putExtra("note", etNote.getText().toString());
            i.putExtra("subtotal", lastSubtotal);

            String summary = LaundryLabel.speedLabel(selectedService.speed) + " - " +
                    LaundryLabel.typeLabel(selectedService.type) + "\n" +
                    String.format(Locale.US, "%.1f", weight) + " Kg";
            i.putExtra("summary", summary);

            startActivity(i);
        });
    }

    private void loadSpeeds() {
        List<String> speeds = serviceDao.getSpeeds();
        if (speeds == null || speeds.isEmpty()) {
            speeds = java.util.Arrays.asList("REGULER");
        }
        spinnerSpeed.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, speeds));

        // NEW: setelah speeds kebaca, langsung update estimasi
        updateServiceEstimate();

        loadTypesBySpeed();
    }

    private void loadTypesBySpeed() {
        String speed = spinnerSpeed.getSelectedItem() != null ? spinnerSpeed.getSelectedItem().toString() : "REGULER";
        List<String> types = serviceDao.getTypesBySpeed(speed);
        if (types == null || types.isEmpty()) {
            types = java.util.Arrays.asList("CUCI_SETIRKA");
        }
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));

        // NEW: pastikan estimasi selalu sesuai speed terkini
        updateServiceEstimate();

        updateTotal();
    }

    private void updateTotal() {
        String speed = spinnerSpeed.getSelectedItem() != null ? spinnerSpeed.getSelectedItem().toString() : "REGULER";
        String type  = spinnerType.getSelectedItem() != null ? spinnerType.getSelectedItem().toString() : "CUCI_SETIRKA";

        selectedService = serviceDao.getBySpeedAndType(speed, type);
        if (selectedService == null) {
            lastSubtotal = 0;
            tvEstimate.setText(FormatUtil.rupiah(0));
            return;
        }

        int parfumPrice = 0;
        String parfum = spinnerParfum.getSelectedItem() != null ? spinnerParfum.getSelectedItem().toString() : "Tanpa Parfum";
        if ("Parfum Sakura".equalsIgnoreCase(parfum)) parfumPrice = 2000;
        else if ("Parfum Tea".equalsIgnoreCase(parfum)) parfumPrice = 3000;

        int base = (int) Math.round(weight * selectedService.pricePerKg);
        lastSubtotal = base + parfumPrice;

        tvEstimate.setText(FormatUtil.rupiah(lastSubtotal));
    }

    // =======================
    // NEW: Estimasi pengerjaan
    // =======================
    private void updateServiceEstimate() {
        if (tvServiceEstimate == null) return;

        String speed = spinnerSpeed.getSelectedItem() != null ? spinnerSpeed.getSelectedItem().toString() : "REGULER";
        tvServiceEstimate.setText("Estimasi pengerjaan: " + speedDurationLabel(speed));
    }

    // hanya menampilkan: 4 jam / 1 hari / 2 hari
    private String speedDurationLabel(String speedCode) {
        if ("INSTANT".equalsIgnoreCase(speedCode)) return "4 jam";
        if ("KILAT".equalsIgnoreCase(speedCode)) return "1 hari";
        return "2 hari"; // REGULER + fallback aman
    }
}
