package com.example.hotellapp.ui.receptionist;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotellapp.R;
import com.example.hotellapp.adapters.BookingServiceAdapter;
import com.example.hotellapp.dao.BookingDAO;
import com.example.hotellapp.dao.ServiceDAO;
import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.models.Booking;
import com.example.hotellapp.models.BookingServiceItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView txtDetailBookingCode;
    private TextView txtDetailGuestName;
    private TextView txtDetailRoomType;
    private TextView txtDetailCheckIn;
    private TextView txtDetailCheckOut;
    private TextView txtDetailGuestCount;
    private TextView txtDetailNumberOfRooms;
    private TextView txtDetailTotalAmount;
    private TextView txtDetailSpecialRequest;
    private TextView txtDetailBookingStatus;
    private TextView txtDetailPaymentStatus;
    private ListView lvBookingServices;
    private TextView txtNoService;
    private Button btnConfirmBooking;
    private Button btnCheckIn;
    private Button btnAddService;
    private Button btnCheckOut;
    private Button btnBackToList;

    private BookingDAO bookingDAO;
    private Booking currentBooking;
    private int bookingId = -1;
    private ArrayList<BookingServiceItem> bookingServiceList;
    private BookingServiceAdapter bookingServiceAdapter;

    private ServiceDAO serviceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        mapping();

        bookingDAO = new BookingDAO(this);
        serviceDAO = new ServiceDAO(this);

        bookingId = getIntent().getIntExtra("BOOKING_ID", -1);
        if (bookingId != -1) {
            loadBookingDetail();
            loadBookingServices();
        }

        addEvents();

        btnBackToList.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        btnConfirmBooking.setOnClickListener(v -> {
            bookingDAO.confirmBooking(bookingId);
            loadBookingDetail();
        });

        btnCheckIn.setOnClickListener(v -> {
            bookingDAO.checkInBooking(bookingId);
            loadBookingDetail();
        });

        btnCheckOut.setOnClickListener(v -> {
            bookingDAO.checkOutBooking(bookingId);
            loadBookingDetail();
        });

        btnAddService.setOnClickListener(v -> {
            showAddServiceDialog();
        });
    }
    private void loadBookingDetail() {
        currentBooking = bookingDAO.getBookingById(bookingId);
        if (currentBooking != null) {
            showBookingDetail(currentBooking);
            updateActionButtons(currentBooking);
        }
    }
    private void loadBookingServices() {
        bookingServiceList = serviceDAO.getServicesByBookingId(bookingId);

        if (bookingServiceList == null || bookingServiceList.isEmpty()) {
            txtNoService.setVisibility(View.VISIBLE);
            lvBookingServices.setVisibility(View.GONE);
        } else {
            txtNoService.setVisibility(View.GONE);
            lvBookingServices.setVisibility(View.VISIBLE);

            bookingServiceAdapter = new BookingServiceAdapter(this, bookingServiceList);
            lvBookingServices.setAdapter(bookingServiceAdapter);

            setListViewHeightBasedOnChildren(lvBookingServices);
        }
    }
    private void setListViewHeightBasedOnChildren(ListView listView) {
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void mapping() {
        txtDetailBookingCode = findViewById(R.id.txtDetailBookingCode);
        txtDetailGuestName = findViewById(R.id.txtDetailGuestName);
        txtDetailRoomType = findViewById(R.id.txtDetailRoomType);
        txtDetailCheckIn = findViewById(R.id.txtDetailCheckIn);
        txtDetailCheckOut = findViewById(R.id.txtDetailCheckOut);
        txtDetailGuestCount = findViewById(R.id.txtDetailGuestCount);
        txtDetailNumberOfRooms = findViewById(R.id.txtDetailNumberOfRooms);
        txtDetailTotalAmount = findViewById(R.id.txtDetailTotalAmount);
        txtDetailSpecialRequest = findViewById(R.id.txtDetailSpecialRequest);
        txtDetailBookingStatus = findViewById(R.id.txtDetailBookingStatus);
        txtDetailPaymentStatus = findViewById(R.id.txtDetailPaymentStatus);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnAddService = findViewById(R.id.btnAddService);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        btnBackToList = findViewById(R.id.btnBackToList);
        lvBookingServices = findViewById(R.id.lvBookingServices);
        txtNoService = findViewById(R.id.txtNoService);
    }

    private void showBookingDetail(Booking booking) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));

        txtDetailBookingCode.setText(booking.getBookingCode());
        txtDetailGuestName.setText("Khách: " + booking.getGuestName());
        txtDetailRoomType.setText("Loại phòng: " + booking.getRoomTypeName());
        txtDetailCheckIn.setText("Ngày nhận phòng: " + booking.getCheckInDate());
        txtDetailCheckOut.setText("Ngày trả phòng: " + booking.getCheckOutDate());
        txtDetailGuestCount.setText("Số khách: " + booking.getGuestCount());
        txtDetailNumberOfRooms.setText("Số phòng: " + booking.getNumberOfRooms());
        txtDetailTotalAmount.setText("Tổng tiền: " + format.format(booking.getTotalAmount()) + " VNĐ");

        if (booking.getSpecialRequest() == null || booking.getSpecialRequest().trim().isEmpty()) {
            txtDetailSpecialRequest.setText("Yêu cầu đặc biệt: Không có");
        } else {
            txtDetailSpecialRequest.setText("Yêu cầu đặc biệt: " + booking.getSpecialRequest());
        }

        txtDetailBookingStatus.setText(booking.getBookingStatus());
        txtDetailPaymentStatus.setText(booking.getPaymentStatus());

        if ("Pending".equals(booking.getBookingStatus())) {
            txtDetailBookingStatus.setBackgroundResource(R.drawable.bg_status_orange);
            txtDetailBookingStatus.setTextColor(0xFFB45309);
        } else if ("Confirmed".equals(booking.getBookingStatus())) {
            txtDetailBookingStatus.setBackgroundResource(R.drawable.bg_status_blue);
            txtDetailBookingStatus.setTextColor(0xFF1D4ED8);
        } else if ("CheckedIn".equals(booking.getBookingStatus())) {
            txtDetailBookingStatus.setBackgroundResource(R.drawable.bg_status_green);
            txtDetailBookingStatus.setTextColor(0xFF15803D);
        } else {
            txtDetailBookingStatus.setBackgroundResource(R.drawable.bg_status_gray);
            txtDetailBookingStatus.setTextColor(0xFF475569);
        }

        if ("Paid".equals(booking.getPaymentStatus())) {
            txtDetailPaymentStatus.setBackgroundResource(R.drawable.bg_status_green);
            txtDetailPaymentStatus.setTextColor(0xFF15803D);
        } else {
            txtDetailPaymentStatus.setBackgroundResource(R.drawable.bg_status_orange);
            txtDetailPaymentStatus.setTextColor(0xFFB45309);
        }
    }
    private void updateActionButtons(Booking booking) {
        String status = booking.getBookingStatus();

        btnConfirmBooking.setEnabled(false);
        btnCheckIn.setEnabled(false);
        btnAddService.setEnabled(false);
        btnCheckOut.setEnabled(false);

        if (DatabaseContract.BookingsTable.STATUS_PENDING.equals(status)) {
            btnConfirmBooking.setEnabled(true);
        } else if (DatabaseContract.BookingsTable.STATUS_CONFIRMED.equals(status)) {
            btnCheckIn.setEnabled(true);
        } else if (DatabaseContract.BookingsTable.STATUS_CHECKED_IN.equals(status)) {
            btnAddService.setEnabled(true);
            btnCheckOut.setEnabled(true);
        }
    }
    private void addEvents() {
        btnBackToList.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        btnConfirmBooking.setOnClickListener(v -> {
            boolean ok = bookingDAO.confirmBooking(bookingId);
            if (ok) {
                android.widget.Toast.makeText(this, "Đã xác nhận booking", android.widget.Toast.LENGTH_SHORT).show();
                loadBookingDetail();
                setResult(RESULT_OK);
            } else {
                android.widget.Toast.makeText(this, "Không thể xác nhận booking", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        btnCheckIn.setOnClickListener(v -> {
            boolean ok = bookingDAO.checkInBooking(bookingId);
            if (ok) {
                android.widget.Toast.makeText(this, "Check-in thành công, đã thanh toán và đã gán phòng", android.widget.Toast.LENGTH_SHORT).show();
                loadBookingDetail();
                loadBookingServices();
                setResult(RESULT_OK);
            } else {
                android.widget.Toast.makeText(this, "Check-in thất bại hoặc không đủ phòng trống", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        btnCheckOut.setOnClickListener(v -> {
            boolean ok = bookingDAO.checkOutBooking(bookingId);
            if (ok) {
                android.widget.Toast.makeText(this, "Check-out thành công, phòng chuyển sang Cleaning", android.widget.Toast.LENGTH_SHORT).show();
                loadBookingDetail();
                loadBookingServices();
                setResult(RESULT_OK);
            } else {
                android.widget.Toast.makeText(this, "Check-out thất bại", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        btnAddService.setOnClickListener(v -> showAddServiceDialog());
    }
    private void showAddServiceDialog() {
        if (currentBooking == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_service, null);
        builder.setView(view);

        Spinner spService = view.findViewById(R.id.spService);
        TextView txtDialogServicePrice = view.findViewById(R.id.txtDialogServicePrice);
        EditText edtServiceQuantity = view.findViewById(R.id.edtServiceQuantity);
        TextView txtDialogServiceTotal = view.findViewById(R.id.txtDialogServiceTotal);

        ArrayList<Integer> serviceIds = new ArrayList<>();
        ArrayList<String> serviceNames = new ArrayList<>();
        ArrayList<Double> servicePrices = new ArrayList<>();

        android.database.Cursor cursor = serviceDAO.getAllServicesCursor();
        while (cursor.moveToNext()) {
            serviceIds.add(cursor.getInt(0));
            String name = cursor.getString(1);
            double price = cursor.getDouble(2);
            String unit = cursor.getString(3);
            String icon = cursor.getString(4);

            serviceNames.add(icon + " " + name);
            servicePrices.add(price);
        }
        cursor.close();

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                serviceNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spService.setAdapter(adapter);

        final int[] selectedIndex = {0};
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));

        Runnable updateTotal = () -> {
            int qty = 1;
            String qtyText = edtServiceQuantity.getText().toString().trim();
            if (!qtyText.isEmpty()) {
                qty = Integer.parseInt(qtyText);
            }

            double unitPrice = servicePrices.get(selectedIndex[0]);
            double total = qty * unitPrice;

            txtDialogServicePrice.setText("Giá: " + format.format(unitPrice) + " VNĐ");
            txtDialogServiceTotal.setText("Thành tiền: " + format.format(total) + " VNĐ");
        };

        spService.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view1, int position, long id) {
                selectedIndex[0] = position;
                updateTotal.run();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        edtServiceQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (s.toString().trim().isEmpty()) {
                    txtDialogServiceTotal.setText("Thành tiền: 0 VNĐ");
                    return;
                }
                updateTotal.run();
            }
        });

        updateTotal.run();

        builder.setPositiveButton("Thêm dịch vụ", (dialog, which) -> {
            String qtyText = edtServiceQuantity.getText().toString().trim();
            if (qtyText.isEmpty()) {
                android.widget.Toast.makeText(this, "Bạn chưa nhập số lượng", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                android.widget.Toast.makeText(this, "Số lượng phải lớn hơn 0", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            int serviceId = serviceIds.get(selectedIndex[0]);
            boolean ok = serviceDAO.addServiceToBooking(bookingId, serviceId, quantity);

            if (ok) {
                android.widget.Toast.makeText(this, "Đã thêm dịch vụ thành công", android.widget.Toast.LENGTH_SHORT).show();
                loadBookingDetail();
                loadBookingServices();
                setResult(RESULT_OK);
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}