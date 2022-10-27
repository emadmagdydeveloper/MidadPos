package com.midad_pos.model.cart;

import java.io.Serializable;
import java.util.List;

public class CartModel implements Serializable {
    private String user_id;
    private String warehouse_id;
    private String customer_id;
    private int total_qty;
    private String total_discount;
    private String total_price;
    private int item;
    private double order_tax;
    private double total_tax;
    private double grand_total;
    private String sale_status;
    private String pos;
    private String draft;
    private double paying_amount;
    private double paid_amount;
    private String paid_by_id;
    private String order_discount;
    private String payment_status;
    private String order_discount_type = "Flat";
    private List<CartDiscount> discounts;
    private List<Details> details;



    public CartModel(String user_id, String warehouse_id, String customer_id, int total_qty, String total_discount, String total_price, int item, double order_tax, double total_tax, double grand_total, String sale_status, String pos, String draft, double paying_amount, double paid_amount, String paid_by_id, String order_discount, String payment_status, String order_discount_type, List<CartDiscount> discounts, List<Details> details) {
        this.user_id = user_id;
        this.warehouse_id = warehouse_id;
        this.customer_id = customer_id;
        this.total_qty = total_qty;
        this.total_discount = total_discount;
        this.total_price = total_price;
        this.item = item;
        this.order_tax = order_tax;
        this.total_tax = total_tax;
        this.grand_total = grand_total;
        this.sale_status = sale_status;
        this.pos = pos;
        this.draft = draft;
        this.paying_amount = paying_amount;
        this.paid_amount = paid_amount;
        this.paid_by_id = paid_by_id;
        this.order_discount = order_discount;
        this.payment_status = payment_status;
        this.order_discount_type = order_discount_type;
        this.discounts = discounts;
        this.details = details;
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

    public int getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(int total_qty) {
        this.total_qty = total_qty;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public double getOrder_tax() {
        return order_tax;
    }

    public void setOrder_tax(double order_tax) {
        this.order_tax = order_tax;
    }

    public double getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(double total_tax) {
        this.total_tax = total_tax;
    }

    public double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }

    public String getSale_status() {
        return sale_status;
    }

    public void setSale_status(String sale_status) {
        this.sale_status = sale_status;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public double getPaying_amount() {
        return paying_amount;
    }

    public void setPaying_amount(double paying_amount) {
        this.paying_amount = paying_amount;
    }

    public double getPaid_amount() {
        return paid_amount;
    }

    public void setPaid_amount(double paid_amount) {
        this.paid_amount = paid_amount;
    }

    public String getPaid_by_id() {
        return paid_by_id;
    }

    public void setPaid_by_id(String paid_by_id) {
        this.paid_by_id = paid_by_id;
    }

    public String getOrder_discount() {
        return order_discount;
    }

    public void setOrder_discount(String order_discount) {
        this.order_discount = order_discount;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public List<CartDiscount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<CartDiscount> discounts) {
        this.discounts = discounts;
    }

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    public static class CartDiscount implements Serializable{
        private String discount_id;

        public String getDiscount_id() {
            return discount_id;
        }

        public void setDiscount_id(String discount_id) {
            this.discount_id = discount_id;
        }
    }

    public static class Details implements Serializable{
        private int product_id;
        private String product_code;
        private int qty;
        private String net_unit_price;
        private String discount;
        private String tax_rate;
        private String tax;
        private String subtotal;
        private String comment;
        private List<Modifier> modifiers;
        private List<CartDiscount> discounts;

        public Details(int product_id, String product_code, int qty, String net_unit_price, String discount, String tax_rate, String tax, String subtotal,String comment, List<Modifier> modifiers, List<CartDiscount> discounts) {
            this.product_id = product_id;
            this.product_code = product_code;
            this.qty = qty;
            this.net_unit_price = net_unit_price;
            this.discount = discount;
            this.tax_rate = tax_rate;
            this.tax = tax;
            this.subtotal = subtotal;
            this.comment = comment;
            this.modifiers = modifiers;
            this.discounts = discounts;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getProduct_code() {
            return product_code;
        }

        public void setProduct_code(String product_code) {
            this.product_code = product_code;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }



        public String getNet_unit_price() {
            return net_unit_price;
        }

        public void setNet_unit_price(String net_unit_price) {
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

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(String subtotal) {
            this.subtotal = subtotal;
        }

        public List<Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(List<Modifier> modifiers) {
            this.modifiers = modifiers;
        }

        public List<CartDiscount> getDiscounts() {
            return discounts;
        }

        public void setDiscounts(List<CartDiscount> discounts) {
            this.discounts = discounts;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class Modifier implements Serializable {
       private int product_id;
       private int modifier_id;
       private double total;
       private List<ModifierDetail> details;

        public Modifier(int product_id, int modifier_id, double total, List<ModifierDetail> details) {
            this.product_id = product_id;
            this.modifier_id = modifier_id;
            this.total = total;
            this.details = details;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public int getModifier_id() {
            return modifier_id;
        }

        public void setModifier_id(int modifier_id) {
            this.modifier_id = modifier_id;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public List<ModifierDetail> getDetails() {
            return details;
        }

        public void setDetails(List<ModifierDetail> details) {
            this.details = details;
        }
    }


    public static class ModifierDetail implements Serializable{
        private String modifier_data_id;
        private double net_cost;
        private double total_cost;

        public ModifierDetail(String modifier_data_id, double net_cost, double total_cost) {
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
