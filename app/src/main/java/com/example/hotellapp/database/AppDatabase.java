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

@Database(entities = {User.class, Role.class}, version = 2, exportSchema = false)
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
                                    "hotel_app_room.db"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();

                    seedInitialData(INSTANCE);
                }
            }
        }
        return INSTANCE;
    }

    private static void seedInitialData(AppDatabase db) {
        db.runInTransaction(() -> {
            RoleDAO roleDAO = db.roleDAO();
            UserDAO userDAO = db.userDAO();

            if (roleDAO.countRoles() == 0) {
                roleDAO.insertAll(Arrays.asList(
                        new Role("Admin", "Quan tri he thong"),
                        new Role("Receptionist", "Le tan khach san"),
                        new Role("Guest", "Khach dat phong")
                ));
            }

            User adminExisting = userDAO.getUserByEmail("admin@gmail.com");
            if (adminExisting == null) {
                User admin = new User();
                admin.roleId = 1;
                admin.fullName = "Admin System";
                admin.email = "admin@gmail.com";
                admin.passwordHash = "12345678";
                admin.status = "Active";
                admin.phone = null;
                admin.citizenId = null;
                admin.address = null;

                userDAO.registerUser(admin);
            }

            User recepExisting = userDAO.getUserByEmail("recep@gmail.com");
            if (recepExisting == null) {
                User recep = new User();
                recep.roleId = 2;
                recep.fullName = "Receptionist System";
                recep.email = "recep@gmail.com";
                recep.passwordHash = "12345678";
                recep.status = "Active";
                recep.phone = null;
                recep.citizenId = null;
                recep.address = null;

                userDAO.registerUser(recep);
            }
        });
    }
}