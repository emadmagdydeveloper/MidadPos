package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ReceiptDetailsMvvm extends AndroidViewModel {
    private MutableLiveData<Boolean> showTicketDetails;
    private MutableLiveData<String> showSendEmailDialog;
    private MutableLiveData<Boolean> isDialogShown;

    private MutableLiveData<Boolean> isSearchOpen;
    private MutableLiveData<String> searchQuery;


    public ReceiptDetailsMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getShowTicketDetails(){
        if (showTicketDetails==null){
            showTicketDetails = new MutableLiveData<>();
        }

        return showTicketDetails;
    }

    public MutableLiveData<Boolean> getIsSearchOpen(){
        if (isSearchOpen==null){
            isSearchOpen = new MutableLiveData<>();
        }

        return isSearchOpen;
    }

    public MutableLiveData<String> getSearchQuery(){
        if (searchQuery==null){
            searchQuery = new MutableLiveData<>();
            searchQuery.setValue("");
        }

        return searchQuery;
    }

    public MutableLiveData<String> getShowSendEmailDialog(){
        if (showSendEmailDialog==null){
            showSendEmailDialog = new MutableLiveData<>();
            showSendEmailDialog.setValue("");
        }

        return showSendEmailDialog;
    }

    public MutableLiveData<Boolean> getIsDialogShown(){
        if (isDialogShown==null){
            isDialogShown = new MutableLiveData<>();
        }

        return isDialogShown;
    }
}
