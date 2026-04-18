package com.example.hotellapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.database.DatabaseHelper;
import com.example.hotellapp.models.Booking;

import com.example.hotellapp.models.BookingDetail;
import com.example.hotellapp.models.PaymentInfo;
import com.example.hotellapp.models.ServiceItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.util.ArrayList;

public class BookingDAO {

    private DatabaseHelper dbHelper;

    public BookingDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private ArrayList<Booking> mapQueryToList(Cursor cursor) {
        ArrayList<Booking> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            Booking booking = new Booking();
            booking.setBookingId(cursor.getInt(0));
            booking.setBookingCode(cursor.getString(1));
            booking.setGuestName(cursor.getString(2));
            booking.setRoomTypeName(cursor.getString(3));
            booking.setCheckInDate(cursor.getString(4));
            booking.setCheckOutDate(cursor.getString(5));
            booking.setBookingStatus(cursor.getString(6));
            booking.setPaymentStatus(cursor.getString(7));
            list.add(booking);
        }

        return list;
    }

    public ArrayList<Booking> getAllBookings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT b." + DatabaseContract.BookingsTable.COLUMN_ID + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + ", " +
                "u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + ", " +
                "rt." + DatabaseContract.RoomTypesTable.COLUMN_TYPE_NAME + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + ", " +
                "p." + DatabaseContract.PaymentsTable.COLUMN_STATUS + " " +
                "FROM " + DatabaseContract.BookingsTable.TABLE_NAME + " b " +
                "INNER JOIN " + DatabaseContract.UsersTable.TABLE_NAME + " u " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_USER_ID + " = u." + DatabaseContract.UsersTable.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.RoomTypesTable.TABLE_NAME + " rt " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + " = rt." + DatabaseContract.RoomTypesTable.COLUMN_ID + " " +
                "LEFT JOIN " + DatabaseContract.PaymentsTable.TABLE_NAME + " p " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ID + " = p." + DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " " +
                "ORDER BY b." + DatabaseContract.BookingsTable.COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Booking> list = mapQueryToList(cursor);
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Booking> searchBookings(String keyword) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String normalizedKeyword = removeVietnameseAccents(keyword);

        String sql = "SELECT b." + DatabaseContract.BookingsTable.COLUMN_ID + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + ", " +
                "u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + ", " +
                "rt." + DatabaseContract.RoomTypesTable.COLUMN_TYPE_NAME + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + ", " +
                "p." + DatabaseContract.PaymentsTable.COLUMN_STATUS + " " +
                "FROM " + DatabaseContract.BookingsTable.TABLE_NAME + " b " +
                "INNER JOIN " + DatabaseContract.UsersTable.TABLE_NAME + " u " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_USER_ID + " = u." + DatabaseContract.UsersTable.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.RoomTypesTable.TABLE_NAME + " rt " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + " = rt." + DatabaseContract.RoomTypesTable.COLUMN_ID + " " +
                "LEFT JOIN " + DatabaseContract.PaymentsTable.TABLE_NAME + " p " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ID + " = p." + DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " " +
                "WHERE b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + " LIKE ? " +
                "OR u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + " LIKE ? " +
                "OR u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME_UNSIGNED + " LIKE ? " +
                "ORDER BY b." + DatabaseContract.BookingsTable.COLUMN_ID + " DESC";

        String value1 = "%" + keyword + "%";
        String value2 = "%" + normalizedKeyword + "%";

        Cursor cursor = db.rawQuery(sql, new String[]{value1, value1, value2});
        ArrayList<Booking> list = mapQueryToList(cursor);
        cursor.close();
        db.close();
        return list;
    }
    private String removeVietnameseAccents(String str) {
        if (str == null) return "";

        String result = str.toLowerCase();
        result = result.replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a");
        result = result.replaceAll("[éèẻẽẹêếềểễệ]", "e");
        result = result.replaceAll("[íìỉĩị]", "i");
        result = result.replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o");
        result = result.replaceAll("[úùủũụưứừửữự]", "u");
        result = result.replaceAll("[ýỳỷỹỵ]", "y");
        result = result.replaceAll("đ", "d");

        result = result.replaceAll("[ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬ]", "A");
        result = result.replaceAll("[ÉÈẺẼẸÊẾỀỂỄỆ]", "E");
        result = result.replaceAll("[ÍÌỈĨỊ]", "I");
        result = result.replaceAll("[ÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]", "O");
        result = result.replaceAll("[ÚÙỦŨỤƯỨỪỬỮỰ]", "U");
        result = result.replaceAll("[ÝỲỶỸỴ]", "Y");
        result = result.replaceAll("Đ", "D");

        return result;
    }

    public ArrayList<Booking> filterBookingsByDate(String fromDate, String toDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT b." + DatabaseContract.BookingsTable.COLUMN_ID + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + ", " +
                "u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + ", " +
                "rt." + DatabaseContract.RoomTypesTable.COLUMN_TYPE_NAME + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + ", " +
                "p." + DatabaseContract.PaymentsTable.COLUMN_STATUS + " " +
                "FROM " + DatabaseContract.BookingsTable.TABLE_NAME + " b " +
                "INNER JOIN " + DatabaseContract.UsersTable.TABLE_NAME + " u " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_USER_ID + " = u." + DatabaseContract.UsersTable.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.RoomTypesTable.TABLE_NAME + " rt " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + " = rt." + DatabaseContract.RoomTypesTable.COLUMN_ID + " " +
                "LEFT JOIN " + DatabaseContract.PaymentsTable.TABLE_NAME + " p " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ID + " = p." + DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " " +
                "WHERE b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + " >= ? " +
                "AND b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + " <= ? " +
                "ORDER BY b." + DatabaseContract.BookingsTable.COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(sql, new String[]{fromDate, toDate});
        ArrayList<Booking> list = mapQueryToList(cursor);
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Booking> searchAndFilterBookings(String keyword, String fromDate, String toDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String normalizedKeyword = removeVietnameseAccents(keyword);

        String sql = "SELECT b." + DatabaseContract.BookingsTable.COLUMN_ID + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + ", " +
                "u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + ", " +
                "rt." + DatabaseContract.RoomTypesTable.COLUMN_TYPE_NAME + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + ", " +
                "p." + DatabaseContract.PaymentsTable.COLUMN_STATUS + " " +
                "FROM " + DatabaseContract.BookingsTable.TABLE_NAME + " b " +
                "INNER JOIN " + DatabaseContract.UsersTable.TABLE_NAME + " u " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_USER_ID + " = u." + DatabaseContract.UsersTable.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.RoomTypesTable.TABLE_NAME + " rt " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + " = rt." + DatabaseContract.RoomTypesTable.COLUMN_ID + " " +
                "LEFT JOIN " + DatabaseContract.PaymentsTable.TABLE_NAME + " p " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ID + " = p." + DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " " +
                "WHERE (b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + " LIKE ? " +
                "OR u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + " LIKE ? " +
                "OR u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME_UNSIGNED + " LIKE ?) " +
                "AND b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + " >= ? " +
                "AND b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + " <= ? " +
                "ORDER BY b." + DatabaseContract.BookingsTable.COLUMN_ID + " DESC";

        String value1 = "%" + keyword + "%";
        String value2 = "%" + normalizedKeyword + "%";

        Cursor cursor = db.rawQuery(sql, new String[]{value1, value1, value2, fromDate, toDate});
        ArrayList<Booking> list = mapQueryToList(cursor);
        cursor.close();
        db.close();
        return list;
    }

    public int countAllBookings() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.BookingsTable.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public int countCheckedInBookings() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT COUNT(*) FROM " + DatabaseContract.BookingsTable.TABLE_NAME +
                " WHERE " + DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{DatabaseContract.BookingsTable.STATUS_CHECKED_IN});

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }
    public Booking getBookingById(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Booking booking = null;

        String sql = "SELECT b." + DatabaseContract.BookingsTable.COLUMN_ID + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + ", " +
                "u." + DatabaseContract.UsersTable.COLUMN_FULL_NAME + ", " +
                "rt." + DatabaseContract.RoomTypesTable.COLUMN_TYPE_NAME + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + ", " +
                "p." + DatabaseContract.PaymentsTable.COLUMN_STATUS + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_GUEST_COUNT + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_NUMBER_OF_ROOMS + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_TOTAL_AMOUNT + ", " +
                "b." + DatabaseContract.BookingsTable.COLUMN_SPECIAL_REQUEST + " " +
                "FROM " + DatabaseContract.BookingsTable.TABLE_NAME + " b " +
                "INNER JOIN " + DatabaseContract.UsersTable.TABLE_NAME + " u " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_USER_ID + " = u." + DatabaseContract.UsersTable.COLUMN_ID + " " +
                "INNER JOIN " + DatabaseContract.RoomTypesTable.TABLE_NAME + " rt " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + " = rt." + DatabaseContract.RoomTypesTable.COLUMN_ID + " " +
                "LEFT JOIN " + DatabaseContract.PaymentsTable.TABLE_NAME + " p " +
                "ON b." + DatabaseContract.BookingsTable.COLUMN_ID + " = p." + DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " " +
                "WHERE b." + DatabaseContract.BookingsTable.COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(bookingId)});

        if (cursor.moveToFirst()) {
            booking = new Booking();
            booking.setBookingId(cursor.getInt(0));
            booking.setBookingCode(cursor.getString(1));
            booking.setGuestName(cursor.getString(2));
            booking.setRoomTypeName(cursor.getString(3));
            booking.setCheckInDate(cursor.getString(4));
            booking.setCheckOutDate(cursor.getString(5));
            booking.setBookingStatus(cursor.getString(6));
            booking.setPaymentStatus(cursor.getString(7));
            booking.setGuestCount(cursor.getInt(8));
            booking.setNumberOfRooms(cursor.getInt(9));
            booking.setTotalAmount(cursor.getDouble(10));
            booking.setSpecialRequest(cursor.getString(11));
        }

        cursor.close();
        db.close();
        return booking;
    }
    public boolean confirmBooking(int bookingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS,
                DatabaseContract.BookingsTable.STATUS_CONFIRMED);

        int rows = db.update(
                DatabaseContract.BookingsTable.TABLE_NAME,
                values,
                DatabaseContract.BookingsTable.COLUMN_ID + " = ? AND " +
                        DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + " = ?",
                new String[]{
                        String.valueOf(bookingId),
                        DatabaseContract.BookingsTable.STATUS_PENDING
                }
        );

        db.close();
        return rows > 0;
    }
    public boolean checkInBooking(int bookingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues bookingValues = new ContentValues();
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS,
                    DatabaseContract.BookingsTable.STATUS_CHECKED_IN);

            int bookingRows = db.update(
                    DatabaseContract.BookingsTable.TABLE_NAME,
                    bookingValues,
                    DatabaseContract.BookingsTable.COLUMN_ID + " = ? AND " +
                            DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + " = ?",
                    new String[]{
                            String.valueOf(bookingId),
                            DatabaseContract.BookingsTable.STATUS_CONFIRMED
                    }
            );

            if (bookingRows <= 0) {
                db.endTransaction();
                db.close();
                return false;
            }

            ContentValues paymentValues = new ContentValues();
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_STATUS,
                    DatabaseContract.PaymentsTable.STATUS_PAID);

            db.update(
                    DatabaseContract.PaymentsTable.TABLE_NAME,
                    paymentValues,
                    DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " = ?",
                    new String[]{String.valueOf(bookingId)}
            );

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    public boolean checkOutBooking(int bookingId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS,
                DatabaseContract.BookingsTable.STATUS_CHECKED_OUT);

        int rows = db.update(
                DatabaseContract.BookingsTable.TABLE_NAME,
                values,
                DatabaseContract.BookingsTable.COLUMN_ID + " = ? AND " +
                        DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + " = ?",
                new String[]{
                        String.valueOf(bookingId),
                        DatabaseContract.BookingsTable.STATUS_CHECKED_IN
                }
        );

        db.close();
        return rows > 0;
    }



    // Booking Nhan lam
    public int createBooking(
            int userId,
            int roomTypeId,
            String checkInDate,
            String checkOutDate,
            int guestCount,
            int numberOfRooms,
            String specialRequest
    ) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            int nights = calculateNights(checkInDate, checkOutDate);
            if (nights <= 0) nights = 1;

            double pricePerNight = getRoomTypePrice(db, roomTypeId);
            double roomTotal = pricePerNight * nights * numberOfRooms;

            String bookingCode = generateBookingCode();

            ContentValues bookingValues = new ContentValues();
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_USER_ID, userId);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID, roomTypeId);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE, bookingCode);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE, checkInDate);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE, checkOutDate);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_GUEST_COUNT, guestCount);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_NUMBER_OF_ROOMS, numberOfRooms);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_TOTAL_AMOUNT, roomTotal);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS, DatabaseContract.BookingsTable.STATUS_PENDING);
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_SPECIAL_REQUEST, specialRequest);

            long bookingIdLong = db.insert(DatabaseContract.BookingsTable.TABLE_NAME, null, bookingValues);
            int bookingId = (int) bookingIdLong;

            ContentValues paymentValues = new ContentValues();
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID, bookingId);
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_AMOUNT, roomTotal);
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_METHOD, "Cash");
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_STATUS, DatabaseContract.PaymentsTable.STATUS_UNPAID);
            paymentValues.putNull(DatabaseContract.PaymentsTable.COLUMN_PAID_AT);
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_NOTE, "Chưa thanh toán");

            db.insert(DatabaseContract.PaymentsTable.TABLE_NAME, null, paymentValues);

            db.setTransactionSuccessful();
            return bookingId;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public BookingDetail getBookingDetail(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql =
                "SELECT " +
                        "b.BookingId, b.BookingCode, b.UserId, " +
                        "u.FullName, u.Phone, u.Email, " +
                        "b.RoomTypeId, rt.TypeName, rt.PricePerNight, " +
                        "b.CheckInDate, b.CheckOutDate, b.GuestCount, b.NumberOfRooms, " +
                        "b.TotalAmount, b.BookingStatus, b.SpecialRequest, " +
                        "p.PaymentStatus, p.PaymentMethod " +
                        "FROM Bookings b " +
                        "JOIN Users u ON b.UserId = u.UserId " +
                        "JOIN RoomTypes rt ON b.RoomTypeId = rt.RoomTypeId " +
                        "LEFT JOIN Payments p ON b.BookingId = p.BookingId " +
                        "WHERE b.BookingId = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(bookingId)});
        BookingDetail detail = null;

        if (cursor.moveToFirst()) {
            detail = new BookingDetail();
            detail.setBookingId(cursor.getInt(0));
            detail.setBookingCode(cursor.getString(1));
            detail.setUserId(cursor.getInt(2));
            detail.setUserName(cursor.getString(3));
            detail.setUserPhone(cursor.getString(4));
            detail.setUserEmail(cursor.getString(5));
            detail.setRoomTypeId(cursor.getInt(6));
            detail.setRoomTypeName(cursor.getString(7));
            detail.setPricePerNight(cursor.getDouble(8));
            detail.setCheckInDate(cursor.getString(9));
            detail.setCheckOutDate(cursor.getString(10));
            detail.setGuestCount(cursor.getInt(11));
            detail.setNumberOfRooms(cursor.getInt(12));
            detail.setTotalAmount(cursor.getDouble(13));
            detail.setBookingStatus(cursor.getString(14));
            detail.setSpecialRequest(cursor.getString(15));
            detail.setPaymentStatus(cursor.getString(16));
            detail.setPaymentMethod(cursor.getString(17));

            int nights = calculateNights(detail.getCheckInDate(), detail.getCheckOutDate());
            if (nights <= 0) nights = 1;
            detail.setNights(nights);

            double roomTotal = detail.getPricePerNight() * nights * detail.getNumberOfRooms();
            detail.setRoomTotal(roomTotal);

            double serviceTotal = detail.getTotalAmount() - roomTotal;
            if (serviceTotal < 0) serviceTotal = 0;
            detail.setServiceTotal(serviceTotal);
        }

        cursor.close();
        db.close();
        return detail;
    }

    public List<ServiceItem> getAllServicesForBooking(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ServiceItem> list = new ArrayList<>();

        String sql =
                "SELECT s.ServiceId, s.ServiceName, s.IconGlyph, s.UnitLabel, s.Price, " +
                        "COALESCE(bs.Quantity, 0) " +
                        "FROM Services s " +
                        "LEFT JOIN BookingServices bs " +
                        "ON s.ServiceId = bs.ServiceId AND bs.BookingId = ? " +
                        "WHERE s.IsActive = 1 " +
                        "ORDER BY s.ServiceId";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(bookingId)});

        while (cursor.moveToNext()) {
            ServiceItem item = new ServiceItem();
            item.setServiceId(cursor.getInt(0));
            item.setServiceName(cursor.getString(1));
            item.setIcon(cursor.getString(2));
            item.setUnitLabel(cursor.getString(3));
            item.setPrice(cursor.getDouble(4));
            item.setQuantity(cursor.getInt(5));
            list.add(item);
        }

        cursor.close();
        db.close();
        return list;
    }

    public void saveBookingServices(int bookingId, List<ServiceItem> items) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(
                    DatabaseContract.BookingServicesTable.TABLE_NAME,
                    DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID + "=?",
                    new String[]{String.valueOf(bookingId)}
            );

            for (ServiceItem item : items) {
                if (item.getQuantity() > 0) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID, bookingId);
                    values.put(DatabaseContract.BookingServicesTable.COLUMN_SERVICE_ID, item.getServiceId());
                    values.put(DatabaseContract.BookingServicesTable.COLUMN_QUANTITY, item.getQuantity());
                    values.put(DatabaseContract.BookingServicesTable.COLUMN_UNIT_PRICE, item.getPrice());
                    values.put(DatabaseContract.BookingServicesTable.COLUMN_USED_AT, getNow());
                    values.put(DatabaseContract.BookingServicesTable.COLUMN_NOTE, item.getServiceName());

                    db.insert(DatabaseContract.BookingServicesTable.TABLE_NAME, null, values);
                }
            }

            double roomTotal = getRoomTotal(db, bookingId);
            double serviceTotal = getServiceTotal(db, bookingId);
            double finalTotal = roomTotal + serviceTotal;

            ContentValues bookingValues = new ContentValues();
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_TOTAL_AMOUNT, finalTotal);
            db.update(
                    DatabaseContract.BookingsTable.TABLE_NAME,
                    bookingValues,
                    DatabaseContract.BookingsTable.COLUMN_ID + "=?",
                    new String[]{String.valueOf(bookingId)}
            );

            ContentValues paymentValues = new ContentValues();
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_AMOUNT, finalTotal);
            db.update(
                    DatabaseContract.PaymentsTable.TABLE_NAME,
                    paymentValues,
                    DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + "=?",
                    new String[]{String.valueOf(bookingId)}
            );

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public PaymentInfo getPaymentInfo(int bookingId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql =
                "SELECT p.PaymentId, p.BookingId, b.BookingCode, p.Amount, p.PaymentMethod, p.PaymentStatus, p.PaidAt " +
                        "FROM Payments p " +
                        "JOIN Bookings b ON p.BookingId = b.BookingId " +
                        "WHERE p.BookingId = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(bookingId)});
        PaymentInfo info = null;

        if (cursor.moveToFirst()) {
            info = new PaymentInfo();
            info.setPaymentId(cursor.getInt(0));
            info.setBookingId(cursor.getInt(1));
            info.setBookingCode(cursor.getString(2));
            info.setAmount(cursor.getDouble(3));
            info.setPaymentMethod(cursor.getString(4));
            info.setPaymentStatus(cursor.getString(5));
            info.setPaidAt(cursor.getString(6));
        }

        cursor.close();
        db.close();
        return info;
    }

    public boolean payBooking(int bookingId, String paymentMethod) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues paymentValues = new ContentValues();
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_METHOD, paymentMethod);
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_STATUS, DatabaseContract.PaymentsTable.STATUS_PAID);
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_PAID_AT, getNow());
            paymentValues.put(DatabaseContract.PaymentsTable.COLUMN_NOTE, "Đã thanh toán");

            int paymentUpdated = db.update(
                    DatabaseContract.PaymentsTable.TABLE_NAME,
                    paymentValues,
                    DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + "=?",
                    new String[]{String.valueOf(bookingId)}
            );

            ContentValues bookingValues = new ContentValues();
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS, DatabaseContract.BookingsTable.STATUS_CONFIRMED);

            int bookingUpdated = db.update(
                    DatabaseContract.BookingsTable.TABLE_NAME,
                    bookingValues,
                    DatabaseContract.BookingsTable.COLUMN_ID + "=?",
                    new String[]{String.valueOf(bookingId)}
            );

            db.setTransactionSuccessful();
            return paymentUpdated > 0 && bookingUpdated > 0;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private double getRoomTypePrice(SQLiteDatabase db, int roomTypeId) {
        double price = 0;
        Cursor cursor = db.rawQuery(
                "SELECT PricePerNight FROM RoomTypes WHERE RoomTypeId = ?",
                new String[]{String.valueOf(roomTypeId)}
        );

        if (cursor.moveToFirst()) {
            price = cursor.getDouble(0);
        }

        cursor.close();
        return price;
    }

    private double getRoomTotal(SQLiteDatabase db, int bookingId) {
        double total = 0;

        String sql =
                "SELECT rt.PricePerNight, b.CheckInDate, b.CheckOutDate, b.NumberOfRooms " +
                        "FROM Bookings b " +
                        "JOIN RoomTypes rt ON b.RoomTypeId = rt.RoomTypeId " +
                        "WHERE b.BookingId = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(bookingId)});

        if (cursor.moveToFirst()) {
            double pricePerNight = cursor.getDouble(0);
            String checkIn = cursor.getString(1);
            String checkOut = cursor.getString(2);
            int numberOfRooms = cursor.getInt(3);

            int nights = calculateNights(checkIn, checkOut);
            if (nights <= 0) nights = 1;

            total = pricePerNight * nights * numberOfRooms;
        }

        cursor.close();
        return total;
    }

    private double getServiceTotal(SQLiteDatabase db, int bookingId) {
        double total = 0;

        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(Quantity * UnitPrice), 0) FROM BookingServices WHERE BookingId = ?",
                new String[]{String.valueOf(bookingId)}
        );

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        return total;
    }

    private int calculateNights(String checkInDate, String checkOutDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            long diff = checkOut.getTime() - checkIn.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            return 1;
        }
    }

    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis();
    }

    private String getNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}