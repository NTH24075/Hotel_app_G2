package com.example.hotellapp.ui.receptionist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.MainActivity;
import com.example.hotellapp.R;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.ui.auth.LoginActivity;
import com.example.hotellapp.utils.SessionManager;

public class ReceptionistMainActivity extends AppCompatActivity {

    private LinearLayout cardBooking;
    private LinearLayout cardRoomStatus;
    private Button btnLogout;
    private SessionManager sessionManager;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_main);
        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDAO();

        if (!sessionManager.isLoggedIn() || sessionManager.getRoleId() != 2) {
            Toast.makeText(this, "Bạn không có quyền truy cập màn hình nhân viên", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ReceptionistMainActivity.class));
            finish();
            return;
        }
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
            Intent intent = new Intent(ReceptionistMainActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}