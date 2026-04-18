package com.example.hotellapp.ui.guest;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.BookingDAO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.RatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextInputEditText edtReviewContent;
    private MaterialButton btnSubmitReview, btnBackReview;

    private BookingDAO bookingDAO;

    private String bookingCode;
    private int roomTypeId;
    private String guestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_review);

        bookingDAO = new BookingDAO(this);

        bookingCode = getIntent().getStringExtra("booking_code");
        roomTypeId = getIntent().getIntExtra("room_type_id", -1);
        guestName = getIntent().getStringExtra("guest_name");

        initViews();
        bindActions();
    }

    private void initViews() {
        ratingBar = findViewById(R.id.ratingBarReview);
        edtReviewContent = findViewById(R.id.edtReviewContent);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnBackReview = findViewById(R.id.btnBackReview);
    }

    private void bindActions() {
        btnBackReview.setOnClickListener(v -> finish());

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            Toast.makeText(this, "Không tìm thấy booking code", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = (int) ratingBar.getRating();
        String reviewContent = edtReviewContent.getText() != null
                ? edtReviewContent.getText().toString().trim()
                : "";

        if (rating <= 0) {
            Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bookingDAO.isReviewed(bookingCode)) {
            Toast.makeText(this, "Booking này đã được đánh giá", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String initials = getInitials(guestName);
        String reviewMonth = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(new Date());

        boolean success = bookingDAO.insertReview(
                roomTypeId,
                safe(guestName),
                initials,
                reviewMonth,
                rating,
                reviewContent,
                bookingCode
        );

        if (success) {
            Toast.makeText(this, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Không thể lưu đánh giá", Toast.LENGTH_SHORT).show();
        }
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "GU";

        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
            if (sb.length() >= 2) break;
        }

        return sb.length() == 0 ? "GU" : sb.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}