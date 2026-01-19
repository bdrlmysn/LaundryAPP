package com.example.laundryapp.data.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.laundryapp.data.db.DbContract;
import com.example.laundryapp.data.db.LaundryDbHelper;
import com.example.laundryapp.data.db.model.ServiceEntity;

import java.util.ArrayList;
import java.util.List;

public class ServiceDao {
    private final LaundryDbHelper helper;

    public ServiceDao(Context ctx) {
        helper = LaundryDbHelper.getInstance(ctx);
    }

    public long insert(String speed, String type, int pricePerKg, boolean active) {
        long now = System.currentTimeMillis();
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DbContract.Services.COL_SPEED, speed);
        cv.put(DbContract.Services.COL_TYPE, type);
        cv.put(DbContract.Services.COL_PRICE_PER_KG, pricePerKg);
        cv.put(DbContract.Services.COL_IS_ACTIVE, active ? 1 : 0);
        cv.put(DbContract.Services.COL_CREATED_AT, now);
        return db.insert(DbContract.Services.TABLE, null, cv);
    }

    public boolean update(long id, String speed, String type, int pricePerKg, boolean active) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Services.COL_SPEED, speed);
        cv.put(DbContract.Services.COL_TYPE, type);
        cv.put(DbContract.Services.COL_PRICE_PER_KG, pricePerKg);
        cv.put(DbContract.Services.COL_IS_ACTIVE, active ? 1 : 0);

        int rows = db.update(DbContract.Services.TABLE, cv, DbContract.Services._ID + "=?",
                new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete(DbContract.Services.TABLE, DbContract.Services._ID + "=?",
                new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public List<ServiceEntity> getAll(boolean onlyActive) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ServiceEntity> list = new ArrayList<>();

        String selection = null;
        String[] args = null;
        if (onlyActive) {
            selection = DbContract.Services.COL_IS_ACTIVE + "=?";
            args = new String[]{"1"};
        }

        Cursor c = db.query(
                DbContract.Services.TABLE,
                new String[]{DbContract.Services._ID, DbContract.Services.COL_SPEED, DbContract.Services.COL_TYPE,
                        DbContract.Services.COL_PRICE_PER_KG, DbContract.Services.COL_IS_ACTIVE},
                selection, args, null, null,
                DbContract.Services.COL_SPEED + " ASC, " + DbContract.Services.COL_TYPE + " ASC"
        );

        try {
            while (c.moveToNext()) {
                list.add(new ServiceEntity(
                        c.getLong(0),
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3),
                        c.getInt(4) == 1
                ));
            }
            return list;
        } finally {
            c.close();
        }
    }

    public ServiceEntity getById(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DbContract.Services.TABLE,
                new String[]{DbContract.Services._ID, DbContract.Services.COL_SPEED, DbContract.Services.COL_TYPE,
                        DbContract.Services.COL_PRICE_PER_KG, DbContract.Services.COL_IS_ACTIVE},
                DbContract.Services._ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );
        try {
            if (c.moveToFirst()) {
                return new ServiceEntity(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4) == 1);
            }
            return null;
        } finally {
            c.close();
        }
    }

    public List<String> getSpeeds() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT DISTINCT " + DbContract.Services.COL_SPEED +
                        " FROM " + DbContract.Services.TABLE +
                        " WHERE " + DbContract.Services.COL_IS_ACTIVE + "=1" +
                        " ORDER BY " + DbContract.Services.COL_SPEED + " ASC",
                null
        );
        try {
            while (c.moveToNext()) list.add(c.getString(0));
            return list;
        } finally {
            c.close();
        }
    }

    public List<String> getTypesBySpeed(String speed) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor c = db.rawQuery(
                "SELECT " + DbContract.Services.COL_TYPE +
                        " FROM " + DbContract.Services.TABLE +
                        " WHERE " + DbContract.Services.COL_SPEED + "=? AND " + DbContract.Services.COL_IS_ACTIVE + "=1" +
                        " ORDER BY " + DbContract.Services.COL_TYPE + " ASC",
                new String[]{speed}
        );
        try {
            while (c.moveToNext()) list.add(c.getString(0));
            return list;
        } finally {
            c.close();
        }
    }

    public ServiceEntity getBySpeedAndType(String speed, String type) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                DbContract.Services.TABLE,
                new String[]{DbContract.Services._ID, DbContract.Services.COL_SPEED, DbContract.Services.COL_TYPE,
                        DbContract.Services.COL_PRICE_PER_KG, DbContract.Services.COL_IS_ACTIVE},
                DbContract.Services.COL_SPEED + "=? AND " + DbContract.Services.COL_TYPE + "=? AND " + DbContract.Services.COL_IS_ACTIVE + "=1",
                new String[]{speed, type},
                null, null, null
        );
        try {
            if (c.moveToFirst()) {
                return new ServiceEntity(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3), c.getInt(4) == 1);
            }
            return null;
        } finally {
            c.close();
        }
    }
}
