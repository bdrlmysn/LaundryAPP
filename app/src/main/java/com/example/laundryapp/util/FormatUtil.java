package com.example.laundryapp.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {
    private static final Locale ID = new Locale("id", "ID");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm", ID);

    public static String rupiah(int amount) {
        return NumberFormat.getCurrencyInstance(ID).format(amount);
    }

    public static String dt(long millis) {
        return SDF.format(new Date(millis));
    }

    public static String initials(String name) {
        if (name == null) return "";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0) return "";
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0,1) + parts[1].substring(0,1)).toUpperCase();
    }
}
