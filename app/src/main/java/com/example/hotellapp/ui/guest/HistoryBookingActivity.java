package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.models.Booking;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryBookingActivity extends AppCompatActivity {

    private MaterialButton btnBackHistory;
    private LinearLayout layoutHistoryList;
    private TextView tvHistoryTitle, tvEmptyHistory;

    private BookingDAO bookingDAO;
    private int userId = -1;

    private final NumberFormat currencyFormat =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_historybooking);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Không tìm thấy user_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);

        initViews();
        bindActions();
        loadBookingHistory();
    }

    private void initViews() {
        btnBackHistory = findViewById(R.id.btnBackHistory);
        layoutHistoryList = findViewById(R.id.layoutHistoryList);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
    }

    private void bindActions() {
        btnBackHistory.setOnClickListener(v -> finish());
    }

    private void loadBookingHistory() {
        ArrayList<Booking> bookings = bookingDAO.getPaidDoneBookingsByUser(userId);
        layoutHistoryList.removeAllViews();

        if (bookings == null || bookings.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            return;
        }

        tvEmptyHistory.setVisibility(View.GONE);

        for (Booking booking : bookings) {
            View itemView = getLayoutInflater().inflate(R.layout.item_history_booking, layoutHistoryList, false);

            TextView tvCode = itemView.findViewById(R.id.tvHistoryBookingCode);
            TextView tvRoom = itemView.findViewById(R.id.tvHistoryRoomType);
            TextView tvDate = itemView.findViewById(R.id.tvHistoryDate);
            TextView tvStatus = itemView.findViewById(R.id.tvHistoryStatus);
            TextView tvAmount = itemView.findViewById(R.id.tvHistoryAmount);
            MaterialButton btnReview = itemView.findViewById(R.id.btnReviewBooking);

            tvCode.setText("Mã booking: " + safe(booking.getBookingCode()));
            tvRoom.setText("Phòng: " + safe(booking.getRoomTypeName()));
            tvDate.setText("Từ " + safe(booking.getCheckInDate()) + " đến " + safe(booking.getCheckOutDate()));
            tvAmount.setText(formatMoney(booking.getTotalAmount()) + " VNĐ");

            String paymentStatus = safe(booking.getPaymentStatus()).trim();

            if (paymentStatus.equalsIgnoreCase("paid")) {
                tvStatus.setText("Chưa xác nhận");
                tvStatus.setTextColor(android.graphics.Color.parseColor("#DC2626"));
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FEE2E2"));
            } else if (paymentStatus.equalsIgnoreCase("done")) {
                tvStatus.setText("Đã xác nhận");
                tvStatus.setTextColor(android.graphics.Color.parseColor("#15803D"));
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#DCFCE7"));
            } else {
                tvStatus.setText(paymentStatus);
                tvStatus.setTextColor(android.graphics.Color.parseColor("#6B7280"));
                tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#F3F4F6"));
            }

            boolean isReviewed = bookingDAO.isReviewed(booking.getBookingCode());

            if (isReviewed) {
                btnReview.setText("Đã đánh giá");
                btnReview.setEnabled(false);
                btnReview.setBackgroundColor(android.graphics.Color.parseColor("#DC2626"));
            } else {
                btnReview.setText("Đánh giá");
                btnReview.setEnabled(true);

                btnReview.setOnClickListener(v -> {
                    Intent intent = new Intent(HistoryBookingActivity.this, ReviewActivity.class);
                    intent.putExtra("booking_code", booking.getBookingCode());
                    intent.putExtra("room_type_id", booking.getRoomTypeId());
                    intent.putExtra("guest_name", booking.getGuestName());
                    startActivity(intent);
                });
            }

            layoutHistoryList.addView(itemView);
        }
    }

    private String formatMoney(double value) {
        return currencyFormat.format(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}