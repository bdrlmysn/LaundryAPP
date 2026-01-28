package com.example.laundryapp.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.laundryapp.data.db.DbContract;
import com.example.laundryapp.data.db.LaundryDbHelper;
import com.example.laundryapp.data.db.model.OrderEntity;

import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private final LaundryDbHelper helper;

    public OrderDao(Context ctx) {
        helper = LaundryDbHelper.getInstance(ctx);
    }

    public String createOrder(long customerId,
                              long serviceId,
                              double weight,
                              String parfum,
                              String note,
                              int subtotal,
                              int tax,
                              int total,
                              String paymentStatus,
                              long createdByUserIdOr0) {

        SQLiteDatabase db = helper.getWritableDatabase();
        long now = System.currentTimeMillis();
        String code = "ORD-" + now;

        ContentValues cv = new ContentValues();
        cv.put(DbContract.Orders.COL_ORDER_CODE, code);
        cv.put(DbContract.Orders.COL_CUSTOMER_ID, customerId);
        cv.put(DbContract.Orders.COL_SERVICE_ID, serviceId);
        cv.put(DbContract.Orders.COL_WEIGHT, weight);
        cv.put(DbContract.Orders.COL_PARFUM, parfum);
        cv.put(DbContract.Orders.COL_NOTE, note);
        cv.put(DbContract.Orders.COL_SUBTOTAL, subtotal);
        cv.put(DbContract.Orders.COL_TAX, tax);
        cv.put(DbContract.Orders.COL_TOTAL, total);
        cv.put(DbContract.Orders.COL_PAYMENT_STATUS, paymentStatus);
        cv.put(DbContract.Orders.COL_STATUS, "DITERIMA");
        if (createdByUserIdOr0 > 0) cv.put(DbContract.Orders.COL_CREATED_BY, createdByUserIdOr0);
        cv.put(DbContract.Orders.COL_CREATED_AT, now);
        cv.put(DbContract.Orders.COL_UPDATED_AT, now);

        db.insert(DbContract.Orders.TABLE, null, cv);
        return code;
    }

    public boolean updateStatus(String orderCode, String newStatus) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Orders.COL_STATUS, newStatus);
        cv.put(DbContract.Orders.COL_UPDATED_AT, System.currentTimeMillis());

        int rows = db.update(DbContract.Orders.TABLE, cv,
                DbContract.Orders.COL_ORDER_CODE + "=?",
                new String[]{orderCode});
        return rows > 0;
    }

    public OrderEntity getByOrderCode(String code) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql =
                "SELECT o." + DbContract.Orders._ID + ", o." + DbContract.Orders.COL_ORDER_CODE + "," +
                        " o." + DbContract.Orders.COL_CUSTOMER_ID + ", c." + DbContract.Customers.COL_NAME + ", c." + DbContract.Customers.COL_PHONE + ", c." + DbContract.Customers.COL_ADDRESS + "," +
                        // ✅ tambah duration_minutes dari services
                        " o." + DbContract.Orders.COL_SERVICE_ID + ", s." + DbContract.Services.COL_SPEED + ", s." + DbContract.Services.COL_TYPE + ", s." + DbContract.Services.COL_PRICE_PER_KG + ", s." + DbContract.Services.COL_DURATION_MINUTES + "," +
                        " o." + DbContract.Orders.COL_WEIGHT + ", o." + DbContract.Orders.COL_PARFUM + ", o." + DbContract.Orders.COL_NOTE + "," +
                        " o." + DbContract.Orders.COL_SUBTOTAL + ", o." + DbContract.Orders.COL_TAX + ", o." + DbContract.Orders.COL_TOTAL + "," +
                        " o." + DbContract.Orders.COL_PAYMENT_STATUS + ", o." + DbContract.Orders.COL_STATUS + "," +
                        " o." + DbContract.Orders.COL_CREATED_AT + ", o." + DbContract.Orders.COL_UPDATED_AT +
                        " FROM " + DbContract.Orders.TABLE + " o" +
                        " JOIN " + DbContract.Customers.TABLE + " c ON c." + DbContract.Customers._ID + "=o." + DbContract.Orders.COL_CUSTOMER_ID +
                        " JOIN " + DbContract.Services.TABLE + " s ON s." + DbContract.Services._ID + "=o." + DbContract.Orders.COL_SERVICE_ID +
                        " WHERE o." + DbContract.Orders.COL_ORDER_CODE + "=?";

        Cursor c = db.rawQuery(sql, new String[]{code});
        try {
            if (!c.moveToFirst()) return null;

            OrderEntity o = new OrderEntity();
            int i = 0;
            o.id = c.getLong(i++);
            o.orderCode = c.getString(i++);

            o.customerId = c.getLong(i++);
            o.customerName = c.getString(i++);
            o.customerPhone = c.getString(i++);
            o.customerAddress = c.getString(i++);

            o.serviceId = c.getLong(i++);
            o.speed = c.getString(i++);
            o.type = c.getString(i++);
            o.pricePerKg = c.getInt(i++);

            // ✅ NEW: durasi dari DB service
            o.durationMinutes = c.getInt(i++);

            o.weight = c.getDouble(i++);
            o.parfum = c.getString(i++);
            o.note = c.getString(i++);

            o.subtotal = c.getInt(i++);
            o.tax = c.getInt(i++);
            o.total = c.getInt(i++);

            o.paymentStatus = c.getString(i++);
            o.status = c.getString(i++);

            o.createdAt = c.getLong(i++);
            o.updatedAt = c.getLong(i++);

            return o;
        } finally {
            c.close();
        }
    }

    public List<OrderEntity> getHistory(long startMillis, long endMillis, String serviceTypeOrNull) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<OrderEntity> list = new ArrayList<>();

        String where = " WHERE o." + DbContract.Orders.COL_CREATED_AT + " BETWEEN ? AND ? ";
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(startMillis));
        args.add(String.valueOf(endMillis));

        if (serviceTypeOrNull != null && !serviceTypeOrNull.trim().isEmpty() && !"ALL".equals(serviceTypeOrNull)) {
            where += " AND s." + DbContract.Services.COL_TYPE + "=? ";
            args.add(serviceTypeOrNull);
        }

        String sql =
                "SELECT o." + DbContract.Orders.COL_ORDER_CODE + "," +
                        " c." + DbContract.Customers.COL_NAME + "," +
                        " s." + DbContract.Services.COL_SPEED + "," +
                        " s." + DbContract.Services.COL_TYPE + "," +
                        " o." + DbContract.Orders.COL_WEIGHT + "," +
                        " o." + DbContract.Orders.COL_TOTAL + "," +
                        " o." + DbContract.Orders.COL_STATUS + "," +
                        " o." + DbContract.Orders.COL_PAYMENT_STATUS + "," +
                        " o." + DbContract.Orders.COL_CREATED_AT +
                        " FROM " + DbContract.Orders.TABLE + " o" +
                        " JOIN " + DbContract.Customers.TABLE + " c ON c." + DbContract.Customers._ID + "=o." + DbContract.Orders.COL_CUSTOMER_ID +
                        " JOIN " + DbContract.Services.TABLE + " s ON s." + DbContract.Services._ID + "=o." + DbContract.Orders.COL_SERVICE_ID +
                        where +
                        " ORDER BY o." + DbContract.Orders.COL_CREATED_AT + " DESC";

        Cursor c = db.rawQuery(sql, args.toArray(new String[0]));
        try {
            while (c.moveToNext()) {
                OrderEntity o = new OrderEntity();
                int i = 0;
                o.orderCode = c.getString(i++);
                o.customerName = c.getString(i++);
                o.speed = c.getString(i++);
                o.type = c.getString(i++);
                o.weight = c.getDouble(i++);
                o.total = c.getInt(i++);
                o.status = c.getString(i++);
                o.paymentStatus = c.getString(i++);
                o.createdAt = c.getLong(i++);
                list.add(o);
            }
            return list;
        } finally {
            c.close();
        }
    }

    public int getTotalPaidRevenue(long startMillis, long endMillis) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(" + DbContract.Orders.COL_TOTAL + "),0) " +
                        " FROM " + DbContract.Orders.TABLE +
                        " WHERE " + DbContract.Orders.COL_CREATED_AT + " BETWEEN ? AND ? " +
                        " AND " + DbContract.Orders.COL_PAYMENT_STATUS + "='PAID'",
                new String[]{String.valueOf(startMillis), String.valueOf(endMillis)}
        );
        try {
            if (c.moveToFirst()) return c.getInt(0);
            return 0;
        } finally {
            c.close();
        }
    }

    public Cursor getReportCursor(long startMillis, long endMillis, String serviceTypeOrNull) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String where = " WHERE o." + DbContract.Orders.COL_CREATED_AT + " BETWEEN ? AND ? ";
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(startMillis));
        args.add(String.valueOf(endMillis));

        if (serviceTypeOrNull != null && !serviceTypeOrNull.trim().isEmpty() && !"ALL".equals(serviceTypeOrNull)) {
            where += " AND s." + DbContract.Services.COL_TYPE + "=? ";
            args.add(serviceTypeOrNull);
        }

        String sql =
                "SELECT o." + DbContract.Orders.COL_ORDER_CODE + " AS order_code," +
                        " o." + DbContract.Orders.COL_CREATED_AT + " AS created_at," +
                        " c." + DbContract.Customers.COL_NAME + " AS customer_name," +
                        " c." + DbContract.Customers.COL_PHONE + " AS customer_phone," +
                        " s." + DbContract.Services.COL_SPEED + " AS speed," +
                        " s." + DbContract.Services.COL_TYPE + " AS type," +
                        " o." + DbContract.Orders.COL_WEIGHT + " AS weight," +
                        " o." + DbContract.Orders.COL_SUBTOTAL + " AS subtotal," +
                        " o." + DbContract.Orders.COL_TAX + " AS tax," +
                        " o." + DbContract.Orders.COL_TOTAL + " AS total," +
                        " o." + DbContract.Orders.COL_PAYMENT_STATUS + " AS payment_status," +
                        " o." + DbContract.Orders.COL_STATUS + " AS status," +
                        " o." + DbContract.Orders.COL_NOTE + " AS note" +
                        " FROM " + DbContract.Orders.TABLE + " o" +
                        " JOIN " + DbContract.Customers.TABLE + " c ON c." + DbContract.Customers._ID + "=o." + DbContract.Orders.COL_CUSTOMER_ID +
                        " JOIN " + DbContract.Services.TABLE + " s ON s." + DbContract.Services._ID + "=o." + DbContract.Orders.COL_SERVICE_ID +
                        where +
                        " ORDER BY o." + DbContract.Orders.COL_CREATED_AT + " DESC";

        return db.rawQuery(sql, args.toArray(new String[0]));
    }

    public double getTotalWeight(long startMillis, long endMillis) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(" + DbContract.Orders.COL_WEIGHT + "),0) " +
                        " FROM " + DbContract.Orders.TABLE + " o " +
                        " WHERE o." + DbContract.Orders.COL_CREATED_AT + " BETWEEN ? AND ?",
                new String[]{String.valueOf(startMillis), String.valueOf(endMillis)}
        );
        try {
            if (c.moveToFirst()) return c.getDouble(0);
            return 0;
        } finally {
            c.close();
        }
    }

    public int getPaidRevenueBySpeed(long startMillis, long endMillis, String... speeds) {
        if (speeds == null || speeds.length == 0) return 0;

        StringBuilder in = new StringBuilder();
        for (int i = 0; i < speeds.length; i++) {
            in.append("?");
            if (i < speeds.length - 1) in.append(",");
        }

        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(startMillis));
        args.add(String.valueOf(endMillis));
        for (String s : speeds) args.add(s);

        String sql =
                "SELECT IFNULL(SUM(o." + DbContract.Orders.COL_TOTAL + "),0) " +
                        " FROM " + DbContract.Orders.TABLE + " o " +
                        " JOIN " + DbContract.Services.TABLE + " s ON s." + DbContract.Services._ID + "=o." + DbContract.Orders.COL_SERVICE_ID +
                        " WHERE o." + DbContract.Orders.COL_CREATED_AT + " BETWEEN ? AND ? " +
                        " AND o." + DbContract.Orders.COL_PAYMENT_STATUS + "='PAID' " +
                        " AND s." + DbContract.Services.COL_SPEED + " IN (" + in + ")";

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(sql, args.toArray(new String[0]));
        try {
            if (c.moveToFirst()) return c.getInt(0);
            return 0;
        } finally {
            c.close();
        }
    }

    public List<OrderEntity> getRecent(int limit) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<OrderEntity> list = new ArrayList<>();

        String sql =
                "SELECT o." + DbContract.Orders.COL_ORDER_CODE + "," +
                        " c." + DbContract.Customers.COL_NAME + "," +
                        " s." + DbContract.Services.COL_SPEED + "," +
                        " s." + DbContract.Services.COL_TYPE + "," +
                        " o." + DbContract.Orders.COL_WEIGHT + "," +
                        " o." + DbContract.Orders.COL_TOTAL + "," +
                        " o." + DbContract.Orders.COL_STATUS + "," +
                        " o." + DbContract.Orders.COL_PAYMENT_STATUS + "," +
                        " o." + DbContract.Orders.COL_CREATED_AT +
                        " FROM " + DbContract.Orders.TABLE + " o" +
                        " JOIN " + DbContract.Customers.TABLE + " c ON c." + DbContract.Customers._ID + "=o." + DbContract.Orders.COL_CUSTOMER_ID +
                        " JOIN " + DbContract.Services.TABLE + " s ON s." + DbContract.Services._ID + "=o." + DbContract.Orders.COL_SERVICE_ID +
                        " ORDER BY o." + DbContract.Orders.COL_CREATED_AT + " DESC" +
                        " LIMIT " + limit;

        Cursor c = db.rawQuery(sql, null);
        try {
            while (c.moveToNext()) {
                OrderEntity o = new OrderEntity();
                int i = 0;
                o.orderCode = c.getString(i++);
                o.customerName = c.getString(i++);
                o.speed = c.getString(i++);
                o.type = c.getString(i++);
                o.weight = c.getDouble(i++);
                o.total = c.getInt(i++);
                o.status = c.getString(i++);
                o.paymentStatus = c.getString(i++);
                o.createdAt = c.getLong(i++);
                list.add(o);
            }
            return list;
        } finally {
            c.close();
        }
    }
}
