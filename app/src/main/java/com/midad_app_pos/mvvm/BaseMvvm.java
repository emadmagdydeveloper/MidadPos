package com.midad_app_pos.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_app_pos.R;
import com.midad_app_pos.model.PaymentDataModel;
import com.midad_app_pos.model.UserModel;
import com.midad_app_pos.preferences.Preferences;
import com.midad_app_pos.remote.Api;
import com.midad_app_pos.tags.Tags;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class BaseMvvm extends AndroidViewModel {
    private final String TAG = BaseMvvm.class.getName();
    private MutableLiveData<String> pinCode;
    private MutableLiveData<Boolean> pay_in_out;
    private MutableLiveData<Boolean> onUserRefreshed;
    private MutableLiveData<PaymentDataModel.Data> payments;
    private UserModel userModel;
    private MutableLiveData<String> onError;
    private MutableLiveData<Boolean> isUserSelected;
    private MutableLiveData<Boolean> onPinSuccess;
    private MutableLiveData<Boolean> onNavigate;


    private CompositeDisposable disposable = new CompositeDisposable();


    public BaseMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());


    }

    public MutableLiveData<String> getPinCode() {
        if (pinCode == null) {
            pinCode = new MutableLiveData<>();
            pinCode.setValue("");
        }
        return pinCode;
    }

    public MutableLiveData<Boolean> getPay_in_out() {
        if (pay_in_out == null) {
            pay_in_out = new MutableLiveData<>();
            pay_in_out.setValue(false);
        }
        return pay_in_out;
    }



    public MutableLiveData<Boolean> getOnUserRefreshed() {
        if (onUserRefreshed == null) {
            onUserRefreshed = new MutableLiveData<>();
        }
        return onUserRefreshed;
    }

    public MutableLiveData<Boolean> getOnPinSuccess() {
        if (onPinSuccess == null) {
            onPinSuccess = new MutableLiveData<>();
        }
        return onPinSuccess;
    }

    public MutableLiveData<Boolean> getOnNavigate() {
        if (onNavigate == null) {
            onNavigate = new MutableLiveData<>();
        }
        return onNavigate;
    }

    public MutableLiveData<Boolean> getIsUserSelected() {
        if (isUserSelected == null) {
            isUserSelected = new MutableLiveData<>();
        }
        return isUserSelected;
    }

    public MutableLiveData<PaymentDataModel.Data> getPaymentsInstance() {
        if (payments == null) {
            payments = new MutableLiveData<>();
        }
        return payments;
    }

    public MutableLiveData<String> getOnError() {
        if (onError == null) {
            onError = new MutableLiveData<>();

        }
        return onError;
    }

    public void getPayments() {
        if (getPaymentsInstance().getValue()!=null){
            getPaymentsInstance().setValue(getPaymentsInstance().getValue());
            return;
        }
        if (userModel==null||userModel.getData()==null||userModel.getData().getSelectedUser()==null){
            return;
        }
        Api.getService(Tags.base_url)
                .getPayments(userModel.getData().getSelectedUser().getId(),userModel.getData().getSelectedWereHouse().getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<PaymentDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<PaymentDataModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                   getPaymentsInstance().setValue(response.body().getData());

                                } else {
                                    getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));
                                }
                            } else {

                                getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            }
                        } else {

                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

                            try {
                                Log.e(TAG, response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Log.e("error", e.getMessage() + "");
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.check_network));
                        } else {
                            getOnError().setValue(getApplication().getApplicationContext().getString(R.string.something_wrong));

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
