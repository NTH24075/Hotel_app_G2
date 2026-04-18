package com.example.hotellapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Roles")
public class Role {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "RoleId")
    public int roleId;

    @ColumnInfo(name = "RoleName")
    public String roleName;

    @ColumnInfo(name = "Description")
    public String description;

    public Role() {
    }

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }
}