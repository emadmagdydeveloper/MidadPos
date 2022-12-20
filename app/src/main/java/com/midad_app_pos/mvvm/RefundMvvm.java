package com.midad_app_pos.mvvm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.midad_app_pos.R;

import io.reactivex.disposables.CompositeDisposable;

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
