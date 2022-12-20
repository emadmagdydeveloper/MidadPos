package com.midad_app_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_app_pos.R;
import com.midad_app_pos.database.AppDatabase;
import com.midad_app_pos.database.DAO;
import com.midad_app_pos.model.PrinterModel;
import com.midad_app_pos.model.StatusResponse;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.preferences.Preferences;
import com.midad_app_pos.remote.Api;
import com.midad_app_pos.share.Common;
import com.midad_app_pos.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class SettingsMvvm extends AndroidViewModel {
    private final String TAG = SettingsMvvm.class.getName();
    private MutableLiveData<Integer> positions;
    private MutableLiveData<Boolean> isDialogTypeVisible;
    private MutableLiveData<Boolean> onLogoutSuccess;

    public boolean showPin =false;
    public boolean forNavigation = false;
    private AppDatabase database;
    private DAO dao;
    private MutableLiveData<List<PrinterModel>> printers;
    private MutableLiveData<Boolean> isDeleteMode;
    private MutableLiveData<List<PrinterModel>> deletedPrinters;

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
    public MutableLiveData<Boolean> getOnLogoutSuccess() {
        if (onLogoutSuccess == null) {
            onLogoutSuccess = new MutableLiveData<>();
        }

        return onLogoutSuccess;
    }

    public MutableLiveData<Boolean> getIsDeleteMode() {
        if (isDeleteMode == null) {
            isDeleteMode = new MutableLiveData<>();
        }

        return isDeleteMode;
    }
    public MutableLiveData<List<PrinterModel>> getDeletedPrinters() {
        if (deletedPrinters == null) {
            deletedPrinters = new MutableLiveData<>();
            deletedPrinters.setValue(new ArrayList<>());
        }

        return deletedPrinters;
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

                        for (PrinterModel printerModel:list){
                            int pos = getDeletedPrinterPos(printerModel.getId());
                            if (pos!=-1){
                                printerModel.setSelected(true);
                            }else {
                                printerModel.setSelected(false);

                            }

                        }

                        if (getDeletedPrinters().getValue()!=null&&getDeletedPrinters().getValue().size()>0){
                            getIsDeleteMode().setValue(true);

                        }else {
                            getIsDeleteMode().setValue(false);

                        }
                        getPrintersInstance().setValue(list);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void clearDeletedPrinters() {
        if (getDeletedPrinters().getValue() != null) {
            getDeletedPrinters().getValue().clear();
            getDeletedPrinters().setValue(getDeletedPrinters().getValue());
            getIsDeleteMode().setValue(false);
            if (getPrintersInstance().getValue() != null) {
                for (PrinterModel model : getPrintersInstance().getValue()) {
                    if (model.isSelected()) {
                        model.setSelected(false);
                    }
                }

                getDeletedPrinters().setValue(getDeletedPrinters().getValue());
            }

        }
    }


    public void addPrinterToDelete(int pos) {
        if (getDeletedPrinters().getValue() != null && getPrintersInstance().getValue() != null) {
            getDeletedPrinters().getValue().add(getPrintersInstance().getValue().get(pos));
            getDeletedPrinters().setValue(getDeletedPrinters().getValue());
            PrinterModel model = getPrintersInstance().getValue().get(pos);
            model.setSelected(true);

        }

    }

    public void removePrinterFromDeletedList(int pos) {
        if (getDeletedPrinters().getValue() != null && getPrintersInstance().getValue() != null) {
            if (getDeletedPrinters().getValue().size() > 0) {
                PrinterModel printerModel = getPrintersInstance().getValue().get(pos);
                int index = getDeletedPrinterPos(printerModel.getId());
                if (index != -1) {
                    getDeletedPrinters().getValue().remove(index);
                    getDeletedPrinters().setValue(getDeletedPrinters().getValue());

                }
                if (getDeletedPrinters().getValue().size() == 0) {
                    getIsDeleteMode().setValue(false);
                }
            }
        }


    }


    private int getDeletedPrinterPos(long id) {
        if (getDeletedPrinters().getValue() != null) {
            for (int index = 0; index < getDeletedPrinters().getValue().size(); index++) {
                if (getDeletedPrinters().getValue().get(index).getId() == id) {
                    return index;
                }
            }
        }
        return -1;
    }

    public void deletePrinters() {
        dao.deletePrinter(getDeletedPrinters().getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        if (getDeletedPrinters().getValue()!=null){
                            getDeletedPrinters().getValue().clear();
                        }
                        getPrinters();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }



    public void logout(Context context){
        ProgressDialog dialog = Common.createProgressDialog(context, context.getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        UserModel userModel = Preferences.getInstance().getUserData(context);



        Api.getService(Tags.base_url)
                .logout(userModel.getData().getSelectedUser().getId(),userModel.getData().getSelectedPos().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getOnLogoutSuccess().setValue(true);
                                }
                            }
                        } else {
                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                        Log.e(TAG, e.getMessage() + "");

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();


                        }
                    }
                });

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
