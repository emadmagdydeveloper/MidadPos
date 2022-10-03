package com.midad_pos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemModel implements Serializable {
    private String id;
    private String type;
    private String name;
    private String image;
    private String image_type;
    private String color;
    private String shape;
    private String price;
    private String cost;
    private String code;
    private String barcode_symbology;
    private String brand_id;
    private String brand;
    private String category_id;
    private CategoryModel category;
    private boolean is_variant;
    private String qty;
    private List<VariantModel> variants;
    private List<ModifierModel> modifiers;
    private TaxModel tax;
    private boolean selected;

    private double totalPrice = 0.0;
    private VariantModel selectedVariant;
    private ModifierModel selectedModifier;
    private int amount = 1;
    private List<DiscountModel> discounts = new ArrayList<>();



    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getImage_type() {
        return image_type;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public String getCode() {
        return code;
    }

    public String getBarcode_symbology() {
        return barcode_symbology;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory_id() {
        return category_id;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public boolean isIs_variant() {
        return is_variant;
    }

    public String getQty() {
        return qty;
    }

    public List<VariantModel> getVariants() {
        return variants;
    }

    public List<ModifierModel> getModifiers() {
        return modifiers;
    }

    public TaxModel getTax() {
        return tax;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
