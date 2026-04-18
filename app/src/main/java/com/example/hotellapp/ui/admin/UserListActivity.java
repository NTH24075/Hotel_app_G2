package com.example.hotellapp.ui.admin;

import android.os.Bundle;
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

public class UserListActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdminAdapter adapter;

    private SessionManager sessionManager;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDAO();

        if (!sessionManager.isLoggedIn() || sessionManager.getRoleId() != 1) {
            Toast.makeText(this, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvUsers = findViewById(R.id.rvUsers);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdminAdapter(this::toggleUserStatus);
        rvUsers.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllGuestsExceptAdmin();

        adapter.setData(users);
    }

    private void toggleUserStatus(User user) {
        if (user == null) return;

        String currentStatus = user.status == null ? "" : user.status;
        String newStatus = currentStatus.equalsIgnoreCase("Active") ? "Inactive" : "Active";

        int updated = userDAO.updateUserStatus(user.userId, newStatus);
        if (updated > 0) {
            Toast.makeText(this, "Đã cập nhật trạng thái user", Toast.LENGTH_SHORT).show();
            loadUsers();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}