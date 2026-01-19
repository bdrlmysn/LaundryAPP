package com.example.laundryapp.data.db;

import android.provider.BaseColumns;

public final class DbContract {
    private DbContract() {}

    public static final String DB_NAME = "laundryapp.db";
    public static final int DB_VERSION = 1;

    public static final class Users implements BaseColumns {
        public static final String TABLE = "users";
        public static final String COL_USERNAME = "username";
        public static final String COL_PASSWORD = "password";
        public static final String COL_ROLE = "role"; // OWNER / CASHIER
        public static final String COL_CREATED_AT = "created_at";
    }

    public static final class Customers implements BaseColumns {
        public static final String TABLE = "customers";
        public static final String COL_NAME = "name";
        public static final String COL_PHONE = "phone";
        public static final String COL_ADDRESS = "address";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
    }

    public static final class Services implements BaseColumns {
        public static final String TABLE = "services";
        public static final String COL_SPEED = "speed"; // REGULER/KILAT/INSTANT
        public static final String COL_TYPE = "type";   // CUCI_SETIRKA/CUCI_SAJA/SETRIKA_SAJA
        public static final String COL_PRICE_PER_KG = "price_per_kg";
        public static final String COL_IS_ACTIVE = "is_active";
        public static final String COL_CREATED_AT = "created_at";
    }

    public static final class Orders implements BaseColumns {
        public static final String TABLE = "orders";
        public static final String COL_ORDER_CODE = "order_code"; // ORD-xxxxx
        public static final String COL_CUSTOMER_ID = "customer_id";
        public static final String COL_SERVICE_ID = "service_id";
        public static final String COL_WEIGHT = "weight";
        public static final String COL_PARFUM = "parfum";
        public static final String COL_NOTE = "note";
        public static final String COL_SUBTOTAL = "subtotal";
        public static final String COL_TAX = "tax";
        public static final String COL_TOTAL = "total";
        public static final String COL_PAYMENT_STATUS = "payment_status"; // PAID/UNPAID
        public static final String COL_STATUS = "status"; // DITERIMA..DIAMBIL
        public static final String COL_CREATED_BY = "created_by";
        public static final String COL_CREATED_AT = "created_at";
        public static final String COL_UPDATED_AT = "updated_at";
    }
}
