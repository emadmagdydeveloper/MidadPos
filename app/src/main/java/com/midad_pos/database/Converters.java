package com.midad_pos.database;

import android.bluetooth.BluetoothDevice;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class Converters {

    @TypeConverter
    public String convertDeviceToString(BluetoothDevice device){
        return new Gson().toJson(device);
    }

    @TypeConverter
    public BluetoothDevice convertStringToDevice(String device){
        return new Gson().fromJson(device,BluetoothDevice.class);
    }
}
