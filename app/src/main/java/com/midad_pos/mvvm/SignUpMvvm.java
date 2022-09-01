package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.model.SignUpModel;

public class SignUpMvvm extends AndroidViewModel {
    private SignUpModel signUpModel;
    private MutableLiveData<SignUpModel> signUpModelMutableLiveData;

    public SignUpMvvm(@NonNull Application application) {
        super(application);
        signUpModel = new SignUpModel();
        getSignUpModel().setValue(signUpModel);
    }

    public MutableLiveData<SignUpModel> getSignUpModel(){
        if (signUpModelMutableLiveData==null){
            signUpModelMutableLiveData = new MutableLiveData<>();
        }
        return signUpModelMutableLiveData;
    }
}
