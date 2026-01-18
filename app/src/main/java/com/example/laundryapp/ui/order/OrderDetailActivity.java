package com.example.laundryapp.ui.order;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laundryapp.R;
import com.example.laundryapp.data.model.HistoryOrder;

public class OrderDetailActivity extends AppCompatActivity {

    HistoryOrder order;

    TextView tvStatus;
    ProgressBar progressOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order = (HistoryOrder) getIntent().getSerializableExtra("order");

        if (order == null) {
            finish();
            return;
        }

        tvStatus = findViewById(R.id.tvStatus);
        progressOrder = findViewById(R.id.progressOrder);

        if ("PROCESSING".equalsIgnoreCase(order.status)) {
            tvStatus.setText("Processing");
            progressOrder.setProgress(50);
        } else {
            tvStatus.setText("Ready for Pickup");
            progressOrder.setProgress(100);
        }
    }
}
