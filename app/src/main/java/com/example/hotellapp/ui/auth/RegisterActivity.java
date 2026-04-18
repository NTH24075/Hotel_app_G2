package com.example.hotellapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.RoleDAO;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.Role;
import com.example.hotellapp.models.User;
import com.example.hotellapp.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone, edtPassword;
    private Button btnRegisterSubmit;
    private TextView tvGoLogin;

    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AppDatabase db = AppDatabase.getInstance(this);
        userDAO = db.userDAO();
        roleDAO = db.roleDAO();

        initViews();
        initActions();
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        tvGoLogin = findViewById(R.id.tvGoLogin);
    }

    private void initActions() {
        btnRegisterSubmit.setOnClickListener(v -> handleRegister());

        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            edtFullName.setError("Vui lòng nhập họ tên");
            edtFullName.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            edtPhone.setError("Số điện thoại phải đủ 10 chữ số");
            edtPhone.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            edtPassword.setError("Mật khẩu phải từ 8 ký tự");
            edtPassword.requestFocus();
            return;
        }

        User existedUser = userDAO.getUserByEmail(email);
        if (existedUser != null) {
            edtEmail.setError("Email đã tồn tại");
            edtEmail.requestFocus();
            return;
        }

        Role guestRole = roleDAO.getRoleByName("Guest");
        if (guestRole == null) {
            Toast.makeText(this, "Không tìm thấy role Guest", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.roleId = guestRole.roleId;
        user.fullName = fullName;
        user.email = email;
        user.phone = phone;
        user.passwordHash = password;
        user.status = "Active";

        try {
            userDAO.registerUser(user);
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
