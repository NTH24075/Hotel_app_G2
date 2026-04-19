package com.example.hotellapp.ui.receptionist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.adapters.BookingAdapter;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.models.Booking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ReceptionistBookingActivity extends AppCompatActivity {

    private TextView txtTotalBooking;
    private TextView txtCheckedInCount;
    private EditText edtKeyword;
    private EditText edtFromDate;
    private EditText edtToDate;
    private Button btnSearch;
    private Button btnRefresh;
    private ListView lvBooking;

    private BookingDAO bookingDAO;
    private ArrayList<Booking> bookingList;
    private BookingAdapter bookingAdapter;

    private ActivityResultLauncher<Intent> detailLauncher;

    private Button btnBackBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptionist_booking);

        mapping();
        initData();
        addEvents();
    }

    private void mapping() {
        txtTotalBooking = findViewById(R.id.txtTotalBooking);
        txtCheckedInCount = findViewById(R.id.txtCheckedInCount);
        edtKeyword = findViewById(R.id.edtKeyword);
        edtFromDate = findViewById(R.id.edtFromDate);
        edtToDate = findViewById(R.id.edtToDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnRefresh = findViewById(R.id.btnRefresh);
        lvBooking = findViewById(R.id.lvBooking);
        btnBackBooking = findViewById(R.id.btnBackBooking);
    }

    private void initData() {
        bookingDAO = new BookingDAO(this);
        loadAllBookings();
        detailLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadAllBookings();
                    }
                }
        );
    }

    private void addEvents() {
        edtFromDate.setOnClickListener(v -> showDatePicker(edtFromDate));
        edtToDate.setOnClickListener(v -> showDatePicker(edtToDate));

        btnSearch.setOnClickListener(v -> handleSearch());

        btnRefresh.setOnClickListener(v -> {
            edtKeyword.setText("");
            edtFromDate.setText("");
            edtToDate.setText("");
            loadAllBookings();
            Toast.makeText(this, "Đã làm mới danh sách", Toast.LENGTH_SHORT).show();
        });

        lvBooking.setOnItemClickListener((parent, view, position, id) -> {
            Booking booking = bookingList.get(position);

            Intent intent = new Intent(ReceptionistBookingActivity.this, BookingDetailActivity.class);
            intent.putExtra("BOOKING_ID", booking.getBookingId());
            detailLauncher.launch(intent);
        });
        btnBackBooking.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadAllBookings() {
        bookingList = bookingDAO.getAllBookings();
        bookingAdapter = new BookingAdapter(this, bookingList);
        lvBooking.setAdapter(bookingAdapter);

        txtTotalBooking.setText(String.valueOf(bookingDAO.countAllBookings()));
        txtCheckedInCount.setText(String.valueOf(bookingDAO.countCheckedInBookings()));
    }

    private void handleSearch() {
        String keyword = edtKeyword.getText().toString().trim();
        String fromDate = edtFromDate.getText().toString().trim();
        String toDate = edtToDate.getText().toString().trim();

        if (keyword.isEmpty() && fromDate.isEmpty() && toDate.isEmpty()) {
            loadAllBookings();
            return;
        }

        if (!fromDate.isEmpty() && toDate.isEmpty()) {
            Toast.makeText(this, "Bạn chưa chọn đến ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fromDate.isEmpty() && !toDate.isEmpty()) {
            Toast.makeText(this, "Bạn chưa chọn từ ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fromDate.isEmpty() && !toDate.isEmpty() && fromDate.compareTo(toDate) > 0) {
            Toast.makeText(this, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!keyword.isEmpty() && !fromDate.isEmpty() && !toDate.isEmpty()) {
            bookingList = bookingDAO.searchAndFilterBookings(keyword, fromDate, toDate);
        } else if (!keyword.isEmpty()) {
            bookingList = bookingDAO.searchBookings(keyword);
        } else {
            bookingList = bookingDAO.filterBookingsByDate(fromDate, toDate);
        }

        bookingAdapter = new BookingAdapter(this, bookingList);
        lvBooking.setAdapter(bookingAdapter);

        Toast.makeText(this, "Tìm thấy " + bookingList.size() + " booking", Toast.LENGTH_SHORT).show();
    }

    private void showDatePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay);
                    targetEditText.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}