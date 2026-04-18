package com.example.hotellapp.ui.receptionist;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.adapters.BookingAdapter;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.models.Booking;

import java.util.ArrayList;

public class ReceptionistBookingActivity extends AppCompatActivity {

    private TextView txtTotalBooking;
    private TextView txtCheckedInCount;
    private ListView lvBooking;

    private BookingDAO bookingDAO;
    private ArrayList<Booking> bookingList;
    private BookingAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_booking);

        txtTotalBooking = findViewById(R.id.txtTotalBooking);
        txtCheckedInCount = findViewById(R.id.txtCheckedInCount);
        lvBooking = findViewById(R.id.lvBooking);

        bookingDAO = new BookingDAO(this);
        bookingList = bookingDAO.getAllBookings();

        txtTotalBooking.setText(String.valueOf(bookingList.size()));
        txtCheckedInCount.setText(String.valueOf(bookingDAO.countCheckedInBookings()));

        bookingAdapter = new BookingAdapter(this, bookingList);
        lvBooking.setAdapter(bookingAdapter);
    }
}