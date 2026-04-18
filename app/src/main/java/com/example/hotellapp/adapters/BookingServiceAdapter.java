package com.example.hotellapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hotellapp.R;
import com.example.hotellapp.models.BookingServiceItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BookingServiceAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<BookingServiceItem> data;
    private LayoutInflater inflater;

    public BookingServiceAdapter(Activity activity, ArrayList<BookingServiceItem> data) {
        this.activity = activity;
        this.data = data;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_booking_service, parent, false);
        }

        TextView txtServiceIcon = convertView.findViewById(R.id.txtServiceIcon);
        TextView txtServiceName = convertView.findViewById(R.id.txtServiceName);
        TextView txtServiceUnitPrice = convertView.findViewById(R.id.txtServiceUnitPrice);
        TextView txtServiceQty = convertView.findViewById(R.id.txtServiceQty);
        TextView txtServiceTotal = convertView.findViewById(R.id.txtServiceTotal);

        BookingServiceItem item = data.get(position);
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));

        txtServiceIcon.setText(item.getServiceIcon());
        txtServiceName.setText(item.getServiceName());
        txtServiceUnitPrice.setText(format.format(item.getUnitPrice()) + " " + item.getUnitLabel());
        txtServiceQty.setText("x" + item.getQuantity());
        txtServiceTotal.setText(format.format(item.getTotalPrice()) + " VNĐ");

        return convertView;
    }
}