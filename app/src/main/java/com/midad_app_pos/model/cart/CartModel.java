package com.midad_app_pos.model.cart;

import java.io.Serializable;
import java.util.List;

public class CartModel implements Serializable {
    private String user_id;
    private String warehouse_id;
    private String customer_id;
    private String pos_id;
    private List<Cart> data;

    public CartModel(String user_id, String warehouse_id, String customer_id,String pos_id,List<Cart> data) {
        this.user_id = user_id;
        this.warehouse_id = warehouse_id;
        this.customer_id = customer_id;
        this.pos_id = pos_id;
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

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public List<Cart> getData() {
        return data;
    }

    public void setData(List<Cart> data) {
        this.data = data;
    }

    public static class Cart implements Serializable{
        private String name;
        private String cash_register_id;
        private String date;
        private double total_price;
        private double order_tax;
        private double order_discount;
        private double grand_total;
        private String dining_id;
        private String delivery_name ="";
        private String pos_id;
        private String sale_status;
        private String sale_id;
        private String draft;
        private List<Payment> payments;
        private List<Discount> discounts;
        private List<Detail> details;

        public Cart(String name,String cash_register_id, String date, double total_price, double order_tax, double order_discount, double grand_total, String dining_id,String delivery_name, String pos_id,String sale_id, String sale_status, String draft, List<Payment> payments, List<Discount> discounts, List<Detail> details) {
            this.name = name;
            this.cash_register_id = cash_register_id;
            this.date = date;
            this.total_price = total_price;
            this.order_tax = order_tax;
            this.order_discount = order_discount;
            this.grand_total = grand_total;
            this.dining_id = dining_id;
            this.delivery_name = delivery_name;
            this.pos_id = pos_id;
            this.sale_id = sale_id;
            this.sale_status = sale_status;
            this.draft = draft;
            this.payments = payments;
            this.discounts = discounts;
            this.details = details;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getDelivery_name() {
            return delivery_name;
        }

        public void setDelivery_name(String delivery_name) {
            this.delivery_name = delivery_name;
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

        public String getSale_id() {
            return sale_id;
        }

        public void setSale_id(String sale_id) {
            this.sale_id = sale_id;
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
        private String product_name;
        private String variant_id;
        private String totalPrice;
        private int qty;
        private double net_unit_price;
        private String discount;
        private String tax_rate;
        private String comment;
        private List<Modifier> modifiers;
        private List<Discount> discounts;

        public Detail(String product_id,String product_name,String totalPrice, String variant_id, int qty, double net_unit_price, String discount, String tax_rate, String comment, List<Modifier> modifiers, List<Discount> discounts) {
            this.product_id = product_id;
            this.product_name = product_name;
            this.totalPrice = totalPrice;
            this.variant_id = variant_id;
            this.qty = qty;
            this.net_unit_price = net_unit_price;
            this.discount = discount;
            this.tax_rate = tax_rate;
            this.comment = comment;
            this.modifiers = modifiers;
            this.discounts = discounts;

        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getVariant_id() {
            return variant_id;
        }

        public void setVariant_id(String variant_id) {
            this.variant_id = variant_id;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public double getNet_unit_price() {
            return net_unit_price;
        }

        public void setNet_unit_price(double net_unit_price) {
            this.net_unit_price = net_unit_price;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getTax_rate() {
            return tax_rate;
        }

        public void setTax_rate(String tax_rate) {
            this.tax_rate = tax_rate;
        }



        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public List<Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(List<Modifier> modifiers) {
            this.modifiers = modifiers;
        }

        public List<Discount> getDiscounts() {
            return discounts;
        }

        public void setDiscounts(List<Discount> discounts) {
            this.discounts = discounts;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }
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
        private List<ModifierDetails> details;

        public Modifier(String product_id, String modifier_id, String total, List<ModifierDetails> details) {
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

        public List<ModifierDetails> getDetails() {
            return details;
        }

        public void setDetails(List<ModifierDetails> details) {
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

    public static class ModifierDetails implements Serializable{
        private String modifier_data_id;
        private double net_cost;
        private double total_cost;

        public ModifierDetails(String modifier_data_id, double net_cost, double total_cost) {
            this.modifier_data_id = modifier_data_id;
            this.net_cost = net_cost;
            this.total_cost = total_cost;
        }

        public String getModifier_data_id() {
            return modifier_data_id;
        }

        public void setModifier_data_id(String modifier_data_id) {
            this.modifier_data_id = modifier_data_id;
        }

        public double getNet_cost() {
            return net_cost;
        }

        public void setNet_cost(double net_cost) {
            this.net_cost = net_cost;
        }

        public double getTotal_cost() {
            return total_cost;
        }

        public void setTotal_cost(double total_cost) {
            this.total_cost = total_cost;
        }
    }
}
