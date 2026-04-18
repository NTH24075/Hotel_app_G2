package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.MainActivity;
import com.example.hotellapp.R;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.User;
import com.example.hotellapp.utils.SessionManager;
import com.example.hotellapp.utils.ValidationUtils;

public class GuestMainActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvPhone, tvCitizenId, tvAddress;

    private Button btnUpdateProfile, btnChangePassword, btnForgotPassword,
            btnBookingHistory, btnLogout, btnBackHome;

    private SessionManager sessionManager;
    private UserDAO userDAO;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main);

        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDAO();

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
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnBookingHistory = findViewById(R.id.btnBookingHistory);
        btnLogout = findViewById(R.id.btnLogout);
        btnBackHome = findViewById(R.id.btnBackHome);
    }

    private void initActions() {
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnUpdateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMainActivity.this, GuestUpdateInfor.class);
            startActivity(intent);
        });
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(GuestMainActivity.this, GuestChangPassword.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(GuestMainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng quên mật khẩu chưa làm", Toast.LENGTH_SHORT).show()
        );

        btnBookingHistory.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng lịch sử booking chưa làm", Toast.LENGTH_SHORT).show()
        );

    }

    private void loadCurrentUserInfo() {
        int userId = sessionManager.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            goToMainAndClear();
            return;
        }

        currentUser = userDAO.getUserById(userId);

        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            goToMainAndClear();
            return;
        }

        bindUserInfoToView(currentUser);
    }

    private void bindUserInfoToView(User user) {
        tvFullName.setText(valueOrDefault(user.fullName));
        tvEmail.setText(valueOrDefault(user.email));
        tvPhone.setText(valueOrDefault(user.phone));
        tvCitizenId.setText(valueOrDefault(user.citizenId));
        tvAddress.setText(valueOrDefault(user.address));
    }

    private void goToMainAndClear() {
        sessionManager.clearSession();
        Intent intent = new Intent(GuestMainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String valueOrDefault(String value) {
        return TextUtils.isEmpty(value) ? "Chưa cập nhật" : value;
    }
}