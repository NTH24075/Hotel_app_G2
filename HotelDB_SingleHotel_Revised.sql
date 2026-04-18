USE master;
GO

IF DB_ID(N'HotelManagementDB') IS NOT NULL
BEGIN
    ALTER DATABASE HotelManagementDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE HotelManagementDB;
END
GO

CREATE DATABASE HotelManagementDB;
GO

USE HotelManagementDB;
GO

/*
    Revised schema for ONE hotel only.
    Key changes vs. old version:
    1) Remove multi-hotel model.
    2) Split room type and physical room into 2 tables:
       - RoomTypes: each row = one room category/type
       - Rooms: each row = one physical room
    3) Bookings now book a room type and quantity.
    4) BookingRoomAssignments stores actual physical rooms assigned to a booking.

    Scope of this file:
    - DATABASE / TABLES / INDEXES / SEED DATA only
    - No procedures, functions, or views are recreated here.
*/

-- ============================================================
-- 1. ROLES
-- ============================================================
CREATE TABLE Roles (
    RoleId       INT IDENTITY(1,1) PRIMARY KEY,
    RoleName     NVARCHAR(50)  NOT NULL UNIQUE,
    Description  NVARCHAR(255) NULL
);
GO

INSERT INTO Roles (RoleName, Description) VALUES
    (N'Admin',        N'Quan tri he thong'),
    (N'Receptionist', N'Le tan khach san'),
    (N'Guest',        N'Khach dat phong');
GO

-- ============================================================
-- 2. USERS
-- ============================================================
CREATE TABLE Users (
    UserId        INT IDENTITY(1,1) PRIMARY KEY,
    RoleId        INT            NOT NULL,
    FullName      NVARCHAR(100)  NOT NULL,
    Email         VARCHAR(100)   NOT NULL,
    Phone         VARCHAR(20)    NULL,
    PasswordHash  VARCHAR(255)   NOT NULL,
    CitizenId     VARCHAR(30)    NULL,
    Address       NVARCHAR(255)  NULL,
    AvatarUrl     VARCHAR(255)   NULL,
    GoogleSub     VARCHAR(100)   NULL,
    FacebookId    VARCHAR(100)   NULL,
    Status        VARCHAR(20)    NOT NULL CONSTRAINT DF_Users_Status DEFAULT 'Active',
    CreatedAt     DATETIME       NOT NULL CONSTRAINT DF_Users_CreatedAt DEFAULT GETDATE(),
    UpdatedAt     DATETIME       NULL,

    CONSTRAINT FK_Users_Roles FOREIGN KEY (RoleId) REFERENCES Roles(RoleId),
    CONSTRAINT UQ_Users_Email UNIQUE (Email),
    CONSTRAINT CK_Users_Status CHECK (Status IN ('Active','Inactive','Blocked','Deleted'))
);
GO

CREATE UNIQUE INDEX UX_Users_GoogleSub ON Users(GoogleSub) WHERE GoogleSub IS NOT NULL;
GO
CREATE UNIQUE INDEX UX_Users_FacebookId ON Users(FacebookId) WHERE FacebookId IS NOT NULL;
GO
CREATE INDEX IX_Users_Email ON Users(Email);
GO
CREATE INDEX IX_Users_RoleId ON Users(RoleId);
GO

DECLARE @AdminRoleId INT, @ReceptionistRoleId INT, @GuestRoleId INT;
SELECT @AdminRoleId = RoleId FROM Roles WHERE RoleName = N'Admin';
SELECT @ReceptionistRoleId = RoleId FROM Roles WHERE RoleName = N'Receptionist';
SELECT @GuestRoleId = RoleId FROM Roles WHERE RoleName = N'Guest';

INSERT INTO Users (RoleId, FullName, Email, Phone, PasswordHash, CitizenId, Address, Status) VALUES
(@AdminRoleId,        N'Admin He Thong',  'admin@hotel.com', '0901000001', '12345678', '001234567890', N'Ha Noi',         'Active'),
(@ReceptionistRoleId, N'Tran Thi Le Tan', 'letan@hotel.com', '0901000002', '12345678', '012345678901', N'Ha Noi',         'Active'),
(@GuestRoleId,        N'Le Van Khach',    'khach1@gmail.com','0901000003', '12345678', '123456789012', N'TP Ho Chi Minh', 'Active'),
(@GuestRoleId,        N'Pham Thi Hoa',    'hoa@gmail.com',   '0901000004', '12345678', '234567890123', N'Da Nang',        'Active'),
(@GuestRoleId,        N'Nguyen Van Guest','guest@gmail.com', '0987654321', '12345678', '345678901234', N'TP Ho Chi Minh', 'Active'),
(@GuestRoleId,        N'Kien Nhan',       'nhan@gmail.com',  '0977000000', '12345678', '456789012345', N'Ha Noi',         'Active');
GO

