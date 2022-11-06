package com.midad_pos.mvvm;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.midad_pos.R;
import com.midad_pos.model.AppSettingModel;
import com.midad_pos.model.PaymentDataModel;
import com.midad_pos.model.ShiftDataModel;
import com.midad_pos.model.ShiftModel;
import com.midad_pos.model.SinglePayInOutData;
import com.midad_pos.model.UserModel;
import com.midad_pos.preferences.Preferences;
import com.midad_pos.remote.Api;
import com.midad_pos.share.Common;
import com.midad_pos.tags.Tags;

import java.io.IOException;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class CashManagementMvvm extends AndroidViewModel {
    private final String TAG = CashManagementMvvm.class.getName();
    private MutableLiveData<ShiftModel.PayInOutModel> payInOut;
    private MutableLiveData<String> amount;
    private MutableLiveData<String> comment;
    private MutableLiveData<Boolean> isDataChanged;

    private UserModel userModel;

    private CompositeDisposable disposable = new CompositeDisposable();


    public CashManagementMvvm(@NonNull Application application) {
        super(application);
        userModel = Preferences.getInstance().getUserData(getApplication().getApplicationContext());


    }
    public MutableLiveData<String> getAmount() {
        if (amount == null) {
            amount = new MutableLiveData<>();
            amount.setValue("");
        }
        return amount;
    }

    public MutableLiveData<String> getComment() {
        if (comment == null) {
            comment = new MutableLiveData<>();

        }
        return comment;
    }

    public MutableLiveData<ShiftModel.PayInOutModel> getPayInOut() {
        if (payInOut == null) {
            payInOut = new MutableLiveData<>();
        }
        return payInOut;
    }

    public MutableLiveData<Boolean> getIsDataChanged() {
        if (isDataChanged == null) {
            isDataChanged = new MutableLiveData<>();

        }
        return isDataChanged;
    }



    public void payment(Context context,String type) {
        AppSettingModel settingModel  = Preferences.getInstance().getAppSetting(getApplication().getApplicationContext());
        if (settingModel.getIsShiftOpen()!=1){
            return;
        }
        ProgressDialog dialog = Common.createProgressDialog(context,context.getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Api.getService(Tags.base_url)
                .payInOut(userModel.getData().getSelectedUser().getId(),settingModel.getShift_id(),getAmount().getValue(),getComment().getValue(),type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<SinglePayInOutData>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<SinglePayInOutData> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    Log.e("c","tyu");
                                    getIsDataChanged().setValue(true);
                                    getPayInOut().setValue(response.body().getData());
                                }else {
                                    Log.e("code",response.body().getStatus()+"");
                                }
                            } else {
                                Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show();


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
                        Log.e("error", e.getMessage() + "");

                        if (e.getMessage() != null && (e.getMessage().contains("host") || e.getMessage().contains("connection"))) {
                            Toast.makeText(context, R.string.check_network, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, R.string.something_wrong, Toast.LENGTH_SHORT).show();


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
