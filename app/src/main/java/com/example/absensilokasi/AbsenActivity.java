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

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
    Double lathome = -7.631651880413749;
    Double lnghome = 112.76422067042313;
    Double curLat, curLong;
    private static final String TAG = AbsenActivity.class.getSimpleName();
    private static final String url_absen = "https://absensilokasi.azurewebsites.net/api/absen/create";
    private ProgressDialog pDialog;

    String jamlist[] = {"08:00","09:30", "15:45"};

    String tanggal[] = {"2020-05-24","2020-05-24","2020-05-24"};
    String alamat[] = {"Bangil","Bangil","Sidoarjo"};
    String status[] = {"success", "success", "failed"};
    int icons[] ={R.drawable.icon_main,R.drawable.icon_error};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        absen_list = findViewById(R.id.list_absen);
        Tjarak = findViewById(R.id.jarak);
        CustomAdapter customAdapter = new CustomAdapter(this, jamlist, tanggal, alamat, status);
        absen_list.setAdapter(customAdapter);


    btn_absen = findViewById(R.id.btn_absen);

    btn_absen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Double lat, lng;
                   lat = mLastLocation.getLatitude();
                   lng = mLastLocation.getLongitude();
            final String strlat, strlng;
                   strlat = curLat.toString();
                   strlng = curLong.toString();
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            kirimAbsen(strlat,strlng);
                            //Toast.makeText(AbsenActivity.this,"Lat = "+ strlat+" & Long = "+ strlng,Toast.LENGTH_LONG).show();
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
        Tjarak.setText("Jarak dari Rumah = "+ strjarak +" Km");
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
    public void kirimAbsen(final String slat, final String slng){
        pDialog = new ProgressDialog(AbsenActivity.this);
        pDialog.setMessage("Sending...");
        pDialog.show();
        session = new SessionManager(getApplicationContext());
        final HashMap<String,String> user = session.getUserDetails();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_absen, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean stat = jsonObject.getBoolean("success");
                    String mess = jsonObject.getString("message");
                    if (stat){

                        alert.showAlertDialog(AbsenActivity.this, "Absen", mess, true);
                    }else {
                        String cd = jsonObject.getString("code");
                        //alert.showAlertDialog(AbsenActivity.this, "Absen", mess, false);
                        if (cd.equals("token")){
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
                params.put("c_token",user.get(SessionManager.KEY_TOKEN));
                params.put("lat", slat);
                params.put("long", slng);
                return params;
            }
        };
        AppSingleton.getInstance(AbsenActivity.this).addToRequestQueue(stringRequest,TAG);
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
