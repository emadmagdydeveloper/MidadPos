package com.midad_pos.model;

import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.midad_pos.BR;

import java.io.Serializable;

public class SignUpModel extends BaseObservable implements Serializable {
    private String email;
    private String password;
    private String name;
    private String phone;
    private boolean valid1;

    private String business_name;
    private String business_type;
    private String country_code;
    private String country_name;
    private int flag_id;
    private boolean accept_terms;
    private boolean valid2;

    public SignUpModel() {
        this.email ="";
        this.password ="";
        this.name ="";
        this.phone ="";
        this.valid1 = false;
        this.business_name ="";
        this.country_code ="+20";
        this.country_name ="Egypt";
        this.accept_terms = false;
        this.valid2 = false;
    }

    private void isDataValid1(){
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()&&
                password.length()>=8&&
                !name.isEmpty()&&
                !phone.isEmpty() &&
                accept_terms
        ){
            setValid1(true);
        }else {
            setValid1(false);
        }
    }

    private void isDataValid2(){
        if (Patterns.EMAIL_ADDRESS.matcher(business_name).matches()&&
                !country_code.isEmpty()&&
                !country_name.isEmpty()&&
                accept_terms

        ){
            setValid2(true);
        }else {
            setValid2(false);
        }
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
        isDataValid1();
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
        isDataValid1();
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
        isDataValid1();
    }



    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
        isDataValid1();
    }

    @Bindable
    public boolean isValid1() {
        return valid1;
    }

    public void setValid1(boolean valid1) {
        this.valid1 = valid1;
        notifyPropertyChanged(BR.valid1);
    }

    @Bindable
    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
        notifyPropertyChanged(BR.business_name);
        isDataValid2();
    }

    @Bindable
    public String getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(String business_type) {
        this.business_type = business_type;
        notifyPropertyChanged(BR.business_type);
        isDataValid2();

    }

    @Bindable
    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
        notifyPropertyChanged(BR.country_code);
        isDataValid2();

    }

    @Bindable
    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
        notifyPropertyChanged(BR.country_name);
        isDataValid2();

    }

    @Bindable
    public boolean isAccept_terms() {
        return accept_terms;
    }

    public void setAccept_terms(boolean accept_terms) {
        this.accept_terms = accept_terms;
        notifyPropertyChanged(BR.accept_terms);
        isDataValid1();

    }

    @Bindable
    public boolean isValid2() {
        return valid2;
    }

    public void setValid2(boolean valid2) {
        this.valid2 = valid2;
        notifyPropertyChanged(BR.valid2);
    }

    public int getFlag_id() {
        return flag_id;
    }

    public void setFlag_id(int flag_id) {
        this.flag_id = flag_id;
    }
}
