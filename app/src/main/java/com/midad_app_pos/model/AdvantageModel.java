package com.midad_app_pos.model;

import java.io.Serializable;

public class AdvantageModel implements Serializable {
    private String id;
    private String user_id;
    private String shifts;
    private String time_recording;
    private String open_tickets;
    private String kitchen_printers;
    private String customer_show;
    private String dining_options;
    private String stock_notification;
    private String negative_stock_notifications;
    private String barcode_weight;
    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getShifts() {
        return shifts;
    }

    public String getTime_recording() {
        return time_recording;
    }

    public String getOpen_tickets() {
        return open_tickets;
    }

    public String getKitchen_printers() {
        return kitchen_printers;
    }

    public String getCustomer_show() {
        return customer_show;
    }

    public String getDining_options() {
        return dining_options;
    }

    public String getStock_notification() {
        return stock_notification;
    }

    public String getNegative_stock_notifications() {
        return negative_stock_notifications;
    }

    public String getBarcode_weight() {
        return barcode_weight;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
