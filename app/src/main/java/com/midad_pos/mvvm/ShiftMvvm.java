package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ShiftMvvm extends AndroidViewModel {
    private MutableLiveData<String> startAmount;
    private MutableLiveData<Boolean> isShiftsOpened;
    private MutableLiveData<Boolean> isShiftReportsOpened;

    public ShiftMvvm(@NonNull Application application) {
        super(application);
        getStartAmount();
    }

    public MutableLiveData<String> getStartAmount(){
        if (startAmount==null){
            startAmount = new MutableLiveData<>();
            startAmount.setValue("0.00");
        }

        return startAmount;
    }

    public MutableLiveData<Boolean> getIsShiftsOpened(){
        if (isShiftsOpened==null){
            isShiftsOpened = new MutableLiveData<>();
            isShiftsOpened.setValue(false);
        }

        return isShiftsOpened;
    }

    public MutableLiveData<Boolean> getIsShiftReportsOpened(){
        if (isShiftReportsOpened==null){
            isShiftReportsOpened = new MutableLiveData<>();
            isShiftReportsOpened.setValue(false);
        }

        return isShiftReportsOpened;
    }


}
