package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.MainActivity;
import com.example.hotellapp.R;

public class GuestMainActivity extends AppCompatActivity {
    private Button btnBackHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main);

        btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMainActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}