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

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.UsersTable.TABLE_NAME + " (" +
                        DatabaseContract.UsersTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.UsersTable.COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                        DatabaseContract.UsersTable.COLUMN_FULL_NAME_UNSIGNED + " TEXT NOT NULL, " +
                        DatabaseContract.UsersTable.COLUMN_EMAIL + " TEXT, " +
                        DatabaseContract.UsersTable.COLUMN_PHONE + " TEXT" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.BookingsTable.TABLE_NAME + " (" +
                        DatabaseContract.BookingsTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.BookingsTable.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.BookingsTable.COLUMN_BOOKING_CODE + " TEXT NOT NULL UNIQUE, " +
                        DatabaseContract.BookingsTable.COLUMN_CHECK_IN_DATE + " TEXT NOT NULL, " +
                        DatabaseContract.BookingsTable.COLUMN_CHECK_OUT_DATE + " TEXT NOT NULL, " +
                        DatabaseContract.BookingsTable.COLUMN_GUEST_COUNT + " INTEGER NOT NULL DEFAULT 1, " +
                        DatabaseContract.BookingsTable.COLUMN_NUMBER_OF_ROOMS + " INTEGER NOT NULL DEFAULT 1, " +
                        DatabaseContract.BookingsTable.COLUMN_TOTAL_AMOUNT + " REAL NOT NULL DEFAULT 0, " +
                        DatabaseContract.BookingsTable.COLUMN_BOOKING_STATUS + " TEXT NOT NULL DEFAULT '" + DatabaseContract.BookingsTable.STATUS_PENDING + "', " +
                        DatabaseContract.BookingsTable.COLUMN_SPECIAL_REQUEST + " TEXT, " +
                        "FOREIGN KEY(" + DatabaseContract.BookingsTable.COLUMN_USER_ID + ") REFERENCES " +
                        DatabaseContract.UsersTable.TABLE_NAME + "(" + DatabaseContract.UsersTable.COLUMN_ID + "), " +
                        "FOREIGN KEY(" + DatabaseContract.BookingsTable.COLUMN_ROOM_TYPE_ID + ") REFERENCES " +
                        DatabaseContract.RoomTypesTable.TABLE_NAME + "(" + DatabaseContract.RoomTypesTable.COLUMN_ID + ")" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.BookingRoomAssignmentsTable.TABLE_NAME + " (" +
                        DatabaseContract.BookingRoomAssignmentsTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.BookingRoomAssignmentsTable.COLUMN_BOOKING_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.BookingRoomAssignmentsTable.COLUMN_ROOM_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.BookingRoomAssignmentsTable.COLUMN_ASSIGNED_AT + " TEXT, " +
                        DatabaseContract.BookingRoomAssignmentsTable.COLUMN_RELEASED_AT + " TEXT, " +
                        DatabaseContract.BookingRoomAssignmentsTable.COLUMN_NOTE + " TEXT, " +
                        "FOREIGN KEY(" + DatabaseContract.BookingRoomAssignmentsTable.COLUMN_BOOKING_ID + ") REFERENCES " +
                        DatabaseContract.BookingsTable.TABLE_NAME + "(" + DatabaseContract.BookingsTable.COLUMN_ID + "), " +
                        "FOREIGN KEY(" + DatabaseContract.BookingRoomAssignmentsTable.COLUMN_ROOM_ID + ") REFERENCES " +
                        DatabaseContract.RoomsTable.TABLE_NAME + "(" + DatabaseContract.RoomsTable.COLUMN_ID + ")" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.PaymentsTable.TABLE_NAME + " (" +
                        DatabaseContract.PaymentsTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + " INTEGER NOT NULL UNIQUE, " +
                        DatabaseContract.PaymentsTable.COLUMN_AMOUNT + " REAL NOT NULL, " +
                        DatabaseContract.PaymentsTable.COLUMN_METHOD + " TEXT NOT NULL, " +
                        DatabaseContract.PaymentsTable.COLUMN_STATUS + " TEXT NOT NULL DEFAULT '" + DatabaseContract.PaymentsTable.STATUS_UNPAID + "', " +
                        DatabaseContract.PaymentsTable.COLUMN_PAID_AT + " TEXT, " +
                        DatabaseContract.PaymentsTable.COLUMN_NOTE + " TEXT, " +
                        "FOREIGN KEY(" + DatabaseContract.PaymentsTable.COLUMN_BOOKING_ID + ") REFERENCES " +
                        DatabaseContract.BookingsTable.TABLE_NAME + "(" + DatabaseContract.BookingsTable.COLUMN_ID + ")" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE " + DatabaseContract.BookingServicesTable.TABLE_NAME + " (" +
                        DatabaseContract.BookingServicesTable.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.BookingServicesTable.COLUMN_SERVICE_ID + " INTEGER NOT NULL, " +
                        DatabaseContract.BookingServicesTable.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1, " +
                        DatabaseContract.BookingServicesTable.COLUMN_UNIT_PRICE + " REAL NOT NULL, " +
                        DatabaseContract.BookingServicesTable.COLUMN_USED_AT + " TEXT, " +
                        DatabaseContract.BookingServicesTable.COLUMN_NOTE + " TEXT, " +
                        "FOREIGN KEY(" + DatabaseContract.BookingServicesTable.COLUMN_BOOKING_ID + ") REFERENCES " +
                        DatabaseContract.BookingsTable.TABLE_NAME + "(" + DatabaseContract.BookingsTable.COLUMN_ID + "), " +
                        "FOREIGN KEY(" + DatabaseContract.BookingServicesTable.COLUMN_SERVICE_ID + ") REFERENCES " +
                        DatabaseContract.ServicesTable.TABLE_NAME + "(" + DatabaseContract.ServicesTable.COLUMN_ID + ")" +
                        ")"
        );

        seedHotel(db);
        seedRoomTypes(db);
        seedRooms(db);
        seedUsers(db);
        seedServices(db);
        seedBookings(db);
        seedBookingRoomAssignments(db);
        seedPayments(db);
        seedReviews(db);
        seedBookingServices(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.BookingServicesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.PaymentsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.BookingRoomAssignmentsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.BookingsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ServicesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RoomsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.RoomTypesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UsersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.HotelTable.TABLE_NAME);
        onCreate(db);
    }

    private void seedHotel(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Hotel VALUES (1,'Grand Palace Hotel','Khách sạn 4 sao sang trọng tại trung tâm thành phố Hồ Chí Minh.','123 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh','028-3800-1234','info@grandpalace.vn',4,'14:00:00','12:00:00')");
    }

    private void seedRoomTypes(SQLiteDatabase db) {
        db.execSQL("INSERT INTO RoomTypes VALUES (1,'Standard Single','Phòng tiêu chuẩn đơn, đầy đủ tiện nghi cơ bản.',1,'1 giường đơn',20,600000,'WiFi miễn phí, TV 32 inch, Điều hòa, Minibar','https://images.unsplash.com/photo-1505691938895-1758d7feb511?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (2,'Standard Double','Phòng tiêu chuẩn đôi cho cặp đôi.',2,'1 giường đôi',25,800000,'WiFi miễn phí, TV 40 inch, Điều hòa, Minibar','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (3,'Deluxe City View','Phòng Deluxe hướng thành phố hiện đại.',2,'1 giường đôi lớn',35,1500000,'WiFi miễn phí, TV 4K, Bồn tắm, View thành phố','https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (4,'Deluxe Garden View','Không gian xanh mát hướng ra khu vườn.',2,'1 giường đôi lớn',38,1600000,'WiFi miễn phí, TV 4K, Bồn tắm, View vườn','https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (5,'Deluxe Twin','Phòng 2 giường đơn tiện lợi.',2,'2 giường đơn',38,1700000,'WiFi miễn phí, TV 4K, 2 giường đơn','https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (6,'Junior Suite','Phòng Suite nhỏ với khu vực tiếp khách.',3,'1 giường đôi lớn + 1 sofa',50,2800000,'WiFi miễn phí, Sofa, Bồn tắm massage','https://images.unsplash.com/photo-1590490360182-c33d57733427?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (7,'Executive Suite','Suite cao cấp tầng cao nhất.',4,'2 giường đôi lớn',75,4500000,'WiFi fiber, Phòng khách riêng, Jacuzzi','https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (8,'Studio Apartment','Phòng dạng căn hộ có bếp nhỏ.',2,'1 giường đôi lớn',45,2200000,'WiFi, Bếp, Tủ lạnh lớn, Máy giặt','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=600',1)");
        db.execSQL("INSERT INTO RoomTypes VALUES (9,'Presidential Suite','Căn hộ Tổng thống xa hoa bậc nhất.',4,'2 giường King size',150,15000000,'Quản gia 24/7, Hồ bơi riêng, View 360 độ','https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=600',1)");
    }

    private void seedRooms(SQLiteDatabase db) {
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= 5; j++) {
                int roomNum = i * 100 + j;
                String status;

                if (j == 1 || j == 2) {
                    status = DatabaseContract.RoomsTable.STATUS_AVAILABLE;
                } else if (j == 3) {
                    status = DatabaseContract.RoomsTable.STATUS_OCCUPIED;
                } else if (j == 4) {
                    status = DatabaseContract.RoomsTable.STATUS_CLEANING;
                } else {
                    status = DatabaseContract.RoomsTable.STATUS_MAINTENANCE;
                }

                db.execSQL("INSERT INTO Rooms (RoomTypeId, RoomNumber, FloorNumber, RoomStatus, IsActive) " +
                        "VALUES (" + i + ", '" + roomNum + "', " + i + ", '" + status + "', 1)");
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

    private void seedUsers(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + DatabaseContract.UsersTable.TABLE_NAME + " VALUES " +
                "(1,'Lê Văn Khách','Le Van Khach','khach1@gmail.com','0901000003')");
        db.execSQL("INSERT INTO " + DatabaseContract.UsersTable.TABLE_NAME + " VALUES " +
                "(2,'Phạm Thị Hoa','Pham Thi Hoa','hoa@gmail.com','0901000004')");
        db.execSQL("INSERT INTO " + DatabaseContract.UsersTable.TABLE_NAME + " VALUES " +
                "(3,'Nguyễn Văn Guest','Nguyen Van Guest','guest@gmail.com','0987654321')");
        db.execSQL("INSERT INTO " + DatabaseContract.UsersTable.TABLE_NAME + " VALUES " +
                "(4,'Kiên Nhân','Kien Nhan','nhan@gmail.com','0977000000')");
    }

    private void seedBookings(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + DatabaseContract.BookingsTable.TABLE_NAME + " VALUES " +
                "(1,1,2,'BK20260501001','2026-05-10','2026-05-12',2,1,1600000,'" +
                DatabaseContract.BookingsTable.STATUS_CONFIRMED + "','Cần thêm gối')");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingsTable.TABLE_NAME + " VALUES " +
                "(2,1,3,'BK20260501002','2026-05-20','2026-05-23',2,1,4500000,'" +
                DatabaseContract.BookingsTable.STATUS_PENDING + "',NULL)");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingsTable.TABLE_NAME + " VALUES " +
                "(3,2,7,'BK20260501003','2026-05-05','2026-05-07',4,1,5000000,'" +
                DatabaseContract.BookingsTable.STATUS_CHECKED_IN + "','Đón sân bay lúc 14h')");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingsTable.TABLE_NAME + " VALUES " +
                "(4,2,2,'BK20260401004','2026-04-01','2026-04-03',1,1,1600000,'" +
                DatabaseContract.BookingsTable.STATUS_CHECKED_OUT + "',NULL)");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingsTable.TABLE_NAME + " VALUES " +
                "(5,3,9,'BK20260501005','2026-05-15','2026-05-18',2,1,45000000,'" +
                DatabaseContract.BookingsTable.STATUS_PENDING + "','Trang trí phòng dịp sinh nhật')");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingsTable.TABLE_NAME + " VALUES " +
                "(6,4,5,'BK20260501006','2026-05-25','2026-05-27',2,2,6800000,'" +
                DatabaseContract.BookingsTable.STATUS_PENDING + "',NULL)");
    }

    private void seedBookingRoomAssignments(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + DatabaseContract.BookingRoomAssignmentsTable.TABLE_NAME + " VALUES " +
                "(1,1,201,'2026-05-10 13:00:00',NULL,'Phòng đã cấp cho khách')");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingRoomAssignmentsTable.TABLE_NAME + " VALUES " +
                "(2,3,701,'2026-05-05 13:30:00',NULL,'Booking đang ở')");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingRoomAssignmentsTable.TABLE_NAME + " VALUES " +
                "(3,4,202,'2026-04-01 12:00:00','2026-04-03 11:30:00','Booking đã check-out')");
    }

    private void seedPayments(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + DatabaseContract.PaymentsTable.TABLE_NAME + " VALUES " +
                "(1,1,1600000,'BankTransfer','" + DatabaseContract.PaymentsTable.STATUS_PAID + "','2026-04-20 10:30:00','Thanh toán online')");

        db.execSQL("INSERT INTO " + DatabaseContract.PaymentsTable.TABLE_NAME + " VALUES " +
                "(2,2,4500000,'Cash','" + DatabaseContract.PaymentsTable.STATUS_UNPAID + "',NULL,NULL)");

        db.execSQL("INSERT INTO " + DatabaseContract.PaymentsTable.TABLE_NAME + " VALUES " +
                "(3,3,5000000,'MoMo','" + DatabaseContract.PaymentsTable.STATUS_PAID + "','2026-05-05 14:00:00','Thanh toán qua MoMo')");

        db.execSQL("INSERT INTO " + DatabaseContract.PaymentsTable.TABLE_NAME + " VALUES " +
                "(4,4,1600000,'Cash','" + DatabaseContract.PaymentsTable.STATUS_PAID + "','2026-04-01 13:00:00',NULL)");

        db.execSQL("INSERT INTO " + DatabaseContract.PaymentsTable.TABLE_NAME + " VALUES " +
                "(5,5,45000000,'BankTransfer','" + DatabaseContract.PaymentsTable.STATUS_UNPAID + "',NULL,'Chưa thanh toán')");

        db.execSQL("INSERT INTO " + DatabaseContract.PaymentsTable.TABLE_NAME + " VALUES " +
                "(6,6,6800000,'Cash','" + DatabaseContract.PaymentsTable.STATUS_UNPAID + "',NULL,NULL)");
    }

    private void seedBookingServices(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + DatabaseContract.BookingServicesTable.TABLE_NAME + " VALUES " +
                "(1,3,1,2,150000,'2026-05-05 08:00:00','Ăn sáng cho 2 người')");

        db.execSQL("INSERT INTO " + DatabaseContract.BookingServicesTable.TABLE_NAME + " VALUES " +
                "(2,3,3,1,350000,'2026-05-05 14:00:00','Đón sân bay')");
    }
}