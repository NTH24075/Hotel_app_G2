package com.example.hotellapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Users",
        foreignKeys = @ForeignKey(
                entity = Role.class,
                parentColumns = "RoleId",
                childColumns = "RoleId"
        ),
        indices = {@Index(value = {"Email"}, unique = true)}
)
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "UserId")
    public int userId;

    @ColumnInfo(name = "RoleId")
    public int roleId;

    @ColumnInfo(name = "FullName")
    public String fullName;

    @ColumnInfo(name = "Email")
    public String email;

    @ColumnInfo(name = "PasswordHash")
    public String passwordHash;

    @ColumnInfo(name = "Phone")
    public String phone;

    @ColumnInfo(name = "Status")
    public String status;

    public User() {
    }
}