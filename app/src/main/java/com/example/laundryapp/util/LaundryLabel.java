package com.example.laundryapp.util;

import java.util.Arrays;
import java.util.List;

public class LaundryLabel {

    public static final List<String> STATUSES = Arrays.asList(
            "DITERIMA", "DICUCI", "DIKERINGKAN", "DISETRIKA", "SELESAI", "DIAMBIL"
    );

    public static String speedLabel(String code) {
        if ("REGULER".equals(code)) return "Reguler";
        if ("KILAT".equals(code)) return "Kilat";
        if ("INSTANT".equals(code)) return "Instant";
        return code;
    }

    public static String typeLabel(String code) {
        if ("CUCI_SETIRKA".equals(code)) return "Cuci Setrika";
        if ("CUCI_SAJA".equals(code)) return "Cuci Saja";
        if ("SETRIKA_SAJA".equals(code)) return "Setrika Saja";
        return code;
    }

    public static String statusLabel(String code) {
        if ("DITERIMA".equals(code)) return "Diterima";
        if ("DICUCI".equals(code)) return "Dicuci";
        if ("DIKERINGKAN".equals(code)) return "Dikeringkan";
        if ("DISETRIKA".equals(code)) return "Disetrika";
        if ("SELESAI".equals(code)) return "Selesai";
        if ("DIAMBIL".equals(code)) return "Diambil";
        return code;
    }

    public static int statusProgress(String code) {
        int idx = STATUSES.indexOf(code);
        if (idx < 0) idx = 0;
        return (int) Math.round((idx + 1) * (100.0 / STATUSES.size()));
    }
}
