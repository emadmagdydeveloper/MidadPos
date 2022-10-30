package com.midad_pos.model;

import java.io.Serializable;

public class ChargeModel implements Serializable {
    private PaymentModel paymentModel;
    private String price;
    private double paidAmount=0.0;
    private boolean isPaid;
    private boolean isEdited = false;

    public ChargeModel() {
    }

    public ChargeModel(PaymentModel paymentModel, String price, boolean isPaid) {
        this.paymentModel = paymentModel;
        this.price = price;
        this.isPaid = isPaid;
    }



    public PaymentModel getPaymentModel() {
        return paymentModel;
    }

    public void setPaymentModel(PaymentModel paymentModel) {
        this.paymentModel = paymentModel;
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

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
}
