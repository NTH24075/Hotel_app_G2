package com.example.hotellapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.database.DatabaseHelper;
import com.example.hotellapp.models.Booking;

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
}