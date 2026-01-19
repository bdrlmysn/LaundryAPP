package com.example.laundryapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.laundryapp.data.db.model.User;

public class SessionManager {
    private static final String PREF = "session_laundry";
    private static final String KEY_LOGGED = "logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences sp;

    public SessionManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void login(User u) {
        sp.edit()
                .putBoolean(KEY_LOGGED, true)
                .putLong(KEY_USER_ID, u.id)
                .putString(KEY_USERNAME, u.username)
                .putString(KEY_ROLE, u.role)
                .apply();
    }

    public void logout() {
        sp.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return sp.getBoolean(KEY_LOGGED, false);
    }

    public long userId() {
        return sp.getLong(KEY_USER_ID, 0);
    }

    public String role() {
        return sp.getString(KEY_ROLE, "");
    }

    public boolean isOwner() {
        return "OWNER".equalsIgnoreCase(role());
    }
}
