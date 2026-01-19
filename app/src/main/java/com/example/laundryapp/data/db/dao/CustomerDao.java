package com.example.laundryapp.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.laundryapp.data.db.DbContract;
import com.example.laundryapp.data.db.LaundryDbHelper;
import com.example.laundryapp.data.db.model.CustomerEntity;

import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    private final LaundryDbHelper helper;

    public CustomerDao(Context ctx) {
        helper = LaundryDbHelper.getInstance(ctx);
    }

    public long insert(String name, String phone, String address) {
        long now = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DbContract.Customers.COL_NAME, name);
        cv.put(DbContract.Customers.COL_PHONE, phone);
        cv.put(DbContract.Customers.COL_ADDRESS, address);
        cv.put(DbContract.Customers.COL_CREATED_AT, now);
        cv.put(DbContract.Customers.COL_UPDATED_AT, now);
        return db.insert(DbContract.Customers.TABLE, null, cv);
    }

    public boolean update(long id, String name, String phone, String address) {
        long now = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DbContract.Customers.COL_NAME, name);
        cv.put(DbContract.Customers.COL_PHONE, phone);
        cv.put(DbContract.Customers.COL_ADDRESS, address);
        cv.put(DbContract.Customers.COL_UPDATED_AT, now);

        int rows = db.update(DbContract.Customers.TABLE, cv, DbContract.Customers._ID + "=?",
                new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(DbContract.Customers.TABLE, DbContract.Customers._ID + "=?",
                new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public CustomerEntity getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.Customers.TABLE,
                new String[]{DbContract.Customers._ID, DbContract.Customers.COL_NAME, DbContract.Customers.COL_PHONE, DbContract.Customers.COL_ADDRESS},
                DbContract.Customers._ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        try {
            if (c.moveToFirst()) {
                return new CustomerEntity(
                        c.getLong(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3)
                );
            }
            return null;
        } finally {
            c.close();
        }
    }

    public List<CustomerEntity> getAll(String query) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<CustomerEntity> list = new ArrayList<>();

        String selection = null;
        String[] args = null;

        if (query != null && !query.trim().isEmpty()) {
            selection = DbContract.Customers.COL_NAME + " LIKE ? OR " +
                    DbContract.Customers.COL_PHONE + " LIKE ?";
            String q = "%" + query.trim() + "%";
            args = new String[]{q, q};
        }

        Cursor c = db.query(
                DbContract.Customers.TABLE,
                new String[]{DbContract.Customers._ID, DbContract.Customers.COL_NAME, DbContract.Customers.COL_PHONE, DbContract.Customers.COL_ADDRESS},
                selection,
                args,
                null, null,
                DbContract.Customers.COL_UPDATED_AT + " DESC"
        );

        try {
            while (c.moveToNext()) {
                list.add(new CustomerEntity(
                        c.getLong(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3)
                ));
            }
            return list;
        } finally {
            c.close();
        }
    }
}
