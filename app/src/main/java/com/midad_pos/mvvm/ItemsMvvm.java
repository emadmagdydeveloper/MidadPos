package com.midad_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ItemsMvvm extends AndroidViewModel {
    private MutableLiveData<Integer> positions;
    private MutableLiveData<String> queryItems;
    private MutableLiveData<String> queryCategory;
    private MutableLiveData<String> queryDiscounts;


    public ItemsMvvm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getPositions() {
        if (positions == null) {
            positions = new MutableLiveData<>();
            positions.setValue(-1);
        }

        return positions;
    }

    public MutableLiveData<String> getQueryItems() {
        if (queryItems == null) {
            queryItems = new MutableLiveData<>();
            queryItems.setValue("");
        }

        return queryItems;
    }

    public MutableLiveData<String> getQueryCategory() {
        if (queryCategory == null) {
            queryCategory = new MutableLiveData<>();
            queryCategory.setValue("");
        }

        return queryCategory;
    }

    public MutableLiveData<String> getQueryDiscounts() {
        if (queryDiscounts == null) {
            queryDiscounts = new MutableLiveData<>();
            queryDiscounts.setValue("");
        }

        return queryDiscounts;
    }

    public void searchItems(String query) {

        getQueryItems().setValue(query);
    }

    public void searchCategories(String query) {

        getQueryCategory().setValue(query);
    }

    public void searchDiscount(String query) {

        getQueryDiscounts().setValue(query);
    }
}
