package com.midad_pos.model;

import android.util.Log;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.midad_pos.BR;
import com.midad_pos.R;

import java.io.Serializable;

public class LoginModel extends BaseObservable implements Serializable {
    private String email;
    private String password;
    private boolean valid;

    public LoginModel() {
        this.email = "";
        this.password = "";
        this.valid = false;
    }

    private void isDataValid() {
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !password.isEmpty()
        ) {
            setValid(true);
        } else {
            setValid(false);
        }
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
        isDataValid();
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
        isDataValid();
    }

    @Bindable
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
        notifyPropertyChanged(BR.valid);

    }
}