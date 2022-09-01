package com.midad_pos.model;

import android.util.Log;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.midad_pos.BR;

import java.io.Serializable;

public class AddCustomerModel extends BaseObservable implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String region;
    private String postal_code;
    private String country;
    private String customer_code;
    private String note;
    private boolean isValid;

    public AddCustomerModel() {
        this.name ="";
        this.email ="";
        this.phone ="";
        this.address ="";
        this.city ="";
        this.region ="";
        this.postal_code ="";
        this.country ="";
        this.customer_code ="";
        this.note="";
        this.isValid =false;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
        setValid(!name.isEmpty());

    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
        setValid(Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
        setValid(!phone.isEmpty());

    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
        setValid(!address.isEmpty());
    }

    @Bindable
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);
        setValid(!city.isEmpty());
    }

    @Bindable
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
        notifyPropertyChanged(BR.region);
        setValid(!region.isEmpty());
    }

    @Bindable
    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
        notifyPropertyChanged(BR.postal_code);
        setValid(!postal_code.isEmpty());
    }

    @Bindable
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        notifyPropertyChanged(BR.country);
        setValid(!country.isEmpty());
    }

    @Bindable
    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
        notifyPropertyChanged(BR.customer_code);
        setValid(!customer_code.isEmpty());
    }

    @Bindable
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
        notifyPropertyChanged(BR.note);
        setValid(!note.isEmpty());
    }

    @Bindable
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
        notifyPropertyChanged(BR.valid);
        Log.e("data",valid+"");
    }
}
