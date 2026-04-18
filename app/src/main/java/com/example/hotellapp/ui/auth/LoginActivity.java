package com.example.hotellapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.MainActivity;
import com.example.hotellapp.R;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.User;
import com.example.hotellapp.ui.admin.AdminMainActivity;
import com.example.hotellapp.ui.guest.GuestMainActivity;
import com.example.hotellapp.utils.SessionManager;
import com.example.hotellapp.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLoginSubmit;
    private TextView tvGoRegister, tvForgotPassword;

    private UserDAO userDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppDatabase db = AppDatabase.getInstance(this);
        userDAO = db.userDAO();
        sessionManager = new SessionManager(this);

        initViews();
        initActions();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    private void initActions() {
        btnLoginSubmit.setOnClickListener(v -> handleLogin());

        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng quên mật khẩu chưa làm", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!ValidationUtils.isValidEmail(email)) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        User user = userDAO.loginActiveUser(email, password);

        if (user == null) {
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.saveLoginSession(user.userId, user.roleId, user.fullName, user.email);
        routeByRole(user.roleId);
    }

    private void routeByRole(int roleId) {
        Intent intent;

        if (roleId == 1) {
            intent = new Intent(LoginActivity.this, AdminMainActivity.class);
        } else if (roleId == 3) {
            intent = new Intent(LoginActivity.this, GuestMainActivity.class);
        } else {
            Toast.makeText(this, "Role hiện tại chưa hỗ trợ màn hình riêng", Toast.LENGTH_SHORT).show();
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        startActivity(intent);
        finishAffinity();
    }
}
