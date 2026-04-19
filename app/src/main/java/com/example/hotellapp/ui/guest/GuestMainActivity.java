package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
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

    private TextView tvSelectedRoomType, tvCheckInDay, tvCheckInYear, tvCheckOutDay, tvCheckOutYear, tvAdultCount, tvChildCount;
    private LinearLayout llRoomTypeDropdown, llCalendarDropdown;
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
        updateGuestViews();
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

        checkInCalendar.set(Calendar.HOUR_OF_DAY, 0);
        checkInCalendar.set(Calendar.MINUTE, 0);
        checkInCalendar.set(Calendar.SECOND, 0);
        checkInCalendar.set(Calendar.MILLISECOND, 0);

        checkOutCalendar = (Calendar) checkInCalendar.clone();
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
            option.setText(roomType.getTypeName() + " (" +
                    String.format(Locale.getDefault(), "%,.0f", roomType.getPricePerNight()) + " đ)");
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
                    Toast.makeText(this, "Ngày trả phòng không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkOutCalendar = selected;
                llCalendarDropdown.setVisibility(View.GONE);
            }

            updateDateViews();
        });

        findViewById(R.id.btn_close_calendar).setOnClickListener(v ->
                llCalendarDropdown.setVisibility(View.GONE));

        findViewById(R.id.btn_check_availability).setOnClickListener(v -> performSearch());

        findViewById(R.id.tv_view_all_rooms).setOnClickListener(v -> openSearchResults(-1));
    }

    private void performSearch() {
        if (selectedRoomType == null) {
            Toast.makeText(this, "Vui lòng chọn loại phòng", Toast.LENGTH_SHORT).show();
            return;
        }
        openSearchResults(selectedRoomType.getId());
    }

    private void openSearchResults(int roomTypeId) {
        Intent intent = new Intent(this, SearchResultsActivity.class);

        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        long diff = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        long nights = diff / (24L * 60 * 60 * 1000);
        nights = Math.max(nights, 1);

        String dates = displayFormat.format(checkInCalendar.getTime()) + " → "
                + displayFormat.format(checkOutCalendar.getTime()) + " · " + nights + " đêm";

        String guests = adultCount + " người" + (childCount > 0 ? ", " + childCount + " trẻ em" : "");

        String selectedCheckInDate = apiFormat.format(checkInCalendar.getTime());
        String selectedCheckOutDate = apiFormat.format(checkOutCalendar.getTime());

        intent.putExtra("SEARCH_DATES", dates);
        intent.putExtra("SEARCH_GUESTS", guests);
        intent.putExtra("ROOM_TYPE_ID", roomTypeId);

        // truyền ngày thật sang SearchResultsActivity
        intent.putExtra("CHECK_IN_DATE", selectedCheckInDate);
        intent.putExtra("CHECK_OUT_DATE", selectedCheckOutDate);
        intent.putExtra("GUEST_COUNT", adultCount);
        intent.putExtra("NUMBER_OF_ROOMS", 1);

        startActivity(intent);
    }

    private void openRoomDetail(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);

        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String selectedCheckInDate = apiFormat.format(checkInCalendar.getTime());
        String selectedCheckOutDate = apiFormat.format(checkOutCalendar.getTime());

        intent.putExtra("ROOM_TYPE_ID", room.getRoomTypeId());
        intent.putExtra("CHECK_IN_DATE", selectedCheckInDate);
        intent.putExtra("CHECK_OUT_DATE", selectedCheckOutDate);
        intent.putExtra("GUEST_COUNT", adultCount);
        intent.putExtra("NUMBER_OF_ROOMS", 1);

        startActivity(intent);
    }

    private void toggleCalendar(boolean selectCheckIn) {
        if (llCalendarDropdown.getVisibility() == View.VISIBLE && isSelectingCheckIn == selectCheckIn) {
            llCalendarDropdown.setVisibility(View.GONE);
            return;
        }

        llCalendarDropdown.setVisibility(View.VISIBLE);
        isSelectingCheckIn = selectCheckIn;
        calendarView.setDate(selectCheckIn
                ? checkInCalendar.getTimeInMillis()
                : checkOutCalendar.getTimeInMillis());

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

    private void updateGuestViews() {
        if (tvAdultCount != null) {
            tvAdultCount.setText(String.valueOf(adultCount));
        }
        if (tvChildCount != null) {
            tvChildCount.setText(String.valueOf(childCount));
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, GuestProfileActivity.class));
                return true;
            }
            return true;
        });
    }
}