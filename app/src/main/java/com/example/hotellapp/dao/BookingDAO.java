package com.example.hotellapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotellapp.database.DatabaseHelper;
import com.example.hotellapp.models.Booking;

import java.util.ArrayList;

public class BookingDAO {

    private DatabaseHelper dbHelper;

    public BookingDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT b.BookingId, b.BookingCode, u.FullName, rt.TypeName, " +
                "b.CheckInDate, b.CheckOutDate, b.BookingStatus, p.PaymentStatus " +
                "FROM Bookings b " +
                "INNER JOIN Users u ON b.UserId = u.UserId " +
                "INNER JOIN RoomTypes rt ON b.RoomTypeId = rt.RoomTypeId " +
                "LEFT JOIN Payments p ON b.BookingId = p.BookingId " +
                "ORDER BY b.BookingId DESC";

        Cursor cursor = db.rawQuery(sql, null);

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

        cursor.close();
        db.close();
        return list;
    }

    public int countCheckedInBookings() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Bookings WHERE BookingStatus = 'CheckedIn'", null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }
}