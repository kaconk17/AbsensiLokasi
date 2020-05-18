package com.example.absensilokasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginActivity extends AppCompatActivity {
    EditText txtemail, txtpass;
    Button btn_login;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());
        txtemail = findViewById(R.id.input_email);
        txtpass = findViewById(R.id.input_password);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtemail.getText().toString();
                String pass = txtpass.getText().toString();

                if (email.trim().length() > 0 && pass.trim().length() > 0){
                    if (email.equals("admin@gmail.com") && pass.equals("admin")){
                        session.createLoginSession("Admin", "admin@gmail.com");

                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        alert.showAlertDialog(loginActivity.this, "Login Gagal !", "Email / Password Salah", false);
                    }
                }else{
                    alert.showAlertDialog(loginActivity.this, "Login Gagal !", "Mohon Masukkan Email dan Password", false);
                }
            }
        });
    }
}
