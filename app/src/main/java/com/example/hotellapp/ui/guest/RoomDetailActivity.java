package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.hotellapp.R;
import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.database.DatabaseHelper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RoomDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int roomTypeId;

    private String checkInDate;
    private String checkOutDate;
    private int guestCount;
    private int numberOfRooms;

    private TextView tvHeaderTitle;
    private TextView tvRoomName;
    private TextView tvHotelChip;
    private TextView tvAvailabilityChip;
    private TextView tvDealChip;
    private TextView tvScore;
    private TextView tvAreaValue;
    private TextView tvBedValue;
    private TextView tvCapacityValue;
    private TextView tvFloorValue;
    private TextView tvAvailabilityLine;
    private TextView tvDescription;
    private TextView tvReadMore;
    private TextView tvOldPrice;
    private TextView tvCurrentPrice;
    private TextView tvTotalPrice;

    private GridLayout amenityContainer;
    private LinearLayout serviceContainer;
    private LinearLayout floorRoomContainer;
    private LinearLayout reviewContainer;

    private boolean expandedDescription = false;
    private String fullDescription = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        roomTypeId = getIntent().getIntExtra("ROOM_TYPE_ID", -1);
        checkInDate = safeIntentDate(getIntent().getStringExtra("CHECK_IN_DATE"), getTodayPlusDays(0));
        checkOutDate = safeIntentDate(getIntent().getStringExtra("CHECK_OUT_DATE"), getTodayPlusDays(2));
        guestCount = getIntent().getIntExtra("GUEST_COUNT", 2);
        numberOfRooms = getIntent().getIntExtra("NUMBER_OF_ROOMS", 1);

        if (roomTypeId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        bindViews();
        bindActions();
        loadRoomDetail();
    }

    private void bindViews() {
        tvHeaderTitle = findViewById(R.id.tv_detail_header_title);
        tvRoomName = findViewById(R.id.tv_detail_room_name);
        tvHotelChip = findViewById(R.id.tv_detail_hotel_chip);
        tvAvailabilityChip = findViewById(R.id.tv_detail_availability_chip);
        tvDealChip = findViewById(R.id.tv_detail_deal_chip);
        tvScore = findViewById(R.id.tv_detail_score);
        tvAreaValue = findViewById(R.id.tv_detail_area);
        tvBedValue = findViewById(R.id.tv_detail_bed);
        tvCapacityValue = findViewById(R.id.tv_detail_capacity);
        tvFloorValue = findViewById(R.id.tv_detail_floor);
        tvAvailabilityLine = findViewById(R.id.tv_detail_availability_line);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvReadMore = findViewById(R.id.tv_detail_read_more);
        tvOldPrice = findViewById(R.id.tv_detail_old_price);
        tvCurrentPrice = findViewById(R.id.tv_detail_price);
        tvTotalPrice = findViewById(R.id.tv_detail_total_price);
        amenityContainer = findViewById(R.id.layout_detail_amenities);
        serviceContainer = findViewById(R.id.layout_detail_services);
        floorRoomContainer = findViewById(R.id.layout_detail_floor_rooms);
        reviewContainer = findViewById(R.id.layout_detail_reviews);
    }

    private void bindActions() {
        findViewById(R.id.btn_back_detail).setOnClickListener(v -> finish());

        findViewById(R.id.btn_book_now).setOnClickListener(v -> {
            Intent intent = new Intent(RoomDetailActivity.this, BookingActivity.class);

            intent.putExtra("ROOM_TYPE_ID", roomTypeId);
            intent.putExtra("ROOM_NAME", tvRoomName.getText().toString());
            intent.putExtra("PRICE_TEXT", tvCurrentPrice.getText().toString());
            intent.putExtra("TOTAL_PRICE_TEXT", tvTotalPrice.getText().toString());
            intent.putExtra("CAPACITY_TEXT", tvCapacityValue.getText().toString());
            intent.putExtra("BED_TEXT", tvBedValue.getText().toString());
            intent.putExtra("AREA_TEXT", tvAreaValue.getText().toString());
            intent.putExtra("AVAILABILITY_TEXT", tvAvailabilityLine.getText().toString());

            intent.putExtra("CHECK_IN_DATE", checkInDate);
            intent.putExtra("CHECK_OUT_DATE", checkOutDate);
            intent.putExtra("GUEST_COUNT", guestCount);
            intent.putExtra("NUMBER_OF_ROOMS", numberOfRooms);

            startActivity(intent);
        });

        tvReadMore.setOnClickListener(v -> toggleDescription());
    }

    private void loadRoomDetail() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            loadPrimaryInfo(db);
            runSafeSection(() -> loadAmenities(db));
            runSafeSection(() -> loadServices(db));
            runSafeSection(() -> loadFloorStatuses(db));
            runSafeSection(() -> loadReviews(db));
        } catch (Exception exception) {
            Toast.makeText(this, "Không thể tải chi tiết phòng", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            db.close();
        }
    }

    private void runSafeSection(Runnable action) {
        try {
            action.run();
        } catch (Exception ignored) {
        }
    }

    private void loadPrimaryInfo(SQLiteDatabase db) {
        String query =
                "SELECT h.HotelName, h.StarRating, h.CheckInTime, h.CheckOutTime, " +
                        "rt.TypeName, rt.Description, rt.BedType, rt.SizeSqm, rt.Capacity, rt.PricePerNight, " +
                        "MIN(r.FloorNumber) AS floor_number, " +
                        "SUM(CASE WHEN r.RoomStatus = 'Available' THEN 1 ELSE 0 END) AS available_count " +
                        "FROM Hotel h, RoomTypes rt " +
                        "LEFT JOIN Rooms r ON rt.RoomTypeId = r.RoomTypeId AND r.IsActive = 1 " +
                        "WHERE rt.RoomTypeId = ? " +
                        "GROUP BY h.HotelName, h.StarRating, h.CheckInTime, h.CheckOutTime, " +
                        "rt.TypeName, rt.Description, rt.BedType, rt.SizeSqm, rt.Capacity, rt.PricePerNight";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roomTypeId)})) {
            if (!cursor.moveToFirst()) {
                throw new IllegalStateException("Room type not found");
            }

            String hotelName = safeText(cursor.getString(0), "Grand Palace Hotel");
            int starRating = cursor.getInt(1);
            String checkInTime = safeText(cursor.getString(2), "14:00");
            String checkOutTime = safeText(cursor.getString(3), "12:00");
            String roomName = safeText(cursor.getString(4), "Phòng");
            fullDescription = safeText(cursor.getString(5), "Phòng hiện đại, đầy đủ tiện nghi.");
            String bedType = safeText(cursor.getString(6), "1 giường đôi");
            double sizeSqm = cursor.isNull(7) ? 0 : cursor.getDouble(7);
            int capacity = cursor.getInt(8);
            double pricePerNight = cursor.getDouble(9);
            int floorNumber = cursor.isNull(10) ? 0 : cursor.getInt(10);
            int availableCount = cursor.getInt(11);

            NumberFormat money = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            double oldPrice = pricePerNight / 0.85d;

            int nights = calculateNights(checkInDate, checkOutDate);
            double totalPrice = pricePerNight * nights * numberOfRooms;

            tvHeaderTitle.setText("Chi tiết phòng");
            tvRoomName.setText("Phòng " + roomName);
            tvHotelChip.setText(hotelName + " · " + starRating + "★");
            tvAvailabilityChip.setText("Còn " + availableCount + " phòng");
            tvDealChip.setText("Đặt trực tiếp -15%");
            tvScore.setText("9.2");
            tvAreaValue.setText(formatNumber(sizeSqm) + " m²");
            tvBedValue.setText(bedType);
            tvCapacityValue.setText(capacity + " người");
            tvFloorValue.setText("Tầng " + floorNumber);
            tvAvailabilityLine.setText(
                    availableCount + " phòng trống · Nhận phòng " + checkInTime +
                            " · Trả phòng " + checkOutTime +
                            " · " + formatDateDisplayShort(checkInDate) +
                            " → " + formatDateDisplayShort(checkOutDate)
            );

            tvDescription.setText(fullDescription);
            tvDescription.setMaxLines(3);
            tvReadMore.setVisibility(fullDescription.length() > 120 ? View.VISIBLE : View.GONE);

            tvOldPrice.setText(money.format(Math.round(oldPrice)) + " đ");
            tvCurrentPrice.setText(money.format(Math.round(pricePerNight)) + " đ");
            tvTotalPrice.setText("Tổng " + nights + " đêm: " +
                    money.format(Math.round(totalPrice)) + " đ · Đã gồm VAT");
        }
    }

    private void loadAmenities(SQLiteDatabase db) {
        amenityContainer.removeAllViews();
        String query = "SELECT Amenities FROM RoomTypes WHERE RoomTypeId = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roomTypeId)})) {
            if (!cursor.moveToFirst()) {
                return;
            }

            String amenities = safeText(cursor.getString(0), "");
            for (String amenity : amenities.split(",")) {
                String trimmed = amenity.trim();
                if (!trimmed.isEmpty()) {
                    amenityContainer.addView(createAmenityCard(trimmed));
                }
            }
        }
    }

    private void loadServices(SQLiteDatabase db) {
        serviceContainer.removeAllViews();
        String query = "SELECT ServiceName, Price, UnitLabel, IconGlyph FROM Services WHERE IsActive = 1 ORDER BY ServiceId";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                serviceContainer.addView(createServiceCard(
                        cursor.getString(3),
                        cursor.getString(0),
                        cursor.getDouble(1),
                        cursor.getString(2)
                ));
            }
        }
    }

    private void loadFloorStatuses(SQLiteDatabase db) {
        floorRoomContainer.removeAllViews();
        String query = "SELECT RoomNumber, RoomStatus FROM Rooms WHERE RoomTypeId = ? AND IsActive = 1 ORDER BY RoomNumber";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roomTypeId)})) {
            while (cursor.moveToNext()) {
                floorRoomContainer.addView(createRoomPill(cursor.getString(0), cursor.getString(1)));
            }
        }
    }

    private void loadReviews(SQLiteDatabase db) {
        reviewContainer.removeAllViews();

        String query =
                "SELECT " +
                        DatabaseContract.ReviewsTable.COLUMN_GUEST_NAME + ", " +
                        DatabaseContract.ReviewsTable.COLUMN_RATING + ", " +
                        DatabaseContract.ReviewsTable.COLUMN_CONTENT + " " +
                        "FROM " + DatabaseContract.ReviewsTable.TABLE_NAME + " " +
                        "WHERE " + DatabaseContract.ReviewsTable.COLUMN_ROOM_TYPE_ID + " = ? " +
                        "ORDER BY " + DatabaseContract.ReviewsTable.COLUMN_ID + " DESC " +
                        "LIMIT 5";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(roomTypeId)})) {
            while (cursor.moveToNext()) {
                reviewContainer.addView(createReviewCard(
                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getString(2)
                ));
            }
        }
    }

    private void toggleDescription() {
        expandedDescription = !expandedDescription;
        tvDescription.setMaxLines(expandedDescription ? Integer.MAX_VALUE : 3);
        tvReadMore.setText(expandedDescription ? "Thu gọn ↑" : "Đọc thêm →");
    }

    private View createAmenityCard(String text) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setBackgroundResource(R.drawable.bg_room_card);
        card.setPadding(dp(10), dp(9), dp(10), dp(9));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(0, 0, dp(7), dp(7));
        card.setLayoutParams(params);

        View dot = new View(this);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dp(5), dp(5));
        dot.setLayoutParams(dotParams);
        dot.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_gold));

        TextView label = new TextView(this);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(dp(8), 0, 0, 0);
        label.setLayoutParams(labelParams);
        label.setText(text);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        label.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        label.setTypeface(Typeface.DEFAULT_BOLD);

        card.addView(dot);
        card.addView(label);
        return card;
    }

    private View createServiceCard(String icon, String name, double price, String unit) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(98), LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, dp(8), 0);
        card.setLayoutParams(params);
        card.setRadius(dp(12));
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        card.setCardElevation(dp(1));

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(10), dp(10), dp(10), dp(10));
        body.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView iconView = new TextView(this);
        iconView.setText(icon);
        iconView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        body.addView(iconView);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setGravity(Gravity.CENTER);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        nameView.setTypeface(Typeface.DEFAULT_BOLD);
        nameView.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        body.addView(nameView);

        NumberFormat money = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        TextView priceView = new TextView(this);
        priceView.setText(money.format(Math.round(price)) + "đ");
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        priceView.setTextColor(ContextCompat.getColor(this, R.color.green_badge_text));
        priceView.setTypeface(Typeface.DEFAULT_BOLD);
        body.addView(priceView);

        TextView unitView = new TextView(this);
        unitView.setText(unit);
        unitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        unitView.setTextColor(Color.parseColor("#B0B0B0"));
        body.addView(unitView);

        card.addView(body);
        return card;
    }

    private View createRoomPill(String roomNumber, String status) {
        TextView pill = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, dp(6), dp(6));
        pill.setLayoutParams(params);
        pill.setPadding(dp(10), dp(5), dp(10), dp(5));
        pill.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        pill.setTypeface(Typeface.DEFAULT_BOLD);

        if (DatabaseContract.RoomsTable.STATUS_AVAILABLE.equals(status)) {
            pill.setBackgroundColor(Color.parseColor("#E6F4EC"));
            pill.setTextColor(Color.parseColor("#1A5C30"));
            pill.setText(roomNumber + " ✓");
        } else if (DatabaseContract.RoomsTable.STATUS_OCCUPIED.equals(status)) {
            pill.setBackgroundColor(Color.parseColor("#FCE8E8"));
            pill.setTextColor(Color.parseColor("#7A1A1A"));
            pill.setText(roomNumber + " ✗");
        } else {
            pill.setBackgroundColor(Color.parseColor("#FFF8E0"));
            pill.setTextColor(Color.parseColor("#7A5E00"));
            pill.setText(roomNumber + " ⟳");
        }

        return pill;
    }

    private View createReviewCard(String guestName, int rating, String content) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(8));
        card.setLayoutParams(cardParams);
        card.setRadius(dp(14));
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        card.setCardElevation(dp(1));

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(12), dp(12), dp(12), dp(12));

        TextView nameView = new TextView(this);
        nameView.setText(safeText(guestName, "Khách hàng"));
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        nameView.setTypeface(Typeface.DEFAULT_BOLD);
        nameView.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        body.addView(nameView);

        TextView starView = new TextView(this);
        LinearLayout.LayoutParams starParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        starParams.setMargins(0, dp(4), 0, 0);
        starView.setLayoutParams(starParams);
        starView.setText(buildStars(rating));
        starView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        starView.setTextColor(ContextCompat.getColor(this, R.color.accent_gold));
        body.addView(starView);

        TextView commentView = new TextView(this);
        LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        commentParams.setMargins(0, dp(6), 0, 0);
        commentView.setLayoutParams(commentParams);
        commentView.setText(safeText(content, "Không có nội dung đánh giá."));
        commentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        commentView.setTextColor(Color.parseColor("#555555"));
        commentView.setLineSpacing(0, 1.3f);
        body.addView(commentView);

        card.addView(body);
        return card;
    }

    private int calculateNights(String checkIn, String checkOut) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);

            Date inDate = sdf.parse(checkIn);
            Date outDate = sdf.parse(checkOut);

            if (inDate == null || outDate == null) return 1;

            long diff = outDate.getTime() - inDate.getTime();
            int nights = (int) (diff / (1000L * 60 * 60 * 24));
            return Math.max(nights, 1);
        } catch (Exception e) {
            return 1;
        }
    }

    private String formatDateDisplayShort(String dateValue) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MM", Locale.getDefault());
            Date date = input.parse(dateValue);
            if (date == null) return dateValue;
            return output.format(date);
        } catch (Exception e) {
            return dateValue;
        }
    }

    private String safeIntentDate(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String getTodayPlusDays(int days) {
        long millis = System.currentTimeMillis() + days * 24L * 60 * 60 * 1000;
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(millis));
    }

    private String buildStars(int rating) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rating; i++) builder.append("★");
        for (int i = rating; i < 5; i++) builder.append("☆");
        return builder.toString();
    }

    private String safeText(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((int) value);
        }
        return String.format(Locale.getDefault(), "%.1f", value);
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}