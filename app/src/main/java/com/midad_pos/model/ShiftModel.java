package com.midad_pos.model;

import java.io.Serializable;
import java.util.List;

public class ShiftModel implements Serializable {
    private String id;
    private String shift_no;
    private String cash_in_hand;
    private String user_id;
    private String warehouse_id;
    private String pos_id;
    private boolean status;
    private String created_at;
    private User user;
    private WereHouse warehouse;
    private POSModel pos;
    private double total_sale_amount;
    private double cash_payment;
    private double cash_refund;
    private double paid_in;
    private double paid_out;
    private double total_order_tax;
    private List<PayInOutModel> data;
    private List<Payment> payments;
    private double expected_cash;
    private double gross_sales;
    private double total_discount;
    private double refund;
    private double net_sales;
    private double total_tendered;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShift_no() {
        return shift_no;
    }

    public String getCash_in_hand() {
        return cash_in_hand;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getWarehouse_id() {
        return warehouse_id;
    }

    public String getPos_id() {
        return pos_id;
    }

    public boolean isStatus() {
        return status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public User getUser() {
        return user;
    }

    public WereHouse getWarehouse() {
        return warehouse;
    }

    public POSModel getPos() {
        return pos;
    }

    public double getTotal_sale_amount() {
        return total_sale_amount;
    }

    public double getCash_payment() {
        return cash_payment;
    }

    public double getCash_refund() {
        return cash_refund;
    }

    public double getPaid_in() {
        return paid_in;
    }

    public double getPaid_out() {
        return paid_out;
    }

    public double getTotal_order_tax() {
        return total_order_tax;
    }

    public List<PayInOutModel> getData() {
        return data;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public double getExpected_cash() {
        return expected_cash;
    }

    public double getGross_sales() {
        return gross_sales;
    }

    public double getTotal_discount() {
        return total_discount;
    }

    public double getRefund() {
        return refund;
    }

    public double getNet_sales() {
        return net_sales;
    }

    public double getTotal_tendered() {
        return total_tendered;
    }

    public static class Payment implements Serializable{
        private String name;
        private double amount;

        public String getName() {
            return name;
        }

        public double getAmount() {
            return amount;
        }
    }

    public static class PayInOutModel implements Serializable{
        private int id;
        private double amount;
        private String comment;
        private String type;
        private String cash_register_id;
        private String user_id;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public double getAmount() {
            return amount;
        }

        public String getComment() {
            return comment;
        }

        public String getType() {
            return type;
        }

        public String getCash_register_id() {
            return cash_register_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }

}
