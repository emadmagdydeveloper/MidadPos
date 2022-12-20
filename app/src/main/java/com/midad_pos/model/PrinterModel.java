package com.midad_pos.model;

import android.bluetooth.BluetoothDevice;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "printers")
public class PrinterModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String printer_type;
    private boolean can_print_receipt_and_bill;
    private boolean can_print_orders;
    private boolean canPrintAutomatic;
    private String ip_address;
    private String bluetooth_name;
    private String paperWidth;
    private BluetoothDevice device;
    @Ignore
    private boolean isSelected;


    @Ignore
    public PrinterModel() {
    }

    public PrinterModel(String name, String printer_type, boolean can_print_receipt_and_bill, boolean can_print_orders, boolean canPrintAutomatic, String ip_address, String bluetooth_name, BluetoothDevice device,String paperWidth) {
        this.paperWidth = paperWidth;
        this.name = name;
        this.printer_type = printer_type;
        this.can_print_receipt_and_bill = can_print_receipt_and_bill;
        this.can_print_orders = can_print_orders;
        this.canPrintAutomatic = canPrintAutomatic;
        this.ip_address = ip_address;
        this.bluetooth_name = bluetooth_name;
        this.device = device;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrinter_type() {
        return printer_type;
    }

    public void setPrinter_type(String printer_type) {
        this.printer_type = printer_type;
    }

    public boolean isCan_print_receipt_and_bill() {
        return can_print_receipt_and_bill;
    }

    public void setCan_print_receipt_and_bill(boolean can_print_receipt_and_bill) {
        this.can_print_receipt_and_bill = can_print_receipt_and_bill;
    }

    public boolean isCan_print_orders() {
        return can_print_orders;
    }

    public void setCan_print_orders(boolean can_print_orders) {
        this.can_print_orders = can_print_orders;
    }

    public boolean isCanPrintAutomatic() {
        return canPrintAutomatic;
    }

    public void setCanPrintAutomatic(boolean canPrintAutomatic) {
        this.canPrintAutomatic = canPrintAutomatic;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getBluetooth_name() {
        return bluetooth_name;
    }

    public void setBluetooth_name(String bluetooth_name) {
        this.bluetooth_name = bluetooth_name;
    }

    @Ignore
    public boolean isSelected() {
        return isSelected;
    }

    @Ignore
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPaperWidth() {
        return paperWidth;
    }

    public void setPaperWidth(String paperWidth) {
        this.paperWidth = paperWidth;
    }
}
