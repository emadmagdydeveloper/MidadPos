package com.midad_app_pos.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.midad_app_pos.model.PrinterModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DAO {
    @Insert
    Single<Long> addPrinter(PrinterModel model);

    @Delete
    Completable deletePrinter(List<PrinterModel> list);

    @Query("SELECT * FROM printers ORDER BY id DESC")
    Single<List<PrinterModel>> getPrinters();

    @Query("SELECT * FROM printers WHERE printer_type =:name LIMIT 1")
    Single<PrinterModel> getPrinterSunmi(String name);

    @Query("SELECT * FROM printers WHERE bluetooth_name =:name LIMIT 1")
    Single<PrinterModel> getPrinterBluetoothName(String name);

    @Update
    Completable updatePrinter(PrinterModel printerModel);

    @Delete
    Completable deletePrinter(PrinterModel printerModel);

}
