package com.example.hotellapp.ui.guest;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotellapp.R;
import com.example.hotellapp.adapters.RoomAdapter;
import com.example.hotellapp.dao.RoomDAO;
import com.example.hotellapp.models.Room;
import com.example.hotellapp.models.RoomType;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GuestMainActivity extends AppCompatActivity {

    private TextView tvSelectedRoomType;
    private TextView tvCheckInDay;
    private TextView tvCheckInYear;
    private TextView tvCheckOutDay;
    private TextView tvCheckOutYear;
    private TextView tvAdultCount;
    private TextView tvChildCount;
    private LinearLayout llRoomTypeDropdown;
    private LinearLayout llCalendarDropdown;
    private CalendarView calendarView;
    private RecyclerView rvRooms;

    private RoomDAO roomDAO;
    private final List<RoomType> roomTypeList = new ArrayList<>();
    private RoomType selectedRoomType;
    private Calendar checkInCalendar = Calendar.getInstance();
    private Calendar checkOutCalendar = Calendar.getInstance();

    private boolean isSelectingCheckIn = true;
    private int adultCount = 2;
    private int childCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_main);

        roomDAO = new RoomDAO(this);
        initViews();
        loadRoomTypes();
        loadFeaturedRooms();
        setupListeners();
        setupBottomNavigation();
        updateDateViews();
    }

    private void initViews() {
        tvSelectedRoomType = findViewById(R.id.tv_selected_room_type);
        tvCheckInDay = findViewById(R.id.tv_check_in_day);
        tvCheckInYear = findViewById(R.id.tv_check_in_year);
        tvCheckOutDay = findViewById(R.id.tv_check_out_day);
        tvCheckOutYear = findViewById(R.id.tv_check_out_year);
        tvAdultCount = findViewById(R.id.tv_adult_count);
        tvChildCount = findViewById(R.id.tv_child_count);

        llRoomTypeDropdown = findViewById(R.id.ll_room_type_dropdown);
        llCalendarDropdown = findViewById(R.id.ll_calendar_dropdown);
        calendarView = findViewById(R.id.cv_selector);
        rvRooms = findViewById(R.id.rooms_recycler);

        rvRooms.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        checkOutCalendar.add(Calendar.DAY_OF_YEAR, 2);
    }

    private void loadRoomTypes() {
        roomTypeList.clear();
        roomTypeList.addAll(roomDAO.getRoomTypes());
        populateRoomTypeDropdown();

        if (!roomTypeList.isEmpty()) {
            selectedRoomType = roomTypeList.get(0);
            tvSelectedRoomType.setText(selectedRoomType.getTypeName());
        }
    }

    private void loadFeaturedRooms() {
        List<Room> featuredRooms = roomDAO.getFeaturedRooms();
        rvRooms.setAdapter(new RoomAdapter(this, featuredRooms, this::openRoomDetail));
    }

    private void populateRoomTypeDropdown() {
        llRoomTypeDropdown.removeAllViews();
        for (RoomType roomType : roomTypeList) {
            TextView option = new TextView(this);
            option.setText(roomType.getTypeName() + " (" + String.format(Locale.getDefault(), "%,.0f", roomType.getPricePerNight()) + " \u0111)");
            option.setPadding(32, 24, 32, 24);
            option.setTextSize(14);
            option.setTextColor(Color.BLACK);
            option.setClickable(true);
            option.setFocusable(true);
            option.setBackgroundResource(android.R.drawable.list_selector_background);
            option.setOnClickListener(v -> {
                selectedRoomType = roomType;
                tvSelectedRoomType.setText(roomType.getTypeName());
                llRoomTypeDropdown.setVisibility(View.GONE);
                findViewById(R.id.tv_room_type_arrow).setRotation(0);
            });
            llRoomTypeDropdown.addView(option);
        }
    }

    private void setupListeners() {
        findViewById(R.id.ll_room_type_select).setOnClickListener(v -> {
            boolean visible = llRoomTypeDropdown.getVisibility() == View.VISIBLE;
            llRoomTypeDropdown.setVisibility(visible ? View.GONE : View.VISIBLE);
            findViewById(R.id.tv_room_type_arrow).setRotation(visible ? 0 : 180);
            llCalendarDropdown.setVisibility(View.GONE);
        });

        findViewById(R.id.ll_check_in_date).setOnClickListener(v -> toggleCalendar(true));
        findViewById(R.id.ll_check_out_date).setOnClickListener(v -> toggleCalendar(false));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            selected.set(Calendar.MILLISECOND, 0);

            if (isSelectingCheckIn) {
                checkInCalendar = selected;
                if (!checkOutCalendar.after(checkInCalendar)) {
                    checkOutCalendar = (Calendar) checkInCalendar.clone();
                    checkOutCalendar.add(Calendar.DAY_OF_YEAR, 1);
                }
                isSelectingCheckIn = false;
                calendarView.setDate(checkOutCalendar.getTimeInMillis());
            } else {
                if (!selected.after(checkInCalendar)) {
                    Toast.makeText(this, R.string.search_date_invalid, Toast.LENGTH_SHORT).show();
                    return;
                }
                checkOutCalendar = selected;
                llCalendarDropdown.setVisibility(View.GONE);
            }

            updateDateViews();
        });

        findViewById(R.id.btn_close_calendar).setOnClickListener(v -> llCalendarDropdown.setVisibility(View.GONE));
        findViewById(R.id.ll_adult_count).setOnClickListener(v -> showGuestInputDialog(true));
        findViewById(R.id.ll_child_count).setOnClickListener(v -> showGuestInputDialog(false));
        findViewById(R.id.btn_check_availability).setOnClickListener(v -> performSearch());
        findViewById(R.id.tv_view_all_rooms).setOnClickListener(v -> openSearchResults(-1));
    }

    private void performSearch() {
        if (selectedRoomType == null) {
            Toast.makeText(this, R.string.search_select_room_type, Toast.LENGTH_SHORT).show();
            return;
        }

        openSearchResults(selectedRoomType.getId());
    }

    private void openSearchResults(int roomTypeId) {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        long diff = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        long nights = diff / (24 * 60 * 60 * 1000);

        String dates = dayFormat.format(checkInCalendar.getTime()) + " \u2192 "
                + dayFormat.format(checkOutCalendar.getTime()) + " \u00b7 " + nights + " \u0111\u00eam";
        String guests = adultCount + " ng\u01b0\u1eddi" + (childCount > 0 ? ", " + childCount + " tr\u1ebb em" : "");

        intent.putExtra("SEARCH_DATES", dates);
        intent.putExtra("SEARCH_GUESTS", guests);
        intent.putExtra("ROOM_TYPE_ID", roomTypeId);
        startActivity(intent);
    }

    private void openRoomDetail(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("ROOM_TYPE_ID", room.getRoomTypeId());
        startActivity(intent);
    }

    private void showGuestInputDialog(boolean adult) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(adult ? R.string.search_adult_dialog : R.string.search_child_dialog);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(adult ? adultCount : childCount));
        input.setSelectAllOnFocus(true);
        builder.setView(input);

        builder.setPositiveButton(R.string.search_ok, (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (value.isEmpty()) {
                return;
            }

            int count = Integer.parseInt(value);
            if (adult) {
                adultCount = count;
                tvAdultCount.setText(adultCount + " ng\u01b0\u1eddi");
            } else {
                childCount = count;
                tvChildCount.setText(childCount + " tr\u1ebb em");
            }
        });
        builder.setNegativeButton(R.string.search_cancel, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void toggleCalendar(boolean selectCheckIn) {
        if (llCalendarDropdown.getVisibility() == View.VISIBLE && isSelectingCheckIn == selectCheckIn) {
            llCalendarDropdown.setVisibility(View.GONE);
            return;
        }

        llCalendarDropdown.setVisibility(View.VISIBLE);
        isSelectingCheckIn = selectCheckIn;
        calendarView.setDate(selectCheckIn ? checkInCalendar.getTimeInMillis() : checkOutCalendar.getTimeInMillis());
        llRoomTypeDropdown.setVisibility(View.GONE);
    }

    private void updateDateViews() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        tvCheckInDay.setText(dayFormat.format(checkInCalendar.getTime()));
        tvCheckInYear.setText(yearFormat.format(checkInCalendar.getTime()));
        tvCheckOutDay.setText(dayFormat.format(checkOutCalendar.getTime()));
        tvCheckOutYear.setText(yearFormat.format(checkOutCalendar.getTime()));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
    }

    private boolean handleBottomNavigation(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            return true;
        }
        if (itemId == R.id.nav_booking) {
            openSearchResults(-1);
            return true;
        }

        Toast.makeText(this, R.string.navigation_coming_soon, Toast.LENGTH_SHORT).show();
        return true;
    }
}
