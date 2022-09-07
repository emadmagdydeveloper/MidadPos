package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.POSModel;

public class SelectedPosMvvm extends AndroidViewModel {
    private MutableLiveData<POSModel> posModel;
    private MutableLiveData<String> posName;
    public SelectedPosMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<POSModel> getPosModel(){
        if (posModel ==null){
            posModel = new MutableLiveData<>();
        }

        return posModel;
    }

    public MutableLiveData<String> getPosName(){
        if (posName ==null){
            posName = new MutableLiveData<>();
            posName.setValue(getApplication().getApplicationContext().getString(R.string.pos_not_selected));
        }

        return posName;
    }
}
