package com.midad_app_pos.database;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.midad_app_pos.model.PrinterModel;

@Database(entities = {PrinterModel.class}, exportSchema = false, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase instance = null;


    public abstract DAO getDAO();

    public static synchronized AppDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application, AppDatabase.class, "MidadPOS_db.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
