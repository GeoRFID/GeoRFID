package com.opencartis.georfid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;

public class Settings extends PreferenceActivity {

    private static String dbServer;
    private static String dbUser;
    private static String dbPass;
    private static String loginUser;
    private static String loginPassword;

    public static String getDbServer() {
        return dbServer;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPass() {
        return dbPass;
    }

    public static String getLoginUser() {
        return loginUser;
    }

    public static String getLoginPassword() {
        return loginPassword;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

    public void LoadPreferences(Context context) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            dbServer = sharedPreferences.getString("dbserver", "");
            dbUser = sharedPreferences.getString("dbuser", "");
            dbPass = sharedPreferences.getString("dbpassword", "");
            loginUser = sharedPreferences.getString("loginuser", "");
            loginPassword = sharedPreferences.getString("loginpassword", "");

        } catch (Exception e) {
        }
    }

    public void SaveLoginUser(Context context, String loginUser) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("loginuser", loginUser);
            editor.commit();

        } catch (Exception e) {
        }
    }

    public void SaveLoginPassword(Context context, String loginPassword) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("loginpassword", loginPassword);
            editor.commit();

        } catch (Exception e) {
        }
    }

}
