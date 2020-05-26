package com.example.absensilokasi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AbsensiPref";
    private static final String IS_LOGIN = "IsLoggedin";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_ID = "id";


    //construktor

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //create login session

    public void createLoginSession(String name, String email, String token, String id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_TOKEN, token);

        editor.putString(KEY_ID, id);

        // commit changes
        editor.commit();
    }

    //get stored session data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // return user
        return user;
    }

    //check user login or not

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, loginActivity.class);
            // Closing all the Activities
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            // Add new Flag to start new Activity
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public  void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, loginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }
}
