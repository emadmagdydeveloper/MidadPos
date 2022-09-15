package com.midad_pos.model;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddItemModel extends BaseObservable implements Serializable {
    private String name;
    private CategoryModel categoryModel;
    private String price;
    private String cost;
    private String sku;
    private String barcode;
    private List<String> modifiers;
    private String vat_id;
    private String color;
    private String image;
    private String shapes;
    private boolean isShape;
    private MutableLiveData<Boolean> isValid;

    private void isDataValid(){
        if (!name.trim().isEmpty()&&
                categoryModel!=null&&
                !price.trim().isEmpty()&&
                !barcode.trim().isEmpty()

        ){
            if (!isShape){
                if (!image.isEmpty()){

                }else {

                }
            }else {

            }
        }else {

        }
    }


    public AddItemModel(MutableLiveData<Boolean> isValid) {
        name ="";
        categoryModel = null;
        price="";
        cost ="0.00";
        sku="";
        barcode ="";
        modifiers = new ArrayList<>();
        vat_id ="";
        color ="#e2e2e2";
        image = "";
        shapes ="1";
        isShape = true;
        this.isValid = isValid;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
        isDataValid();
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
        isDataValid();
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);

        isDataValid();
    }

    @Bindable
    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
        notifyPropertyChanged(BR.cost);
        isDataValid();
    }

    @Bindable
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
        notifyPropertyChanged(BR.sku);
        isDataValid();
    }

    @Bindable
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
        notifyPropertyChanged(BR.barcode);
        isDataValid();
    }

    public List<String> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<String> modifiers) {
        this.modifiers = modifiers;
    }

    public String getVat_id() {
        return vat_id;
    }

    public void setVat_id(String vat_id) {
        this.vat_id = vat_id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        isDataValid();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        isDataValid();
    }

    public String getShapes() {
        return shapes;
    }

    public void setShapes(String shapes) {
        this.shapes = shapes;
    }

    public boolean isShape() {
        return isShape;
    }

    public void setShape(boolean shape) {
        isShape = shape;
        isDataValid();
    }
}
