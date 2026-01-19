package com.example.laundryapp.util;

import android.database.Cursor;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CsvUtil {

    // Export cursor -> CSV with header
    public static void writeCursorToCsv(Cursor c, OutputStream os) throws Exception {
        if (c == null) return;

        // header
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < c.getColumnCount(); i++) {
            header.append(escape(c.getColumnName(i)));
            if (i < c.getColumnCount() - 1) header.append(",");
        }
        header.append("\n");
        os.write(header.toString().getBytes(StandardCharsets.UTF_8));

        // rows
        while (c.moveToNext()) {
            StringBuilder row = new StringBuilder();
            for (int i = 0; i < c.getColumnCount(); i++) {
                String val = c.isNull(i) ? "" : c.getString(i);
                row.append(escape(val));
                if (i < c.getColumnCount() - 1) row.append(",");
            }
            row.append("\n");
            os.write(row.toString().getBytes(StandardCharsets.UTF_8));
        }
        os.flush();
    }

    private static String escape(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}
