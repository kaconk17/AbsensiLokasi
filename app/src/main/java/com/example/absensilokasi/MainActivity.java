package com.example.absensilokasi;

import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    SessionManager session;
    CircleImageView imgProfile;
    AlertDialogManager alert = new AlertDialogManager();
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
                                alert.showAlertDialog(MainActivity.this, "Logout !", "Anda logout", false);
                                return true;
                            case R.id.one:
                                alert.showAlertDialog(MainActivity.this, "One !", "Anda logout", true);
                                return true;
                            default:
                                return false;
                        }

                    }
                });
                popup.show();

            }
        });

    }

}
