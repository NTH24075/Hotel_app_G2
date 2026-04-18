package com.example.hotellapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
}