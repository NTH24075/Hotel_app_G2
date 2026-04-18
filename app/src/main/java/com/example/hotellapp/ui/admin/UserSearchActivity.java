package com.example.hotellapp.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.User;
import com.example.hotellapp.utils.SessionManager;

import java.util.List;

public class UserSearchActivity extends AppCompatActivity {

    private EditText edtKeyword;
    private Button btnSearch;
    private RecyclerView rvSearchUsers;

    private SessionManager sessionManager;
    private UserDAO userDAO;
    private UserAdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDAO();

        if (!sessionManager.isLoggedIn() || sessionManager.getRoleId() != 1) {
            Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initRecycler();
        initActions();
    }

    private void initViews() {
        edtKeyword = findViewById(R.id.edtKeyword);
        btnSearch = findViewById(R.id.btnSearch);
        rvSearchUsers = findViewById(R.id.rvSearchUsers);
    }

    private void initRecycler() {
        rvSearchUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdminAdapter(this::toggleUserStatus);
        rvSearchUsers.setAdapter(adapter);
    }

    private void initActions() {
        btnSearch.setOnClickListener(v -> handleSearch());
    }

    private void handleSearch() {
        String keyword = edtKeyword.getText().toString().trim();

        if (TextUtils.isEmpty(keyword)) {
            edtKeyword.setError("Nhập từ khóa để tìm user");
            edtKeyword.requestFocus();
            return;
        }

        List<User> users = userDAO.searchUsers(keyword);
        adapter.setData(users);

        if (users == null || users.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy user phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleUserStatus(User user) {
        if (user == null) return;

        String currentStatus = user.status == null ? "" : user.status;
        String newStatus = currentStatus.equalsIgnoreCase("Active") ? "Inactive" : "Active";

        int updated = userDAO.updateUserStatus(user.userId, newStatus);
        if (updated > 0) {
            Toast.makeText(this, "Đã cập nhật trạng thái user", Toast.LENGTH_SHORT).show();
            handleSearch();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}