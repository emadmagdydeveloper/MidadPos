package com.midad_pos.model.cart;

import java.io.Serializable;
import java.util.List;

public class CartModel implements Serializable {
    private String user_id;
    private String warehouse_id;
    private List<Cart> data;

    public CartModel(String user_id, String warehouse_id, List<Cart> data) {
        this.user_id = user_id;
        this.warehouse_id = warehouse_id;
        this.data = data;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWarehouse_id() {
        return warehouse_id;
    }

    public void setWarehouse_id(String warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public List<Cart> getData() {
        return data;
    }

    public void setData(List<Cart> data) {
        this.data = data;
    }

    public static class Cart implements Serializable{
        private String cash_register_id;
        private String date;
        private double total_price;
        private double order_tax;
        private double order_discount;
        private double grand_total;
        private String dining_id;
        private String pos_id;
        private String sale_status;
        private String draft;
        private String paid_amount;
        private String pay_id;
        private List<Payment> payments;
        private List<Discount> discounts;
        private List<Detail> details;

        public Cart(String cash_register_id, String date, double total_price, double order_tax, double order_discount, double grand_total, String dining_id, String pos_id, String sale_status, String draft, String paid_amount, String pay_id, List<Payment> payments, List<Discount> discounts, List<Detail> details) {
            this.cash_register_id = cash_register_id;
            this.date = date;
            this.total_price = total_price;
            this.order_tax = order_tax;
            this.order_discount = order_discount;
            this.grand_total = grand_total;
            this.dining_id = dining_id;
            this.pos_id = pos_id;
            this.sale_status = sale_status;
            this.draft = draft;
            this.paid_amount = paid_amount;
            this.pay_id = pay_id;
            this.payments = payments;
            this.discounts = discounts;
            this.details = details;
        }

        public String getCash_register_id() {
            return cash_register_id;
        }

        public void setCash_register_id(String cash_register_id) {
            this.cash_register_id = cash_register_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getTotal_price() {
            return total_price;
        }

        public void setTotal_price(double total_price) {
            this.total_price = total_price;
        }

        public double getOrder_tax() {
            return order_tax;
        }

        public void setOrder_tax(double order_tax) {
            this.order_tax = order_tax;
        }

        public double getOrder_discount() {
            return order_discount;
        }

        public void setOrder_discount(double order_discount) {
            this.order_discount = order_discount;
        }

        public double getGrand_total() {
            return grand_total;
        }

        public void setGrand_total(double grand_total) {
            this.grand_total = grand_total;
        }

        public String getDining_id() {
            return dining_id;
        }

        public void setDining_id(String dining_id) {
            this.dining_id = dining_id;
        }

        public String getPos_id() {
            return pos_id;
        }

        public void setPos_id(String pos_id) {
            this.pos_id = pos_id;
        }

        public String getSale_status() {
            return sale_status;
        }

        public void setSale_status(String sale_status) {
            this.sale_status = sale_status;
        }

        public String getDraft() {
            return draft;
        }

        public void setDraft(String draft) {
            this.draft = draft;
        }

        public String getPaid_amount() {
            return paid_amount;
        }

        public void setPaid_amount(String paid_amount) {
            this.paid_amount = paid_amount;
        }

        public String getPay_id() {
            return pay_id;
        }

        public void setPay_id(String pay_id) {
            this.pay_id = pay_id;
        }

        public List<Payment> getPayments() {
            return payments;
        }

        public void setPayments(List<Payment> payments) {
            this.payments = payments;
        }

        public List<Discount> getDiscounts() {
            return discounts;
        }

        public void setDiscounts(List<Discount> discounts) {
            this.discounts = discounts;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public void setDetails(List<Detail> details) {
            this.details = details;
        }
    }

    public static class Detail implements Serializable{
        private String product_id;
        private String product_code;
        private int qty;
        private double net_unit_price;
        private String discount;
        private String tax_rate;
        private String tax;
        private String subtotal;
        private String comment;
        private List<Modifier> modifiers;
        private List<Discount> discounts;
        private String modifier_data_id;
        private double net_cost;
        private double total_cost;
    }

    public static class Discount implements Serializable{
        private String discount_id;

        public Discount(String discount_id) {
            this.discount_id = discount_id;
        }

        public String getDiscount_id() {
            return discount_id;
        }

        public void setDiscount_id(String discount_id) {
            this.discount_id = discount_id;
        }
    }

    public static class Modifier implements Serializable{
        private String product_id;
        private String modifier_id;
        private String total;
        private List<Detail> details;

        public Modifier(String product_id, String modifier_id, String total, List<Detail> details) {
            this.product_id = product_id;
            this.modifier_id = modifier_id;
            this.total = total;
            this.details = details;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getModifier_id() {
            return modifier_id;
        }

        public void setModifier_id(String modifier_id) {
            this.modifier_id = modifier_id;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<Detail> getDetails() {
            return details;
        }

        public void setDetails(List<Detail> details) {
            this.details = details;
        }
    }

    public static class Payment implements Serializable{
        private double total;
        private double paid;
        private double remaining;
        private String pay_id;

        public Payment(double total, double paid, double remaining, String pay_id) {
            this.total = total;
            this.paid = paid;
            this.remaining = remaining;
            this.pay_id = pay_id;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getPaid() {
            return paid;
        }

        public void setPaid(double paid) {
            this.paid = paid;
        }

        public double getRemaining() {
            return remaining;
        }

        public void setRemaining(double remaining) {
            this.remaining = remaining;
        }

        public String getPay_id() {
            return pay_id;
        }

        public void setPay_id(String pay_id) {
            this.pay_id = pay_id;
        }
    }
}
