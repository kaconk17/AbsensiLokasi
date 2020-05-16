package com.example.absensilokasi;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    SessionManager session;
Boolean login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        HashMap<String, String> user = session.getUserDetails();

        String name = user.get(SessionManager.KEY_NAME);
        String email = user.get(SessionManager.KEY_EMAIL);

    }
}
