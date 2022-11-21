package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.database.AppDatabase;
import com.midad_pos.database.DAO;
import com.midad_pos.model.PrinterModel;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SettingsMvvm extends AndroidViewModel {
    private MutableLiveData<Integer> positions;
    private MutableLiveData<Boolean> isDialogTypeVisible;
    public boolean showPin =false;
    public boolean forNavigation = false;
    private AppDatabase database;
    private DAO dao;
    private MutableLiveData<List<PrinterModel>> printers;
    private CompositeDisposable disposable = new CompositeDisposable();

    public SettingsMvvm(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        dao = database.getDAO();
    }

    public MutableLiveData<Integer> getPositions() {
        if (positions == null) {
            positions = new MutableLiveData<>();
            positions.setValue(-1);
        }

        return positions;
    }

    public MutableLiveData<Boolean> getIsDialogTypeVisible() {
        if (isDialogTypeVisible == null) {
            isDialogTypeVisible = new MutableLiveData<>();
        }

        return isDialogTypeVisible;
    }

    public MutableLiveData<List<PrinterModel>> getPrintersInstance () {
        if (printers == null) {
            printers = new MutableLiveData<>();
        }

        return printers;
    }

    public void getPrinters(){
        dao.getPrinters().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<PrinterModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<PrinterModel> list) {
                        getPrintersInstance().setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
