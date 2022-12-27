package com.midad_app_pos.model;

import java.io.Serializable;

public class InvoiceSettings implements Serializable {
    private int id;
    private int warehouse_id;
    private String email_receipts;
    private String printed_receipts;
    private String header;
    private String footer;
    private int show_customer_information;
    private int view_comments;
    private String receipt_language;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public int getWarehouse_id() {
        return warehouse_id;
    }

    public String getEmail_receipts() {
        return email_receipts;
    }

    public String getPrinted_receipts() {
        return printed_receipts;
    }

    public String getHeader() {
        return header;
    }

    public String getFooter() {
        return footer;
    }

    public int getShow_customer_information() {
        return show_customer_information;
    }

    public int getView_comments() {
        return view_comments;
    }

    public String getReceipt_language() {
        return receipt_language;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
