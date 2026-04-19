package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.MainActivity;
import com.example.hotellapp.R;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.User;
import com.example.hotellapp.utils.SessionManager;

public class GuestProfileActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhone, tvCitizenId, tvAddress;
    private Button btnUpdateProfile, btnChangePassword,
            btnBookingHistory, btnLogout, btnBackHome;

    private SessionManager sessionManager;
    private UserDAO userDAO;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_profile);

        sessionManager = new SessionManager(this);
        try {
            userDAO = AppDatabase.getInstance(this).userDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initViews();
        initActions();
        loadCurrentUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentUserInfo();
    }

    private void initViews() {
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvCitizenId = findViewById(R.id.tvCCCD);
        tvAddress = findViewById(R.id.tvAddress);

        btnUpdateProfile = findViewById(R.id.btnUpdateInfo);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBookingHistory = findViewById(R.id.btnBookingHistory);
        btnLogout = findViewById(R.id.btnLogout);
        btnBackHome = findViewById(R.id.btnBackHome);
    }

    private void initActions() {
        if (btnBackHome != null) {
            btnBackHome.setOnClickListener(v -> finish());
        }
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                startActivity(new Intent(this, GuestChangPassword.class));
            });
        }
        if (btnUpdateProfile != null) {
            btnUpdateProfile.setOnClickListener(v -> {
                 startActivity(new Intent(this, GuestUpdateInfor.class));
            });
        }

        if (btnBookingHistory != null) {
            btnBookingHistory.setOnClickListener(v -> {
                int userId = sessionManager.getUserId();
                if (userId <= 0) {
                    Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(GuestProfileActivity.this, HistoryBookingActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                sessionManager.clearSession();
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void loadCurrentUserInfo() {
        if (sessionManager == null || userDAO == null) {
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            return;
        }

        currentUser = userDAO.getUserById(userId);
        if (currentUser != null) {
            bindUserInfoToView(currentUser);
        }
    }

    private void bindUserInfoToView(User user) {
        if (tvFullName != null) tvFullName.setText(valueOrDefault(user.fullName));
        if (tvEmail != null) tvEmail.setText(valueOrDefault(user.email));
        if (tvPhone != null) tvPhone.setText(valueOrDefault(user.phone));
        if (tvCitizenId != null) tvCitizenId.setText(valueOrDefault(user.citizenId));
        if (tvAddress != null) tvAddress.setText(valueOrDefault(user.address));
    }

    private String valueOrDefault(String value) {
        return TextUtils.isEmpty(value) ? "Chưa cập nhật" : value;
    }
}