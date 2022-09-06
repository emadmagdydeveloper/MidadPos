package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.WereHouse;

public class SelectedWereHouseMvvm extends AndroidViewModel {
    private MutableLiveData<WereHouse> wereHouse;
    private MutableLiveData<String> wereHouseName;
    public SelectedWereHouseMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<WereHouse> getWereHouse(){
        if (wereHouse==null){
            wereHouse = new MutableLiveData<>();
        }

        return wereHouse;
    }

    public MutableLiveData<String> getWereHouseName(){
        if (wereHouseName==null){
            wereHouseName = new MutableLiveData<>();
            wereHouseName.setValue(getApplication().getApplicationContext().getString(R.string.store_not_selected));
        }

        return wereHouseName;
    }
}
