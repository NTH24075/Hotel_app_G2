package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.User;
import com.example.hotellapp.utils.SessionManager;
import com.example.hotellapp.utils.ValidationUtils;

public class GuestUpdateInfor extends AppCompatActivity {

    private EditText edtUpdateFullName, edtUpdateEmail, edtUpdatePhone, edtUpdateCitizenId, edtUpdateAddress;
    private Button btnSubmitUpdateInfo, btnCancelUpdateInfo;

    private SessionManager sessionManager;
    private UserDAO userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_self_infor_panel_);

        sessionManager = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDAO();

        initViews();
        loadCurrentUser();
        initActions();
    }

    private void initViews() {
        edtUpdateFullName = findViewById(R.id.edtUpdateFullName);
        edtUpdateEmail = findViewById(R.id.edtUpdateEmail);
        edtUpdatePhone = findViewById(R.id.edtUpdatePhone);
        edtUpdateCitizenId = findViewById(R.id.edtUpdateCitizenId);
        edtUpdateAddress = findViewById(R.id.edtUpdateAddress);

        btnSubmitUpdateInfo = findViewById(R.id.btnSubmitUpdateInfo);
        btnCancelUpdateInfo = findViewById(R.id.btnCancelUpdateInfo);
    }

    private void loadCurrentUser() {
        int userId = sessionManager.getUserId();

        if (userId == -1) {
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            backToGuestMain();
            return;
        }

        currentUser = userDao.getUserById(userId);

        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            backToGuestMain();
            return;
        }

        bindUserData(currentUser);
    }

    private void bindUserData(User user) {
        edtUpdateFullName.setText(nullToEmpty(user.fullName));
        edtUpdateEmail.setText(nullToEmpty(user.email));
        edtUpdatePhone.setText(nullToEmpty(user.phone));
        edtUpdateCitizenId.setText(nullToEmpty(user.citizenId));
        edtUpdateAddress.setText(nullToEmpty(user.address));

        // DAO hiện chưa hỗ trợ update email -> để chỉ đọc
        edtUpdateEmail.setEnabled(false);
        edtUpdateEmail.setFocusable(false);
        edtUpdateEmail.setClickable(false);
    }

    private void initActions() {
        btnSubmitUpdateInfo.setOnClickListener(v -> handleUpdateInfo());
        btnCancelUpdateInfo.setOnClickListener(v -> finish());
    }

    private void handleUpdateInfo() {
        if (currentUser == null) return;

        String fullName = edtUpdateFullName.getText().toString().trim();
        String phone = edtUpdatePhone.getText().toString().trim();
        String citizenId = edtUpdateCitizenId.getText().toString().trim();
        String address = edtUpdateAddress.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            edtUpdateFullName.setError("Vui lòng nhập họ và tên");
            edtUpdateFullName.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(phone) && !ValidationUtils.isValidPhone(phone)) {
            edtUpdatePhone.setError("Số điện thoại phải gồm 10 chữ số");
            edtUpdatePhone.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(citizenId) && !citizenId.matches("\\d{12}")) {
            edtUpdateCitizenId.setError("CCCD phải gồm đúng 12 chữ số");
            edtUpdateCitizenId.requestFocus();
            return;
        }

        int updatedRows = userDao.updateGuestProfile(
                currentUser.userId,
                fullName,
                phone,
                citizenId,
                address
        );

        if (updatedRows > 0) {
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Không có thay đổi hoặc cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void backToGuestMain() {
        Intent intent = new Intent(this, GuestMainActivity.class);
        startActivity(intent);
        finish();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}