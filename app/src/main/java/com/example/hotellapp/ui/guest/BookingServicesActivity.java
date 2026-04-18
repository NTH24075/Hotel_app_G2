package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.models.BookingDetail;
import com.example.hotellapp.models.ServiceItem;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BookingServicesActivity extends AppCompatActivity {

    private TextView tvBookingCode, tvRoomType, tvStayInfo, tvRoomTotal, tvServiceTotal, tvFinalTotal;
    private LinearLayout layoutServices;
    private Button btnSaveServices, btnGoPayment, btnBack;

    private BookingDAO bookingDAO;
    private int bookingId = -1;
    private BookingDetail bookingDetail;
    private List<ServiceItem> serviceItems;

    private final NumberFormat currencyFormat =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_bookingservice);

        bookingId = getIntent().getIntExtra("booking_id", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Không tìm thấy booking_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        bookingDAO = new BookingDAO(this);

        loadBookingInfo();
        loadServices();

        btnSaveServices.setOnClickListener(v -> saveServices());

        btnGoPayment.setOnClickListener(v -> {
            saveServices();

            Intent intent = new Intent(BookingServicesActivity.this, PaymentActivity.class);
            intent.putExtra("booking_id", bookingId);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvBookingCode = findViewById(R.id.tvBookingCode);
        tvRoomType = findViewById(R.id.tvRoomType);
        tvStayInfo = findViewById(R.id.tvStayInfo);
        tvRoomTotal = findViewById(R.id.tvRoomTotal);
        tvServiceTotal = findViewById(R.id.tvServiceTotal);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);

        layoutServices = findViewById(R.id.layoutServices);

        btnSaveServices = findViewById(R.id.btnSaveServices);
        btnGoPayment = findViewById(R.id.btnGoPayment);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadBookingInfo() {
        bookingDetail = bookingDAO.getBookingDetail(bookingId);
        if (bookingDetail == null) {
            Toast.makeText(this, "Không tìm thấy thông tin booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvBookingCode.setText("Mã booking: " + safe(bookingDetail.getBookingCode()));
        tvRoomType.setText("Phòng: " + safe(bookingDetail.getRoomTypeName()));
        tvStayInfo.setText(
                "Nhận phòng: " + safe(bookingDetail.getCheckInDate()) +
                        "\nTrả phòng: " + safe(bookingDetail.getCheckOutDate()) +
                        "\nSố đêm: " + bookingDetail.getNights() +
                        "\nSố phòng: " + bookingDetail.getNumberOfRooms()
        );

        updateSummary(
                bookingDetail.getRoomTotal(),
                bookingDetail.getServiceTotal()
        );
    }

    private void loadServices() {
        serviceItems = bookingDAO.getAllServicesForBooking(bookingId);
        layoutServices.removeAllViews();

        if (serviceItems == null || serviceItems.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có dịch vụ nào.");
            tvEmpty.setTextSize(16f);
            layoutServices.addView(tvEmpty);
            return;
        }

        for (ServiceItem item : serviceItems) {
            View itemView = getLayoutInflater().inflate(R.layout.item_service_booking, layoutServices, false);

            TextView tvIcon = itemView.findViewById(R.id.tvServiceIcon);
            TextView tvName = itemView.findViewById(R.id.tvServiceName);
            TextView tvPrice = itemView.findViewById(R.id.tvServicePrice);
            TextView tvQuantity = itemView.findViewById(R.id.tvQuantity);
            MaterialButton btnMinus = itemView.findViewById(R.id.btnMinus);
            MaterialButton btnPlus = itemView.findViewById(R.id.btnPlus);

            tvIcon.setText(safe(item.getIcon()).isEmpty() ? "•" : item.getIcon());
            tvName.setText(item.getServiceName() + " (" + safe(item.getUnitLabel()) + ")");
            tvPrice.setText(formatMoney(item.getPrice()) + " / " + safe(item.getUnitLabel()));
            tvQuantity.setText(String.valueOf(item.getQuantity()));

            btnMinus.setOnClickListener(v -> {
                int qty = item.getQuantity();
                if (qty > 0) {
                    qty--;
                    item.setQuantity(qty);
                    tvQuantity.setText(String.valueOf(qty));
                    refreshTotalsOnlyUI();
                }
            });

            btnPlus.setOnClickListener(v -> {
                int qty = item.getQuantity() + 1;
                item.setQuantity(qty);
                tvQuantity.setText(String.valueOf(qty));
                refreshTotalsOnlyUI();
            });

            layoutServices.addView(itemView);
        }

        refreshTotalsOnlyUI();
    }

    private void refreshTotalsOnlyUI() {
        double roomTotal = bookingDetail != null ? bookingDetail.getRoomTotal() : 0;
        double serviceTotal = 0;

        if (serviceItems != null) {
            for (ServiceItem item : serviceItems) {
                serviceTotal += item.getPrice() * item.getQuantity();
            }
        }

        updateSummary(roomTotal, serviceTotal);
    }

    private void updateSummary(double roomTotal, double serviceTotal) {
        double finalTotal = roomTotal + serviceTotal;

        tvRoomTotal.setText(formatMoney(roomTotal) + " VNĐ");
        tvServiceTotal.setText(formatMoney(serviceTotal) + " VNĐ");
        tvFinalTotal.setText(formatMoney(finalTotal) + " VNĐ");
    }

    private void saveServices() {
        if (bookingId == -1 || serviceItems == null) return;

        bookingDAO.saveBookingServices(bookingId, serviceItems);
        Toast.makeText(this, "Đã lưu dịch vụ", Toast.LENGTH_SHORT).show();

        bookingDetail = bookingDAO.getBookingDetail(bookingId);
        if (bookingDetail != null) {
            updateSummary(bookingDetail.getRoomTotal(), bookingDetail.getServiceTotal());
        }
    }

    private String formatMoney(double value) {
        return currencyFormat.format(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}