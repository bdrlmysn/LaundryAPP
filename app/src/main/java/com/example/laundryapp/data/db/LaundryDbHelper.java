package com.example.laundryapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

        // SERVICES (NEW: duration_minutes)
        db.execSQL("CREATE TABLE " + DbContract.Services.TABLE + " (" +
                DbContract.Services._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.Services.COL_SPEED + " TEXT NOT NULL," +
                DbContract.Services.COL_TYPE + " TEXT NOT NULL," +
                DbContract.Services.COL_PRICE_PER_KG + " INTEGER NOT NULL," +
                DbContract.Services.COL_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1," +
                DbContract.Services.COL_CREATED_AT + " INTEGER NOT NULL," +
                DbContract.Services.COL_DURATION_MINUTES + " INTEGER NOT NULL DEFAULT 2880," +
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

        // default services + DURASI
        // REGULER = 2 hari = 2880 menit
        insertService(db, "REGULER", "CUCI_SETIRKA", 12000, 2880, now);
        insertService(db, "REGULER", "CUCI_SAJA", 10000, 2880, now);
        insertService(db, "REGULER", "SETRIKA_SAJA", 8000, 2880, now);

        // KILAT = 1 hari = 1440 menit
        insertService(db, "KILAT", "CUCI_SETIRKA", 24000, 1440, now);
        insertService(db, "KILAT", "CUCI_SAJA", 20000, 1440, now);
        insertService(db, "KILAT", "SETRIKA_SAJA", 16000, 1440, now);

        // INSTANT = 4 jam = 240 menit
        insertService(db, "INSTANT", "CUCI_SETIRKA", 32000, 240, now);
        insertService(db, "INSTANT", "CUCI_SAJA", 28000, 240, now);
        insertService(db, "INSTANT", "SETRIKA_SAJA", 22000, 240, now);
    }

    private void insertUser(SQLiteDatabase db, String u, String p, String role, long now) {
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Users.COL_USERNAME, u);
        cv.put(DbContract.Users.COL_PASSWORD, p);
        cv.put(DbContract.Users.COL_ROLE, role);
        cv.put(DbContract.Users.COL_CREATED_AT, now);
        db.insert(DbContract.Users.TABLE, null, cv);
    }

    private void insertService(SQLiteDatabase db, String speed, String type, int price, int durationMinutes, long now) {
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Services.COL_SPEED, speed);
        cv.put(DbContract.Services.COL_TYPE, type);
        cv.put(DbContract.Services.COL_PRICE_PER_KG, price);
        cv.put(DbContract.Services.COL_IS_ACTIVE, 1);
        cv.put(DbContract.Services.COL_CREATED_AT, now);
        cv.put(DbContract.Services.COL_DURATION_MINUTES, durationMinutes);
        db.insert(DbContract.Services.TABLE, null, cv);
    }

    private boolean hasColumn(SQLiteDatabase db, String table, String column) {
        Cursor c = null;
        try {
            c = db.rawQuery("PRAGMA table_info(" + table + ")", null);
            while (c.moveToNext()) {
                String colName = c.getString(c.getColumnIndexOrThrow("name"));
                if (column.equalsIgnoreCase(colName)) return true;
            }
            return false;
        } finally {
            if (c != null) c.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade ke v2: tambah duration_minutes di services (tanpa hapus data)
        if (oldVersion < 2) {
            if (!hasColumn(db, DbContract.Services.TABLE, DbContract.Services.COL_DURATION_MINUTES)) {
                db.execSQL("ALTER TABLE " + DbContract.Services.TABLE +
                        " ADD COLUMN " + DbContract.Services.COL_DURATION_MINUTES +
                        " INTEGER NOT NULL DEFAULT 2880");
            }

            // set default durasi berdasarkan speed yang sudah ada
            db.execSQL("UPDATE " + DbContract.Services.TABLE +
                    " SET " + DbContract.Services.COL_DURATION_MINUTES + " = 2880" +
                    " WHERE UPPER(" + DbContract.Services.COL_SPEED + ")='REGULER'");

            db.execSQL("UPDATE " + DbContract.Services.TABLE +
                    " SET " + DbContract.Services.COL_DURATION_MINUTES + " = 1440" +
                    " WHERE UPPER(" + DbContract.Services.COL_SPEED + ")='KILAT'");

            db.execSQL("UPDATE " + DbContract.Services.TABLE +
                    " SET " + DbContract.Services.COL_DURATION_MINUTES + " = 240" +
                    " WHERE UPPER(" + DbContract.Services.COL_SPEED + ")='INSTANT'");
        }
    }
}
