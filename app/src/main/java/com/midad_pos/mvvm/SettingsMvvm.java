package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SettingsMvvm extends AndroidViewModel {
    private MutableLiveData<Integer> positions;


    public SettingsMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getPositions() {
        if (positions == null) {
            positions = new MutableLiveData<>();
            positions.setValue(-1);
        }

        return positions;
    }


}
