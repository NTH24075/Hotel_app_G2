package com.example.hotellapp.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.MainActivity;
import com.example.hotellapp.R;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.database.AppDatabase;
import com.example.hotellapp.models.RevenueStats;
import com.example.hotellapp.utils.SessionManager;

public class AdminMainActivity extends AppCompatActivity {
    private TextView tvRevenueToday, tvRevenueMonth, tvRevenueTotal;
    private BookingDAO bookingDAO;
    private TextView tvAdminName, tvAdminEmail;
    private TextView tvTotalUsers, tvTotalAdmins, tvTotalGuests, tvTotalReceptionists;

    private Button btnManageUsers;
    private Button btnSearchUser;

    private Button btnCreateReceps;

    private Button btnLogout;

    private SessionManager sessionManager;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        sessionManager = new SessionManager(this);
        userDAO = AppDatabase.getInstance(this).userDAO();

        if (!sessionManager.isLoggedIn() || sessionManager.getRoleId() != 1) {
            Toast.makeText(this, "Bạn không có quyền truy cập màn hình Admin", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initViews();
        loadRevenueStats();
        bindAdminInfo();
        loadStats();
        initActions();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
        loadRevenueStats();
    }
    private void initViews() {
        tvAdminName = findViewById(R.id.tvAdminName);
        tvAdminEmail = findViewById(R.id.tvAdminEmail);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalAdmins = findViewById(R.id.tvTotalAdmins);
        tvTotalGuests = findViewById(R.id.tvTotalGuests);
        tvTotalReceptionists = findViewById(R.id.tvTotalReceptionists);
        btnCreateReceps = findViewById(R.id.btnCreateReceps);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnSearchUser = findViewById(R.id.btnSearchUser);

        tvRevenueToday = findViewById(R.id.tvRevenueToday);
        tvRevenueMonth = findViewById(R.id.tvRevenueMonth);
        tvRevenueTotal = findViewById(R.id.tvRevenueTotal);

        bookingDAO = new BookingDAO(this);

//        btnHotelInfo = findViewById(R.id.btnHotelInfo);
//        btnUpdateHotel = findViewById(R.id.btnUpdateHotel);

        btnLogout = findViewById(R.id.btnLogout);

    }

    private void bindAdminInfo() {
        tvAdminName.setText(sessionManager.getFullName());
        tvAdminEmail.setText(sessionManager.getEmail());
    }

    private void loadStats() {
        int totalUsers = userDAO.countAllUsers();
        int totalAdmins = userDAO.countUsersByRole(1);
        int totalReceptionists = userDAO.countUsersByRole(2);
        int totalGuests = userDAO.countUsersByRole(3);

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvTotalAdmins.setText(String.valueOf(totalAdmins));
        tvTotalReceptionists.setText(String.valueOf(totalReceptionists));
        tvTotalGuests.setText(String.valueOf(totalGuests));
    }

    private void initActions() {
        setOnClickListenerV2(btnManageUsers, UserListActivity.class);
        setOnClickListenerV2(btnSearchUser, UserSearchActivity.class);
        setOnClickListenerV2(btnCreateReceps, CreateReceptionAccountActivity.class);
//        setOnClickListenerV2(btnHotelInfo, HotelInfoActivity.class);
//        setOnClickListenerV2(btnUpdateHotel, UpdateHotelActivity.class);
//        setOnClickListenerV2(btnRoomManagement, RoomManagerActivity.class);

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void setOnClickListenerV2(View view, Class<?> destinationClass) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, destinationClass);
            startActivity(intent);
        });
    }

    private void loadRevenueStats() {
        RevenueStats stats = bookingDAO.getRevenueStats();

        tvRevenueToday.setText(formatMoney(stats.getTodayRevenue()));
        tvRevenueMonth.setText(formatMoney(stats.getMonthRevenue()));
        tvRevenueTotal.setText(formatMoney(stats.getTotalRevenue()));
    }

    private String formatMoney(double amount) {
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
        return formatter.format(amount) + " đ";
    }
}
