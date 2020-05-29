package com.example.absensilokasi;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AbsenModel> datalist;

    public ListAdapter(Context context, ArrayList<AbsenModel> datalist){
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_layout,parent, false);

            holder.imv = (ImageView) convertView.findViewById(R.id.icon);
            holder.tjam = (TextView) convertView.findViewById(R.id.txt_jam);
            holder.ttanggal =  (TextView) convertView.findViewById(R.id.firstLine);
            holder.talamat =  (TextView) convertView.findViewById(R.id.secondLine);
            holder.tstatus =  (TextView) convertView.findViewById(R.id.txt_status);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        String s = datalist.get(position).getStatus();

        if (s.equals("OK")){
            holder.imv.setImageResource(R.drawable.check_solid);
            holder.tstatus.setText("Success");
            holder.tstatus.setTextColor(Color.parseColor("#3BE23B"));
        }else {
            holder.imv.setImageResource(R.drawable.icon_error);
            holder.tstatus.setText("Diluar Area");
            holder.tstatus.setTextColor(Color.parseColor("#ED2C2C"));
        }
        String mlat = getAddress(datalist.get(position).getLat(),datalist.get(position).getLng());
        holder.tjam.setText(datalist.get(position).getJam());
        holder.ttanggal.setText(datalist.get(position).getTanggal());

        holder.talamat.setText(mlat);

        return convertView;
    }

    private class ViewHolder{
        protected TextView tjam, ttanggal, talamat, tstatus;
        protected ImageView imv;
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
