package com.midad_pos.database;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.midad_pos.model.PrinterModel;

@Database(entities = {PrinterModel.class}, exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase instance = null;


    public abstract DAO getDAO();

    public static synchronized AppDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application, AppDatabase.class, "midad_pos_db.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
