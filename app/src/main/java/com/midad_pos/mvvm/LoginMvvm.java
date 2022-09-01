package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.model.LoginModel;

public class LoginMvvm extends AndroidViewModel {
    private LoginModel loginModel;
    private MutableLiveData<LoginModel> loginModelMutableLiveData;
    public LoginMvvm(@NonNull Application application) {
        super(application);
        loginModel = new LoginModel();
        getLoginModel().setValue(loginModel);
    }

    public MutableLiveData<LoginModel> getLoginModel(){
        if (loginModelMutableLiveData==null){
            loginModelMutableLiveData = new MutableLiveData<>();
        }

        return loginModelMutableLiveData;
    }
}
