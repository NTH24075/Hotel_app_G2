package com.example.hotellapp.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.models.BookingDetail;
import com.example.hotellapp.models.PaymentInfo;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPaymentBookingCode;
    private TextView tvPaymentRoomInfo;
    private TextView tvPaymentStayInfo;
    private TextView tvPaymentAmount;
    private TextView tvPaymentStatus;
    private TextView tvBookingStatus;
    private TextView tvPaidAt;
    private TextView tvQrGuide;

    private RadioGroup rgPaymentMethod;
    private RadioButton rbBanking, rbMomo;

    private ImageView imgFakeQr;

    private Button btnBackPayment;
    private Button btnShowQr;
    private Button btnConfirmPayment;
    private Button btnGoHome;

    private BookingDAO bookingDAO;
    private int bookingId = -1;

    private BookingDetail bookingDetail;
    private PaymentInfo paymentInfo;

    private boolean qrDisplayed = false;

    private final NumberFormat currencyFormat =
            NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_payment);

        bookingId = getIntent().getIntExtra("booking_id", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Không tìm thấy booking_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingDAO = new BookingDAO(this);

        initViews();
        loadData();
        bindActions();
    }

    private void initViews() {
        tvPaymentBookingCode = findViewById(R.id.tvPaymentBookingCode);
        tvPaymentRoomInfo = findViewById(R.id.tvPaymentRoomInfo);
        tvPaymentStayInfo = findViewById(R.id.tvPaymentStayInfo);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvBookingStatus = findViewById(R.id.tvBookingStatus);
        tvPaidAt = findViewById(R.id.tvPaidAt);
        tvQrGuide = findViewById(R.id.tvQrGuide);

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbBanking = findViewById(R.id.rbBanking);
        rbMomo = findViewById(R.id.rbMomo);

        imgFakeQr = findViewById(R.id.imgFakeQr);

        btnBackPayment = findViewById(R.id.btnBackPayment);
        btnShowQr = findViewById(R.id.btnShowQr);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnGoHome = findViewById(R.id.btnGoHome);
    }

    private void bindActions() {
        btnBackPayment.setOnClickListener(v -> finish());

        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, GuestMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnShowQr.setOnClickListener(v -> showFakeQr());

        btnConfirmPayment.setOnClickListener(v -> handlePayment());
    }

    private void loadData() {
        bookingDetail = bookingDAO.getBookingDetail(bookingId);
        paymentInfo = bookingDAO.getPaymentInfo(bookingId);

        if (bookingDetail == null) {
            Toast.makeText(this, "Không tìm thấy thông tin booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvPaymentBookingCode.setText("Mã booking: " + safe(bookingDetail.getBookingCode()));
        tvPaymentRoomInfo.setText(
                "Phòng: " + safe(bookingDetail.getRoomTypeName()) +
                        "\nSố phòng: " + bookingDetail.getNumberOfRooms() +
                        "\nSố khách: " + bookingDetail.getGuestCount()
        );

        tvPaymentStayInfo.setText(
                "Nhận phòng: " + safe(bookingDetail.getCheckInDate()) +
                        "\nTrả phòng: " + safe(bookingDetail.getCheckOutDate()) +
                        "\nSố đêm: " + bookingDetail.getNights()
        );

        tvPaymentAmount.setText(formatMoney(bookingDetail.getTotalAmount()) + " VNĐ");
        tvBookingStatus.setText("Trạng thái booking: " + safe(bookingDetail.getBookingStatus()));

        if (paymentInfo != null) {
            tvPaymentStatus.setText("Trạng thái thanh toán: " + safe(paymentInfo.getPaymentStatus()));

            String paidAt = paymentInfo.getPaidAt();
            tvPaidAt.setText("Thời gian thanh toán: " +
                    (paidAt == null || paidAt.trim().isEmpty() ? "Chưa thanh toán" : paidAt));

            String method = paymentInfo.getPaymentMethod();
            if ("Banking".equalsIgnoreCase(method)) {
                rbBanking.setChecked(true);
            } else if ("Momo".equalsIgnoreCase(method)) {
                rbMomo.setChecked(true);
            } else {
                rbBanking.setChecked(true);
            }
        } else {
            tvPaymentStatus.setText("Trạng thái thanh toán: Chưa có dữ liệu");
            tvPaidAt.setText("Thời gian thanh toán: Chưa thanh toán");
            rbBanking.setChecked(true);
        }

        setupPaymentUiState();
    }

    private void setupPaymentUiState() {
        boolean alreadyPaid = paymentInfo != null && "PAID".equalsIgnoreCase(paymentInfo.getPaymentStatus());

        if (alreadyPaid) {
            qrDisplayed = true;

            imgFakeQr.setVisibility(View.VISIBLE);
            tvQrGuide.setVisibility(View.VISIBLE);
            tvQrGuide.setText("Booking này đã được thanh toán.");

            btnShowQr.setEnabled(false);
            btnShowQr.setText("Đã hiển thị QR");

            btnConfirmPayment.setEnabled(false);
            btnConfirmPayment.setText("Đã thanh toán");

            rgPaymentMethod.setEnabled(false);
            rbBanking.setEnabled(false);
            rbMomo.setEnabled(false);
        } else {
            qrDisplayed = false;

            imgFakeQr.setVisibility(View.GONE);
            tvQrGuide.setVisibility(View.GONE);

            btnShowQr.setEnabled(true);
            btnShowQr.setText("Thực hiện thanh toán");

            btnConfirmPayment.setEnabled(false);
            btnConfirmPayment.setText("Xác nhận thanh toán");

            rgPaymentMethod.setEnabled(true);
            rbBanking.setEnabled(true);
            rbMomo.setEnabled(true);
        }
    }

    private void showFakeQr() {
        if (paymentInfo != null && "PAID".equalsIgnoreCase(paymentInfo.getPaymentStatus())) {
            Toast.makeText(this, "Booking này đã được thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        qrDisplayed = true;
        imgFakeQr.setVisibility(View.VISIBLE);
        tvQrGuide.setVisibility(View.VISIBLE);

        String paymentMethod = getSelectedPaymentMethod();
        tvQrGuide.setText(
                "Đây là mã QR mô phỏng cho phương thức: " + paymentMethod +
                        "\nSau khi quét/chuyển khoản xong, bấm \"Xác nhận thanh toán\"."
        );

        btnConfirmPayment.setEnabled(true);
        Toast.makeText(this, "Đã hiển thị mã QR thanh toán", Toast.LENGTH_SHORT).show();
    }

    private void handlePayment() {
        if (paymentInfo != null && "PAID".equalsIgnoreCase(paymentInfo.getPaymentStatus())) {
            Toast.makeText(this, "Booking này đã được thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!qrDisplayed) {
            Toast.makeText(this, "Hãy bấm 'Thực hiện thanh toán' để hiện mã QR trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = getSelectedPaymentMethod();
        boolean success = bookingDAO.payBooking(bookingId, paymentMethod);

        if (success) {
            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            loadData();
        } else {
            Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedPaymentMethod() {
        int checkedId = rgPaymentMethod.getCheckedRadioButtonId();

        if (checkedId == R.id.rbMomo) {
            return "Momo";
        }
        return "Banking";
    }

    private String formatMoney(double value) {
        return currencyFormat.format(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}