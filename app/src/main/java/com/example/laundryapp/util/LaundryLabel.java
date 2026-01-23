package com.example.laundryapp.util;

import java.util.Arrays;
import java.util.List;

public class LaundryLabel {

    // === STATUS CODE ===
    public static final String ST_DITERIMA = "DITERIMA";
    public static final String ST_DICUCI = "DICUCI";
    public static final String ST_DIKERINGKAN = "DIKERINGKAN";
    public static final String ST_DISETRIKA = "DISETRIKA";
    public static final String ST_SELESAI = "SELESAI";
    public static final String ST_DIAMBIL = "DIAMBIL";

    // === TYPE CODE (BENAR) ===
    public static final String TYPE_CUCI_SETRIKA = "CUCI_SETRIKA";
    public static final String TYPE_CUCI_SAJA = "CUCI_SAJA";
    public static final String TYPE_SETRIKA_SAJA = "SETRIKA_SAJA";

    // === TYPE CODE (LEGACY TYPO) ===
    public static final String TYPE_CUCI_SETIRKA_LEGACY = "CUCI_SETIRKA"; // typo lama

    // Flow lengkap
    public static final List<String> STATUSES = Arrays.asList(
            ST_DITERIMA, ST_DICUCI, ST_DIKERINGKAN, ST_DISETRIKA, ST_SELESAI, ST_DIAMBIL
    );

    /** Normalisasi type agar typo lama jadi benar. */
    public static String normalizeType(String type) {
        if (type == null) return null;
        if (TYPE_CUCI_SETIRKA_LEGACY.equals(type)) return TYPE_CUCI_SETRIKA;
        return type;
    }

    /** Flow status berdasarkan layanan (logis). */
    public static List<String> flowForType(String rawType) {
        String type = normalizeType(rawType);
        if (type == null) return STATUSES;

        switch (type) {
            case TYPE_SETRIKA_SAJA:
                // DITERIMA -> DISETRIKA -> SELESAI -> DIAMBIL
                return Arrays.asList(ST_DITERIMA, ST_DISETRIKA, ST_SELESAI, ST_DIAMBIL);

            case TYPE_CUCI_SAJA:
                // DITERIMA -> DICUCI -> DIKERINGKAN -> SELESAI -> DIAMBIL
                return Arrays.asList(ST_DITERIMA, ST_DICUCI, ST_DIKERINGKAN, ST_SELESAI, ST_DIAMBIL);

            case TYPE_CUCI_SETRIKA:
            default:
                // Full flow
                return STATUSES;
        }
    }

    /** Teks urutan untuk ditampilkan di UI. */
    public static String flowText(List<String> flow) {
        if (flow == null || flow.isEmpty()) return "Urutan: -";
        StringBuilder sb = new StringBuilder("Urutan: ");
        for (int i = 0; i < flow.size(); i++) {
            sb.append(flow.get(i));
            if (i < flow.size() - 1) sb.append(" \u2192 "); // →
        }
        return sb.toString();
    }

    /** Progress 0..100 berdasarkan flow. */
    public static int progressForFlow(String status, List<String> flow) {
        if (flow == null || flow.isEmpty()) return 0;
        int idx = flow.indexOf(status);
        if (idx < 0) idx = 0;
        if (flow.size() == 1) return 100;
        return Math.round((idx * 100f) / (flow.size() - 1));
    }

    /** Backward compat: progress default flow lengkap. */
    public static int statusProgress(String code) {
        return progressForFlow(code, STATUSES);
    }

    // === LABELS ===
    public static String speedLabel(String code) {
        if ("REGULER".equals(code)) return "Reguler";
        if ("KILAT".equals(code)) return "Kilat";
        if ("INSTANT".equals(code)) return "Instant";
        return code;
    }

    public static String typeLabel(String rawCode) {
        String code = normalizeType(rawCode);
        if (TYPE_CUCI_SETRIKA.equals(code)) return "Cuci Setrika";
        if (TYPE_CUCI_SAJA.equals(code)) return "Cuci Saja";
        if (TYPE_SETRIKA_SAJA.equals(code)) return "Setrika Saja";
        return code;
    }

    public static String statusLabel(String code) {
        if (ST_DITERIMA.equals(code)) return "Diterima";
        if (ST_DICUCI.equals(code)) return "Dicuci";
        if (ST_DIKERINGKAN.equals(code)) return "Dikeringkan";
        if (ST_DISETRIKA.equals(code)) return "Disetrika";
        if (ST_SELESAI.equals(code)) return "Selesai";
        if (ST_DIAMBIL.equals(code)) return "Diambil";
        return code;
    }
}
