package com.midad_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.AppSettingModel;
import com.midad_pos.model.ShiftDataModel;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.tags.Tags;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ShiftMvvm extends AndroidViewModel {
    private final String TAG = ShiftMvvm.class.getName();
    private MutableLiveData<String> startAmount;
    private MutableLiveData<Boolean> isShiftsOpened;
    private MutableLiveData<Boolean> isShiftsClosed;

    private MutableLiveData<Boolean> isShiftReportsOpened;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> isOpenSuccess;
    private MutableLiveData<ShiftModel> shift;
    private MutableLiveData<Boolean> showPin;
    private MutableLiveData<Boolean> forNavigation;

    private UserModel userModel;
    private CompositeDisposable disposable = new CompositeDisposable();


    public ShiftMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());
        if (settingModel!=null&&settingModel.getIsShiftOpen()==1){
            getCurrentShift();
        }
        getStartAmount();
    }

    public MutableLiveData<String> getStartAmount() {
        if (startAmount == null) {
            startAmount = new MutableLiveData<>();
            startAmount.setValue("0.00");
        }

        return startAmount;
    }

    public MutableLiveData<Boolean> getIsShiftsOpened() {
        if (isShiftsOpened == null) {
            isShiftsOpened = new MutableLiveData<>();
        }

        return isShiftsOpened;
    }

    public MutableLiveData<ShiftModel> getShift() {
        if (shift == null) {
            shift = new MutableLiveData<>();
        }

        return shift;
    }

    public MutableLiveData<Boolean> getIsShiftsClosed() {
        if (isShiftsClosed == null) {
            isShiftsClosed = new MutableLiveData<>();
        }

        return isShiftsClosed;
    }
    public MutableLiveData<Boolean> getForNavigation() {
        if (forNavigation == null) {
            forNavigation = new MutableLiveData<>();
            forNavigation.setValue(false);
        }

        return forNavigation;
    }

    public MutableLiveData<Boolean> getShowPin() {
        if (showPin == null) {
            showPin = new MutableLiveData<>();
            showPin.setValue(false);
        }

        return showPin;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }

        return isLoading;
    }

    public MutableLiveData<Boolean> getIsOpenSuccess() {
        if (isOpenSuccess == null) {
            isOpenSuccess = new MutableLiveData<>();
        }

        return isOpenSuccess;
    }


    public MutableLiveData<Boolean> getIsShiftReportsOpened() {
        if (isShiftReportsOpened == null) {
            isShiftReportsOpened = new MutableLiveData<>();
            isShiftReportsOpened.setValue(false);
        }

        return isShiftReportsOpened;
    }


    public void openShift() {
        getIsLoading().setValue(true);
        Api.getService(Tags.base_url)
                .openShift(userModel.getData().getSelectedUser().getId(), getStartAmount().getValue(), userModel.getData().getSelectedWereHouse().getId(), userModel.getData().getSelectedPos().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ShiftDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ShiftDataModel> response) {
                        getIsLoading().setValue(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                if (response.body().getStatus() == 200) {
                                    AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());
                                    settingModel.setIsShiftOpen(1);
                                    settingModel.setShift_id(response.body().getData().getId());
                                    Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                    getIsOpenSuccess().setValue(true);
                                } else if (response.body().getStatus() == 431) {
                                    Toast.makeText(getApplication().getApplicationContext(), R.string.shift_alrady_opned, Toast.LENGTH_SHORT).show();

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
                        getIsLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    public void closeShift() {
        AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());

        getIsLoading().setValue(true);
        Api.getService(Tags.base_url)
                .closeShift(userModel.getData().getSelectedUser().getId(), settingModel.getShift_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<StatusResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<StatusResponse> response) {
                        getIsLoading().setValue(false);

                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                if (response.body().getStatus() == 200) {
                                    settingModel.setIsShiftOpen(0);
                                    settingModel.setShift_id(null);
                                    Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                    getIsShiftsClosed().setValue(true);
                                } else if (response.body().getStatus() == 406) {
                                    getIsShiftsClosed().setValue(true);
                                    Toast.makeText(getApplication().getApplicationContext(), R.string.shift_already_closed, Toast.LENGTH_SHORT).show();

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
                        getIsLoading().setValue(false);

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    public void getCurrentShift() {
        getIsLoading().setValue(true);
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        Api.getService(Tags.base_url)
                .currentShift(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId(), userModel.getData().getSelectedPos().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ShiftDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ShiftDataModel> response) {
                        getIsLoading().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getShift().setValue(response.body().getData());
                                }
                            }
                        } else {
                            Log.e("v","v");

                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsLoading().setValue(false);
                        Log.e("error", e.getMessage() + "");

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
