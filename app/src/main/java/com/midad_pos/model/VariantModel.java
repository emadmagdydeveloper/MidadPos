package com.midad_pos.model;

import java.io.Serializable;

public class VariantModel implements Serializable {
    private String id;
    private String name;
    private String cost;
    private String price;
    private String code;
    private String qty;
    private boolean isSelected;
    private Pivot pivot;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCost() {
        return cost;
    }

    public String getPrice() {
        return price;
    }

    public String getCode() {
        return code;
    }

    public String getQty() {
        return qty;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Pivot getPivot() {
        return pivot;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public static class Pivot implements Serializable{
        private String product_id;
        private String variant_id;
        private String id;
        private String item_code;
        private String additional_cost;
        private String additional_price;

        public String getProduct_id() {
            return product_id;
        }

        public String getVariant_id() {
            return variant_id;
        }

        public String getId() {
            return id;
        }

        public String getItem_code() {
            return item_code;
        }

        public String getAdditional_cost() {
            return additional_cost;
        }

        public String getAdditional_price() {
            return additional_price;
        }
    }
}
