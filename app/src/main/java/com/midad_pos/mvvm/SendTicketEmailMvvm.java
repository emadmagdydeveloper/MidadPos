package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class SendTicketEmailMvvm extends AndroidViewModel {
    private MutableLiveData<String> email;

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
