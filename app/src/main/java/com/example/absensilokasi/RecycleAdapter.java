package com.example.absensilokasi;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    private Context context;
    private List<AbsenModel> datalist;


    public RecycleAdapter(Context context, List<AbsenModel> datalist){
        this.context = context;
        this.datalist= datalist;

    }
    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {

        AbsenModel absenModel = datalist.get(position);
       String alamat = getAddress(absenModel.getLat(),absenModel.getLng());
        holder.ttanggal.setText(absenModel.getTanggal());
        holder.talamat.setText(alamat);
        holder.tjam.setText(absenModel.getJam());
        String s = absenModel.getStatus();
        if (s.equals("OK")){
            holder.imv.setImageResource(R.drawable.check_solid);
            holder.tstatus.setText("Success");
            holder.tstatus.setTextColor(Color.parseColor("#3BE23B"));
        }else {
            holder.imv.setImageResource(R.drawable.icon_error);
            holder.tstatus.setText("Diluar Area");
            holder.tstatus.setTextColor(Color.parseColor("#ED2C2C"));
        }


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tjam, ttanggal, talamat, tstatus;
        public ImageView imv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tjam = itemView.findViewById(R.id.txt_jam);
            ttanggal = itemView.findViewById(R.id.firstLine);
            talamat = itemView.findViewById(R.id.secondLine);
            tstatus = itemView.findViewById(R.id.txt_status);
            imv = itemView.findViewById(R.id.icon);
        }
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String alamat;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            alamat = obj.getLocality() +", "+ obj.getSubAdminArea();
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
