package com.example.hotellapp.ui.receptionist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;

public class ReceptionistMainActivity extends AppCompatActivity {

    private LinearLayout cardBooking;
    private LinearLayout cardRoomStatus;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_main);

        mapping();
        addEvents();
    }

    private void mapping() {
        cardBooking = findViewById(R.id.cardBooking);
        cardRoomStatus = findViewById(R.id.cardRoomStatus);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void addEvents() {
        cardBooking.setOnClickListener(v -> {
            Intent intent = new Intent(ReceptionistMainActivity.this, ReceptionistBookingActivity.class);
            startActivity(intent);
        });

        cardRoomStatus.setOnClickListener(v -> {
            Intent intent = new Intent(ReceptionistMainActivity.this, RoomStatusActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }
}