package com.example.hotellapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hotellapp.models.Role;

import java.util.List;

@Dao
public interface RoleDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Role> roles);

    @Query("SELECT COUNT(*) FROM Roles")
    int countRoles();

    @Query("SELECT * FROM Roles WHERE roleName = :roleName LIMIT 1")
    Role getRoleByName(String roleName);
}