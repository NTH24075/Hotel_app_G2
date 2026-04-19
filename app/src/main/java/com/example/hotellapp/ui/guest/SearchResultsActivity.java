package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotellapp.R;
import com.example.hotellapp.adapters.SearchResultAdapter;
import com.example.hotellapp.dao.RoomDAO;
import com.example.hotellapp.models.Room;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private String checkInDate;
    private String checkOutDate;
    private int guestCount;
    private int numberOfRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        String dates = getIntent().getStringExtra("SEARCH_DATES");
        String guests = getIntent().getStringExtra("SEARCH_GUESTS");
        int roomTypeId = getIntent().getIntExtra("ROOM_TYPE_ID", -1);

        // nhận ngày thật từ GuestMainActivity
        checkInDate = getIntent().getStringExtra("CHECK_IN_DATE");
        checkOutDate = getIntent().getStringExtra("CHECK_OUT_DATE");
        guestCount = getIntent().getIntExtra("GUEST_COUNT", 2);
        numberOfRooms = getIntent().getIntExtra("NUMBER_OF_ROOMS", 1);

        TextView tvDates = findViewById(R.id.tv_search_dates);
        TextView tvGuests = findViewById(R.id.tv_search_guests);
        TextView tvResultCount = findViewById(R.id.tv_result_count);
        RecyclerView rvResults = findViewById(R.id.rv_search_results);

        if (dates != null) {
            tvDates.setText(dates);
        }
        if (guests != null) {
            tvGuests.setText(guests);
        }

        RoomDAO roomDAO = new RoomDAO(this);
        List<Room> results = roomDAO.getRoomsByFilters(roomTypeId);

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(new SearchResultAdapter(this, results, this::openRoomDetail));

        if (tvResultCount != null) {
            tvResultCount.setText(getString(R.string.search_result_count, results.size()));
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        setupBottomNavigation();
    }

    private void openRoomDetail(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("ROOM_TYPE_ID", room.getRoomTypeId());

        // truyền tiếp ngày thật
        intent.putExtra("CHECK_IN_DATE", checkInDate);
        intent.putExtra("CHECK_OUT_DATE", checkOutDate);
        intent.putExtra("GUEST_COUNT", guestCount);
        intent.putExtra("NUMBER_OF_ROOMS", numberOfRooms);

        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_booking);
            bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
        }
    }

    private boolean handleBottomNavigation(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            startActivity(new Intent(this, GuestMainActivity.class));
            finish();
            return true;
        }

        if (itemId == R.id.nav_booking) {
            return true;
        }

        Toast.makeText(this, R.string.navigation_coming_soon, Toast.LENGTH_SHORT).show();
        return true;
    }
}