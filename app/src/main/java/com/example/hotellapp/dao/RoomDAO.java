package com.example.hotellapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hotellapp.database.DatabaseContract;
import com.example.hotellapp.database.DatabaseHelper;
import com.example.hotellapp.models.Room;
import com.example.hotellapp.models.RoomType;

import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final DatabaseHelper databaseHelper;

    public RoomDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    public List<RoomType> getRoomTypes() {
        List<RoomType> roomTypes = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT RoomTypeId, TypeName, Capacity, PricePerNight, ThumbnailUrl " +
                "FROM RoomTypes WHERE IsActive = 1 ORDER BY PricePerNight ASC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor != null && cursor.moveToNext()) {
                roomTypes.add(new RoomType(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                ));
            }
        } catch (Exception e) {
            Log.e("RoomDAO", "Lỗi lấy danh sách hạng phòng", e);
        }
        return roomTypes;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query =
                "SELECT r.RoomId, r.RoomTypeId, r.RoomNumber, r.FloorNumber, r.RoomStatus, " +
                        "rt.TypeName, rt.BedType, rt.PricePerNight, rt.SizeSqm, rt.Capacity, rt.Amenities, rt.ThumbnailUrl " +
                        "FROM Rooms r " +
                        "JOIN RoomTypes rt ON r.RoomTypeId = rt.RoomTypeId " +
                        "WHERE r.IsActive = 1 AND rt.IsActive = 1 " +
                        "ORDER BY CASE r.RoomStatus " +
                        "   WHEN 'Available' THEN 1 " +
                        "   WHEN 'Occupied' THEN 2 " +
                        "   WHEN 'Cleaning' THEN 3 " +
                        "   WHEN 'Maintenance' THEN 4 " +
                        "   ELSE 5 END, " +
                        "r.RoomNumber ASC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor != null && cursor.moveToNext()) {
                rooms.add(new Room(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getDouble(7),
                        cursor.getDouble(8),
                        cursor.getInt(9),
                        cursor.getString(10),
                        cursor.getString(11)
                ));
            }
        } catch (Exception e) {
            Log.e("RoomDAO", "Lỗi lấy danh sách phòng", e);
        }

        return rooms;
    }

    public List<Room> getRoomsByFilters(int roomTypeId) {
        List<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT r.RoomId, r.RoomTypeId, r.RoomNumber, r.FloorNumber, r.RoomStatus, " +
                        "rt.TypeName, rt.BedType, rt.PricePerNight, rt.SizeSqm, rt.Capacity, rt.Amenities, rt.ThumbnailUrl " +
                        "FROM Rooms r " +
                        "JOIN RoomTypes rt ON r.RoomTypeId = rt.RoomTypeId " +
                        "WHERE r.IsActive = 1 AND rt.IsActive = 1 "
        );

        List<String> args = new ArrayList<>();
        if (roomTypeId > 0) {
            queryBuilder.append("AND r.RoomTypeId = ? ");
            args.add(String.valueOf(roomTypeId));
        }

        queryBuilder.append("ORDER BY r.RoomNumber ASC");

        try (Cursor cursor = db.rawQuery(queryBuilder.toString(), args.isEmpty() ? null : args.toArray(new String[0]))) {
            while (cursor != null && cursor.moveToNext()) {
                rooms.add(new Room(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getDouble(7),
                        cursor.getDouble(8),
                        cursor.getInt(9),
                        cursor.getString(10),
                        cursor.getString(11)
                ));
            }
        } catch (Exception e) {
            Log.e("RoomDAO", "Lỗi lấy danh sách phòng", e);
        }
        return rooms;
    }

    public List<Room> getFeaturedRooms() {
        List<Room> all = getAllRooms();
        if (all.size() > 5) {
            return all.subList(0, 5);
        }
        return all;
    }
    public List<Room> getRoomsByStatus(String status) {
        List<Room> rooms = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query =
                "SELECT r.RoomId, r.RoomTypeId, r.RoomNumber, r.FloorNumber, r.RoomStatus, " +
                        "rt.TypeName, rt.BedType, rt.PricePerNight, rt.SizeSqm, rt.Capacity, rt.Amenities, rt.ThumbnailUrl " +
                        "FROM Rooms r " +
                        "JOIN RoomTypes rt ON r.RoomTypeId = rt.RoomTypeId " +
                        "WHERE r.IsActive = 1 AND rt.IsActive = 1 AND r.RoomStatus = ? " +
                        "ORDER BY r.RoomNumber ASC";

        try (Cursor cursor = db.rawQuery(query, new String[]{status})) {
            while (cursor != null && cursor.moveToNext()) {
                rooms.add(new Room(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getDouble(7),
                        cursor.getDouble(8),
                        cursor.getInt(9),
                        cursor.getString(10),
                        cursor.getString(11)
                ));
            }
        } catch (Exception e) {
            Log.e("RoomDAO", "Lỗi lấy phòng theo trạng thái", e);
        }

        return rooms;
    }
    public int countRoomsByStatus(String status) {
        int count = 0;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM Rooms WHERE IsActive = 1 AND RoomStatus = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{status})) {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("RoomDAO", "Lỗi đếm phòng theo trạng thái", e);
        }

        return count;
    }
    public boolean markRoomAsAvailable(int roomId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        try {
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(DatabaseContract.RoomsTable.COLUMN_ROOM_STATUS,
                    DatabaseContract.RoomsTable.STATUS_AVAILABLE);

            int rows = db.update(
                    DatabaseContract.RoomsTable.TABLE_NAME,
                    values,
                    DatabaseContract.RoomsTable.COLUMN_ID + " = ? AND " +
                            DatabaseContract.RoomsTable.COLUMN_ROOM_STATUS + " = ?",
                    new String[]{
                            String.valueOf(roomId),
                            DatabaseContract.RoomsTable.STATUS_CLEANING
                    }
            );

            return rows > 0;
        } catch (Exception e) {
            Log.e("RoomDAO", "Lỗi cập nhật Cleaning -> Available", e);
            return false;
        } finally {
            db.close();
        }
    }
}
