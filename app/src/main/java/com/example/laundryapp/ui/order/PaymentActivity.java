package com.example.laundryapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.CustomerDao;
import com.example.laundryapp.data.db.dao.OrderDao;
import com.example.laundryapp.data.db.dao.ServiceDao;
import com.example.laundryapp.data.db.model.CustomerEntity;
import com.example.laundryapp.data.db.model.ServiceEntity;
import com.example.laundryapp.ui.home.HomeActivity;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) { finish(); return; }

        long customerId = getIntent().getLongExtra("customer_id", 0);
        long serviceId  = getIntent().getLongExtra("service_id", 0);
        double weight   = getIntent().getDoubleExtra("weight", 0);
        String parfum   = getIntent().getStringExtra("parfum");
        String summary  = getIntent().getStringExtra("summary");
        String note     = getIntent().getStringExtra("note");
        int subtotal    = getIntent().getIntExtra("subtotal", 0);

        if (customerId <= 0 || serviceId <= 0) { finish(); return; }

        CustomerDao customerDao = new CustomerDao(this);
        ServiceDao serviceDao = new ServiceDao(this);
        OrderDao orderDao = new OrderDao(this);

        CustomerEntity customer = customerDao.getById(customerId);
        ServiceEntity service = serviceDao.getById(serviceId);
        if (customer == null || service == null) { finish(); return; }

        int tax = (int) Math.round(subtotal * 0.10);
        int total = subtotal + tax;

        ((TextView) findViewById(R.id.tvCustomerName)).setText(customer.name);
        ((TextView) findViewById(R.id.tvOrderSummary)).setText(summary != null ? summary : "-");

        TextView tvSubtotalSummary = findViewById(R.id.tvSubtotalSummary);
        if (tvSubtotalSummary != null) tvSubtotalSummary.setText(FormatUtil.rupiah(subtotal));

        TextView tvSubtotalDetail = findViewById(R.id.tvSubtotal);
        if (tvSubtotalDetail != null) tvSubtotalDetail.setText(FormatUtil.rupiah(subtotal));

        ((TextView) findViewById(R.id.tvTax)).setText(FormatUtil.rupiah(tax));
        ((TextView) findViewById(R.id.tvTotal)).setText(FormatUtil.rupiah(total));
        ((TextView) findViewById(R.id.tvTotalPay)).setText(FormatUtil.rupiah(total));

        TextView tvNote = findViewById(R.id.tvNote);
        tvNote.setText(note == null || note.trim().isEmpty() ? "-" : note);

        // Estimate date/time (NGIKUT DB SERVICE)
        TextView tvEstimateDate = findViewById(R.id.tvEstimateDate);
        tvEstimateDate.setText(buildEstimateTextFromMinutes(service.durationMinutes));

        RadioGroup rgPayment = findViewById(R.id.rgPaymentMethod);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            int checkedId = rgPayment.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Pilih metode pembayaran dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentStatus = "PAID"; // cuma Cash / QRIS
            if (!(checkedId == R.id.rbCash || checkedId == R.id.rbQris)) {
                Toast.makeText(this, "Metode pembayaran tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderCode = orderDao.createOrder(
                    customerId,
                    serviceId,
                    weight,
                    parfum,
                    note,
                    subtotal,
                    tax,
                    total,
                    paymentStatus,
                    session.userId()
            );

            Toast.makeText(this, "Order dibuat: " + orderCode, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("open_history", true);
            startActivity(i);
            finish();
        });
    }

    private String buildEstimateTextFromMinutes(int durationMinutes) {
        if (durationMinutes <= 0) durationMinutes = 2880;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, durationMinutes);

        if (durationMinutes >= 1440) {
            return "Estimasi selesai: " + String.format(Locale.US, "%02d/%02d/%04d",
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
        } else {
            return "Estimasi selesai: " + String.format(Locale.US, "%02d/%02d/%04d %02d:%02d",
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));
        }
    }
}
