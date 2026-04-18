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

public class GuestChangPassword extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    private Button btnSubmitChangePassword, btnCancelChangePassword;

    private SessionManager sessionManager;
    private UserDAO userDao;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_self_password_);

        sessionManager = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDAO();

        initViews();
        loadCurrentUser();
        initActions();
    }

    private void initViews() {
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);

        btnSubmitChangePassword = findViewById(R.id.btnSubmitChangePassword);
        btnCancelChangePassword = findViewById(R.id.btnCancelChangePassword);
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
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            backToGuestMain();
        }
    }

    private void initActions() {
        btnSubmitChangePassword.setOnClickListener(v -> handleChangePassword());
        btnCancelChangePassword.setOnClickListener(v -> finish());
    }

    private void handleChangePassword() {
        if (currentUser == null) return;

        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            edtOldPassword.setError("Vui lòng nhập mật khẩu cũ");
            edtOldPassword.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 8 ký tự");
            edtNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            edtConfirmNewPassword.setError("Mật khẩu nhập lại không khớp");
            edtConfirmNewPassword.requestFocus();
            return;
        }

        if (oldPassword.equals(newPassword)) {
            edtNewPassword.setError("Mật khẩu mới không được trùng mật khẩu cũ");
            edtNewPassword.requestFocus();
            return;
        }

        User checkUser = userDao.checkCurrentPassword(currentUser.userId, oldPassword);
        if (checkUser == null) {
            edtOldPassword.setError("Mật khẩu cũ không đúng");
            edtOldPassword.requestFocus();
            return;
        }

        int updatedRows = userDao.updatePassword(currentUser.userId, newPassword);

        if (updatedRows > 0) {
            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            clearInputs();
            finish();
        } else {
            Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        edtOldPassword.setText("");
        edtNewPassword.setText("");
        edtConfirmNewPassword.setText("");
    }

    private void backToGuestMain() {
        Intent intent = new Intent(this, GuestMainActivity.class);
        startActivity(intent);
        finish();
    }
}