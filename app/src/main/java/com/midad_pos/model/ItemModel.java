package com.midad_pos.model;

import android.util.Log;

import androidx.browser.trusted.sharing.ShareTarget;

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
    private String price = "0.0";
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

    private double netTotal = 0.0;
    private double totalPrice = 0.0;
    private VariantModel selectedVariant;
    private List<ModifierModel.Data> selectedModifiers = new ArrayList<>();
    private int amount = 1;
    private List<DiscountModel> discounts = new ArrayList<>();


    public ItemModel() {
    }

    public ItemModel(String id, String type, String name, String image, String image_type, String color, String shape, String price, String cost, String code, String barcode_symbology, String brand_id, String brand, String category_id, CategoryModel category, boolean is_variant, String qty, List<VariantModel> variants, List<ModifierModel> modifiers, TaxModel tax, boolean selected, double totalPrice, VariantModel selectedVariant, List<ModifierModel.Data> selectedModifiers, int amount, List<DiscountModel> discounts) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.image = image;
        this.image_type = image_type;
        this.color = color;
        this.shape = shape;
        this.price = price;
        this.cost = cost;
        this.code = code;
        this.barcode_symbology = barcode_symbology;
        this.brand_id = brand_id;
        this.brand = brand;
        this.category_id = category_id;
        this.category = category;
        this.is_variant = is_variant;
        this.qty = qty;
        this.variants = variants;
        this.modifiers = modifiers;
        this.tax = tax;
        this.selected = selected;
        this.totalPrice = totalPrice;
        this.selectedVariant = selectedVariant;
        this.selectedModifiers = selectedModifiers;
        this.amount = amount;
        this.discounts = discounts;
    }

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
        calculateTotal();
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

    public void setModifiers(List<ModifierModel> modifiers) {
        this.modifiers = modifiers;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public VariantModel getSelectedVariant() {
        return selectedVariant;
    }

    public void setSelectedVariant(VariantModel selectedVariant) {
        this.selectedVariant = selectedVariant;
    }

    public List<ModifierModel.Data> getSelectedModifiers() {
        return selectedModifiers;
    }

    public void setSelectedModifiers(List<ModifierModel.Data> selectedModifiers) {
        this.selectedModifiers = selectedModifiers;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        calculateTotal();
    }

    public List<DiscountModel> getDiscounts() {
        return discounts;
    }

    public void calculateTotal() {
        double price = Double.parseDouble(getPrice());
        if (selectedVariant!=null){
            price = Double.parseDouble(selectedVariant.getPrice());
        }
        Log.e("price,",price+"");
        double totalPrice =  price* amount;
        double totalModifiers = 0.0;
        for (ModifierModel.Data data : getSelectedModifiers()) {
            Log.e("data",data.getTitle());
            if (data.isSelected()){
                totalModifiers += Double.parseDouble(data.getCost())*getAmount();

            }

        }
        if (totalPrice>0){
            double finalTotalPrice = totalPrice+totalModifiers;
            setTotalPrice(finalTotalPrice);
        }else {
            setTotalPrice(0.0);
        }

    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addModifier(ModifierModel.Data item) {
        int pos = getModifierPos(item);

        if (pos == -1) {
            getSelectedModifiers().add(item);
        }
        calculateTotal();
    }
    public void removeModifier(ModifierModel.Data item) {
        int pos = getModifierPos(item);
        if (pos != -1) {
            getSelectedModifiers().remove(pos);
        }
        calculateTotal();
    }

    private int getModifierPos(ModifierModel.Data data) {
        int index = 0;
        for (ModifierModel.Data item : getSelectedModifiers()) {
            if (item.getId().equals(data.getId())) {
                return index;
            }

            index++;
        }

        return -1;
    }

    public void addDiscount(DiscountModel discountModel){
        int index = getDiscountIndex(discountModel);
        if (index==-1){
            if (discountModel.isSelected()){
                getDiscounts().add(discountModel);
            }
        }else {
            if (!discountModel.isSelected()){
                getDiscounts().remove(index);
            }
        }


    }

    private int getDiscountIndex(DiscountModel discountModel){
        int index =0;
        for (DiscountModel model:getDiscounts()){
            if (model.getId().equals(discountModel.getId())){
                return index;
            }
            index++;
        }

        return -1;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }
}
