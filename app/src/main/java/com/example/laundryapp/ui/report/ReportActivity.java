package com.example.laundryapp.ui.report;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.OrderDao;
import com.example.laundryapp.data.db.model.OrderEntity;
import com.example.laundryapp.util.CsvUtil;
import com.example.laundryapp.util.DateRangeUtil;
import com.example.laundryapp.util.FormatUtil;
import com.example.laundryapp.util.SessionManager;

import java.io.OutputStream;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private OrderDao orderDao;
    private SessionManager session;

    private TextView tvRange, tvRevenue;
    private Spinner spFilterType;
    private ReportAdapter adapter;

    private long startMillis;
    private long endMillis;

    private ActivityResultLauncher<String> createCsvLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) { finish(); return; }

        setContentView(R.layout.activity_report);

        orderDao = new OrderDao(this);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        tvRange = findViewById(R.id.tvRange);
        tvRevenue = findViewById(R.id.tvRevenue);

        Button btnDaily = findViewById(R.id.btnDaily);
        Button btnMonthly = findViewById(R.id.btnMonthly);
        Button btnExport = findViewById(R.id.btnExport);

        spFilterType = findViewById(R.id.spFilterType);
        spFilterType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"ALL", "CUCI_SETIRKA", "CUCI_SAJA", "SETRIKA_SAJA"}));

        RecyclerView rv = findViewById(R.id.rvReport);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter();
        rv.setAdapter(adapter);

        // default = today
        setRangeToday();
        load();

        btnDaily.setOnClickListener(v -> { setRangeToday(); load(); });
        btnMonthly.setOnClickListener(v -> { setRangeThisMonth(); load(); });

        spFilterType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) { load(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // OWNER only export
        if (!session.isOwner()) {
            btnExport.setEnabled(false);
            btnExport.setText("Export CSV (OWNER only)");
        }

        createCsvLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), uri -> {
            if (uri == null) return;
            exportCsvToUri(uri);
        });

        btnExport.setOnClickListener(v -> {
            if (!session.isOwner()) {
                Toast.makeText(this, "Hanya OWNER yang bisa export", Toast.LENGTH_SHORT).show();
                return;
            }
            createCsvLauncher.launch("laporan_laundry.csv");
        });
    }

    private void setRangeToday() {
        startMillis = DateRangeUtil.startOfToday();
        endMillis = DateRangeUtil.endOfToday();
        tvRange.setText("Range: Hari ini");
    }

    private void setRangeThisMonth() {
        startMillis = DateRangeUtil.startOfThisMonth();
        endMillis = DateRangeUtil.endOfThisMonth();
        tvRange.setText("Range: Bulan ini");
    }

    private void load() {
        String type = spFilterType.getSelectedItem().toString();
        List<OrderEntity> list = orderDao.getHistory(startMillis, endMillis, type);
        adapter.submit(list);

        int revenue = orderDao.getTotalPaidRevenue(startMillis, endMillis);
        tvRevenue.setText("Total Pendapatan (PAID): " + FormatUtil.rupiah(revenue));
    }

    private void exportCsvToUri(Uri uri) {
        String type = spFilterType.getSelectedItem().toString();
        android.database.Cursor c = null;
        try {
            c = orderDao.getReportCursor(startMillis, endMillis, type);
            OutputStream os = getContentResolver().openOutputStream(uri);
            if (os == null) throw new Exception("OutputStream null");
            CsvUtil.writeCursorToCsv(c, os);
            os.close();
            Toast.makeText(this, "Export berhasil", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Export gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (c != null) c.close();
        }
    }
}
