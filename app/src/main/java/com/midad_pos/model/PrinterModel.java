package com.midad_pos.model;

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
    private boolean isOtherPrinter;
    private String bluetooth_name;
    private String bluetooth_address;
    private String ip_address;
    private String paper_width;
    private boolean can_print_receipt_and_bill;
    private boolean can_print_orders;
    @Ignore
    private boolean isSelected;


    @Ignore
    public PrinterModel() {
    }

    public PrinterModel(String name, String printer_type, boolean isOtherPrinter, String bluetooth_name, String bluetooth_address, String ip_address, String paper_width, boolean can_print_receipt_and_bill, boolean can_print_orders) {
        this.name = name;
        this.printer_type = printer_type;
        this.isOtherPrinter = isOtherPrinter;
        this.bluetooth_name = bluetooth_name;
        this.bluetooth_address = bluetooth_address;
        this.ip_address = ip_address;
        this.paper_width = paper_width;
        this.can_print_receipt_and_bill = can_print_receipt_and_bill;
        this.can_print_orders = can_print_orders;
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

    public boolean isOtherPrinter() {
        return isOtherPrinter;
    }

    public void setOtherPrinter(boolean otherPrinter) {
        isOtherPrinter = otherPrinter;
    }

    public String getBluetooth_name() {
        return bluetooth_name;
    }

    public void setBluetooth_name(String bluetooth_name) {
        this.bluetooth_name = bluetooth_name;
    }

    public String getBluetooth_address() {
        return bluetooth_address;
    }

    public void setBluetooth_address(String bluetooth_address) {
        this.bluetooth_address = bluetooth_address;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getPaper_width() {
        return paper_width;
    }

    public void setPaper_width(String paper_width) {
        this.paper_width = paper_width;
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

    @Ignore
    public boolean isSelected() {
        return isSelected;
    }

    @Ignore
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
