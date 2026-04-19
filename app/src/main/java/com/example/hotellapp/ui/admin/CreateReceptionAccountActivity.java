package com.example.hotellapp.ui.admin;

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

public class CreateReceptionAccountActivity extends AppCompatActivity {

    private EditText edtReceptionName, edtReceptionEmail, edtReceptionPassword, edtReceptionPhone;
    private Button btnCreateReceptionAccount, btnCancelCreateReception;

    private SessionManager sessionManager;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reception_account);

        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDAO();

        if (!sessionManager.isLoggedIn() || sessionManager.getRoleId() != 1) {
            Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initActions();
    }

    private void initViews() {
        edtReceptionName = findViewById(R.id.edtReceptionName);
        edtReceptionEmail = findViewById(R.id.edtReceptionEmail);
        edtReceptionPassword = findViewById(R.id.edtReceptionPassword);
        edtReceptionPhone = findViewById(R.id.edtReceptionPhone);

        btnCreateReceptionAccount = findViewById(R.id.btnCreateReceptionAccount);
        btnCancelCreateReception = findViewById(R.id.btnCancelCreateReception);
    }

    private void initActions() {
        btnCreateReceptionAccount.setOnClickListener(v -> handleCreateReceptionAccount());
        btnCancelCreateReception.setOnClickListener(v -> finish());
    }

    private void handleCreateReceptionAccount() {
        String email = edtReceptionEmail.getText().toString().trim();
        String password = edtReceptionPassword.getText().toString().trim();
        String phone = edtReceptionPhone.getText().toString().trim();

        if (!ValidationUtils.isValidEmail(email)) {
            edtReceptionEmail.setError("Email không hợp lệ");
            edtReceptionEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            edtReceptionPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            edtReceptionPassword.requestFocus();
            return;
        }

        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null) {
            edtReceptionEmail.setError("Email đã tồn tại");
            edtReceptionEmail.requestFocus();
            return;
        }

        User user = new User();
        user.roleId = 2;
        user.fullName = "Receptionist";
        user.email = email;
        user.passwordHash = password;
        user.phone = phone;
        user.status = "Active";
        user.citizenId = null;
        user.address = null;

        long result = userDAO.registerUser(user);

        if (result > 0) {
            Toast.makeText(this, "Tạo tài khoản receptionist thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        edtReceptionName.setText("");
        edtReceptionEmail.setText("");
        edtReceptionPassword.setText("");
        edtReceptionPhone.setText("");
    }
}