package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.PaymentDataModel;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.tags.Tags;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class RefundMvvm extends AndroidViewModel {
    private final String TAG = RefundMvvm.class.getName();
    public boolean showPin =false;

    private CompositeDisposable disposable = new CompositeDisposable();


    public RefundMvvm(@NonNull Application application) {
        super(application);


    }
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
