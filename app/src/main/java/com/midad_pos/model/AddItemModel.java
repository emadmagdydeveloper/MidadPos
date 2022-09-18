package com.midad_pos.model;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.BR;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private String uri;
    private String shapes;
    private String unit_id;
    private String tax_id;
    private String barcode_type;
    private boolean isShape;
    private boolean isValid;


    private void isDataValid() {


        if (!name.trim().isEmpty() &&
                !barcode.trim().isEmpty() &&
                !unit_id.isEmpty()

        ) {


            if (!isShape) {
                if (!image.isEmpty()) {
                    setValid(true);
                } else {
                    setValid(false);

                }
            } else {
                setValid(true);

            }
        } else {
            setValid(false);
        }
    }


    public AddItemModel() {
        name = "";
        categoryModel = null;
        setPrice("");
        setCost("");
        sku = "";
        barcode = "";
        modifiers = new ArrayList<>();
        vat_id = "";
        tax_id = "";
        color = "#e2e2e2";
        image = "";
        uri = "";
        shapes = "1";
        isShape = true;
        unit_id = "";
        this.isValid = false;
        barcode_type ="C128";
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
        if (price.isEmpty()){
            price ="0";

        }
        this.price = price.replace(".","");
        this.price = this.price.replace(",","");
        BigDecimal num = BigDecimal.valueOf(Float.parseFloat(this.price)).divide(BigDecimal.valueOf(100.0));
        if (num.floatValue()==0){
            this.price ="0.00";
        }else {

            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("###,###.##");

            if (decimalFormat.format(num).length()==9){
                this.price ="999,999.99";
            }else {
                this.price = decimalFormat.format(num);

            }




        }



        notifyPropertyChanged(BR.price);

        isDataValid();
    }

    @Bindable
    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {

        if (cost.isEmpty()){
            cost ="0";


        }
        this.cost = cost.replace(".","");
        this.cost = this.cost.replace(",","");
        BigDecimal num = BigDecimal.valueOf(Float.parseFloat(this.cost)).divide(BigDecimal.valueOf(100.0));
        if (num.floatValue()==0){
            this.cost ="0.00";
        }else {

            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            decimalFormat.applyPattern("###,###.##");

            if (decimalFormat.format(num).length()==9){
                this.cost ="999,999.99";
            }else {
                this.cost = decimalFormat.format(num);

            }




        }
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    @Bindable
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
    }

    public String getBarcode_type() {
        return barcode_type;
    }

    public void setBarcode_type(String barcode_type) {
        this.barcode_type = barcode_type;
    }

    public void addModifier(ModifierModel modifierModel) {
        int pos = getModifierPos(modifierModel);
        if (pos != -1) {
            if (!modifierModel.isChecked()) {
                getModifiers().remove(pos);
            }
        } else {
            getModifiers().add(modifierModel.getId());
        }
    }

    private int getModifierPos(ModifierModel model) {
        for (int index = 0; index < getModifiers().size(); index++) {
            if (getModifiers().get(index).equals(model.getId())) {
                return index;
            }
        }

        return -1;
    }
}
