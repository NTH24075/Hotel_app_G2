package com.example.hotellapp.ui.guest;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingServicesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int bookingId = getIntent().getIntExtra("booking_id", -1);

        TextView tv = new TextView(this);
        tv.setPadding(40, 80, 40, 40);
        tv.setTextSize(18f);
        tv.setText("Màn chọn dịch vụ\nbooking_id = " + bookingId);

        setContentView(tv);
    }
}