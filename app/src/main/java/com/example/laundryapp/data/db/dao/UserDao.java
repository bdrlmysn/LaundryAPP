package com.example.laundryapp.data.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.laundryapp.data.db.DbContract;
import com.example.laundryapp.data.db.LaundryDbHelper;
import com.example.laundryapp.data.db.model.User;

public class UserDao {
    private final LaundryDbHelper helper;

    public UserDao(Context ctx) {
        helper = LaundryDbHelper.getInstance(ctx);
    }

    public User login(String username, String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(
                DbContract.Users.TABLE,
                new String[]{DbContract.Users._ID, DbContract.Users.COL_USERNAME, DbContract.Users.COL_ROLE},
                DbContract.Users.COL_USERNAME + "=? AND " + DbContract.Users.COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null
        );
        try {
            if (c.moveToFirst()) {
                long id = c.getLong(0);
                String u = c.getString(1);
                String role = c.getString(2);
                return new User(id, u, role);
            }
            return null;
        } finally {
            c.close();
        }
    }
}
