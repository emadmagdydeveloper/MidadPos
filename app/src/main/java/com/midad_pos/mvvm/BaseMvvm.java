package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class BaseMvvm extends AndroidViewModel {
    private MutableLiveData<String> pinCode ;
    private MutableLiveData<Boolean> pay_in_out;
    public BaseMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getPinCode(){
        if (pinCode==null){
            pinCode = new MutableLiveData<>();
            pinCode.setValue("");
        }
        return pinCode;
    }

    public MutableLiveData<Boolean> getPay_in_out(){
        if (pay_in_out==null){
            pay_in_out = new MutableLiveData<>();
            pay_in_out.setValue(false);
        }
        return pay_in_out;
    }
}