-- ============================================================
-- 3. HOTEL
--    One application manages exactly one hotel.
-- ============================================================
CREATE TABLE Hotel (
    HotelId        INT IDENTITY(1,1) PRIMARY KEY,
    HotelName      NVARCHAR(200) NOT NULL,
    Description    NVARCHAR(MAX) NULL,
    Address        NVARCHAR(255) NOT NULL,
    Phone          NVARCHAR(20)  NULL,
    Email          NVARCHAR(100) NULL,
    StarRating     TINYINT       NOT NULL DEFAULT 3,
    CheckInTime    TIME          NULL,
    CheckOutTime   TIME          NULL,
    CreatedAt      DATETIME      NOT NULL DEFAULT GETDATE(),
    UpdatedAt      DATETIME      NULL,

    CONSTRAINT CHK_Hotel_Star CHECK (StarRating BETWEEN 1 AND 5)
);
GO

INSERT INTO Hotel (HotelName, Description, Address, Phone, Email, StarRating, CheckInTime, CheckOutTime)
VALUES (
    N'Grand Palace Hotel',
    N'Khach san 4 sao sang trong tai trung tam thanh pho Ho Chi Minh.',
    N'123 Nguyen Hue, Quan 1, TP. Ho Chi Minh',
    '028-3800-1234',
    'info@grandpalace.vn',
    4,
    '14:00:00',
    '12:00:00'
);
GO

-- ============================================================
-- 4. HOTEL IMAGES
-- ============================================================
CREATE TABLE HotelImages (
    ImageId        INT IDENTITY(1,1) PRIMARY KEY,
    HotelId        INT            NOT NULL,
    ImageUrl       NVARCHAR(500)  NOT NULL,
    Caption        NVARCHAR(200)  NULL,
    DisplayOrder   INT            NOT NULL DEFAULT 0,

    CONSTRAINT FK_HotelImages_Hotel FOREIGN KEY (HotelId)
        REFERENCES Hotel(HotelId) ON DELETE CASCADE
);
GO

