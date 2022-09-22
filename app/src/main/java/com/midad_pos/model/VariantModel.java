package com.midad_pos.model;

import java.io.Serializable;

public class VariantModel implements Serializable {
    private String id;
    private String name;
    private String cost;
    private String price;
    private String code;
    private String qty;

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
}
