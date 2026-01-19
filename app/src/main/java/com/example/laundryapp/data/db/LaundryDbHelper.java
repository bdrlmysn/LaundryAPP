package com.example.laundryapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LaundryDbHelper extends SQLiteOpenHelper {

    private static volatile LaundryDbHelper INSTANCE;

    public static LaundryDbHelper getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (LaundryDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LaundryDbHelper(ctx.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private LaundryDbHelper(@Nullable Context context) {
        super(context, DbContract.DB_NAME, null, DbContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // USERS
        db.execSQL("CREATE TABLE " + DbContract.Users.TABLE + " (" +
                DbContract.Users._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Users.COL_USERNAME + " TEXT NOT NULL UNIQUE," +
                DbContract.Users.COL_PASSWORD + " TEXT NOT NULL," +
                DbContract.Users.COL_ROLE + " TEXT NOT NULL," +
                DbContract.Users.COL_CREATED_AT + " INTEGER NOT NULL" +
                ");");

        // CUSTOMERS
        db.execSQL("CREATE TABLE " + DbContract.Customers.TABLE + " (" +
                DbContract.Customers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Customers.COL_NAME + " TEXT NOT NULL," +
                DbContract.Customers.COL_PHONE + " TEXT NOT NULL," +
                DbContract.Customers.COL_ADDRESS + " TEXT," +
                DbContract.Customers.COL_CREATED_AT + " INTEGER NOT NULL," +
                DbContract.Customers.COL_UPDATED_AT + " INTEGER NOT NULL" +
                ");");

        db.execSQL("CREATE INDEX idx_customers_name ON " + DbContract.Customers.TABLE +
                "(" + DbContract.Customers.COL_NAME + ");");

        // SERVICES
        db.execSQL("CREATE TABLE " + DbContract.Services.TABLE + " (" +
                DbContract.Services._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Services.COL_SPEED + " TEXT NOT NULL," +
                DbContract.Services.COL_TYPE + " TEXT NOT NULL," +
                DbContract.Services.COL_PRICE_PER_KG + " INTEGER NOT NULL," +
                DbContract.Services.COL_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1," +
                DbContract.Services.COL_CREATED_AT + " INTEGER NOT NULL," +
                "UNIQUE(" + DbContract.Services.COL_SPEED + ", " + DbContract.Services.COL_TYPE + ")" +
                ");");

        // ORDERS
        db.execSQL("CREATE TABLE " + DbContract.Orders.TABLE + " (" +
                DbContract.Orders._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Orders.COL_ORDER_CODE + " TEXT NOT NULL UNIQUE," +
                DbContract.Orders.COL_CUSTOMER_ID + " INTEGER NOT NULL," +
                DbContract.Orders.COL_SERVICE_ID + " INTEGER NOT NULL," +
                DbContract.Orders.COL_WEIGHT + " REAL NOT NULL," +
                DbContract.Orders.COL_PARFUM + " TEXT," +
                DbContract.Orders.COL_NOTE + " TEXT," +
                DbContract.Orders.COL_SUBTOTAL + " INTEGER NOT NULL," +
                DbContract.Orders.COL_TAX + " INTEGER NOT NULL," +
                DbContract.Orders.COL_TOTAL + " INTEGER NOT NULL," +
                DbContract.Orders.COL_PAYMENT_STATUS + " TEXT NOT NULL," +
                DbContract.Orders.COL_STATUS + " TEXT NOT NULL," +
                DbContract.Orders.COL_CREATED_BY + " INTEGER," +
                DbContract.Orders.COL_CREATED_AT + " INTEGER NOT NULL," +
                DbContract.Orders.COL_UPDATED_AT + " INTEGER NOT NULL," +
                "FOREIGN KEY(" + DbContract.Orders.COL_CUSTOMER_ID + ") REFERENCES " +
                DbContract.Customers.TABLE + "(" + DbContract.Customers._ID + ")," +
                "FOREIGN KEY(" + DbContract.Orders.COL_SERVICE_ID + ") REFERENCES " +
                DbContract.Services.TABLE + "(" + DbContract.Services._ID + ")" +
                ");");

        db.execSQL("CREATE INDEX idx_orders_created_at ON " + DbContract.Orders.TABLE +
                "(" + DbContract.Orders.COL_CREATED_AT + ");");

        seed(db);
    }

    private void seed(SQLiteDatabase db) {
        long now = System.currentTimeMillis();

        // default users
        insertUser(db, "admin", "admin123", "OWNER", now);
        insertUser(db, "kasir", "kasir123", "CASHIER", now);

        // default services
        // REGULER
        insertService(db, "REGULER", "CUCI_SETIRKA", 12000, now);
        insertService(db, "REGULER", "CUCI_SAJA", 10000, now);
        insertService(db, "REGULER", "SETRIKA_SAJA", 8000, now);
        // KILAT
        insertService(db, "KILAT", "CUCI_SETIRKA", 24000, now);
        insertService(db, "KILAT", "CUCI_SAJA", 20000, now);
        insertService(db, "KILAT", "SETRIKA_SAJA", 16000, now);
        // INSTANT
        insertService(db, "INSTANT", "CUCI_SETIRKA", 32000, now);
        insertService(db, "INSTANT", "CUCI_SAJA", 28000, now);
        insertService(db, "INSTANT", "SETRIKA_SAJA", 22000, now);
    }

    private void insertUser(SQLiteDatabase db, String u, String p, String role, long now) {
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Users.COL_USERNAME, u);
        cv.put(DbContract.Users.COL_PASSWORD, p);
        cv.put(DbContract.Users.COL_ROLE, role);
        cv.put(DbContract.Users.COL_CREATED_AT, now);
        db.insert(DbContract.Users.TABLE, null, cv);
    }

    private void insertService(SQLiteDatabase db, String speed, String type, int price, long now) {
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Services.COL_SPEED, speed);
        cv.put(DbContract.Services.COL_TYPE, type);
        cv.put(DbContract.Services.COL_PRICE_PER_KG, price);
        cv.put(DbContract.Services.COL_IS_ACTIVE, 1);
        cv.put(DbContract.Services.COL_CREATED_AT, now);
        db.insert(DbContract.Services.TABLE, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // simple: destructive migration for MVP
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Orders.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Services.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Customers.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.Users.TABLE);
        onCreate(db);
    }
}
