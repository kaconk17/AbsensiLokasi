package com.example.absensilokasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<String> {

  private final Context context;
  private final String[] jam;
  private final String[] tgl;
  private final String[] alamat;
 private final String[] status;

    public CustomAdapter(Context context, String[] values, String[] tanggal, String[] lokasi, String[] status) {
        super(context, R.layout.list_layout, values);
        this.context = context;
        this.jam = values;
        this.tgl = tanggal;
        this.alamat = lokasi;
       this.status = status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_layout, parent, false);
        TextView tvjam = (TextView) rowView.findViewById(R.id.txt_jam);

        TextView _tanggal =(TextView) rowView.findViewById(R.id.firstLine);
        TextView _alamat =(TextView) rowView.findViewById(R.id.secondLine);
        TextView _status =(TextView) rowView.findViewById(R.id.txt_status);


        _tanggal.setText(tgl[position]);
        _alamat.setText(alamat[position]);
        _status.setText(status[position]);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        tvjam.setText(jam[position]);
        // Change the icon for Windows and iPhone
        String s = status[position];
        if (s.equals("failed")) {
            imageView.setImageResource(R.drawable.icon_error);
        } else {
            imageView.setImageResource(R.drawable.icon_main);
        }

        return rowView;
    }
}
