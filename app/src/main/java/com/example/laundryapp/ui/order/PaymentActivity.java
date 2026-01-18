package com.example.laundryapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laundryapp.R;
import com.example.laundryapp.data.model.Customer;
import com.example.laundryapp.data.model.HistoryOrder;
import com.example.laundryapp.data.repository.OrderRepository;
import com.example.laundryapp.ui.home.HomeActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ambil data
        Customer customer = (Customer) getIntent().getSerializableExtra("customer");
        String summary = getIntent().getStringExtra("summary");
        int subtotal = getIntent().getIntExtra("subtotal", 0);
        String note = getIntent().getStringExtra("note");

        int tax = (int) (subtotal * 0.10);
        int total = subtotal + tax;

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        ((TextView) findViewById(R.id.tvCustomerName)).setText(customer.name);
        ((TextView) findViewById(R.id.tvOrderSummary)).setText(summary);
        ((TextView) findViewById(R.id.tvSubtotal)).setText(nf.format(subtotal));
        ((TextView) findViewById(R.id.tvTax)).setText(nf.format(tax));
        ((TextView) findViewById(R.id.tvTotal)).setText(nf.format(total));
        ((TextView) findViewById(R.id.tvTotalPay)).setText(nf.format(total));

        TextView tvNote = findViewById(R.id.tvNote);
        tvNote.setText(note == null || note.trim().isEmpty() ? "-" : note);

        // PAYMENT METHOD
        RadioGroup rgPayment = findViewById(R.id.rgPaymentMethod);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {

            int checkedId = rgPayment.getCheckedRadioButtonId();
            String paymentStatus = "UNPAID";

            if (checkedId == R.id.rbCash || checkedId == R.id.rbQris) {
                paymentStatus = "PAID";
            }

            // BUAT ORDER HISTORY
            HistoryOrder order = new HistoryOrder(
                    "ORD-" + System.currentTimeMillis(), // orderId
                    customer.name,                       // customer
                    summary,                             // service
                    summary,                             // detail (sementara pakai summary)
                    nf.format(total),                    // price
                    "PROCESSING",                        // status
                    paymentStatus,                       // paymentStatus
                    "Hari ini",                          // time
                    note                                 // note
            );




            OrderRepository.add(order);

            // KEMBALI KE HOME → HISTORY
            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }
}