INSERT INTO HotelImages (HotelId, ImageUrl, Caption, DisplayOrder) VALUES
(1, 'https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800', N'Sanh khach san',      0),
(1, 'https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800', N'Ho boi ngoai troi',   1),
(1, 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800', N'Nha hang',            2);
GO

-- ============================================================
-- 5. ROOM TYPES
--    Each row = one room category/type.
-- ============================================================
CREATE TABLE RoomTypes (
    RoomTypeId        INT IDENTITY(1,1) PRIMARY KEY,
    TypeName          NVARCHAR(100)  NOT NULL,
    Description       NVARCHAR(MAX)  NULL,
    Capacity          INT            NOT NULL DEFAULT 2,
    BedType           NVARCHAR(100)  NULL,
    SizeSqm           DECIMAL(6,2)   NULL,
    PricePerNight     DECIMAL(18,2)  NOT NULL,
    Amenities         NVARCHAR(MAX)  NULL,
    ThumbnailUrl      NVARCHAR(500)  NULL,
    IsActive          BIT            NOT NULL DEFAULT 1,
    CreatedAt         DATETIME       NOT NULL DEFAULT GETDATE(),
    UpdatedAt         DATETIME       NULL,

    CONSTRAINT UQ_RoomTypes_TypeName UNIQUE (TypeName),
    CONSTRAINT CHK_RoomTypes_Capacity CHECK (Capacity > 0),
    CONSTRAINT CHK_RoomTypes_Price CHECK (PricePerNight > 0),
    CONSTRAINT CHK_RoomTypes_Size CHECK (SizeSqm IS NULL OR SizeSqm > 0)
);
GO

INSERT INTO RoomTypes (TypeName, Description, Capacity, BedType, SizeSqm, PricePerNight, Amenities, ThumbnailUrl, IsActive) VALUES
(N'Standard', N'Phong tieu chuan, day du tien nghi co ban.', 2, N'1 giuong doi', 25, 800000,
 N'WiFi mien phi, TV 40 inch, Dieu hoa, Minibar, Ket an toan',
 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=600', 1),

(N'Deluxe', N'Phong rong hon, noi that hien dai, view thanh pho.', 2, N'1 giuong doi', 35, 1500000,
 N'WiFi mien phi, TV 4K 50 inch, Bon tam, Minibar, May pha ca phe',
 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=600', 1),

(N'Deluxe Twin', N'Phong 2 giuong don, phu hop cho ban be hoac dong nghiep.', 3, N'2 giuong don', 38, 1700000,
 N'WiFi mien phi, TV 4K 50 inch, Bon tam, Minibar, 2 giuong don',
 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=600', 1),

(N'Family', N'Phong gia dinh 2 phong ngu, phu hop cho nhom 4-6 nguoi.', 6, N'2 giuong doi', 70, 2500000,
 N'WiFi mien phi, 2 TV, Bep nho, 2 phong ngu, May giat',
 'https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=600', 1),

(N'Suite', N'Phong suite cao cap, co phong khach rieng va jacuzzi.', 4, N'1 giuong doi lon', 60, 3500000,
 N'WiFi mien phi, TV 4K 65 inch, Jacuzzi, Phong khach rieng, Bar mini',
 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=600', 1),

(N'Presidential Suite', N'Can phong tong thong cao cap nhat cua khach san.', 4, N'1 giuong doi lon', 150, 15000000,
 N'WiFi fiber, TV OLED 85 inch, Ho boi rieng, Phong an rieng, Butler 24/7',
 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=600', 1);
GO

CREATE INDEX IX_RoomTypes_IsActive ON RoomTypes(IsActive);
GO

-- ============================================================
-- 6. ROOMS
--    Each row = one physical room.
-- ============================================================
CREATE TABLE Rooms (
    RoomId            INT IDENTITY(1,1) PRIMARY KEY,
    RoomTypeId        INT            NOT NULL,
    RoomNumber        NVARCHAR(20)   NOT NULL,
    FloorNumber       INT            NULL,
    RoomStatus        NVARCHAR(20)   NOT NULL DEFAULT 'Available',
    HousekeepingNote  NVARCHAR(500)  NULL,
    IsActive          BIT            NOT NULL DEFAULT 1,
    CreatedAt         DATETIME       NOT NULL DEFAULT GETDATE(),
    UpdatedAt         DATETIME       NULL,

    CONSTRAINT FK_Rooms_RoomTypes FOREIGN KEY (RoomTypeId) REFERENCES RoomTypes(RoomTypeId),
    CONSTRAINT UQ_Rooms_RoomNumber UNIQUE (RoomNumber),
    CONSTRAINT CK_Rooms_Status CHECK (RoomStatus IN ('Available','Occupied','Cleaning','Maintenance','OutOfService'))
);
GO

CREATE INDEX IX_Rooms_RoomTypeId ON Rooms(RoomTypeId);
GO
CREATE INDEX IX_Rooms_Status ON Rooms(RoomStatus);
GO

INSERT INTO Rooms (RoomTypeId, RoomNumber, FloorNumber, RoomStatus, IsActive) VALUES
-- Standard
(1, N'101', 1, 'Available', 1),
(1, N'102', 1, 'Available', 1),
(1, N'103', 1, 'Available', 1),
(1, N'104', 1, 'Available', 1),
(1, N'105', 1, 'Cleaning',  1),
(1, N'106', 1, 'Maintenance', 1),
-- Deluxe
(2, N'201', 2, 'Available', 1),
(2, N'202', 2, 'Available', 1),
(2, N'203', 2, 'Occupied',  1),
(2, N'204', 2, 'Available', 1),
-- Deluxe Twin
(3, N'205', 2, 'Available', 1),
(3, N'206', 2, 'Available', 1),
(3, N'207', 2, 'Cleaning',  1),
-- Family
(4, N'301', 3, 'Available', 1),
(4, N'302', 3, 'Occupied',  1),
(4, N'303', 3, 'Available', 1),
-- Suite
(5, N'401', 4, 'Available', 1),
(5, N'402', 4, 'Occupied',  1),
-- Presidential Suite
(6, N'501', 5, 'Available', 1);
GO

-- ============================================================
-- 7. BOOKINGS
--    Booking is made by room type + quantity.
--    Actual physical rooms can be assigned later in BookingRoomAssignments.
-- ============================================================
CREATE TABLE Bookings (
    BookingId          INT IDENTITY(1,1) PRIMARY KEY,
    UserId             INT            NOT NULL,
    RoomTypeId         INT            NOT NULL,
    BookingCode        NVARCHAR(20)   NOT NULL,
    CheckInDate        DATE           NOT NULL,
    CheckOutDate       DATE           NOT NULL,
    GuestCount         INT            NOT NULL DEFAULT 1,
    NumberOfRooms      INT            NOT NULL DEFAULT 1,
    TotalAmount        DECIMAL(18,2)  NOT NULL DEFAULT 0,
    BookingStatus      NVARCHAR(20)   NOT NULL DEFAULT 'Pending',
    SpecialRequest     NVARCHAR(500)  NULL,
    CreatedAt          DATETIME       NOT NULL DEFAULT GETDATE(),
    UpdatedAt          DATETIME       NULL,

    CONSTRAINT FK_Bookings_Users FOREIGN KEY (UserId) REFERENCES Users(UserId),
    CONSTRAINT FK_Bookings_RoomTypes FOREIGN KEY (RoomTypeId) REFERENCES RoomTypes(RoomTypeId),
    CONSTRAINT UQ_Bookings_BookingCode UNIQUE (BookingCode),
    CONSTRAINT CHK_Bookings_Dates CHECK (CheckOutDate > CheckInDate),
    CONSTRAINT CHK_Bookings_GuestCount CHECK (GuestCount > 0),
    CONSTRAINT CHK_Bookings_NumberOfRooms CHECK (NumberOfRooms > 0),
    CONSTRAINT CHK_Bookings_TotalAmount CHECK (TotalAmount >= 0),
    CONSTRAINT CHK_Bookings_Status CHECK (BookingStatus IN ('Pending','Confirmed','CheckedIn','CheckedOut','Cancelled'))
);
GO

CREATE INDEX IX_Bookings_UserId ON Bookings(UserId);
GO
CREATE INDEX IX_Bookings_RoomTypeId ON Bookings(RoomTypeId);
GO
CREATE INDEX IX_Bookings_Status ON Bookings(BookingStatus);
GO
CREATE INDEX IX_Bookings_CheckInDate ON Bookings(CheckInDate);
GO
CREATE INDEX IX_Bookings_CheckOutDate ON Bookings(CheckOutDate);
GO

INSERT INTO Bookings (UserId, RoomTypeId, BookingCode, CheckInDate, CheckOutDate, GuestCount, NumberOfRooms, TotalAmount, BookingStatus, SpecialRequest)
VALUES
(3, 1, N'BK20260501001', '2026-05-10', '2026-05-12', 2, 1, 1600000,  N'Confirmed',  N'Can them goi'),
(3, 2, N'BK20260501002', '2026-05-20', '2026-05-23', 2, 1, 4500000,  N'Pending',    NULL),
(4, 4, N'BK20260501003', '2026-05-05', '2026-05-07', 4, 1, 5000000,  N'CheckedIn',  N'Don san bay luc 14h'),
(4, 1, N'BK20260401004', '2026-04-01', '2026-04-03', 1, 1, 1600000,  N'CheckedOut', NULL),
(5, 6, N'BK20260501005', '2026-05-15', '2026-05-18', 2, 1, 45000000, N'Confirmed',  N'Trang tri phong dip sinh nhat'),
(6, 3, N'BK20260501006', '2026-05-25', '2026-05-27', 2, 2, 6800000,  N'Pending',    NULL);
GO

-- ============================================================
-- 8. BOOKING ROOM ASSIGNMENTS
--    Assign actual physical rooms to each booking.
--    Useful for check-in / receptionist operations.
-- ============================================================
CREATE TABLE BookingRoomAssignments (
    AssignmentId      INT IDENTITY(1,1) PRIMARY KEY,
    BookingId         INT           NOT NULL,
    RoomId            INT           NOT NULL,
    AssignedAt        DATETIME      NOT NULL DEFAULT GETDATE(),
    ReleasedAt        DATETIME      NULL,
    Note              NVARCHAR(300) NULL,

    CONSTRAINT FK_BookingRoomAssignments_Bookings FOREIGN KEY (BookingId)
        REFERENCES Bookings(BookingId) ON DELETE CASCADE,
    CONSTRAINT FK_BookingRoomAssignments_Rooms FOREIGN KEY (RoomId)
        REFERENCES Rooms(RoomId),
    CONSTRAINT UQ_BookingRoomAssignments UNIQUE (BookingId, RoomId)
);
GO

CREATE INDEX IX_BookingRoomAssignments_BookingId ON BookingRoomAssignments(BookingId);
GO
CREATE INDEX IX_BookingRoomAssignments_RoomId ON BookingRoomAssignments(RoomId);
GO

INSERT INTO BookingRoomAssignments (BookingId, RoomId, AssignedAt, Note) VALUES
(1, 1, '2026-05-10 13:00:00', N'Phong Standard da cap cho khach'),
(3, 15, '2026-05-05 13:30:00', N'Cap phong Family cho booking dang o'),
(4, 2, '2026-04-01 12:00:00', N'Booking da check-out'),
(5, 19, '2026-05-15 13:00:00', N'Phong tong thong da duoc giu cho khach');
GO

-- ============================================================
-- 9. PAYMENTS
-- ============================================================
CREATE TABLE Payments (
    PaymentId         INT IDENTITY(1,1) PRIMARY KEY,
    BookingId         INT            NOT NULL UNIQUE,
    Amount            DECIMAL(18,2)  NOT NULL,
    PaymentMethod     NVARCHAR(50)   NOT NULL DEFAULT 'Cash',
    PaymentStatus     NVARCHAR(20)   NOT NULL DEFAULT 'Pending',
    PaidAt            DATETIME       NULL,
    Note              NVARCHAR(500)  NULL,

    CONSTRAINT FK_Payments_Bookings FOREIGN KEY (BookingId)
        REFERENCES Bookings(BookingId) ON DELETE CASCADE,
    CONSTRAINT CHK_Payments_Amount CHECK (Amount >= 0),
    CONSTRAINT CHK_Payments_Method CHECK (PaymentMethod IN ('Cash','BankTransfer','VNPay','MoMo')),
    CONSTRAINT CHK_Payments_Status CHECK (PaymentStatus IN ('Pending','Paid','Refunded'))
);
GO

INSERT INTO Payments (BookingId, Amount, PaymentMethod, PaymentStatus, PaidAt, Note) VALUES
(1, 1600000,  N'BankTransfer', N'Paid',    '2026-04-20 10:30:00', N'Thanh toan online'),
(2, 4500000,  N'Cash',         N'Pending', NULL,                   NULL),
(3, 5000000,  N'MoMo',         N'Paid',    '2026-05-05 14:00:00', N'Thanh toan qua MoMo'),
(4, 1600000,  N'Cash',         N'Paid',    '2026-04-01 13:00:00', NULL),
(5, 45000000, N'BankTransfer', N'Paid',    '2026-05-01 09:00:00', N'Dat phong nhan dip sinh nhat'),
(6, 6800000,  N'Cash',         N'Pending', NULL,                   NULL);
GO

-- ============================================================
-- 10. SERVICES
-- ============================================================
CREATE TABLE Services (
    ServiceId         INT IDENTITY(1,1) PRIMARY KEY,
    ServiceName       NVARCHAR(100) NOT NULL,
    Description       NVARCHAR(500) NULL,
    Price             DECIMAL(18,2) NOT NULL,
    Unit              NVARCHAR(50)  NOT NULL DEFAULT N'Lan',
    IconEmoji         NVARCHAR(10)  NULL,
    IsActive          BIT           NOT NULL DEFAULT 1,
    CreatedAt         DATETIME      NOT NULL DEFAULT GETDATE(),
    UpdatedAt         DATETIME      NULL,

    CONSTRAINT CHK_Services_Price CHECK (Price >= 0)
);
GO

INSERT INTO Services (ServiceName, Description, Price, Unit, IconEmoji, IsActive) VALUES
(N'An sang buffet',  N'Buffet sang tai nha hang',              150000, N'Nguoi/lan', N'🍳', 1),
(N'Spa & Massage',   N'Massage thu gian toan than 60 phut',    500000, N'Gio',       N'💆', 1),
(N'Thue xe san bay', N'Dua don san bay',                        350000, N'Luot',      N'🚗', 1),
(N'Giat ui',         N'Giat ui quan ao trong ngay',              80000, N'Kg',        N'👕', 1),
(N'Phong gym',       N'Su dung phong tap the duc',             100000, N'Ngay',      N'🏋️', 1),
(N'Ho boi',          N'Su dung ho boi ngoai troi',             100000, N'Ngay',      N'🏊', 1),
(N'Wi-Fi cao cap',   N'Internet toc do cao 1Gbps',              50000, N'Ngay',      N'📶', 1),
(N'Nha hang',        N'Dat ban nha hang khach san',          1000000, N'Buoi',      N'🍽', 1);
GO

-- ============================================================
-- 11. BOOKING SERVICES
-- ============================================================
CREATE TABLE BookingServices (
    BookingServiceId  INT IDENTITY(1,1) PRIMARY KEY,
    BookingId         INT            NOT NULL,
    ServiceId         INT            NOT NULL,
    Quantity          INT            NOT NULL DEFAULT 1,
    UnitPrice         DECIMAL(18,2)  NOT NULL,
    TotalPrice        AS (Quantity * UnitPrice) PERSISTED,
    UsedAt            DATETIME       NOT NULL DEFAULT GETDATE(),
    Note              NVARCHAR(300)  NULL,

    CONSTRAINT FK_BookingServices_Bookings FOREIGN KEY (BookingId)
        REFERENCES Bookings(BookingId) ON DELETE CASCADE,
    CONSTRAINT FK_BookingServices_Services FOREIGN KEY (ServiceId)
        REFERENCES Services(ServiceId),
    CONSTRAINT CHK_BookingServices_Qty CHECK (Quantity > 0),
    CONSTRAINT CHK_BookingServices_UnitPrice CHECK (UnitPrice >= 0)
);
GO

CREATE INDEX IX_BookingServices_BookingId ON BookingServices(BookingId);
GO
CREATE INDEX IX_BookingServices_ServiceId ON BookingServices(ServiceId);
GO

INSERT INTO BookingServices (BookingId, ServiceId, Quantity, UnitPrice, UsedAt, Note) VALUES
(3, 1, 4, 150000, '2026-05-05 08:00:00', N'An sang 4 nguoi'),
(3, 3, 1, 350000, '2026-05-05 14:00:00', N'Don san bay'),
(4, 2, 2, 500000, '2026-04-02 15:00:00', NULL);
GO

-- ============================================================
-- 12. REVIEWS
-- ============================================================
CREATE TABLE Reviews (
    ReviewId          INT IDENTITY(1,1) PRIMARY KEY,
    BookingId         INT            NOT NULL,
    UserId            INT            NOT NULL,
    Rating            TINYINT        NOT NULL,
    Comment           NVARCHAR(1000) NULL,
    Status            NVARCHAR(20)   NOT NULL DEFAULT 'Visible',
    CreatedAt         DATETIME       NOT NULL DEFAULT GETDATE(),
    UpdatedAt         DATETIME       NULL,

    CONSTRAINT FK_Reviews_Bookings FOREIGN KEY (BookingId) REFERENCES Bookings(BookingId),
    CONSTRAINT FK_Reviews_Users FOREIGN KEY (UserId) REFERENCES Users(UserId),
    CONSTRAINT UQ_Reviews_BookingId UNIQUE (BookingId),
    CONSTRAINT CHK_Reviews_Rating CHECK (Rating BETWEEN 1 AND 5),
    CONSTRAINT CHK_Reviews_Status CHECK (Status IN ('Visible','Hidden'))
);
GO

CREATE INDEX IX_Reviews_UserId ON Reviews(UserId);
GO

INSERT INTO Reviews (BookingId, UserId, Rating, Comment, Status) VALUES
(4, 4, 5, N'Phong sach se, nhan vien than thien.', N'Visible'),
(3, 4, 4, N'Phong gia dinh rong va tien nghi, dich vu tot.', N'Visible');
GO
