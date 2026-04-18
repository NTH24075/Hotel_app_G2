package com.example.hotellapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + DatabaseContract.HotelTable.TABLE_NAME + " (" +
                        DatabaseContract.HotelTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.HotelTable.COLUMN_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.HotelTable.COLUMN_DESCRIPTION + " TEXT, " +
                        DatabaseContract.HotelTable.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                        DatabaseContract.HotelTable.COLUMN_PHONE + " TEXT, " +
                        DatabaseContract.HotelTable.COLUMN_EMAIL + " TEXT, " +
                        DatabaseContract.HotelTable.COLUMN_STAR_RATING + " INTEGER NOT NULL DEFAULT 3, " +
                        DatabaseContract.HotelTable.COLUMN_CHECK_IN_TIME + " TEXT, " +
                        DatabaseContract.HotelTable.COLUMN_CHECK_OUT_TIME + " TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.RoomTypesTable.TABLE_NAME + " (" +
                        DatabaseContract.RoomTypesTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.RoomTypesTable.COLUMN_TYPE_NAME + " TEXT NOT NULL UNIQUE, " +
                        DatabaseContract.RoomTypesTable.COLUMN_DESCRIPTION + " TEXT, " +
                        DatabaseContract.RoomTypesTable.COLUMN_CAPACITY + " INTEGER NOT NULL DEFAULT 2, " +
                        DatabaseContract.RoomTypesTable.COLUMN_BED_TYPE + " TEXT, " +
                        DatabaseContract.RoomTypesTable.COLUMN_SIZE_SQM + " REAL, " +
                        DatabaseContract.RoomTypesTable.COLUMN_PRICE_PER_NIGHT + " REAL NOT NULL, " +
                        DatabaseContract.RoomTypesTable.COLUMN_AMENITIES + " TEXT, " +
                        DatabaseContract.RoomTypesTable.COLUMN_THUMBNAIL_URL + " TEXT, " +
                        DatabaseContract.RoomTypesTable.COLUMN_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.RoomsTable.TABLE_NAME + " (" +
                        DatabaseContract.RoomsTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.RoomsTable.COLUMN_ROOM_TYPE_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.RoomsTable.COLUMN_ROOM_NUMBER + " TEXT NOT NULL UNIQUE, " +
                        DatabaseContract.RoomsTable.COLUMN_FLOOR_NUMBER + " INTEGER, " +
                        DatabaseContract.RoomsTable.COLUMN_ROOM_STATUS + " TEXT NOT NULL DEFAULT '" + DatabaseContract.RoomsTable.STATUS_AVAILABLE + "', " +
                        DatabaseContract.RoomsTable.COLUMN_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1, " +
                        "FOREIGN KEY(" + DatabaseContract.RoomsTable.COLUMN_ROOM_TYPE_ID + ") REFERENCES " +
                        DatabaseContract.RoomTypesTable.TABLE_NAME + "(" + DatabaseContract.RoomTypesTable.COLUMN_ID + ")" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.ServicesTable.TABLE_NAME + " (" +
                        DatabaseContract.ServicesTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.ServicesTable.COLUMN_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.ServicesTable.COLUMN_PRICE + " REAL NOT NULL, " +
                        DatabaseContract.ServicesTable.COLUMN_UNIT + " TEXT NOT NULL, " +
                        DatabaseContract.ServicesTable.COLUMN_ICON + " TEXT NOT NULL, " +
                        DatabaseContract.ServicesTable.COLUMN_IS_ACTIVE + " INTEGER NOT NULL DEFAULT 1" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.ReviewsTable.TABLE_NAME + " (" +
                        DatabaseContract.ReviewsTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.ReviewsTable.COLUMN_ROOM_TYPE_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.ReviewsTable.COLUMN_GUEST_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.ReviewsTable.COLUMN_GUEST_INITIALS + " TEXT NOT NULL, " +
                        DatabaseContract.ReviewsTable.COLUMN_REVIEW_MONTH + " TEXT NOT NULL, " +
                        DatabaseContract.ReviewsTable.COLUMN_RATING + " INTEGER NOT NULL, " +
                        DatabaseContract.ReviewsTable.COLUMN_CONTENT + " TEXT NOT NULL, " +
                        DatabaseContract.ReviewsTable.COLUMN_BOOKING_CODE + " TEXT NOT NULL, " +
                        "FOREIGN KEY(" + DatabaseContract.ReviewsTable.COLUMN_ROOM_TYPE_ID + ") REFERENCES " +
                        DatabaseContract.RoomTypesTable.TABLE_NAME + "(" + DatabaseContract.RoomTypesTable.COLUMN_ID + ")" +
                        ")"
        );

        seedHotel(db);
        seedRoomTypes(db);
        seedRooms(db);
        seedServices(db);
        seedReviews(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ServicesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RoomsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RoomTypesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.HotelTable.TABLE_NAME);
        onCreate(db);
    }

    private void seedHotel(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Hotel VALUES (1,'Grand Palace Hotel','Khách sạn 4 sao sang trọng tại trung tâm thành phố Hồ Chí Minh.','123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh','028-3800-1234','info@grandpalace.vn',4,'14:00:00','12:00:00')");
    }

    private void seedRoomTypes(SQLiteDatabase db) {
        // Standard variants
        db.execSQL("INSERT INTO RoomTypes VALUES (1,'Standard Single','Phòng tiêu chuẩn đơn, đầy đủ tiện nghi cơ bản.',1,'1 giường đơn',20,600000,'WiFi miễn phí, TV 32 inch, Điều hòa, Minibar','https://images.unsplash.com/photo-1505691938895-1758d7feb511?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (2,'Standard Double','Phòng tiêu chuẩn đôi cho cặp đôi.',2,'1 giường đôi',25,800000,'WiFi miễn phí, TV 40 inch, Điều hòa, Minibar','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600',1)");
        
        // Deluxe variants
        db.execSQL("INSERT INTO RoomTypes VALUES (3,'Deluxe City View','Phòng Deluxe hướng thành phố hiện đại.',2,'1 giường đôi lớn',35,1500000,'WiFi miễn phí, TV 4K, Bồn tắm, View thành phố','https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (4,'Deluxe Garden View','Không gian xanh mát hướng ra khu vườn.',2,'1 giường đôi lớn',38,1600000,'WiFi miễn phí, TV 4K, Bồn tắm, View vườn','https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (5,'Deluxe Twin','Phòng 2 giường đơn tiện lợi.',2,'2 giường đơn',38,1700000,'WiFi miễn phí, TV 4K, 2 giường đơn','https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600',1)");
        
        // Suite variants
        db.execSQL("INSERT INTO RoomTypes VALUES (6,'Junior Suite','Phòng Suite nhỏ với khu vực tiếp khách.',3,'1 giường đôi lớn + 1 sofa',50,2800000,'WiFi miễn phí, Sofa, Bồn tắm massage','https://images.unsplash.com/photo-1590490360182-c33d57733427?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (7,'Executive Suite','Suite cao cấp tầng cao nhất.',4,'2 giường đôi lớn',75,4500000,'WiFi fiber, Phòng khách riêng, Jacuzzi','https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600',1)");
        
        // Premium variants
        db.execSQL("INSERT INTO RoomTypes VALUES (8,'Studio Apartment','Phòng dạng căn hộ có bếp nhỏ.',2,'1 giường đôi lớn',45,2200000,'WiFi, Bếp, Tủ lạnh lớn, Máy giặt','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (9,'Presidential Suite','Căn hộ Tổng thống xa hoa bậc nhất.',4,'2 giường King size',150,15000000,'Quản gia 24/7, Hồ bơi riêng, View 360 độ','https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=600',1)");
    }

    private void seedRooms(SQLiteDatabase db) {
        // Nạp nhiều phòng vật lý cho mỗi loại để tìm kiếm luôn có kết quả
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 5; j++) {
                int roomNum = i * 100 + j;
                db.execSQL("INSERT INTO Rooms (RoomTypeId, RoomNumber, FloorNumber, RoomStatus, IsActive) " +
                        "VALUES (" + i + ", '" + roomNum + "', " + i + ", 'Available', 1)");
            }
        }
    }

    private void seedServices(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Services VALUES (1,'Ăn sáng buffet',150000,'/ người / lần','🍳',1)");
        db.execSQL("INSERT INTO Services VALUES (2,'Spa và Massage',500000,'/ giờ','💆',1)");
        db.execSQL("INSERT INTO Services VALUES (3,'Xe sân bay',350000,'/ lượt','🚗',1)");
        db.execSQL("INSERT INTO Services VALUES (4,'Hồ bơi',100000,'/ ngày','🏊',1)");
        db.execSQL("INSERT INTO Services VALUES (5,'Phòng gym',100000,'/ ngày','🏋',1)");
        db.execSQL("INSERT INTO Services VALUES (6,'Nhà hàng',1000000,'/ buổi','🍽',1)");
        db.execSQL("INSERT INTO Services VALUES (7,'Giặt ủi',80000,'/ kg','👕',1)");
        db.execSQL("INSERT INTO Services VALUES (8,'Wi-Fi cao cấp',50000,'/ ngày','📶',1)");
    }

    private void seedReviews(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Reviews VALUES (1,2,'Phạm Thị Hoa','PH','Tháng 4/2026',4,'Phòng rộng và tiện nghi, dịch vụ tốt. Nhân viên thân thiện, hỗ trợ đón sân bay rất chu đáo.','BK20260501003')");
        db.execSQL("INSERT INTO Reviews VALUES (2,2,'Lê Văn Khách','LK','Tháng 4/2026',5,'Phòng sạch sẽ, nhân viên thân thiện. Check-in nhanh, view thành phố đẹp về đêm.','BK20260401004')");
        db.execSQL("INSERT INTO Reviews VALUES (3,7,'Trần Minh Anh','TA','Tháng 3/2026',5,'Suite rất đáng tiền, không gian yên tĩnh và jacuzzi hoạt động tốt.','BK20260322007')");
    }
}
