package com.midad_app_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SendTicketEmailMvvm extends AndroidViewModel {
    private MutableLiveData<String> email;
    public boolean showPin =false;

    public SendTicketEmailMvvm(@NonNull Application application) {
        super(application);
    }



    public MutableLiveData<String > getEmail(){
        if (email==null){
            email = new MutableLiveData<>();
            email.setValue("");
        }

        return email;
    }
}
