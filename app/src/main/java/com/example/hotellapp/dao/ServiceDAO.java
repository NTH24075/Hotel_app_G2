package com.example.hotellapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.database.DatabaseHelper;
import com.example.hotellapp.models.BookingServiceItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ServiceDAO {

    private DatabaseHelper dbHelper;

    public ServiceDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public ArrayList<String> getAllServiceDisplayNames() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT " +
                DatabaseContract.ServicesTable.COLUMN_ICON + ", " +
                DatabaseContract.ServicesTable.COLUMN_NAME + ", " +
                DatabaseContract.ServicesTable.COLUMN_PRICE + ", " +
                DatabaseContract.ServicesTable.COLUMN_UNIT +
                " FROM " + DatabaseContract.ServicesTable.TABLE_NAME +
                " WHERE " + DatabaseContract.ServicesTable.COLUMN_IS_ACTIVE + " = 1";

        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String icon = cursor.getString(0);
            String name = cursor.getString(1);
            double price = cursor.getDouble(2);
            String unit = cursor.getString(3);

            list.add(icon + " " + name + " - " + ((long) price) + " " + unit);
        }

        cursor.close();
        db.close();
        return list;
    }

    public Cursor getAllServicesCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT " +
                DatabaseContract.ServicesTable.COLUMN_ID + ", " +
                DatabaseContract.ServicesTable.COLUMN_NAME + ", " +
                DatabaseContract.ServicesTable.COLUMN_PRICE + ", " +
                DatabaseContract.ServicesTable.COLUMN_UNIT + ", " +
                DatabaseContract.ServicesTable.COLUMN_ICON +
                " FROM " + DatabaseContract.ServicesTable.TABLE_NAME +
                " WHERE " + DatabaseContract.ServicesTable.COLUMN_IS_ACTIVE + " = 1";

        return db.rawQuery(sql, null);
    }

    public boolean addServiceToBooking(int bookingId, int serviceId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor serviceCursor = db.rawQuery(
                "SELECT " +
                        DatabaseContract.ServicesTable.COLUMN_PRICE +
                        " FROM " + DatabaseContract.ServicesTable.TABLE_NAME +
                        " WHERE " + DatabaseContract.ServicesTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(serviceId)}
        );

        if (!serviceCursor.moveToFirst()) {
            serviceCursor.close();
            db.close();
            return false;
        }

        double unitPrice = serviceCursor.getDouble(0);
        serviceCursor.close();

        Cursor bookingCursor = db.rawQuery(
                "SELECT " + DatabaseContract.BookingsTable.COLUMN_TOTAL_AMOUNT +
                        " FROM " + DatabaseContract.BookingsTable.TABLE_NAME +
                        " WHERE " + DatabaseContract.BookingsTable.COLUMN_ID + " = ?",
                new String[]{String.valueOf(bookingId)}
        );

        double oldTotal = 0;
        if (bookingCursor.moveToFirst()) {
            oldTotal = bookingCursor.getDouble(0);
        }
        bookingCursor.close();

        ContentValues bookingServiceValues = new ContentValues();
        bookingServiceValues.put(DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID, bookingId);
        bookingServiceValues.put(DatabaseContract.BookingServicesTable.COLUMN_SERVICE_ID, serviceId);
        bookingServiceValues.put(DatabaseContract.BookingServicesTable.COLUMN_QUANTITY, quantity);
        bookingServiceValues.put(DatabaseContract.BookingServicesTable.COLUMN_UNIT_PRICE, unitPrice);
        bookingServiceValues.put(DatabaseContract.BookingServicesTable.COLUMN_USED_AT,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        bookingServiceValues.put(DatabaseContract.BookingServicesTable.COLUMN_NOTE, "");

        long row = db.insert(DatabaseContract.BookingServicesTable.TABLE_NAME, null, bookingServiceValues);

        if (row > 0) {
            double newTotal = oldTotal + (unitPrice * quantity);

            ContentValues bookingValues = new ContentValues();
            bookingValues.put(DatabaseContract.BookingsTable.COLUMN_TOTAL_AMOUNT, newTotal);

            db.update(
                    DatabaseContract.BookingsTable.TABLE_NAME,
                    bookingValues,
                    DatabaseContract.BookingsTable.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(bookingId)}
            );
        }

        db.close();
        return row > 0;
    }

    public ArrayList<BookingServiceItem> getServicesByBookingId(int bookingId) {
        ArrayList<BookingServiceItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "SELECT bs." + DatabaseContract.BookingServicesTable.COLUMN_ID + ", " +
                "bs." + DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID + ", " +
                "bs." + DatabaseContract.BookingServicesTable.COLUMN_SERVICE_ID + ", " +
                "s." + DatabaseContract.ServicesTable.COLUMN_NAME + ", " +
                "s." + DatabaseContract.ServicesTable.COLUMN_ICON + ", " +
                "s." + DatabaseContract.ServicesTable.COLUMN_UNIT + ", " +
                "bs." + DatabaseContract.BookingServicesTable.COLUMN_QUANTITY + ", " +
                "bs." + DatabaseContract.BookingServicesTable.COLUMN_UNIT_PRICE + ", " +
                "bs." + DatabaseContract.BookingServicesTable.COLUMN_USED_AT + " " +
                "FROM " + DatabaseContract.BookingServicesTable.TABLE_NAME + " bs " +
                "INNER JOIN " + DatabaseContract.ServicesTable.TABLE_NAME + " s " +
                "ON bs." + DatabaseContract.BookingServicesTable.COLUMN_SERVICE_ID + " = s." + DatabaseContract.ServicesTable.COLUMN_ID + " " +
                "WHERE bs." + DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID + " = ? " +
                "ORDER BY bs." + DatabaseContract.BookingServicesTable.COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(bookingId)});

        while (cursor.moveToNext()) {
            BookingServiceItem item = new BookingServiceItem();
            item.setBookingServiceId(cursor.getInt(0));
            item.setBookingId(cursor.getInt(1));
            item.setServiceId(cursor.getInt(2));
            item.setServiceName(cursor.getString(3));
            item.setServiceIcon(cursor.getString(4));
            item.setUnitLabel(cursor.getString(5));
            item.setQuantity(cursor.getInt(6));
            item.setUnitPrice(cursor.getDouble(7));
            item.setUsedAt(cursor.getString(8));
            item.setTotalPrice(item.getQuantity() * item.getUnitPrice());
            list.add(item);
        }

        cursor.close();
        db.close();
        return list;
    }
}