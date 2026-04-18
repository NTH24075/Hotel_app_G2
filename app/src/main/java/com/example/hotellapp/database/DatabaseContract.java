package com.example.hotellapp.database;

public final class DatabaseContract {
    public static final String DATABASE_NAME = "hotel_app.db";
    public static final int DATABASE_VERSION = 10;

    private DatabaseContract() {
    }

    public static final class HotelTable {
        public static final String TABLE_NAME = "Hotel";
        public static final String COLUMN_ID = "HotelId";
        public static final String COLUMN_NAME = "HotelName";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String COLUMN_ADDRESS = "Address";
        public static final String COLUMN_PHONE = "Phone";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_STAR_RATING = "StarRating";
        public static final String COLUMN_CHECK_IN_TIME = "CheckInTime";
        public static final String COLUMN_CHECK_OUT_TIME = "CheckOutTime";
    }

    public static final class RoomTypesTable {
        public static final String TABLE_NAME = "RoomTypes";
        public static final String COLUMN_ID = "RoomTypeId";
        public static final String COLUMN_TYPE_NAME = "TypeName";
        public static final String COLUMN_DESCRIPTION = "Description";
        public static final String COLUMN_CAPACITY = "Capacity";
        public static final String COLUMN_BED_TYPE = "BedType";
        public static final String COLUMN_SIZE_SQM = "SizeSqm";
        public static final String COLUMN_PRICE_PER_NIGHT = "PricePerNight";
        public static final String COLUMN_AMENITIES = "Amenities";
        public static final String COLUMN_THUMBNAIL_URL = "ThumbnailUrl";
        public static final String COLUMN_IS_ACTIVE = "IsActive";
    }

    public static final class RoomsTable {
        public static final String TABLE_NAME = "Rooms";
        public static final String COLUMN_ID = "RoomId";
        public static final String COLUMN_ROOM_TYPE_ID = "RoomTypeId";
        public static final String COLUMN_ROOM_NUMBER = "RoomNumber";
        public static final String COLUMN_FLOOR_NUMBER = "FloorNumber";
        public static final String COLUMN_ROOM_STATUS = "RoomStatus";
        public static final String COLUMN_IS_ACTIVE = "IsActive";

        public static final String STATUS_AVAILABLE = "Available";
        public static final String STATUS_OCCUPIED = "Occupied";
        public static final String STATUS_CLEANING = "Cleaning";
        public static final String STATUS_MAINTENANCE = "Maintenance";
    }

    public static final class ServicesTable {
        public static final String TABLE_NAME = "Services";
        public static final String COLUMN_ID = "ServiceId";
        public static final String COLUMN_NAME = "ServiceName";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_UNIT = "UnitLabel";
        public static final String COLUMN_ICON = "IconGlyph";
        public static final String COLUMN_IS_ACTIVE = "IsActive";
    }

    public static final class ReviewsTable {
        public static final String TABLE_NAME = "Reviews";
        public static final String COLUMN_ID = "ReviewId";
        public static final String COLUMN_ROOM_TYPE_ID = "RoomTypeId";
        public static final String COLUMN_GUEST_NAME = "GuestName";
        public static final String COLUMN_GUEST_INITIALS = "GuestInitials";
        public static final String COLUMN_REVIEW_MONTH = "ReviewMonth";
        public static final String COLUMN_RATING = "Rating";
        public static final String COLUMN_CONTENT = "ReviewContent";
        public static final String COLUMN_BOOKING_CODE = "BookingCode";
    }

    public static final class UsersTable {
        public static final String TABLE_NAME = "Users";
        public static final String COLUMN_ID = "UserId";
        public static final String COLUMN_FULL_NAME = "FullName";
        public static final String COLUMN_FULL_NAME_UNSIGNED = "FullNameUnsigned";
        public static final String COLUMN_EMAIL = "Email";
        public static final String COLUMN_PHONE = "Phone";
    }

    public static final class BookingsTable {
        public static final String TABLE_NAME = "Bookings";

        public static final String COLUMN_ID = "BookingId";
        public static final String COLUMN_USER_ID = "UserId";
        public static final String COLUMN_ROOM_TYPE_ID = "RoomTypeId";
        public static final String COLUMN_BOOKING_CODE = "BookingCode";
        public static final String COLUMN_CHECK_IN_DATE = "CheckInDate";
        public static final String COLUMN_CHECK_OUT_DATE = "CheckOutDate";
        public static final String COLUMN_GUEST_COUNT = "GuestCount";
        public static final String COLUMN_NUMBER_OF_ROOMS = "NumberOfRooms";
        public static final String COLUMN_TOTAL_AMOUNT = "TotalAmount";
        public static final String COLUMN_BOOKING_STATUS = "BookingStatus";
        public static final String COLUMN_SPECIAL_REQUEST = "SpecialRequest";

        public static final String STATUS_PENDING = "Pending";
        public static final String STATUS_CONFIRMED = "Confirmed";
        public static final String STATUS_CHECKED_IN = "CheckedIn";
        public static final String STATUS_CHECKED_OUT = "CheckedOut";
        public static final String STATUS_CANCELLED = "Cancelled";
    }

    public static final class PaymentsTable {
        public static final String TABLE_NAME = "Payments";

        public static final String COLUMN_ID = "PaymentId";
        public static final String COLUMN_BOOKING_ID = "BookingId";
        public static final String COLUMN_AMOUNT = "Amount";
        public static final String COLUMN_METHOD = "PaymentMethod";
        public static final String COLUMN_STATUS = "PaymentStatus";
        public static final String COLUMN_PAID_AT = "PaidAt";
        public static final String COLUMN_NOTE = "Note";

        public static final String STATUS_UNPAID = "Unpaid";
        public static final String STATUS_PAID = "Paid";
        public static final String STATUS_REFUNDED = "Refunded";
    }

    public static final class BookingRoomAssignmentsTable {
        public static final String TABLE_NAME = "BookingRoomAssignments";

        public static final String COLUMN_ID = "AssignmentId";
        public static final String COLUMN_BOOKING_ID = "BookingId";
        public static final String COLUMN_ROOM_ID = "RoomId";
        public static final String COLUMN_ASSIGNED_AT = "AssignedAt";
        public static final String COLUMN_RELEASED_AT = "ReleasedAt";
        public static final String COLUMN_NOTE = "Note";
    }

    public static final class BookingServicesTable {
        public static final String TABLE_NAME = "BookingServices";

        public static final String COLUMN_ID = "BookingServiceId";
        public static final String COLUMN_BOOKING_ID = "BookingId";
        public static final String COLUMN_SERVICE_ID = "ServiceId";
        public static final String COLUMN_QUANTITY = "Quantity";
        public static final String COLUMN_UNIT_PRICE = "UnitPrice";
        public static final String COLUMN_USED_AT = "UsedAt";
        public static final String COLUMN_NOTE = "Note";
    }
}