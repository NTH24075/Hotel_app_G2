package com.example.hotellapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hotellapp.R;
import com.example.hotellapp.models.Booking;

import java.util.ArrayList;

public class BookingAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Booking> data;
    private LayoutInflater inflater;

    public BookingAdapter(Activity activity, ArrayList<Booking> data) {
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
            convertView = inflater.inflate(R.layout.item_booking, parent, false);
        }

        TextView txtBookingCode = convertView.findViewById(R.id.txtBookingCode);
        TextView txtGuestName = convertView.findViewById(R.id.txtGuestName);
        TextView txtRoomType = convertView.findViewById(R.id.txtRoomType);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtBookingStatus = convertView.findViewById(R.id.txtBookingStatus);
        TextView txtPaymentStatus = convertView.findViewById(R.id.txtPaymentStatus);

        Booking booking = data.get(position);

        txtBookingCode.setText(booking.getBookingCode());
        txtGuestName.setText(booking.getGuestName());
        txtRoomType.setText("Loại phòng: " + booking.getRoomTypeName());
        txtDate.setText("Ngày ở: " + booking.getCheckInDate() + "  →  " + booking.getCheckOutDate());
        txtBookingStatus.setText(booking.getBookingStatus());
        txtPaymentStatus.setText(booking.getPaymentStatus());

        if ("Pending".equals(booking.getBookingStatus())) {
            txtBookingStatus.setBackgroundResource(R.drawable.bg_status_orange);
            txtBookingStatus.setTextColor(0xFFB45309);
        } else if ("Confirmed".equals(booking.getBookingStatus())) {
            txtBookingStatus.setBackgroundResource(R.drawable.bg_status_blue);
            txtBookingStatus.setTextColor(0xFF1D4ED8);
        } else if ("CheckedIn".equals(booking.getBookingStatus())) {
            txtBookingStatus.setBackgroundResource(R.drawable.bg_status_green);
            txtBookingStatus.setTextColor(0xFF15803D);
        } else {
            txtBookingStatus.setBackgroundResource(R.drawable.bg_status_gray);
            txtBookingStatus.setTextColor(0xFF475569);
        }

        if ("Paid".equals(booking.getPaymentStatus())) {
            txtPaymentStatus.setBackgroundResource(R.drawable.bg_status_green);
            txtPaymentStatus.setTextColor(0xFF15803D);
        } else {
            txtPaymentStatus.setBackgroundResource(R.drawable.bg_status_orange);
            txtPaymentStatus.setTextColor(0xFFB45309);
        }

        return convertView;
    }
}