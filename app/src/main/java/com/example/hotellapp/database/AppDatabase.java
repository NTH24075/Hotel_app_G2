package com.example.hotellapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hotellapp.dao.RoleDAO;
import com.example.hotellapp.dao.UserDAO;
import com.example.hotellapp.models.Role;
import com.example.hotellapp.models.User;

import java.util.Arrays;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Role.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDAO userDAO();
    public abstract RoleDAO roleDAO();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "hotel_app.db"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();

                    seedRolesIfNeeded(INSTANCE);
                }
            }
        }
        return INSTANCE;
    }

    private static void seedRolesIfNeeded(AppDatabase db) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (db.roleDAO().countRoles() == 0) {
                db.roleDAO().insertAll(Arrays.asList(
                        new Role("Admin", "Quan tri he thong"),
                        new Role("Receptionist", "Le tan khach san"),
                        new Role("Guest", "Khach dat phong")
                ));
            }
        });
    }
}