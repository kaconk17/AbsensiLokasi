package com.example.absensilokasi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private TextView txt_name, txt_nik, txt_email, txt_tgl, txt_jenis, txt_no, txt_dept, txt_jabatan, txt_alamat, txt_kor, txt_long;
    private String id, token;
    private String base_url ;
    SessionManager session;
    private ProgressDialog pDialog;
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        txt_name = findViewById(R.id.txt_name);
        txt_nik = findViewById(R.id.txt_nik);
        txt_email = findViewById(R.id.txt_email);
        txt_tgl = findViewById(R.id.txt_tgl);
        txt_jenis = findViewById(R.id.txt_jenis);
        txt_no = findViewById(R.id.txt_no);
        txt_dept = findViewById(R.id.txt_dept);
        txt_jabatan = findViewById(R.id.txt_jabatan);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_kor = findViewById(R.id.txt_kor);
        txt_long = findViewById(R.id.txt_long);
        session = new SessionManager(getApplicationContext());
        final HashMap<String,String> user = session.getUserDetails();
        base_url = getString(R.string.base_url);
        id = user.get(SessionManager.KEY_ID);
        token = user.get(SessionManager.KEY_TOKEN);

        getProfile(id,token);

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void getProfile(String id, String ctoken){
        pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        String uri = String.format(base_url+"user/profile/"+id+"?c_token=%1$s",ctoken);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean stat = jsonObject.getBoolean("success");
                    String mess = jsonObject.getString("message");

                    if (stat){
                        JSONObject usr = jsonObject.getJSONObject("user");
                        String alamat = getAddress(usr.getDouble("lat"),usr.getDouble("long"));
                        String strlat = String.valueOf(usr.getDouble("lat"));
                        String strlng = String.valueOf(usr.getDouble("long"));
                        String kelamin;
                        if (usr.getString("kelamin").equals("L")){
                            kelamin = "Laki-laki";
                        }else{
                            kelamin = "Perempuan";
                        }
                        txt_name.setText(usr.getString("nama"));
                        txt_nik.setText(usr.getString("nik"));
                        txt_email.setText(usr.getString("email"));
                        txt_tgl.setText(usr.getString("tgl_lahir"));
                        txt_jenis.setText(kelamin);
                        txt_no.setText(usr.getString("no_telepon"));
                        txt_dept.setText(usr.getString("dept"));
                        txt_jabatan.setText(usr.getString("jabatan"));
                        txt_alamat.setText(alamat);
                        txt_kor.setText("Latitude : "+strlat);
                        txt_long.setText("Longitude : "+strlng);
                    }else{
                        String cod = jsonObject.getString("code");
                        if (cod.equals("token")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            session.logoutUser();
                                    }
                                }
                            };
                            builder.setTitle("Absen").setMessage("Sesi Anda telah berakhir !").setPositiveButton("OK", dialogClickListener).show();
                        }else {
                            Toast.makeText(ProfileActivity.this,mess,Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest,TAG);
    }

    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(ProfileActivity.this, Locale.getDefault());
        String alamat;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            alamat = obj.getSubThoroughfare()+", "+ obj.getLocality() +", "+ obj.getSubAdminArea()+", "+ obj.getAdminArea()+", "+ obj.getCountryName();
            // String add = obj.getAddressLine(0);
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            // add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            // add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            //Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
            return alamat;
        } catch (IOException e) {

            return e.toString();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}
