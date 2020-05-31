package com.example.absensilokasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AbsenActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    SessionManager session;
    ImageButton btn_absen;
    ListView absen_list;
    AlertDialogManager alert = new AlertDialogManager();
    TextView Tjarak;
    //Double lathome = -7.631651880413749;
    //Double lnghome = 112.76422067042313;
    Double lathome = 0.0;
    Double lnghome = 0.0;
    Double rad= 0.0;
    Double curLat, curLong;
    private static final String TAG = AbsenActivity.class.getSimpleName();
    private static final String url_absen = "https://absensilokasi.azurewebsites.net/api/absen/create";
    private static final String url_get = "https://absensilokasi.azurewebsites.net/api/absen/getabsen/";
    private ProgressDialog pDialog;
    ArrayList<AbsenModel> listAbsen;
    private ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        session = new SessionManager(getApplicationContext());
        absen_list = findViewById(R.id.list_absen);
        Tjarak = findViewById(R.id.jarak);
        btn_absen = findViewById(R.id.btn_absen);
        final HashMap<String,String> user = session.getUserDetails();

        try {
            rad = Double.parseDouble(user.get(SessionManager.KEY_RAD));
            lathome = Double.parseDouble(user.get(SessionManager.KEY_LAT));
            lnghome = Double.parseDouble(user.get(SessionManager.KEY_LNG));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        loadAbsen(user.get(SessionManager.KEY_ID),user.get(SessionManager.KEY_TOKEN));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);







    btn_absen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String strlat, strlng;
                   strlat = curLat.toString();
                   strlng = curLong.toString();
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            kirimAbsen(strlat,strlng,user.get(SessionManager.KEY_TOKEN), user.get(SessionManager.KEY_ID));
                            //Toast.makeText(AbsenActivity.this,"Lat = "+ strlat+" & Long = "+ strlng,Toast.LENGTH_LONG).show();
                            //getAddress(curLat,curLong);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(AbsenActivity.this);
            builder.setTitle("Absen").setMessage("Apakah anda akan Mengirim Lokasi saat ini ?").setPositiveButton("YES", dialogClickListener).setNegativeButton("NO", dialogClickListener).show();
        }
    });

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        curLat = mLastLocation.getLatitude();
        curLong = mLastLocation.getLongitude();
        LatLng homelok = new LatLng(lathome,lnghome);
        MarkerOptions myhome = new MarkerOptions();
        myhome.position(homelok);
        myhome.title("Home");
        myhome.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(myhome);
        //Place current location marker
        LatLng latLng = new LatLng(curLat, curLong);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        Double jarak = distance(lathome,lnghome,curLat,curLong);
        String strjarak = String.format("%.2f",jarak);
        if (jarak > rad){
            Tjarak.setText("Jarak dari Rumah = "+ strjarak +" Km");
            Tjarak.setTextColor(Color.parseColor("#FFFF0000"));
        }else {
            Tjarak.setText("Jarak dari Rumah = "+ strjarak +" Km");
            Tjarak.setTextColor(Color.parseColor("#000000"));
        }

        //stop location updates
        /*
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
         */


    }
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }
    @Override
    public void onConnectionSuspended(int i) {

    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void loadAbsen(String id, final String utoken){
        pDialog = new ProgressDialog(AbsenActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        String uri = String.format("https://absensilokasi.azurewebsites.net/api/absen/getabsen/"+id+"?c_token=%1$s",utoken);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pDialog.hide();
                pDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    Boolean stat = obj.getBoolean("success");

                    if (stat){
                        JSONArray jsonArray = obj.getJSONArray("data");
                        listAbsen = new ArrayList<>();
                        for (int i =0 ;i < jsonArray.length(); i++){
                            AbsenModel absenModel = new AbsenModel();

                            JSONObject dataobj = jsonArray.getJSONObject(i);

                            absenModel.setId(dataobj.getString("id_absensi"));
                            absenModel.setJam(dataobj.getString("jam"));
                            absenModel.setTanggal(dataobj.getString("tanggal"));
                            absenModel.setStatus(dataobj.getString("status"));
                           absenModel.setLat(dataobj.getDouble("lat"));
                           absenModel.setLng(dataobj.getDouble("long"));
                        listAbsen.add(absenModel);
                        }
                        setupListview();
                    }else {
                        String cod = obj.getString("code");
                        String mess = obj.getString("message");
                        if (cod.equals("token")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(AbsenActivity.this);
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
                            Toast.makeText(AbsenActivity.this,mess,Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AbsenActivity.this,"Network Error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        AppSingleton.getInstance(AbsenActivity.this).addToRequestQueue(stringRequest,TAG);
    }
    public void kirimAbsen(final String slat, final String slng, final String ctoken, final String cid){
        pDialog = new ProgressDialog(AbsenActivity.this);
        pDialog.setMessage("Sending...");
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_absen, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //pDialog.hide();
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean stat = jsonObject.getBoolean("success");
                    String mess = jsonObject.getString("message");
                    if (stat){
                        AlertDialog.Builder ab = new AlertDialog.Builder(AbsenActivity.this);
                        DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        loadAbsen(cid,ctoken);
                                        break;
                                }
                            }
                        };
                        ab.setTitle("Absen").setMessage(mess).setPositiveButton("OK", dialogClick).show();
                    }else {
                        String cd = jsonObject.getString("code");

                        if (cd.equals("token")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(AbsenActivity.this);
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            session.logoutUser();
                                            break;
                                    }
                                }
                            };
                            builder.setTitle("Absen").setMessage("Sesi Anda telah berakhir !").setPositiveButton("OK", dialogClickListener).show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AbsenActivity.this,"Network Error",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params =  new HashMap<String, String>();
                params.put("c_token",ctoken);
                params.put("lat", slat);
                params.put("long", slng);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance(AbsenActivity.this).addToRequestQueue(stringRequest,TAG);
    }
    private void setupListview(){
        listAdapter = new ListAdapter(AbsenActivity.this,listAbsen);
        absen_list.setAdapter(listAdapter);
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
