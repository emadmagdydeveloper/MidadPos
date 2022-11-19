package com.midad_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.midad_pos.R;
import com.midad_pos.model.DiscountModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class AddPrinterMvvm extends AndroidViewModel {
    private final String TAG = AddPrinterMvvm.class.getName();
    private MutableLiveData<Integer> selectedPrinterPos;
    private MutableLiveData<Integer> selectedPaperPos;
    private MutableLiveData<Integer> selectedInterfacePos;
    private MutableLiveData<Boolean> canPrint;
    private MutableLiveData<String> name;

    public boolean showPin =false;



    private CompositeDisposable disposable = new CompositeDisposable();


    public AddPrinterMvvm(@NonNull Application application) {
        super(application);

    }


    public MutableLiveData<Integer> getSelectedPrinterPos() {
        if (selectedPrinterPos == null) {
            selectedPrinterPos = new MutableLiveData<>();
            selectedPrinterPos.setValue(0);
        }

        return selectedPrinterPos;
    }

    public MutableLiveData<Integer> getSelectedInterfacePos() {
        if (selectedInterfacePos == null) {
            selectedInterfacePos = new MutableLiveData<>();
            selectedInterfacePos.setValue(-1);
        }

        return selectedInterfacePos;
    }

    public MutableLiveData<Integer> getSelectedPaperPos() {
        if (selectedPaperPos == null) {
            selectedPaperPos = new MutableLiveData<>();
            selectedPaperPos.setValue(0);
        }

        return selectedPaperPos;
    }

    public MutableLiveData<Boolean> getCanPrint() {
        if (canPrint == null) {
            canPrint = new MutableLiveData<>();
            canPrint.setValue(false);
        }

        return canPrint;
    }

    public MutableLiveData<String> getName() {
        if (name == null) {
            name = new MutableLiveData<>();
        }

        return name;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
