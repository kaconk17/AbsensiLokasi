package com.example.absensilokasi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

//TODO: notifikasi = https://www.tutorialspoint.com/how-to-schedule-local-notifications-in-android
//TODO: simpan device ID sebaiagai identitas tambahan token

public class MainActivity extends AppCompatActivity {
    SessionManager session;
    CircleImageView imgProfile;
    TextView user_name,uemail;
    CardView btn_absesn, btn_hist;
    AlertDialogManager alert = new AlertDialogManager();
Boolean login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show();
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        } else {
            // Permission has already been granted
            Toast.makeText(this, "Izin Lokasi diberikan", Toast.LENGTH_SHORT).show();
        }
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        btn_absesn = findViewById(R.id.absen_btn);
        btn_hist = findViewById(R.id.btn_history);
        HashMap<String, String> user = session.getUserDetails();

        String name = user.get(SessionManager.KEY_NAME);
        String email = user.get(SessionManager.KEY_EMAIL);
        user_name = findViewById(R.id.txtuser);
        uemail = findViewById(R.id.txtemail);
        user_name.setText(name);
        uemail.setText(email);

        imgProfile = findViewById(R.id.profile_pic);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, imgProfile);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.logout:
                                //alert.showAlertDialog(MainActivity.this, "Logout !", "Anda logout", false);
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                    session.logoutUser();
                                                break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                        }
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("logout").setMessage("Apakah anda akan Logout ?").setPositiveButton("YES", dialogClickListener).setNegativeButton("NO", dialogClickListener).show();
                                return true;
                            case R.id.profile:
                                //alert.showAlertDialog(MainActivity.this, "One !", "Profile", true);
                                Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                                startActivity(i);
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popup.show();

            }
        });

        btn_absesn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),AbsenActivity.class);
                startActivity(i);
            }
        });

        btn_hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),HistoryActivity.class);
                startActivity(i);
            }
        });

    }

}
