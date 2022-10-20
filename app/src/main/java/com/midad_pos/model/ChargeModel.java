package com.midad_pos.model;

import java.io.Serializable;

public class ChargeModel implements Serializable {
    private String payment_type;
    private String price;
    private boolean isPaid;
    private boolean isEdited = false;

    public ChargeModel() {
    }

    public ChargeModel(String payment_type, String price, boolean isPaid) {
        this.payment_type = payment_type;
        this.price = price;
        this.isPaid = isPaid;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
