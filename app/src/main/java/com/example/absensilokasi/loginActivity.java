package com.example.absensilokasi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class loginActivity extends AppCompatActivity {
    EditText txtemail, txtpass;
    Button btn_login;
    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String url ="https://absensilokasi.azurewebsites.net/api/login";
    private ProgressDialog pDialog;

    private List modelList = new ArrayList();

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
                final String email = txtemail.getText().toString();
                final String pass = txtpass.getText().toString();

                if (email.trim().length() > 0 && pass.trim().length() > 0){
                    pDialog = new ProgressDialog(loginActivity.this);
                    pDialog.setMessage("Loading...");
                    pDialog.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pDialog.hide();
                            try {
                                //JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject(response);
                                Boolean stat = jsonObject.getBoolean("success");
                                String mess = jsonObject.getString("message");

                                if (stat){
                                    JSONObject user = new JSONObject(jsonObject.getString("user"));
                                    String token = jsonObject.getString("token");
                                    String rad = String.valueOf(jsonObject.getDouble("radius"));
                                    //Toast.makeText(loginActivity.this,user.getString("lat"),Toast.LENGTH_LONG).show();

                                   if (user.getString("lat").equals("null") || user.getString("lat").equals("") || user.getString("long").equals("null") || user.getString("long").equals("")){
                                       //alert.showAlertDialog(loginActivity.this, "Login !", "Data lokasi belum ada!", false);
                                       session.createLoginSession(user.getString("nama"), user.getString("email"), token, user.getString("id"), null,null, rad);
                                       Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                                       startActivity(intent);
                                       finish();
                                   }else{
                                       String slat = String.valueOf(user.getDouble("lat"));
                                       String slng = String.valueOf(user.getDouble("long"));
                                       session.createLoginSession(user.getString("nama"), user.getString("email"), token, user.getString("id"), slat,slng, rad );
                                       Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                       startActivity(i);
                                       finish();
                                   }



                                }else {
                                    alert.showAlertDialog(loginActivity.this, "Login Gagal !", mess, false);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(loginActivity.this,"Error",Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    })
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params =  new HashMap<String, String>();
                            params.put("email",email);
                            params.put("pass", pass);
                            return params;
                        }
                    } ;
                    AppSingleton.getInstance(loginActivity.this).addToRequestQueue(stringRequest,TAG);
                    /*
                    if (email.equals("admin@gmail.com") && pass.equals("admin")){
                        session.createLoginSession("Admin", "admin@gmail.com");

                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        alert.showAlertDialog(loginActivity.this, "Login Gagal !", "Email / Password Salah", false);
                    }
                    */
                }else{
                    alert.showAlertDialog(loginActivity.this, "Login Gagal !", "Mohon Masukkan Email dan Password", false);
                }
            }
        });
    }
}
