package com.example.hotellapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import com.example.hotellapp.models.User;

@Dao
public interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long registerUser(User user);

    @Query("SELECT * FROM Users WHERE Email = :email AND PasswordHash = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM Users WHERE Email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM Users WHERE Email = :email AND PasswordHash = :password AND Status = 'Active' LIMIT 1")
    User loginActiveUser(String email, String password);

    @Query("SELECT * FROM Users WHERE UserId = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("UPDATE Users SET FullName = :fullName, Phone = :phone, CitizenId = :citizenId, Address = :address WHERE UserId = :userId")
    int updateGuestProfile(int userId, String fullName, String phone, String citizenId, String address);

    @Query("UPDATE Users SET PasswordHash = :newPassword WHERE UserId = :userId")
    int updatePassword(int userId, String newPassword);

    @Query("SELECT * FROM Users WHERE UserId = :userId AND PasswordHash = :password LIMIT 1")
    User checkCurrentPassword(int userId, String password);

    @Query("SELECT COUNT(*) FROM Users")
    int countAllUsers();

    @Query("SELECT COUNT(*) FROM Users WHERE RoleId = :roleId")
    int countUsersByRole(int roleId);

    @Query("SELECT COUNT(*) FROM Users WHERE Status != 'Active'")
    int countInactiveUsers();
    // ===== ADMIN - USER MANAGEMENT =====

    @Query("SELECT * FROM Users ORDER BY UserId DESC")
    List<User> getAllUsers();
    @Query("SELECT * FROM Users" +
            " WHERE FullName LIKE '%' || :keyword || '%'" +
            " OR Email LIKE '%' || :keyword || '%'" +
            " OR Phone LIKE '%' || :keyword || '%'" +
            " ORDER BY UserId DESC")
    List<User> searchUsers(String keyword);

    @Query("SELECT * FROM Users Where RoleId != 1")
    List<User> getAllGuestsExceptAdmin();

    @Query("SELECT * FROM Users WHERE Status IS NULL OR Status != 'Active' ORDER BY UserId DESC")
    List<User> getBlockedOrInactiveUsers();

    @Query("UPDATE Users SET Status = :status WHERE UserId = :userId")
    int updateUserStatus(int userId, String status);


}
