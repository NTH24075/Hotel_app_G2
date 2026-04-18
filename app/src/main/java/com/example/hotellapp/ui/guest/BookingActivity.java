package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.BookingDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private TextView tvBookingCode, tvUserName, tvPhone, tvEmail;
    private TextView tvRoomType, tvCheckIn, tvCheckOut, tvGuests, tvRooms, tvNights;
    private TextView tvRoomTotal, tvServiceTotal, tvTotalAmount;
    private TextView tvBookingStatus, tvPaymentStatus, tvSpecialRequest;

    private Button btnBackBooking, btnConfirmBooking;

    private int roomTypeId;
    private String roomName;
    private String priceText;
    private String totalPriceText;
    private String capacityText;
    private String bedText;
    private String areaText;
    private String availabilityText;

    private String checkInDate;
    private String checkOutDate;
    private int guestCount;
    private int numberOfRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_bookingdetail);

        initViews();
        readIntentData();

        if (roomTypeId == -1) {
            Toast.makeText(this, "Không có thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPreviewData();
        bindActions();
    }

    private void initViews() {
        tvBookingCode = findViewById(R.id.tvBookingCode);
        tvUserName = findViewById(R.id.tvUserName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        tvRoomType = findViewById(R.id.tvRoomType);
        tvCheckIn = findViewById(R.id.tvCheckIn);
        tvCheckOut = findViewById(R.id.tvCheckOut);
        tvGuests = findViewById(R.id.tvGuests);
        tvRooms = findViewById(R.id.tvRooms);
        tvNights = findViewById(R.id.tvNights);
        tvRoomTotal = findViewById(R.id.tvRoomTotal);
        tvServiceTotal = findViewById(R.id.tvServiceTotal);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvBookingStatus = findViewById(R.id.tvBookingStatus);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvSpecialRequest = findViewById(R.id.tvSpecialRequest);

        btnBackBooking = findViewById(R.id.btnBackBooking);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
    }

    private void readIntentData() {
        roomTypeId = getIntent().getIntExtra("ROOM_TYPE_ID", -1);
        roomName = getIntent().getStringExtra("ROOM_NAME");
        priceText = getIntent().getStringExtra("PRICE_TEXT");
        totalPriceText = getIntent().getStringExtra("TOTAL_PRICE_TEXT");
        capacityText = getIntent().getStringExtra("CAPACITY_TEXT");
        bedText = getIntent().getStringExtra("BED_TEXT");
        areaText = getIntent().getStringExtra("AREA_TEXT");
        availabilityText = getIntent().getStringExtra("AVAILABILITY_TEXT");

        checkInDate = getIntent().getStringExtra("CHECK_IN_DATE");
        checkOutDate = getIntent().getStringExtra("CHECK_OUT_DATE");
        guestCount = getIntent().getIntExtra("GUEST_COUNT", 2);
        numberOfRooms = getIntent().getIntExtra("NUMBER_OF_ROOMS", 1);
    }

    private void loadPreviewData() {
        tvBookingCode.setText("CHƯA TẠO BOOKING");
        tvBookingStatus.setText("Chưa xác nhận");
        tvPaymentStatus.setText("Unpaid");

        tvUserName.setText("Khách hàng");
        tvPhone.setText("Chưa có số điện thoại");
        tvEmail.setText("Chưa có email");

        tvRoomType.setText(safe(roomName, "Chưa có thông tin phòng"));
        tvCheckIn.setText("Check-in: " + safe(checkInDate, getTodayPlusDays(0)));
        tvCheckOut.setText("Check-out: " + safe(checkOutDate, getTodayPlusDays(2)));
        tvGuests.setText("Sức chứa: " + safe(capacityText, guestCount + " người"));
        tvRooms.setText("Số phòng: " + numberOfRooms);
        tvNights.setText("Số đêm: " + calculateNightsText(checkInDate, checkOutDate));

        tvRoomTotal.setText("Tiền phòng: " + safe(priceText, "0 VNĐ"));
        tvServiceTotal.setText("Tiền dịch vụ: 0 VNĐ");
        tvTotalAmount.setText(safe(totalPriceText, "0 VNĐ"));

        StringBuilder special = new StringBuilder();
        if (bedText != null && !bedText.trim().isEmpty()) {
            special.append("Giường: ").append(bedText);
        }
        if (areaText != null && !areaText.trim().isEmpty()) {
            if (special.length() > 0) special.append("\n");
            special.append("Diện tích: ").append(areaText);
        }
        if (availabilityText != null && !availabilityText.trim().isEmpty()) {
            if (special.length() > 0) special.append("\n");
            special.append(availabilityText);
        }

        tvSpecialRequest.setText(special.toString());
    }

    private void bindActions() {
        btnBackBooking.setOnClickListener(v -> finish());

        btnConfirmBooking.setOnClickListener(v -> {
            BookingDAO bookingDAO = new BookingDAO(this);

            int bookingId = bookingDAO.createBooking(
                    1,
                    roomTypeId,
                    safe(checkInDate, getTodayPlusDays(0)),
                    safe(checkOutDate, getTodayPlusDays(2)),
                    guestCount,
                    numberOfRooms,
                    tvSpecialRequest.getText().toString().trim()
            );

            if (bookingId <= 0) {
                Toast.makeText(this, "Tạo booking thất bại", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Đã xác nhận booking", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(BookingActivity.this, BookingServicesActivity.class);
            intent.putExtra("booking_id", bookingId);
            startActivity(intent);
            finish();
        });
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String calculateNightsText(String checkIn, String checkOut) {
        try {
            String inValue = safe(checkIn, getTodayPlusDays(0));
            String outValue = safe(checkOut, getTodayPlusDays(2));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date inDate = sdf.parse(inValue);
            Date outDate = sdf.parse(outValue);

            if (inDate == null || outDate == null) return "2";

            long diff = outDate.getTime() - inDate.getTime();
            int nights = (int) (diff / (1000L * 60 * 60 * 24));
            return String.valueOf(Math.max(nights, 1));
        } catch (Exception e) {
            return "2";
        }
    }

    private String getTodayPlusDays(int days) {
        long millis = System.currentTimeMillis() + days * 24L * 60 * 60 * 1000;
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(millis));
    }
}