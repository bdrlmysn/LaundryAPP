package com.example.laundryapp.ui.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.OrderDao;
import com.example.laundryapp.data.db.dao.ServiceDao;
import com.example.laundryapp.data.db.model.OrderEntity;
import com.example.laundryapp.data.model.HistoryOrder;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.LaundryLabel;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private OrderDao orderDao;
    private OrderEntity order;

    private TextView tvOrderId, tvStatus;
    private ProgressBar progressOrder;

    private TextView tvCustomerName, tvCustomerPhone, tvCustomerAddress;
    private TextView tvServiceName, tvServiceDetail;
    private TextView tvEstimateFinish; // NEW

    private TextView tvSubtotal, tvTax, tvTotal, tvPaymentStatus, tvNote;
    private TextView tvTotalPrice; // fallback (lama)
    private Button btnNextStatus;

    private TextView tvStatusFlow;
    private List<String> flow;

    private View cardReadyPickup;
    private Button btnReadyPickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderDao = new OrderDao(this);

        String orderCode = null;
        HistoryOrder passed = (HistoryOrder) getIntent().getSerializableExtra("order");
        if (passed != null) orderCode = passed.orderId;
        if (orderCode == null) { finish(); return; }

        order = orderDao.getByOrderCode(orderCode);
        if (order == null) { finish(); return; }

        order.type = LaundryLabel.normalizeType(order.type);
        flow = LaundryLabel.flowForType(order.type);

        bindViews();
        render();

        btnNextStatus.setOnClickListener(v -> {
            String next = nextStatus(order.status);
            if (next == null) {
                Toast.makeText(this, "Status sudah final (DIAMBIL)", Toast.LENGTH_SHORT).show();
                return;
            }

            btnNextStatus.setEnabled(false);

            new Thread(() -> {
                boolean ok = orderDao.updateStatus(order.orderCode, next);
                if (ok) order = orderDao.getByOrderCode(order.orderCode);

                runOnUiThread(() -> {
                    if (!ok || order == null) {
                        Toast.makeText(this, "Gagal update status", Toast.LENGTH_SHORT).show();
                        btnNextStatus.setEnabled(true);
                        return;
                    }

                    order.type = LaundryLabel.normalizeType(order.type);
                    flow = LaundryLabel.flowForType(order.type);

                    render();
                    Toast.makeText(this, "Status -> " + LaundryLabel.statusLabel(order.status), Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        if (btnReadyPickup != null) {
            btnReadyPickup.setOnClickListener(v -> {
                if (order == null) return;
                if (!LaundryLabel.ST_SELESAI.equals(order.status)) {
                    Toast.makeText(this, "Tombol ini aktif saat status SELESAI", Toast.LENGTH_SHORT).show();
                    return;
                }

                String phone = order.customerPhone;
                if (phone == null || phone.trim().isEmpty()) {
                    Toast.makeText(this, "Nomor pelanggan kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                String waNumber = normalizeToWaNumber(phone);
                String msg = buildReadyPickupMessage(order);
                openWhatsApp(waNumber, msg);
            });
        }
    }

    private void bindViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        progressOrder = findViewById(R.id.progressOrder);

        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress);

        tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceDetail = findViewById(R.id.tvServiceDetail);
        tvEstimateFinish = findViewById(R.id.tvEstimateFinish);

        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvNote = findViewById(R.id.tvNote);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnNextStatus = findViewById(R.id.btnNextStatus);
        tvStatusFlow = findViewById(R.id.tvStatusFlow);

        cardReadyPickup = findViewById(R.id.cardReadyPickup);
        btnReadyPickup = findViewById(R.id.btnReadyPickup);
    }

    private void render() {
        tvOrderId.setText(order.orderCode);
        tvStatus.setText(LaundryLabel.statusLabel(order.status));

        if (progressOrder != null) {
            progressOrder.setMax(100);
            progressOrder.setProgress(LaundryLabel.progressForFlow(order.status, flow));
        }

        tvCustomerName.setText(order.customerName);
        tvCustomerPhone.setText(order.customerPhone);
        tvCustomerAddress.setText(order.customerAddress == null || order.customerAddress.trim().isEmpty()
                ? "-" : order.customerAddress);

        // SERVICE
        tvServiceName.setText(LaundryLabel.speedLabel(order.speed) + " - " + LaundryLabel.typeLabel(order.type));

        String durationLabel = ServiceDao.durationLabel(order.durationMinutes);
        tvServiceDetail.setText(String.format(
                Locale.US,
                "%.1f kg • %s/kg • %s",
                order.weight,
                FormatUtil.rupiah(order.pricePerKg),
                durationLabel
        ));

        if (tvEstimateFinish != null) {
            String estimate = buildEstimateFinishText(order.createdAt, order.durationMinutes);
            tvEstimateFinish.setText("Estimasi selesai: " + estimate);
        }

        // PAYMENT
        if (tvSubtotal != null) tvSubtotal.setText(FormatUtil.rupiah(order.subtotal));
        if (tvTax != null) tvTax.setText(FormatUtil.rupiah(order.tax));
        if (tvTotal != null) tvTotal.setText(FormatUtil.rupiah(order.total));
        if (tvPaymentStatus != null) tvPaymentStatus.setText(order.paymentStatus);
        if (tvNote != null) tvNote.setText(order.note == null || order.note.trim().isEmpty() ? "-" : order.note);

        if (tvTotalPrice != null) tvTotalPrice.setText(FormatUtil.rupiah(order.total));
        if (tvStatusFlow != null) tvStatusFlow.setText(LaundryLabel.flowText(flow));

        btnNextStatus.setEnabled(nextStatus(order.status) != null);

        if (cardReadyPickup != null) {
            cardReadyPickup.setVisibility(LaundryLabel.ST_SELESAI.equals(order.status) ? View.VISIBLE : View.GONE);
        }
    }

    private String buildEstimateFinishText(long createdAtMillis, int durationMinutes) {
        if (durationMinutes <= 0) durationMinutes = 2880;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(createdAtMillis);
        cal.add(Calendar.MINUTE, durationMinutes);

        if (durationMinutes >= 1440) {
            return String.format(Locale.US, "%02d/%02d/%04d",
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR));
        } else {
            return String.format(Locale.US, "%02d/%02d/%04d %02d:%02d",
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));
        }
    }

    private String nextStatus(String current) {
        List<String> list = flow;
        if (list == null || list.isEmpty()) list = LaundryLabel.STATUSES;

        int idx = list.indexOf(current);
        if (idx < 0) return list.get(0);
        if (idx >= list.size() - 1) return null;
        return list.get(idx + 1);
    }

    // ===== WhatsApp Helpers =====

    private String normalizeToWaNumber(String phone) {
        String p = phone.trim();
        p = p.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");
        if (p.startsWith("+")) p = p.substring(1);

        if (p.startsWith("0")) {
            p = "62" + p.substring(1);
        }
        return p;
    }

    private String buildReadyPickupMessage(OrderEntity o) {
        return "Halo " + safe(o.customerName) + ",\n"
                + "Laundry kamu sudah *SELESAI* dan *siap diambil* ✅\n\n"
                + "Order: " + safe(o.orderCode) + "\n"
                + "Layanan: " + LaundryLabel.speedLabel(o.speed) + " - " + LaundryLabel.typeLabel(o.type) + "\n"
                + "Total: " + FormatUtil.rupiah(o.total) + "\n\n"
                + "Terima kasih 🙏";
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }

    private void openWhatsApp(String waNumber, String message) {
        try {
            String encoded = URLEncoder.encode(message, "UTF-8");
            Uri uri = Uri.parse("https://wa.me/" + waNumber + "?text=" + encoded);

            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setPackage("com.whatsapp");

            if (i.resolveActivity(getPackageManager()) != null) {
                startActivity(i);
            } else {
                Intent fallback = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(fallback);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membuka WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }
}
