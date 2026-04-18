package com.example.hotellapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "hotel_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE_ID = "role_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGGED_IN = "logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveLoginSession(int userId, int roleId, String fullName, String email) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_ROLE_ID, roleId);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public void updateUserSession(String fullName, String email) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public int getRoleId() {
        return prefs.getInt(KEY_ROLE_ID, -1);
    }

    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void clearSession() {
        editor.clear().apply();
    }
}