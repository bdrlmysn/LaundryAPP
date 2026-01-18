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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laundryapp.R;
import com.example.laundryapp.data.model.Customer;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewOrderActivity extends AppCompatActivity {

    Customer customer;

    double weight = 1.0;
    int lastTotal = 0;

    Spinner spinnerLaundry, spinnerService, spinnerParfum;
    EditText etWeight, etNote;
    TextView tvEstimate;

    Map<String, Map<String, Integer>> priceMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Customer
        customer = (Customer) getIntent().getSerializableExtra("customer");
        if (customer == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvCustomerName)).setText(customer.name);
        ((TextView) findViewById(R.id.tvCustomerPhone)).setText(customer.phone);

        initPriceMap();
        initViews();

        Button btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(v -> {
            Intent i = new Intent(this, PaymentActivity.class);

            // kirim customer
            i.putExtra("customer", customer);

            // summary
            String summary =
                    spinnerLaundry.getSelectedItem().toString() + " - " +
                            spinnerService.getSelectedItem().toString() + "\n" +
                            String.format(Locale.US, "%.1f", weight) + " Kg";

            i.putExtra("summary", summary);
            i.putExtra("subtotal", lastTotal);

            // NOTE
            String note = etNote.getText().toString();
            i.putExtra("note", note);

            startActivity(i);
        });

        updateTotal();
    }

    private void initViews() {
        spinnerLaundry = findViewById(R.id.spinnerLaundryType);
        spinnerService = findViewById(R.id.spinnerService);
        spinnerParfum = findViewById(R.id.spinnerParfum);
        etWeight = findViewById(R.id.etWeight);
        etNote = findViewById(R.id.etNote);
        tvEstimate = findViewById(R.id.tvEstimate);

        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnMinus = findViewById(R.id.btnMinus);

        spinnerLaundry.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Standard Laundry", "Express Laundry"}
        ));

        spinnerService.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Cuci Kering + Gosok",
                        "Cuci Kering Only",
                        "Gosok Only"
                }
        ));

        spinnerParfum.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{
                        "Tanpa Parfum",
                        "Parfum Sakura",
                        "Parfum Tea"
                }
        ));

        spinnerLaundry.setOnItemSelectedListener(SimpleListener.onChange(this::updateTotal));
        spinnerService.setOnItemSelectedListener(SimpleListener.onChange(this::updateTotal));
        spinnerParfum.setOnItemSelectedListener(SimpleListener.onChange(this::updateTotal));

        btnPlus.setOnClickListener(v -> {
            weight += 0.1;
            etWeight.setText(String.format(Locale.US, "%.1f", weight));
            updateTotal();
        });

        btnMinus.setOnClickListener(v -> {
            if (weight > 0.1) {
                weight -= 0.1;
                etWeight.setText(String.format(Locale.US, "%.1f", weight));
                updateTotal();
            }
        });

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    weight = Double.parseDouble(s.toString());
                } catch (Exception e) {
                    weight = 0;
                }
                updateTotal();
            }
        });
    }

    private void initPriceMap() {
        Map<String, Integer> standard = new HashMap<>();
        standard.put("Cuci Kering + Gosok", 12000);
        standard.put("Cuci Kering Only", 10000);
        standard.put("Gosok Only", 8000);

        Map<String, Integer> express = new HashMap<>();
        express.put("Cuci Kering + Gosok", 24000);
        express.put("Cuci Kering Only", 20000);
        express.put("Gosok Only", 16000);

        priceMap.put("Standard Laundry", standard);
        priceMap.put("Express Laundry", express);
    }

    private void updateTotal() {
        String laundryType = spinnerLaundry.getSelectedItem().toString();
        String service = spinnerService.getSelectedItem().toString();

        int pricePerKg = priceMap.get(laundryType).get(service);

        int parfumPrice = 0;
        String parfum = spinnerParfum.getSelectedItem().toString();
        if (parfum.equals("Parfum Sakura")) parfumPrice = 4000;
        else if (parfum.equals("Parfum Tea")) parfumPrice = 3000;

        int total = (int) (weight * pricePerKg) + parfumPrice;
        lastTotal = total;

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvEstimate.setText(nf.format(total));
    }
}
