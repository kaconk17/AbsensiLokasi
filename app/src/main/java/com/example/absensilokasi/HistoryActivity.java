package com.example.absensilokasi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class HistoryActivity extends AppCompatActivity implements RecycleAdapter.OnListListener {
    EditText tgl_awal, tgl_akhir;
    DatePickerDialog picker;
    ImageButton btn_cari;
    SessionManager session;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private RecyclerView mlist;
    private List<AbsenModel> absenList;
    private RecyclerView.Adapter adapter;
    private ProgressDialog pDialog;
    private String base_url, id, token;
    private static final String TAG = HistoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        tgl_awal = findViewById(R.id.tgl_1);
        tgl_akhir = findViewById(R.id.tgl_2);
        mlist = findViewById(R.id.list_absen);
        btn_cari = findViewById(R.id.btn_cari);
        base_url = getString(R.string.base_url);
        session = new SessionManager(getApplicationContext());
        final HashMap<String,String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        token = user.get(SessionManager.KEY_TOKEN);

        tgl_awal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance(TimeZone.getDefault());
                final int day = cldr.get(Calendar.DAY_OF_MONTH);
                final int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {
                        String bulan, tanggal;
                        monthOfyear +=1;
                        if(monthOfyear < 10){

                            bulan = "0" + monthOfyear;
                        }else {
                            bulan = String.valueOf(monthOfyear);
                        }
                        if(dayOfMonth < 10){

                            tanggal  = "0" + dayOfMonth ;
                        }else{
                            tanggal = String.valueOf(dayOfMonth);
                        }
                        tgl_awal.setText(year+"-"+bulan+"-"+tanggal);
                    }
                }, year, month,day);
                picker.show();
            }
        });

        tgl_akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance(TimeZone.getDefault());
                final int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfyear, int dayOfMonth) {
                        String bulan, tanggal;
                        monthOfyear +=1;
                        if(monthOfyear < 10){

                            bulan = "0" + monthOfyear;
                        }else {
                            bulan = String.valueOf(monthOfyear);
                        }
                        if(dayOfMonth < 10){

                            tanggal  = "0" + dayOfMonth ;
                        }else{
                            tanggal = String.valueOf(dayOfMonth);
                        }
                        tgl_akhir.setText(year+"-"+bulan+"-"+tanggal);
                    }
                }, year, month,day);
                picker.show();
            }
        });

        btn_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tglawal = tgl_awal.getText().toString();
                String tglakhir = tgl_akhir.getText().toString();
                getData(id,token,tglawal,tglakhir);
            }
        });
    }

    private void getData(String cId, String cToken, String Tglawal, String Tglakhir){
        pDialog = new ProgressDialog(HistoryActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        String uri = String.format(base_url+"absen/history/"+cId+"?c_token=%1$s&tgl_awal=%2$s&tgl_akhir=%3$s",cToken,Tglawal,Tglakhir);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean stat = jsonObject.getBoolean("success");
                    String mess = jsonObject.getString("message");

                    if (stat){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        absenList = new ArrayList<>();
                        for (int i =0 ;i < jsonArray.length(); i++){
                            AbsenModel absenModel = new AbsenModel();

                            JSONObject dataobj = jsonArray.getJSONObject(i);

                            absenModel.setId(dataobj.getString("id_absensi"));
                            absenModel.setJam(dataobj.getString("jam"));
                            absenModel.setTanggal(dataobj.getString("tanggal"));
                            absenModel.setStatus(dataobj.getString("status"));
                            absenModel.setLat(dataobj.getDouble("lat"));
                            absenModel.setLng(dataobj.getDouble("long"));
                            absenList.add(absenModel);
                        }
                        setupList();
                        adapter.notifyDataSetChanged();
                    }else{
                        String cod = jsonObject.getString("code");

                        if (cod.equals("token")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
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
                            Toast.makeText(HistoryActivity.this,mess,Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HistoryActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(HistoryActivity.this).addToRequestQueue(stringRequest,TAG);
    }
    private void setupList(){
        adapter = new RecycleAdapter(HistoryActivity.this,absenList, this);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mlist.getContext(), linearLayoutManager.getOrientation());
        mlist.setHasFixedSize(true);
        mlist.setLayoutManager(linearLayoutManager);
        mlist.addItemDecoration(dividerItemDecoration);
        mlist.setAdapter(adapter);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onListClick(int position) {
        //String t= String.valueOf(position);
        AbsenModel absenModel= absenList.get(position);
        Toast.makeText(HistoryActivity.this,absenModel.getId(),Toast.LENGTH_LONG).show();
    }
}
