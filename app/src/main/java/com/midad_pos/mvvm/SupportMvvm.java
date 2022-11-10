package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SupportMvvm extends AndroidViewModel {
    private MutableLiveData<Integer> positions;
    private MutableLiveData<Boolean> forNavigation;
    private MutableLiveData<Boolean> showPin;

    public SupportMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getForNavigation() {
        if (forNavigation == null) {
            forNavigation = new MutableLiveData<>();
            forNavigation.setValue(false);
        }

        return forNavigation;
    }
    public MutableLiveData<Boolean> getShowPin() {
        if (showPin == null) {
            showPin = new MutableLiveData<>();
            showPin.setValue(false);
        }

        return showPin;
    }

    public MutableLiveData<Integer> getPositions() {
        if (positions == null) {
            positions = new MutableLiveData<>();
            positions.setValue(-1);
        }

        return positions;
    }


}
