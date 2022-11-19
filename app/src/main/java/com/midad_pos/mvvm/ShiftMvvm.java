package com.midad_pos.mvvm;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.AppSettingModel;
import com.midad_pos.model.ShiftDataModel;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.model.ShiftsDataModel;
import com.midad_pos.model.StatusResponse;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.tags.Tags;

import java.io.IOException;
import java.util.List;


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
    private MutableLiveData<Boolean> showDialogCloseShift;
    private MutableLiveData<Boolean> isShiftReportsOpened;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Boolean> isLoadingShifts;
    private MutableLiveData<Boolean> isOpenSuccess;
    private MutableLiveData<ShiftModel> shift;
    private MutableLiveData<String> actualAmount;
    private MutableLiveData<List<ShiftModel>> shifts;
    private MutableLiveData<ShiftModel> selectedHistoryShift;
    public boolean showPin =false;
    public boolean forNavigation = false;
    private UserModel userModel;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public ShiftMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());
        if (settingModel != null && settingModel.getIsShiftOpen() == 1) {
            getCurrentShift();
            getShiftsData();
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

    public MutableLiveData<Boolean> getShowDialogCloseShift() {
        if (showDialogCloseShift == null) {
            showDialogCloseShift = new MutableLiveData<>();
        }

        return showDialogCloseShift;
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



    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }

        return isLoading;
    }

    public MutableLiveData<Boolean> getIsLoadingShifts() {
        if (isLoadingShifts == null) {
            isLoadingShifts = new MutableLiveData<>();
        }

        return isLoadingShifts;
    }

    public MutableLiveData<Boolean> getIsOpenSuccess() {
        if (isOpenSuccess == null) {
            isOpenSuccess = new MutableLiveData<>();
        }

        return isOpenSuccess;
    }

    public MutableLiveData<String> getActualAmount() {
        if (actualAmount == null) {
            actualAmount = new MutableLiveData<>();
        }

        return actualAmount;
    }

    public MutableLiveData<Boolean> getIsShiftReportsOpened() {
        if (isShiftReportsOpened == null) {
            isShiftReportsOpened = new MutableLiveData<>();
            isShiftReportsOpened.setValue(false);
        }

        return isShiftReportsOpened;
    }

    public MutableLiveData<List<ShiftModel>> getShifts() {
        if (shifts == null) {
            shifts = new MutableLiveData<>();
        }

        return shifts;
    }

    public MutableLiveData<ShiftModel> getSelectedHistoryShift() {
        if (selectedHistoryShift == null) {
            selectedHistoryShift = new MutableLiveData<>();
        }

        return selectedHistoryShift;
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
                                if (response.errorBody() != null) {
                                    Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                }
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

    public void closeShift() {
        if (getShift().getValue() != null) {
            AppSettingModel settingModel = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());

            getIsLoading().setValue(true);
            Api.getService(Tags.base_url)
                    .closeShift(userModel.getData().getSelectedUser().getId(), settingModel.getShift_id(), getActualAmount().getValue() == null ? "0.00" : getActualAmount().getValue(), getShift().getValue().getExpected_cash() + "")
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
                                        getShowDialogCloseShift().setValue(false);
                                        Preferences.getInstance().createUpdateAppSetting(getApplication().getApplicationContext(), settingModel);
                                        getIsShiftsClosed().setValue(true);
                                    } else if (response.body().getStatus() == 406) {
                                        getShowDialogCloseShift().setValue(false);
                                        getIsShiftsClosed().setValue(true);
                                        Toast.makeText(getApplication().getApplicationContext(), R.string.shift_already_closed, Toast.LENGTH_SHORT).show();

                                    } else {
                                        getShowDialogCloseShift().setValue(true);
                                        getIsOpenSuccess().setValue(true);

                                    }
                                }
                            } else {


                                try {
                                    if (response.errorBody() != null) {
                                        Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                        @Override
                        public void onError(Throwable e) {
                            getIsLoading().setValue(false);
                            getShowDialogCloseShift().setValue(true);
                            Log.e("error", e.getMessage() + "");

                            if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                                Toast.makeText(getApplication().getApplicationContext(), R.string.check_network, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
        }

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
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                }
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

    public void getShiftsData() {
        getIsLoadingShifts().setValue(true);
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());
        Api.getService(Tags.base_url)
                .shifts(userModel.getData().getSelectedUser().getId(), userModel.getData().getSelectedWereHouse().getId(), userModel.getData().getSelectedPos().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ShiftsDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<ShiftsDataModel> response) {
                        getIsLoadingShifts().setValue(false);
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    getShifts().setValue(response.body().getData());
                                }
                            }
                        } else {
                            Toast.makeText(getApplication().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, response.code() + "__" + response.errorBody().string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        getIsLoadingShifts().setValue(false);
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
